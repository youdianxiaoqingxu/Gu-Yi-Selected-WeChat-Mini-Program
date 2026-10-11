package com.guyi.controller;

import com.guyi.common.PageResult;
import com.guyi.common.Result;
import com.guyi.dto.GoodsQueryDTO;
import com.guyi.service.GoodsService;
import com.guyi.vo.GoodsDetailVO;
import com.guyi.vo.GoodsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/goods")
@RequiredArgsConstructor
public class GoodsController {

    private final GoodsService goodsService;

    @GetMapping
    public Result<PageResult<GoodsVO>> list(GoodsQueryDTO query) {
        return Result.ok(goodsService.page(query));
    }

    @GetMapping("/{id}")
    public Result<GoodsDetailVO> detail(@PathVariable Integer id) {
        return Result.ok(goodsService.detail(id));
    }
}
