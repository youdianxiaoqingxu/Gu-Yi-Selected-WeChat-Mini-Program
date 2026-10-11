package com.guyi.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HomeVO {

    private HomeVideoVO video;
    private List<BannerVO> banners;
    private List<CategoryVO> categories;
    private List<GoodsVO> hotGoods;
}

