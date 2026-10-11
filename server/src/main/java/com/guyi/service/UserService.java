package com.guyi.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.guyi.common.BusinessException;
import com.guyi.common.UserContext;
import com.guyi.dto.UserProfileDTO;
import com.guyi.entity.User;
import com.guyi.mapper.UserMapper;
import com.guyi.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    public UserVO getProfile() {
        User user = userMapper.selectById(currentUserId());
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return BeanUtil.copyProperties(user, UserVO.class);
    }

    public UserVO updateProfile(UserProfileDTO dto) {
        Integer userId = currentUserId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        userMapper.update(null, new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getNickname, dto.getNickname())
                .set(dto.getAvatarUrl() != null, User::getAvatarUrl, dto.getAvatarUrl())
                .set(dto.getPhone() != null, User::getPhone, dto.getPhone())
                .set(dto.getRealName() != null, User::getRealName, dto.getRealName())
                .set(dto.getGender() != null, User::getGender, dto.getGender())
                .set(dto.getBirthday() != null, User::getBirthday, dto.getBirthday())
                .set(dto.getRegion() != null, User::getRegion, dto.getRegion()));
        return getProfile();
    }

    private Integer currentUserId() {
        Integer userId = UserContext.get();
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }
        return userId;
    }
}
