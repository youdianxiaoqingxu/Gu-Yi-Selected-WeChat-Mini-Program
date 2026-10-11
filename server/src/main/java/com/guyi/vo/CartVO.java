package com.guyi.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CartVO {

    private Integer id;
    private Integer goodsId;
    private Integer specId;
    private Integer quantity;
    private Integer checked;

    private String goodsTitle;
    private String coverImg;
    private String craft;
    private String unit;
    private BigDecimal price;
    private Integer stock;
    private String specText;
}

