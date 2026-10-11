package com.guyi.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressVO {

    private Integer id;
    private String name;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String detail;
    private Integer isDefault;
}

