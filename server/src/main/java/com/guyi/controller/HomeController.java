package com.guyi.controller;

import com.guyi.common.Result;
import com.guyi.service.HomeService;
import com.guyi.vo.HomeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping
    public Result<HomeVO> home() {
        return Result.ok(homeService.home());
    }
}

