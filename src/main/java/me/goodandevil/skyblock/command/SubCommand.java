package me.goodandevil.skyblock.command;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public abstract class SubCommand {

	public abstract void onCommandByPlayer(Player player, String[] args);

	public abstract void onCommandByConsole(ConsoleCommandSender sender, String[] args);

	public abstract String getName();

	public abstract String getInfo();

	public abstract SubCommand setInfo(String info);

	public abstract String[] getAliases();

	public abstract String[] getArguments();

	public abstract CommandManager.Type getType();
}
