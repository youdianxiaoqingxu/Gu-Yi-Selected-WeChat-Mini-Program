package com.guyi.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
public class GoodsSpecVO {

    private Integer id;
    private Map<String, Object> specJson;
    private BigDecimal price;
    private Integer stock;
    private String image;
}

