package com.guyi.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guyi.common.BusinessException;
import com.guyi.common.PageResult;
import com.guyi.common.UserContext;
import com.guyi.dto.GoodsQueryDTO;
import com.guyi.entity.Category;
import com.guyi.entity.Favorite;
import com.guyi.entity.Goods;
import com.guyi.entity.GoodsSpec;
import com.guyi.entity.Review;
import com.guyi.entity.User;
import com.guyi.mapper.CategoryMapper;
import com.guyi.mapper.FavoriteMapper;
import com.guyi.mapper.GoodsMapper;
import com.guyi.mapper.GoodsSpecMapper;
import com.guyi.mapper.ReviewMapper;
import com.guyi.mapper.UserMapper;
import com.guyi.vo.GoodsDetailVO;
import com.guyi.vo.GoodsSpecVO;
import com.guyi.vo.GoodsVO;
import com.guyi.vo.ReviewVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoodsService {

    private final GoodsMapper goodsMapper;
    private final GoodsSpecMapper goodsSpecMapper;
    private final ReviewMapper reviewMapper;
    private final UserMapper userMapper;
    private final FavoriteMapper favoriteMapper;
    private final CategoryMapper categoryMapper;

    /** 商品分页列表：状态、分类、关键词、排序组合查询。 */
    public PageResult<GoodsVO> page(GoodsQueryDTO query) {
        Page<Goods> page = goodsMapper.selectPage(
                new Page<>(query.getPage(), query.getSize()),
                buildListWrapper(query));

        List<GoodsVO> list = page.getRecords().stream()
                .map(goods -> BeanUtil.copyProperties(goods, GoodsVO.class))
                .toList();
        return new PageResult<>(list, page.getTotal(), page.getCurrent(), page.getSize());
    }

    /** 商品详情：基础信息 + 规格 + 评价 + 当前用户是否已收藏。 */
    public GoodsDetailVO detail(Integer id) {
        Goods goods = goodsMapper.selectById(id);
        if (goods == null || goods.getStatus() == null || goods.getStatus() != 1) {
            throw new BusinessException(404, "商品不存在或已下架");
        }

        GoodsDetailVO vo = BeanUtil.copyProperties(goods, GoodsDetailVO.class);
        vo.setSpecs(buildSpecs(id));
        vo.setReviews(buildReviews(id));
        vo.setFavorite(isFavorite(id));
        return vo;
    }

    private LambdaQueryWrapper<Goods> buildListWrapper(GoodsQueryDTO query) {
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Goods::getStatus, 1);

        if (query.getCategoryId() != null) {
            wrapper.in(Goods::getCategoryId, resolveCategoryIds(query.getCategoryId()));
        }

        String keyword = query.getKeyword();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Goods::getTitle, keyword);
        }

        applySort(wrapper, query.getSort());
        return wrapper;
    }

    /** 一级分类查询时下钻到其二级分类，保证分类页能返回完整商品。 */
    private List<Integer> resolveCategoryIds(Integer categoryId) {
        List<Category> children = categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getParentId, categoryId)
                .eq(Category::getStatus, 1));
        if (children.isEmpty()) {
            return List.of(categoryId);
        }
        return children.stream().map(Category::getId).collect(Collectors.toList());
    }

    private void applySort(LambdaQueryWrapper<Goods> wrapper, String sort) {
        if (sort == null || "comprehensive".equals(sort)) {
            wrapper.orderByAsc(Goods::getSort).orderByDesc(Goods::getSales);
            return;
        }
        switch (sort) {
            case "sales" -> wrapper.orderByDesc(Goods::getSales);
            case "price_asc" -> wrapper.orderByAsc(Goods::getPrice);
            case "price_desc" -> wrapper.orderByDesc(Goods::getPrice);
            case "new" -> wrapper.orderByDesc(Goods::getCreateTime);
            default -> wrapper.orderByAsc(Goods::getSort);
        }
    }

    private List<GoodsSpecVO> buildSpecs(Integer goodsId) {
        return goodsSpecMapper.selectList(new LambdaQueryWrapper<GoodsSpec>()
                        .eq(GoodsSpec::getGoodsId, goodsId)
                        .orderByAsc(GoodsSpec::getId))
                .stream()
                .map(spec -> BeanUtil.copyProperties(spec, GoodsSpecVO.class))
                .toList();
    }

    private List<ReviewVO> buildReviews(Integer goodsId) {
        List<Review> reviews = reviewMapper.selectList(new LambdaQueryWrapper<Review>()
                .eq(Review::getGoodsId, goodsId)
                .orderByDesc(Review::getCreateTime));

        Set<Integer> userIds = reviews.stream()
                .map(Review::getUserId)
                .collect(Collectors.toSet());
        Map<Integer, User> userMap = userIds.isEmpty()
                ? Map.of()
                : userMapper.selectByIds(userIds).stream()
                        .collect(Collectors.toMap(User::getId, user -> user));

        return reviews.stream().map(review -> {
            ReviewVO vo = new ReviewVO();
            vo.setRating(review.getRating());
            vo.setContent(review.getContent());
            vo.setImages(review.getImages());
            vo.setCreateTime(review.getCreateTime());
            User user = userMap.get(review.getUserId());
            if (user != null) {
                vo.setNickname(user.getNickname());
                vo.setAvatarUrl(user.getAvatarUrl());
            }
            return vo;
        }).toList();
    }

    /** 游客未携带 token 时固定返回 false。 */
    private boolean isFavorite(Integer goodsId) {
        Integer userId = UserContext.get();
        if (userId == null) {
            return false;
        }
        Long count = favoriteMapper.selectCount(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getGoodsId, goodsId));
        return count != null && count > 0;
    }
}
