package com.songoda.skyblock.command.commands.island;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.island.IslandStatus;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.player.OfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Set;
import java.util.UUID;

public class WhitelistCommand extends SubCommand {
    
    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = skyblock.getMessageManager();
        IslandManager islandManager = skyblock.getIslandManager();
        FileManager fileManager = skyblock.getFileManager();
        SoundManager soundManager = skyblock.getSoundManager();
    
        FileManager.Config language = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration languageLoad = language.getFileConfiguration();
        
        FileManager.Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();
        
        Island island = islandManager.getIsland(player);
        
        if (island == null) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Whitelist.Owner.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
        } else if (island.hasRole(IslandRole.Owner, player.getUniqueId())
                || (island.hasRole(IslandRole.Operator, player.getUniqueId())
                && skyblock.getPermissionManager().hasPermission(island, "Visitor", IslandRole.Operator))) {
            if(args.length == 1) {
                if(args[0].equalsIgnoreCase("on")){
                    island.setStatus(IslandStatus.WHITELISTED);
                    messageManager.sendMessage(player, languageLoad.getString("Command.Island.Whitelist.On.Message"));
                } else if(args[0].equalsIgnoreCase("off")){
                    island.setStatus(IslandStatus.CLOSED);
                    messageManager.sendMessage(player, languageLoad.getString("Command.Island.Whitelist.Off.Message"));
                } else if(args[0].equalsIgnoreCase("list")){
                    Set<UUID> whitelistedPlayers = island.getWhitelistedPlayers();
                    if(!whitelistedPlayers.isEmpty()){
                        messageManager.sendMessage(player, languageLoad.getString("Command.Island.Whitelist.List.Start.Message"));
                        for(UUID uuid : whitelistedPlayers) {
                            messageManager.sendMessage(player, languageLoad.getString("Command.Island.Whitelist.List.List.Message")
                                    .replace("%owner", new OfflinePlayer(uuid).getName()));
                        }
                        messageManager.sendMessage(player, languageLoad.getString("Command.Island.Whitelist.List.End.Message"));
                    } else {
                        messageManager.sendMessage(player, languageLoad.getString("Command.Island.Whitelist.List.Empty.Message"));
                    }
                } else { // Invalid args
                    messageManager.sendMessage(player, languageLoad.getString("Command.Island.Whitelist.InvalidArgNumber.Message"));
                }
            } else if(args.length == 2) {
                OfflinePlayer offlinePlayer = new OfflinePlayer(args[1]);
                if(offlinePlayer.getBukkitOfflinePlayer().hasPlayedBefore()) {
                    if(args[0].equalsIgnoreCase("add")){
                        if (!island.isPlayerWhitelisted(offlinePlayer.getUUID())) {
                            island.addWhitelistedPlayer(offlinePlayer.getUUID());
                            messageManager.sendMessage(player, languageLoad.getString("Command.Island.Whitelist.Added.Message")
                                    .replace("%player", offlinePlayer.getName()));
                        } else {
                            messageManager.sendMessage(player, languageLoad.getString("Command.Island.Whitelist.AlreadyAdded.Message")
                                    .replace("%player", offlinePlayer.getName()));
                        }
                    } else if(args[0].equalsIgnoreCase("remove")){
                        if (island.isPlayerWhitelisted(offlinePlayer.getUUID())) {
                            island.removeWhitelistedPlayer(offlinePlayer.getUUID());
                            messageManager.sendMessage(player, languageLoad.getString("Command.Island.Whitelist.Removed.Message")
                                    .replace("%player", offlinePlayer.getName()));
                        } else {
                            messageManager.sendMessage(player, languageLoad.getString("Command.Island.Whitelist.NotWhitelisted.Message")
                                    .replace("%player", offlinePlayer.getName()));
                        }
                    } else { // Invalid args
                        messageManager.sendMessage(player, languageLoad.getString("Command.Island.Whitelist.InvalidArgNumber.Message"));
                    }
                } else { // Player not found
                    messageManager.sendMessage(player, languageLoad.getString("Command.Island.Whitelist.PlayerNotFound.Message")
                            .replace("%player", offlinePlayer.getName()));
                }
            } else { // Invalid args
                messageManager.sendMessage(player, languageLoad.getString("Command.Island.Whitelist.InvalidArgNumber.Message"));
            }
        } else {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Whitelist.Permission.Message"));
            soundManager.playSound(player,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
        }
    }
    
    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }
    
    @Override
    public String getName() {
        return "whitelist";
    }
    
    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Public.Info.Message";
    }
    
    @Override
    public String[] getAliases() {
        return new String[0];
    }
    
    @Override
    public String[] getArguments() {
        return new String[]{"on", "off", "add", "remove", "list"};
    }
}
