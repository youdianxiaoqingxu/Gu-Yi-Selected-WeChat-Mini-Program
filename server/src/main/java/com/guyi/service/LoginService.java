package com.guyi.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.guyi.entity.User;
import com.guyi.mapper.UserMapper;
import com.guyi.util.JwtUtil;
import com.guyi.util.WxUtil;
import com.guyi.vo.LoginVO;
import com.guyi.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final WxUtil wxUtil;

    public LoginVO login(String code) {
        WxUtil.WxSession session = wxUtil.code2Session(code);

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getOpenid, session.getOpenid()));
        if (user == null) {
            user = new User();
            user.setOpenid(session.getOpenid());
            user.setUnionid(session.getUnionid());
            user.setNickname("微信用户");
            userMapper.insert(user);
        }

        LoginVO login = new LoginVO();
        login.setToken(jwtUtil.generateToken(user.getId()));
        login.setUser(BeanUtil.copyProperties(user, UserVO.class));
        return login;
    }
}

