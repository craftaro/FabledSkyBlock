package com.songoda.skyblock.permission;

import java.lang.reflect.Method;

public class HandlerWrapper {
    private final BasicPermission permission;
    private final Method handler;

    public HandlerWrapper(BasicPermission permission, Method handler) {
        this.permission = permission;
        this.handler = handler;
    }

    public BasicPermission getPermission() {
        return this.permission;
    }

    public Method getHandler() {
        return this.handler;
    }
}
