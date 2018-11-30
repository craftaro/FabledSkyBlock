package me.goodandevil.skyblock.command;

import org.bukkit.entity.Player;

public abstract class SubCommand {
	
	public abstract void onCommand(Player player, String[] args);
	
	public abstract String getName();
	public abstract String getInfo();
	public abstract SubCommand setInfo(String info);
	
	public abstract String[] getAliases();
	
	public abstract CommandManager.Type getType();
}
