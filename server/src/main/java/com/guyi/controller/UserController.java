package com.guyi.controller;

import com.guyi.common.Result;
import com.guyi.dto.UserProfileDTO;
import com.guyi.service.UserService;
import com.guyi.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public Result<UserVO> profile() {
        return Result.ok(userService.getProfile());
    }

    @PutMapping("/profile")
    public Result<UserVO> updateProfile(@Valid @RequestBody UserProfileDTO dto) {
        return Result.ok(userService.updateProfile(dto));
    }
}
