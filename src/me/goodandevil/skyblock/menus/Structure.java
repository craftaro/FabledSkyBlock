package me.goodandevil.skyblock.menus;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class Structure implements Listener {

    private static Structure instance;

    public static Structure getInstance(){
        if(instance == null) {
            instance = new Structure();
        }
        
        return instance;
    }
	
	public void openBrowse(Player player) {
		// TODO Display Structures
	}
}
