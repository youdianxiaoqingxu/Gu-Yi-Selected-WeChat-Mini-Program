package com.guyi.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FavoriteVO {

    private Integer goodsId;
    private String goodsTitle;
    private String coverImg;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String craft;
}

