package com.guyi.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.guyi.entity.Banner;
import com.guyi.entity.Goods;
import com.guyi.entity.HomeVideo;
import com.guyi.mapper.BannerMapper;
import com.guyi.mapper.GoodsMapper;
import com.guyi.mapper.HomeVideoMapper;
import com.guyi.vo.BannerVO;
import com.guyi.vo.GoodsVO;
import com.guyi.vo.HomeVO;
import com.guyi.vo.HomeVideoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final HomeVideoMapper homeVideoMapper;
    private final BannerMapper bannerMapper;
    private final GoodsMapper goodsMapper;
    private final CategoryService categoryService;

    public HomeVO home() {
        HomeVO home = new HomeVO();

        HomeVideo video = homeVideoMapper.selectOne(new LambdaQueryWrapper<HomeVideo>()
                .eq(HomeVideo::getStatus, 1)
                .orderByAsc(HomeVideo::getSort)
                .last("LIMIT 1"));
        if (video != null) {
            home.setVideo(BeanUtil.copyProperties(video, HomeVideoVO.class));
        }

        List<Banner> banners = bannerMapper.selectList(new LambdaQueryWrapper<Banner>()
                .eq(Banner::getStatus, 1)
                .orderByAsc(Banner::getSort));
        home.setBanners(banners.stream()
                .map(banner -> BeanUtil.copyProperties(banner, BannerVO.class))
                .toList());

        home.setCategories(categoryService.tree());

        List<Goods> hotGoods = goodsMapper.selectList(new LambdaQueryWrapper<Goods>()
                .eq(Goods::getStatus, 1)
                .orderByDesc(Goods::getSales)
                .last("LIMIT 10"));
        home.setHotGoods(hotGoods.stream()
                .map(goods -> BeanUtil.copyProperties(goods, GoodsVO.class))
                .toList());

        return home;
    }
}
