package com.songoda.skyblock.island;

import com.songoda.skyblock.permission.BasicPermission;

public class IslandPermission {

    private final BasicPermission permission;
    private boolean status;

    public IslandPermission(BasicPermission permission, boolean status) {
        this.permission = permission;
        this.status = status;
    }

    public BasicPermission getPermission() {
        return permission;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
