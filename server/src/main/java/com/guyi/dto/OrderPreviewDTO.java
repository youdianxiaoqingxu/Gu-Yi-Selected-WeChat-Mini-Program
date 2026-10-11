package com.guyi.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderPreviewDTO {

    @NotEmpty(message = "购物车不能为空")
    private List<Integer> cartIds;
}

