package com.guyi.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserVO {

    private Integer id;
    private String nickname;
    private String realName;
    private String avatarUrl;
    private String phone;
    private Integer gender;
    private String birthday;
    private String region;
}
