package com.guyi.controller;

import com.guyi.common.Result;
import com.guyi.common.UserContext;
import com.guyi.dto.CartAddDTO;
import com.guyi.dto.CartUpdateDTO;
import com.guyi.service.CartService;
import com.guyi.vo.CartVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public Result<List<CartVO>> list() {
        return Result.ok(cartService.list(UserContext.get()));
    }

    @PostMapping
    public Result<Void> add(@Valid @RequestBody CartAddDTO dto) {
        cartService.add(UserContext.get(), dto);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Integer id, @RequestBody CartUpdateDTO dto) {
        cartService.update(UserContext.get(), id, dto);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        cartService.remove(UserContext.get(), id);
        return Result.ok();
    }
}
