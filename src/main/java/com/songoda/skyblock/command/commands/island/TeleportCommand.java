package com.songoda.skyblock.command.commands.island;

import com.craftaro.core.compatibility.CompatibleSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandEnvironment;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandStatus;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.world.LocationUtil;
import com.songoda.skyblock.visit.Visit;
import com.songoda.skyblock.visit.VisitManager;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class TeleportCommand extends SubCommand {
    public TeleportCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        VisitManager visitManager = this.plugin.getVisitManager();

        Config config = this.plugin.getFileManager().getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length == 1) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
            Island island = islandManager.getIsland(offlinePlayer);
            if (island == null) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Teleport.Island.None.Message", "Command.Island.Teleport.Island.None.Message"));
                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                if (this.plugin.getIslandManager().getIsland(player) == null) {
                    String commandToExecute = configLoad.getString("Command.IslandTeleport.Aliases.NoIsland", "");
                    if (!commandToExecute.isEmpty()) {
                        Bukkit.dispatchCommand(player, commandToExecute);
                    }
                }

                return;
            }
            UUID islandOwnerUUID = island.getOwnerUUID();
            if (islandOwnerUUID == null) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Teleport.Island.None.Message"));
                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                return;
            } else if (!islandOwnerUUID.equals(playerDataManager.getPlayerData(player).getOwner())) {
                if (visitManager.hasIsland(islandOwnerUUID)) {
                    Visit visit = visitManager.getIsland(islandOwnerUUID);
                    boolean isCoopPlayer = false;
                    boolean isWhitelistedPlayer = false;

                    if (islandManager.containsIsland(islandOwnerUUID)) {
                        if (islandManager.getIsland(Bukkit.getServer().getOfflinePlayer(islandOwnerUUID)).isCoopPlayer(player.getUniqueId())) {
                            isCoopPlayer = true;
                        }

                        if (visit.getStatus() == IslandStatus.WHITELISTED && islandManager.getIsland(Bukkit.getServer().getOfflinePlayer(islandOwnerUUID)).isPlayerWhitelisted(player.getUniqueId())) {
                            isWhitelistedPlayer = true;
                        }

                    }


                    if (visit.getStatus() == IslandStatus.OPEN ||
                            isCoopPlayer ||
                            isWhitelistedPlayer ||
                            player.hasPermission("fabledskyblock.bypass") ||
                            player.hasPermission("fabledskyblock.bypass.*") ||
                            player.hasPermission("fabledskyblock.*")) {

                        if (!islandManager.containsIsland(islandOwnerUUID)) {
                            islandManager.loadIsland(Bukkit.getServer().getOfflinePlayer(islandOwnerUUID));
                        }

                        islandManager.visitIsland(player, islandManager.getIsland(Bukkit.getServer().getOfflinePlayer(islandOwnerUUID)));

                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Teleport.Teleported.Other.Message").replace("%player", args[0]));
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
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Teleport.Owner.Message", "Command.Island.Teleport.Owner.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
        } else {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Teleport.Teleported.Yourself.Message", "Command.Island.Teleport.Teleported.Yourself.Message"));
            soundManager.playSound(player, CompatibleSound.ENTITY_ENDERMAN_TELEPORT.getSound(), 1.0F, 1.0F);
            Bukkit.getServer().getScheduler().runTask(this.plugin, () -> {
                Location loc = island.getLocation(IslandWorld.NORMAL, IslandEnvironment.MAIN);
                PaperLib.getChunkAtAsync(loc).thenRun((() -> {
                    if (this.plugin.getFileManager().getConfig(new File(this.plugin.getDataFolder(), "config.yml"))
                            .getFileConfiguration().getBoolean("Island.Teleport.RemoveWater", false)) {
                        LocationUtil.removeWaterFromLoc(loc);
                    }
                    PaperLib.teleportAsync(player, loc);
                }));

                if (!configLoad.getBoolean("Island.Teleport.FallDamage", true)) {
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
        return new String[]{"tp", "spawn", "home", "go", "warp"};
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
