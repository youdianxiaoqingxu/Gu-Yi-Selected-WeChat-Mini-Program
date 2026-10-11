package com.guyi.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategoryVO {

    private Integer id;
    private String name;
    private String icon;
    private List<CategoryVO> children;
}

