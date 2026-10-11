package com.guyi.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("favorite")
public class Favorite {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer userId;
    private Integer goodsId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

