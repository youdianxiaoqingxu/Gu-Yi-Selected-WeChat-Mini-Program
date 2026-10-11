package com.guyi.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderDetailVO {

    private Integer id;
    private String orderNo;
    private Integer status;
    private BigDecimal totalAmount;
    private BigDecimal freight;
    private BigDecimal payAmount;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddr;
    private String remark;
    private List<OrderItemVO> items;
    private LocalDateTime payTime;
    private LocalDateTime shipTime;
    private LocalDateTime finishTime;
    private LocalDateTime cancelTime;
    private LocalDateTime createTime;
}

