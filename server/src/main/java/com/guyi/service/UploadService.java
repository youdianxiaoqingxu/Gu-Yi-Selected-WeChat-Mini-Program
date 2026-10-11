package com.guyi.service;

import com.guyi.common.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

@Service
public class UploadService {

    private static final Set<String> IMAGE_EXTS = Set.of("jpg", "jpeg", "png", "webp");
    private static final long MAX_IMAGE_SIZE = 5L * 1024 * 1024;
    private static final DateTimeFormatter DATE_DIR_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Value("${guyi.upload.path}")
    private String uploadPath;

    public String uploadAvatar(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "请选择要上传的文件");
        }
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new BusinessException(400, "图片大小不能超过 5MB");
        }
        String ext = resolveExt(file.getOriginalFilename());
        if (ext == null || !IMAGE_EXTS.contains(ext)) {
            throw new BusinessException(400, "仅支持 jpg/jpeg/png/webp 图片");
        }

        String dateDir = LocalDate.now().format(DATE_DIR_FORMATTER);
        String filename = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        String relativePath = "/static/upload/avatar/" + dateDir + "/" + filename;

        Path dir = Paths.get(uploadPath, "avatar", dateDir).toAbsolutePath();
        try {
            Files.createDirectories(dir);
            file.transferTo(dir.resolve(filename).toFile());
        } catch (IOException e) {
            throw new BusinessException("头像上传失败");
        }
        return relativePath;
    }

    private String resolveExt(String originalName) {
        if (originalName == null || !originalName.contains(".")) {
            return null;
        }
        return originalName.substring(originalName.lastIndexOf('.') + 1).toLowerCase();
    }
}
