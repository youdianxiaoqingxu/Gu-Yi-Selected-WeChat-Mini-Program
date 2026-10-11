package com.guyi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.guyi.common.BusinessException;
import com.guyi.entity.Favorite;
import com.guyi.entity.Goods;
import com.guyi.mapper.FavoriteMapper;
import com.guyi.mapper.GoodsMapper;
import com.guyi.vo.FavoriteVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final GoodsMapper goodsMapper;

    /** 查询当前用户收藏，并补齐商品展示信息。 */
    public List<FavoriteVO> list(Integer userId) {
        List<Favorite> favorites = favoriteMapper.selectList(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getId));
        if (favorites.isEmpty()) {
            return List.of();
        }

        Set<Integer> goodsIds = favorites.stream()
                .map(Favorite::getGoodsId)
                .collect(Collectors.toSet());
        Map<Integer, Goods> goodsMap = goodsMapper.selectByIds(goodsIds).stream()
                .collect(Collectors.toMap(Goods::getId, goods -> goods));

        return favorites.stream()
                .map(favorite -> toVO(goodsMap.get(favorite.getGoodsId())))
                .toList();
    }

    public void add(Integer userId, Integer goodsId) {
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null || goods.getStatus() == null || goods.getStatus() != 1) {
            throw new BusinessException(404, "商品不存在或已下架");
        }

        // 已收藏则幂等返回，避免重复插入
        Long count = favoriteMapper.selectCount(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getGoodsId, goodsId));
        if (count > 0) {
            return;
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setGoodsId(goodsId);
        favoriteMapper.insert(favorite);
    }

    public void remove(Integer userId, Integer goodsId) {
        favoriteMapper.delete(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getGoodsId, goodsId));
    }

    private FavoriteVO toVO(Goods goods) {
        FavoriteVO vo = new FavoriteVO();
        if (goods != null) {
            vo.setGoodsId(goods.getId());
            vo.setGoodsTitle(goods.getTitle());
            vo.setCoverImg(goods.getCoverImg());
            vo.setPrice(goods.getPrice());
            vo.setOriginalPrice(goods.getOriginalPrice());
            vo.setCraft(goods.getCraft());
        }
        return vo;
    }
}
