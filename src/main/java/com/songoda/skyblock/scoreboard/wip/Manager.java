package com.songoda.skyblock.scoreboard.wip;

import com.songoda.skyblock.SkyBlock;

import java.util.Map;

public class Manager {
    
    Map<String, Driver> drivers;
    
    public void newDriver(String board, boolean isDefault) {
        Driver driver = new Driver(board);
        driver.runTaskTimerAsynchronously(SkyBlock.getInstance(), 1L, 1L);
        drivers.put(board, driver);
        driver.setDefault(isDefault);
    }
    
    public void clearDrivers()
    {
        for(Driver driver : drivers.values())
            driver.cancel();
        drivers.clear();
    }
}
