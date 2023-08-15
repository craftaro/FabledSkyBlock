package com.craftaro.skyblock.playerdata;

import com.craftaro.skyblock.menus.MenuType;

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
