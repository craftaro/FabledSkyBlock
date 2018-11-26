package me.goodandevil.skyblock.command.commands;

import java.io.File;

import org.bukkit.entity.Player;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.command.CommandManager.Type;
import me.goodandevil.skyblock.menus.Leaderboard;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.utils.version.Sounds;

public class LeaderboardCommand extends SubCommand {

	private final SkyBlock skyblock;
	private String info;
	
	public LeaderboardCommand(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}
	
	@Override
	public void onCommand(Player player, String[] args) {
		PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
		
		if (playerDataManager.hasPlayerData(player)) {
			if (skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Visitor.Vote")) {
				playerDataManager.getPlayerData(player).setViewer(new Leaderboard.Viewer(Leaderboard.Viewer.Type.Browse));
			} else {
				playerDataManager.getPlayerData(player).setViewer(new Leaderboard.Viewer(Leaderboard.Viewer.Type.Level));
			}
			
			Leaderboard.getInstance().open(player);
			skyblock.getSoundManager().playSound(player, Sounds.CHEST_OPEN.bukkitSound(), 1.0F, 1.0F);
		}
	}

	@Override
	public String getName() {
		return "leaderboard";
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
		return new String[] { "lb", "top" };
	}

	@Override
	public Type getType() {
		return CommandManager.Type.Default;
	}
}
