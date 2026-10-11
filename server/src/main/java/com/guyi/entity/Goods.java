package com.guyi.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@TableName(value = "goods", autoResultMap = true)
public class Goods {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer categoryId;
    private String title;
    private String subtitle;
    private String coverImg;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> images;

    private String detailHtml;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private Integer sales;
    private String unit;
    private String craft;
    private String origin;
    private String material;
    private Integer status;
    private Integer sort;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

