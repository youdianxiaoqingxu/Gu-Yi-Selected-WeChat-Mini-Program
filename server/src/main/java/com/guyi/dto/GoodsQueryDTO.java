package com.guyi.dto;

import com.guyi.common.PageQuery;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoodsQueryDTO extends PageQuery {

    private Integer categoryId;
    private String keyword;
    private String sort;
}

