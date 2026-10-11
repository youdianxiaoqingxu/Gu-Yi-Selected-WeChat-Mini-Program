package com.guyi.util;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.guyi.common.BusinessException;
import com.guyi.config.WxConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class WxUtil {

    private static final String CODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session";

    private final WxConfig wxConfig;

    public WxSession code2Session(String code) {
        String url = UriComponentsBuilder.fromUriString(CODE2SESSION_URL)
                .queryParam("appid", wxConfig.getAppId())
                .queryParam("secret", wxConfig.getSecret())
                .queryParam("js_code", code)
                .queryParam("grant_type", "authorization_code")
                .toUriString();

        String body = RestClient.create().get().uri(url).retrieve().body(String.class);
        JSONObject json = JSONUtil.parseObj(body);
        Integer errcode = json.getInt("errcode");
        if (errcode != null && errcode != 0) {
            log.error("微信 code2Session 失败：errcode={}, errmsg={}", errcode, json.getStr("errmsg"));
            throw new BusinessException("微信登录失败");
        }

        WxSession session = new WxSession();
        session.setOpenid(json.getStr("openid"));
        session.setSessionKey(json.getStr("session_key"));
        session.setUnionid(json.getStr("unionid"));
        return session;
    }

    public static class WxSession {
        private String openid;
        private String sessionKey;
        private String unionid;

        public String getOpenid() {
            return openid;
        }

        public void setOpenid(String openid) {
            this.openid = openid;
        }

        public String getSessionKey() {
            return sessionKey;
        }

        public void setSessionKey(String sessionKey) {
            this.sessionKey = sessionKey;
        }

        public String getUnionid() {
            return unionid;
        }

        public void setUnionid(String unionid) {
            this.unionid = unionid;
        }
    }
}

