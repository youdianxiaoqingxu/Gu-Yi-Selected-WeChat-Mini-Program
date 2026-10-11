package com.guyi.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guyi.common.BusinessException;
import com.guyi.common.PageQuery;
import com.guyi.common.PageResult;
import com.guyi.dto.OrderCreateDTO;
import com.guyi.entity.Address;
import com.guyi.entity.Cart;
import com.guyi.entity.Goods;
import com.guyi.entity.GoodsSpec;
import com.guyi.entity.OrderItem;
import com.guyi.entity.Orders;
import com.guyi.mapper.AddressMapper;
import com.guyi.mapper.CartMapper;
import com.guyi.mapper.GoodsMapper;
import com.guyi.mapper.GoodsSpecMapper;
import com.guyi.mapper.OrderItemMapper;
import com.guyi.mapper.OrderMapper;
import com.guyi.util.OrderNoUtil;
import com.guyi.vo.OrderDetailVO;
import com.guyi.vo.OrderItemVO;
import com.guyi.vo.OrderPreviewItemVO;
import com.guyi.vo.OrderPreviewVO;
import com.guyi.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartMapper cartMapper;
    private final AddressMapper addressMapper;
    private final GoodsMapper goodsMapper;
    private final GoodsSpecMapper goodsSpecMapper;

    /** 结算预览：只计算金额，不产生订单。 */
    public OrderPreviewVO preview(Integer userId, List<Integer> cartIds) {
        List<Cart> carts = loadCheckedCarts(userId, cartIds);
        OrderPreviewVO vo = new OrderPreviewVO();

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderPreviewItemVO> items = new ArrayList<>();
        for (Cart cart : carts) {
            Goods goods = requireOnShelfGoods(cart.getGoodsId());
            if (cart.getQuantity() > goods.getStock()) {
                throw new BusinessException(400, "商品库存不足");
            }

            OrderPreviewItemVO item = new OrderPreviewItemVO();
            item.setGoodsId(goods.getId());
            item.setSpecId(cart.getSpecId());
            item.setGoodsTitle(goods.getTitle());
            item.setCoverImg(goods.getCoverImg());
            item.setSpecText(specText(goods, cart.getSpecId()));
            item.setPrice(goods.getPrice());
            item.setQuantity(cart.getQuantity());
            item.setSubtotal(goods.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
            item.setStock(goods.getStock());
            items.add(item);
            totalAmount = totalAmount.add(item.getSubtotal());
        }

        // 本期默认包邮，后续可在此扩展运费规则
        vo.setItems(items);
        vo.setTotalAmount(totalAmount);
        vo.setFreight(BigDecimal.ZERO);
        vo.setPayAmount(totalAmount);
        return vo;
    }

    /** 创建订单：生成订单 + 商品快照 + 条件扣库存 + 清购物车，全部在同一事务内。 */
    @Transactional
    public Integer create(Integer userId, OrderCreateDTO dto) {
        Address address = addressMapper.selectById(dto.getAddressId());
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(400, "收货地址不存在");
        }

        List<Cart> carts = loadCheckedCarts(userId, dto.getCartIds());
        if (carts.isEmpty()) {
            throw new BusinessException(400, "请选择要结算的商品");
        }

        String orderNo = OrderNoUtil.generate();
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (Cart cart : carts) {
            Goods goods = requireOnShelfGoods(cart.getGoodsId());
            // 带库存条件的更新，避免并发超卖
            int updated = goodsMapper.deductStock(goods.getId(), cart.getQuantity());
            if (updated == 0) {
                throw new BusinessException(400, "商品库存不足");
            }

            OrderItem item = new OrderItem();
            item.setGoodsId(goods.getId());
            item.setSpecId(cart.getSpecId());
            item.setGoodsTitle(goods.getTitle());
            item.setGoodsImg(goods.getCoverImg());
            item.setSpecText(specText(goods, cart.getSpecId()));
            item.setPrice(goods.getPrice());
            item.setQuantity(cart.getQuantity());
            item.setSubtotal(goods.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
            orderItems.add(item);
            totalAmount = totalAmount.add(item.getSubtotal());
        }

        Orders order = new Orders();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setFreight(BigDecimal.ZERO);
        order.setPayAmount(totalAmount);
        order.setStatus(0);
        order.setReceiverName(address.getName());
        order.setReceiverPhone(address.getPhone());
        order.setReceiverAddr(joinAddress(address));
        order.setRemark(dto.getRemark());
        orderMapper.insert(order);

        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }

        // 下单成功后清除对应的购物车记录
        cartMapper.delete(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .in(Cart::getId, dto.getCartIds()));
        return order.getId();
    }

    /** 模拟支付：待付款转为待发货。 */
    public void pay(Integer userId, Integer id) {
        Orders order = requireOwnedOrder(userId, id);
        if (order.getStatus() != 0) {
            throw new BusinessException(400, "当前订单状态不可支付");
        }
        order.setStatus(1);
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    /** 取消订单：仅待付款可取消，并回滚库存。 */
    @Transactional
    public void cancel(Integer userId, Integer id) {
        Orders order = requireOwnedOrder(userId, id);
        if (order.getStatus() != 0) {
            throw new BusinessException(400, "仅待付款订单可取消");
        }

        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, id));
        for (OrderItem item : items) {
            goodsMapper.restoreStock(item.getGoodsId(), item.getQuantity());
        }

        order.setStatus(4);
        order.setCancelTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    public PageResult<OrderVO> list(Integer userId, Integer status, String keyword, PageQuery query) {
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<Orders>()
                .eq(Orders::getUserId, userId)
                .eq(status != null, Orders::getStatus, status)
                .orderByDesc(Orders::getId);

        if (keyword != null && !keyword.isBlank()) {
            List<Integer> matchedOrderIds = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                            .like(OrderItem::getGoodsTitle, keyword))
                    .stream()
                    .map(OrderItem::getOrderId)
                    .distinct()
                    .toList();

            wrapper.and(w -> {
                w.like(Orders::getOrderNo, keyword);
                if (!matchedOrderIds.isEmpty()) {
                    w.or().in(Orders::getId, matchedOrderIds);
                }
            });
        }

        Page<Orders> page = orderMapper.selectPage(
                new Page<>(query.getPage(), query.getSize()),
                wrapper);

        // 一次性查询所有订单明细，避免逐单查询
        List<Integer> orderIds = page.getRecords().stream().map(Orders::getId).toList();
        Map<Integer, List<OrderItem>> itemMap = orderIds.isEmpty()
                ? Map.of()
                : orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                        .in(OrderItem::getOrderId, orderIds))
                        .stream()
                        .collect(Collectors.groupingBy(OrderItem::getOrderId));

        List<OrderVO> list = page.getRecords().stream()
                .map(order -> toOrderVO(order, itemMap.getOrDefault(order.getId(), List.of())))
                .toList();
        return new PageResult<>(list, page.getTotal(), page.getCurrent(), page.getSize());
    }

    public OrderDetailVO detail(Integer userId, Integer id) {
        Orders order = requireOwnedOrder(userId, id);
        OrderDetailVO vo = BeanUtil.copyProperties(order, OrderDetailVO.class);
        vo.setItems(orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderId, id))
                .stream()
                .map(item -> BeanUtil.copyProperties(item, OrderItemVO.class))
                .toList());
        return vo;
    }

    private List<Cart> loadCheckedCarts(Integer userId, List<Integer> cartIds) {
        return cartMapper.selectList(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .in(Cart::getId, cartIds)
                .eq(Cart::getChecked, 1));
    }

    private Goods requireOnShelfGoods(Integer goodsId) {
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null || goods.getStatus() == null || goods.getStatus() != 1) {
            throw new BusinessException(400, "商品已下架");
        }
        return goods;
    }

    private Orders requireOwnedOrder(Integer userId, Integer id) {
        Orders order = orderMapper.selectById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(404, "订单不存在");
        }
        return order;
    }

    private String specText(Goods goods, Integer specId) {
        if (specId == null) {
            return "标准款";
        }
        GoodsSpec spec = goodsSpecMapper.selectById(specId);
        if (spec == null || spec.getSpecJson() == null) {
            return "标准款";
        }
        return spec.getSpecJson().values().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" / "));
    }

    private String joinAddress(Address address) {
        return Stream.of(address.getProvince(), address.getCity(), address.getDistrict(), address.getDetail())
                .filter(value -> value != null && !value.isBlank())
                .collect(Collectors.joining(""));
    }

    private OrderVO toOrderVO(Orders order, List<OrderItem> items) {
        OrderVO vo = BeanUtil.copyProperties(order, OrderVO.class);
        vo.setItems(items.stream()
                .map(item -> BeanUtil.copyProperties(item, OrderItemVO.class))
                .toList());
        vo.setItemCount(items.stream().mapToInt(OrderItem::getQuantity).sum());
        return vo;
    }
}
