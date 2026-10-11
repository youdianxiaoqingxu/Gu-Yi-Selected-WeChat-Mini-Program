package com.guyi.common;

public class UserContext {

    private static final ThreadLocal<Integer> HOLDER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(Integer userId) {
        HOLDER.set(userId);
    }

    public static Integer get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}

