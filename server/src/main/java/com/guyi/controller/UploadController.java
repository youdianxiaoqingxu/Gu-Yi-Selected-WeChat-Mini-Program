package com.guyi.controller;

import com.guyi.common.Result;
import com.guyi.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @PostMapping("/avatar")
    public Result<String> avatar(@RequestParam("file") MultipartFile file) {
        return Result.ok(uploadService.uploadAvatar(file));
    }
}
