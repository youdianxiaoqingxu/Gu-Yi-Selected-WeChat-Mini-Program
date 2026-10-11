package com.guyi.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class GoodsDetailVO {

    private Integer id;
    private String title;
    private String subtitle;
    private String coverImg;
    private List<String> images;
    private String detailHtml;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private Integer sales;
    private String unit;
    private String craft;
    private String origin;
    private String material;

    private List<GoodsSpecVO> specs;
    private List<ReviewVO> reviews;
    private boolean favorite;
}

