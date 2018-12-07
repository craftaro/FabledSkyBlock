package me.goodandevil.skyblock.menus;

import org.bukkit.entity.Player;

public class Border {
	
    private static Border instance;

    public static Border getInstance(){
        if(instance == null) {
            instance = new Border();
        }
        
        return instance;
    }
    
    public void open(Player player) {
    	
    }
}
