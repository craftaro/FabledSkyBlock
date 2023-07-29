package com.songoda.skyblock.playerdata;

import com.songoda.skyblock.menus.MenuType;

public class MenuPage {
    MenuType type;
    int page;

    public MenuPage(MenuType type, int page) {
        this.type = type;
        this.page = page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public MenuType getType() {
        return this.type;
    }

    public int getPage() {
        return this.page;
    }
}
