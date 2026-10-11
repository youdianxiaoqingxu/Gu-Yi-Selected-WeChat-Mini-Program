package com.guyi.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderCreateDTO {

    @NotNull(message = "收货地址不能为空")
    private Integer addressId;

    @NotEmpty(message = "购物车不能为空")
    private List<Integer> cartIds;

    private String remark;
}

