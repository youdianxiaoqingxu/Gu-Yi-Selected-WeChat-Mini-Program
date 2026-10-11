package com.guyi.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderVO {

    private Integer id;
    private String orderNo;
    private Integer status;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private Integer itemCount;
    private List<OrderItemVO> items;
    private LocalDateTime createTime;
}

