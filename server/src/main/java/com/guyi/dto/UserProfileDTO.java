package com.guyi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileDTO {

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    private String avatarUrl;
    private String phone;
    private String realName;
    private Integer gender;
    private String birthday;
    private String region;
}
