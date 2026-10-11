package com.guyi.controller;

import com.guyi.common.Result;
import com.guyi.dto.LoginDTO;
import com.guyi.service.LoginService;
import com.guyi.vo.LoginVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.ok(loginService.login(dto.getCode()));
    }
}

