package me.goodandevil.skyblock.command.commands;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.command.CommandManager.Type;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.menus.Information;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.OfflinePlayer;
import me.goodandevil.skyblock.utils.version.Sounds;

public class InformationCommand extends SubCommand {

	private final SkyBlock skyblock;
	private String info;
	
	public InformationCommand(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}
	
	@Override
	public void onCommand(Player player, String[] args) {
		PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
		MessageManager messageManager = skyblock.getMessageManager();
		IslandManager islandManager = skyblock.getIslandManager();
		SoundManager soundManager = skyblock.getSoundManager();
		
		if (playerDataManager.hasPlayerData(player)) {
			Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
			FileConfiguration configLoad = config.getFileConfiguration();
			
			UUID islandOwnerUUID = null;
			
			if (args.length == 1) {
				if (player.hasPermission("skyblock.teleport") || player.hasPermission("skyblock.*")) {
					Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
					
					if (targetPlayer == null) {
						OfflinePlayer targetOfflinePlayer = new OfflinePlayer(args[0]);
						islandOwnerUUID = targetOfflinePlayer.getOwner();
					} else {
						islandOwnerUUID = playerDataManager.getPlayerData(targetPlayer).getOwner();
					}
					
					if (islandOwnerUUID == null) {
						messageManager.sendMessage(player, configLoad.getString("Command.Island.Information.Island.Message"));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
						
						return;
					}
				} else {
					messageManager.sendMessage(player, configLoad.getString("Command.Island.Information.Permission.Message"));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					
					return;
				}
			} else if (args.length != 0) {
				messageManager.sendMessage(player, configLoad.getString("Command.Island.Information.Invalid.Message"));
				soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
				
				return;
			}
			
			PlayerData playerData = skyblock.getPlayerDataManager().getPlayerData(player);
			
			if (islandOwnerUUID == null) {
				if (islandManager.hasIsland(player)) {
					islandOwnerUUID = playerData.getOwner();
				} else {
					messageManager.sendMessage(player, configLoad.getString("Command.Island.Information.Owner.Message"));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
				
					return;
				}
			}
			
			playerData.setViewer(new Information.Viewer(islandOwnerUUID, Information.Viewer.Type.Categories));
			Information.getInstance().open(player);
			soundManager.playSound(player, Sounds.CHEST_OPEN.bukkitSound(), 1.0F, 1.0F);
		}
	}

	@Override
	public String getName() {
		return "information";
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
		return new String[] { "info" };
	}

	@Override
	public Type getType() {
		return CommandManager.Type.Default;
	}
}
