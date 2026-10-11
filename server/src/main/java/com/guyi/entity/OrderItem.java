package com.guyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@TableName("order_item")
public class OrderItem {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer orderId;
    private Integer goodsId;
    private Integer specId;
    private String goodsTitle;
    private String goodsImg;
    private String specText;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
}

