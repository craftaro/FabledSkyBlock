package me.goodandevil.skyblock.command.commands;

import org.bukkit.entity.Player;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.command.CommandManager.Type;
import me.goodandevil.skyblock.menus.Visit;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.utils.version.Sounds;

public class VisitCommand extends SubCommand {

	private final Main plugin;
	private String info;
	
	public VisitCommand(Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onCommand(Player player, String[] args) {
		PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
		playerData.setType(Visit.Type.Default);
		playerData.setSort(Visit.Sort.Default);
		
		Visit.getInstance().open(player, (Visit.Type) playerData.getType(), (Visit.Sort) playerData.getSort());
		plugin.getSoundManager().playSound(player, Sounds.CHEST_OPEN.bukkitSound(), 1.0F, 1.0F);
	}

	@Override
	public String getName() {
		return "visit";
	}

	@Override
	public String getInfo() {
		return info;
	}

	@Override
	public SubCommand setInfo(String info) {
		this.info = info;
		
		return this;
	}

	@Override
	public String[] getAliases() {
		return new String[0];
	}

	@Override
	public Type getType() {
		return CommandManager.Type.Default;
	}
}
