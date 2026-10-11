package com.guyi.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class GoodsVO {

    private Integer id;
    private String title;
    private String subtitle;
    private String coverImg;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer sales;
    private String unit;
    private String craft;
}

