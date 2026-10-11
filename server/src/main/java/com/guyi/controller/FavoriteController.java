package com.guyi.controller;

import com.guyi.common.Result;
import com.guyi.common.UserContext;
import com.guyi.service.FavoriteService;
import com.guyi.vo.FavoriteVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public Result<List<FavoriteVO>> list() {
        return Result.ok(favoriteService.list(UserContext.get()));
    }

    @PostMapping("/{goodsId}")
    public Result<Void> add(@PathVariable Integer goodsId) {
        favoriteService.add(UserContext.get(), goodsId);
        return Result.ok();
    }

    @DeleteMapping("/{goodsId}")
    public Result<Void> remove(@PathVariable Integer goodsId) {
        favoriteService.remove(UserContext.get(), goodsId);
        return Result.ok();
    }
}
