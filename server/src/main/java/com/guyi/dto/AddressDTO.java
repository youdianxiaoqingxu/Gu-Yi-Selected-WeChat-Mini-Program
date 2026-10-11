package com.guyi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDTO {

    @NotBlank(message = "收货人姓名不能为空")
    private String name;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    private String province;
    private String city;
    private String district;

    @NotBlank(message = "详细地址不能为空")
    private String detail;

    private Integer isDefault;
}

