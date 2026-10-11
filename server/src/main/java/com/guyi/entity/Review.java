package com.guyi.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@TableName(value = "review", autoResultMap = true)
public class Review {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer goodsId;
    private Integer userId;
    private Integer rating;
    private String content;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> images;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

