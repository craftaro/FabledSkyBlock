package com.songoda.skyblock.command.commands.island;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandEnvironment;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.player.OfflinePlayer;
import com.songoda.skyblock.visit.Visit;
import com.songoda.skyblock.visit.VisitManager;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class TeleportCommand extends SubCommand {

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
        MessageManager messageManager = skyblock.getMessageManager();
        IslandManager islandManager = skyblock.getIslandManager();
        SoundManager soundManager = skyblock.getSoundManager();
        VisitManager visitManager = skyblock.getVisitManager();

        Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length == 1) {
            Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
            UUID islandOwnerUUID = null;
            String targetPlayerName;

            if (targetPlayer == null) {
                OfflinePlayer targetOfflinePlayer = new OfflinePlayer(args[0]);
                islandOwnerUUID = targetOfflinePlayer.getOwner();
                targetPlayerName = targetOfflinePlayer.getName();
            } else {
                islandOwnerUUID = playerDataManager.getPlayerData(targetPlayer).getOwner();
                targetPlayerName = targetPlayer.getName();
            }

            if (islandOwnerUUID == null) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Teleport.Island.None.Message"));
                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                return;
            } else if (!islandOwnerUUID.equals(playerDataManager.getPlayerData(player).getOwner())) {
                if (visitManager.hasIsland(islandOwnerUUID)) {
                    Visit visit = visitManager.getIsland(islandOwnerUUID);
                    boolean isCoopPlayer = false;

                    if (islandManager.containsIsland(islandOwnerUUID)) {
                        if (islandManager.getIsland(Bukkit.getServer().getOfflinePlayer(islandOwnerUUID)).isCoopPlayer(player.getUniqueId())) {
                            isCoopPlayer = true;
                        }
                    }

                    if (isCoopPlayer || player.hasPermission("fabledskyblock.bypass") || player.hasPermission("fabledskyblock.bypass.*") || player.hasPermission("fabledskyblock.*") || visit.isOpen()) {
                        if (!islandManager.containsIsland(islandOwnerUUID)) {
                            islandManager.loadIsland(Bukkit.getServer().getOfflinePlayer(islandOwnerUUID));
                        }

                        islandManager.visitIsland(player, islandManager.getIsland(Bukkit.getServer().getOfflinePlayer(islandOwnerUUID)));

                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Teleport.Teleported.Other.Message").replace("%player", targetPlayerName));
                        soundManager.playSound(player, CompatibleSound.ENTITY_ENDERMAN_TELEPORT.getSound(), 1.0F, 1.0F);

                        return;
                    } else {
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Teleport.Island.Closed.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                    }

                    return;
                }

                messageManager.sendMessage(player, configLoad.getString("Command.Island.Teleport.Island.None.Message"));
                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                return;
            }
        } else if (args.length != 0) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Teleport.Invalid.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

            return;
        }

        Island island = islandManager.getIsland(player);

        if (island == null) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Teleport.Owner.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
        } else {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Teleport.Teleported.Yourself.Message"));
            soundManager.playSound(player, CompatibleSound.ENTITY_ENDERMAN_TELEPORT.getSound(), 1.0F, 1.0F);

            Bukkit.getServer().getScheduler().runTask(skyblock, () -> {
                player.teleport(island.getLocation(IslandWorld.Normal, IslandEnvironment.Main));

                if(!configLoad.getBoolean("Island.Teleport.FallDamage")){
                    player.setFallDistance(0.0F);
                }
            });
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "teleport";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Teleport.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[] { "tp", "spawn", "home", "go", "warp" };
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
