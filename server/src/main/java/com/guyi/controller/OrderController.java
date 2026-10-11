package com.guyi.controller;

import com.guyi.common.PageQuery;
import com.guyi.common.PageResult;
import com.guyi.common.Result;
import com.guyi.common.UserContext;
import com.guyi.dto.OrderCreateDTO;
import com.guyi.dto.OrderPreviewDTO;
import com.guyi.service.OrderService;
import com.guyi.vo.OrderDetailVO;
import com.guyi.vo.OrderPreviewVO;
import com.guyi.vo.OrderVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/preview")
    public Result<OrderPreviewVO> preview(@Valid @RequestBody OrderPreviewDTO dto) {
        return Result.ok(orderService.preview(UserContext.get(), dto.getCartIds()));
    }

    @PostMapping("/create")
    public Result<Integer> create(@Valid @RequestBody OrderCreateDTO dto) {
        return Result.ok(orderService.create(UserContext.get(), dto));
    }

    @PostMapping("/{id}/pay")
    public Result<Void> pay(@PathVariable Integer id) {
        orderService.pay(UserContext.get(), id);
        return Result.ok();
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Integer id) {
        orderService.cancel(UserContext.get(), id);
        return Result.ok();
    }

    @GetMapping("/list")
    public Result<PageResult<OrderVO>> list(Integer status, String keyword, PageQuery query) {
        return Result.ok(orderService.list(UserContext.get(), status, keyword, query));
    }

    @GetMapping("/{id}")
    public Result<OrderDetailVO> detail(@PathVariable Integer id) {
        return Result.ok(orderService.detail(UserContext.get(), id));
    }
}
