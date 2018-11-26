package me.goodandevil.skyblock.menus;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class Information implements Listener {

    private static Information instance;

    public static Information getInstance(){
        if(instance == null) {
            instance = new Information();
        }
        
        return instance;
    }
    
    public void open(Player player) {
    	
    }
	
    public class Viewer {
    	
    	private String name;
    	
    	public Viewer(String name) {
    		this.name = name;
    	}
    	
    	public String getName() {
    		return name;
    	}
    }
}
