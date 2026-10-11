package com.guyi.controller;

import com.guyi.common.Result;
import com.guyi.service.CategoryService;
import com.guyi.vo.CategoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/tree")
    public Result<List<CategoryVO>> tree() {
        return Result.ok(categoryService.tree());
    }
}
