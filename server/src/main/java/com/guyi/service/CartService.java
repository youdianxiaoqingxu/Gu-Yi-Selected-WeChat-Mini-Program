package com.guyi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.guyi.common.BusinessException;
import com.guyi.dto.CartAddDTO;
import com.guyi.dto.CartUpdateDTO;
import com.guyi.entity.Cart;
import com.guyi.entity.Goods;
import com.guyi.entity.GoodsSpec;
import com.guyi.mapper.CartMapper;
import com.guyi.mapper.GoodsMapper;
import com.guyi.mapper.GoodsSpecMapper;
import com.guyi.vo.CartVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartMapper cartMapper;
    private final GoodsMapper goodsMapper;
    private final GoodsSpecMapper goodsSpecMapper;

    /** 查询当前用户的购物车，并补齐商品与规格展示信息。 */
    public List<CartVO> list(Integer userId) {
        List<Cart> carts = cartMapper.selectList(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .orderByDesc(Cart::getId));
        if (carts.isEmpty()) {
            return List.of();
        }

        Set<Integer> goodsIds = carts.stream().map(Cart::getGoodsId).collect(Collectors.toSet());
        Map<Integer, Goods> goodsMap = goodsMapper.selectByIds(goodsIds).stream()
                .collect(Collectors.toMap(Goods::getId, goods -> goods));

        Set<Integer> specIds = carts.stream()
                .map(Cart::getSpecId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Integer, GoodsSpec> specMap = specIds.isEmpty()
                ? Map.of()
                : goodsSpecMapper.selectByIds(specIds).stream()
                        .collect(Collectors.toMap(GoodsSpec::getId, spec -> spec));

        return carts.stream()
                .map(cart -> toVO(cart, goodsMap.get(cart.getGoodsId()), specMap.get(cart.getSpecId())))
                .toList();
    }

    public void add(Integer userId, CartAddDTO dto) {
        Goods goods = requireGoods(dto.getGoodsId());
        Integer specId = dto.getSpecId();
        if (specId != null) {
            GoodsSpec spec = goodsSpecMapper.selectById(specId);
            if (spec == null || !spec.getGoodsId().equals(goods.getId())) {
                throw new BusinessException(400, "商品规格不合法");
            }
        }

        int addQuantity = dto.getQuantity() == null ? 1 : dto.getQuantity();
        Cart existing = cartMapper.selectOne(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getGoodsId, dto.getGoodsId())
                .eq(Cart::getSpecId, specId));

        int newQuantity = (existing == null ? 0 : existing.getQuantity()) + addQuantity;
        if (newQuantity > goods.getStock()) {
            throw new BusinessException(400, "库存不足");
        }

        if (existing == null) {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setGoodsId(dto.getGoodsId());
            cart.setSpecId(specId);
            cart.setQuantity(newQuantity);
            cart.setChecked(1);
            cartMapper.insert(cart);
        } else {
            existing.setQuantity(newQuantity);
            cartMapper.updateById(existing);
        }
    }

    public void update(Integer userId, Integer id, CartUpdateDTO dto) {
        Cart cart = requireCart(userId, id);

        if (dto.getQuantity() != null) {
            Goods goods = requireGoods(cart.getGoodsId());
            if (dto.getQuantity() < 1) {
                throw new BusinessException(400, "数量至少为1");
            }
            if (dto.getQuantity() > goods.getStock()) {
                throw new BusinessException(400, "库存不足");
            }
            cart.setQuantity(dto.getQuantity());
        }
        if (dto.getChecked() != null) {
            cart.setChecked(dto.getChecked());
        }
        cartMapper.updateById(cart);
    }

    public void remove(Integer userId, Integer id) {
        requireCart(userId, id);
        cartMapper.deleteById(id);
    }

    private Cart requireCart(Integer userId, Integer id) {
        Cart cart = cartMapper.selectById(id);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException(404, "购物车记录不存在");
        }
        return cart;
    }

    private Goods requireGoods(Integer goodsId) {
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null || goods.getStatus() == null || goods.getStatus() != 1) {
            throw new BusinessException(404, "商品不存在或已下架");
        }
        return goods;
    }

    private CartVO toVO(Cart cart, Goods goods, GoodsSpec spec) {
        CartVO vo = new CartVO();
        vo.setId(cart.getId());
        vo.setGoodsId(cart.getGoodsId());
        vo.setSpecId(cart.getSpecId());
        vo.setQuantity(cart.getQuantity());
        vo.setChecked(cart.getChecked());

        if (goods != null) {
            vo.setGoodsTitle(goods.getTitle());
            vo.setCoverImg(goods.getCoverImg());
            vo.setCraft(goods.getCraft());
            vo.setUnit(goods.getUnit());
            vo.setPrice(goods.getPrice());
            vo.setStock(goods.getStock());
        }
        if (spec != null && spec.getSpecJson() != null) {
            vo.setSpecText(spec.getSpecJson().values().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(" / ")));
        }
        return vo;
    }
}
