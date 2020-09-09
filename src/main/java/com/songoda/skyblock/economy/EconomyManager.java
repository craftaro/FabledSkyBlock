package com.songoda.skyblock.economy;

import com.songoda.core.hooks.Hook;
import com.songoda.core.hooks.economies.Economy;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.manager.Manager;

public class EconomyManager extends Manager {
    
    private Economy economy;
    
    public EconomyManager(SkyBlock plugin) {
        super(plugin);
        com.songoda.core.hooks.EconomyManager.load();
        economy = com.songoda.core.hooks.EconomyManager.getEconomy();
    }
    
    public void setEconomy(String economyString) {
        Hook hook = com.songoda.core.hooks.EconomyManager.getManager().getHook(economyString);
        if(hook != null &&
                hook.isEnabled() &&
                hook instanceof Economy &&
                !hook.equals(com.songoda.core.hooks.EconomyManager.getManager().getCurrentHook())) {
            this.economy = (Economy) hook;
        }
    }
    
    public Economy getEconomy() {
        return this.economy;
    }
    
}
