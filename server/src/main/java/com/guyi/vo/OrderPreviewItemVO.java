package com.guyi.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderPreviewItemVO {

    private Integer goodsId;
    private Integer specId;
    private String goodsTitle;
    private String coverImg;
    private String specText;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
    private Integer stock;
}

