package com.craftaro.skyblock.command.commands.island;

import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.command.SubCommand;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandEnvironment;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.island.IslandStatus;
import com.craftaro.skyblock.island.IslandWorld;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.playerdata.PlayerDataManager;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.utils.world.LocationUtil;
import com.craftaro.skyblock.visit.Visit;
import com.craftaro.skyblock.visit.VisitManager;
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

        FileManager.Config config = this.plugin.getFileManager().getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length == 1) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
            Island island = islandManager.getIsland(offlinePlayer);
            if (island == null) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Teleport.Island.None.Message", "Command.Island.Teleport.Island.None.Message"));
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

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
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

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
                        soundManager.playSound(player, XSound.ENTITY_ENDERMAN_TELEPORT);

                        return;
                    } else {
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Teleport.Island.Closed.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                    }

                    return;
                }

                messageManager.sendMessage(player, configLoad.getString("Command.Island.Teleport.Island.None.Message"));
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                return;
            }
        } else if (args.length != 0) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Teleport.Invalid.Message"));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

            return;
        }

        Island island = islandManager.getIsland(player);

        if (island == null) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Teleport.Owner.Message", "Command.Island.Teleport.Owner.Message"));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
        } else {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Teleport.Teleported.Yourself.Message", "Command.Island.Teleport.Teleported.Yourself.Message"));
            soundManager.playSound(player, XSound.ENTITY_ENDERMAN_TELEPORT);
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
