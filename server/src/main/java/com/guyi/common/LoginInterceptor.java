package com.guyi.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guyi.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    /** 游客可直接访问的接口前缀，其余接口必须携带有效 token。 */
    private static final List<String> WHITELIST_PREFIXES = List.of(
            "/api/login",
            "/api/home",
            "/api/category",
            "/api/goods"
    );

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        // 白名单接口也尝试解析 token，便于商品详情返回“是否已收藏”。
        Integer userId = parseUserId(request);
        if (userId != null) {
            UserContext.set(userId);
            return true;
        }

        if (!requiresLogin(request.getRequestURI())) {
            return true;
        }

        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(401, "未登录或登录已失效")));
        return false;
    }

    private Integer parseUserId(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            return null;
        }
        try {
            return jwtUtil.parseUserId(auth.substring(7));
        } catch (Exception ignored) {
            return null;
        }
    }

    private boolean requiresLogin(String uri) {
        return WHITELIST_PREFIXES.stream().noneMatch(uri::startsWith);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
