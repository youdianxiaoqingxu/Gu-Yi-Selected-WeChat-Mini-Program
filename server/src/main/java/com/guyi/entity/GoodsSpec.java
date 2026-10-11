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
import java.util.Map;

@Getter
@Setter
@TableName(value = "goods_spec", autoResultMap = true)
public class GoodsSpec {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer goodsId;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> specJson;

    private BigDecimal price;
    private Integer stock;
    private String image;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

