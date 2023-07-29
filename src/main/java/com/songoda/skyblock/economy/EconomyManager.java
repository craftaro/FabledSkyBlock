package com.songoda.skyblock.economy;

import com.craftaro.core.hooks.Hook;
import com.craftaro.core.hooks.economies.Economy;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.manager.Manager;

public class EconomyManager extends Manager {
    private Economy economy;

    public EconomyManager(SkyBlock plugin) {
        super(plugin);
        com.craftaro.core.hooks.EconomyManager.load();
        this.economy = com.craftaro.core.hooks.EconomyManager.getEconomy();
    }

    public void setEconomy(String economyString) {
        Hook hook = com.craftaro.core.hooks.EconomyManager.getManager().getHook(economyString);
        if (hook != null &&
                hook.isEnabled() &&
                hook instanceof Economy &&
                !hook.equals(com.craftaro.core.hooks.EconomyManager.getManager().getCurrentHook())) {
            this.economy = (Economy) hook;
        }
    }

    public Economy getEconomy() {
        return this.economy;
    }
}
