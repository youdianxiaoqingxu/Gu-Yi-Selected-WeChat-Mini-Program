package com.guyi.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemVO {

    private Integer goodsId;
    private String goodsTitle;
    private String goodsImg;
    private String specText;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
}

