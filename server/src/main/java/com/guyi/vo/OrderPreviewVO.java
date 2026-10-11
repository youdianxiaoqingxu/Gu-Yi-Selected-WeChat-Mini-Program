package com.guyi.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class OrderPreviewVO {

    private List<OrderPreviewItemVO> items;
    private BigDecimal totalAmount;
    private BigDecimal freight;
    private BigDecimal payAmount;
}

