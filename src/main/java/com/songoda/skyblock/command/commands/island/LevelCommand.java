package com.songoda.skyblock.command.commands.island;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.cooldown.Cooldown;
import com.songoda.skyblock.cooldown.CooldownManager;
import com.songoda.skyblock.cooldown.CooldownPlayer;
import com.songoda.skyblock.cooldown.CooldownType;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.levelling.IslandLevelManager;
import com.songoda.skyblock.menus.Levelling;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.NumberUtil;
import com.songoda.skyblock.utils.player.OfflinePlayer;
import com.songoda.skyblock.visit.Visit;
import com.songoda.skyblock.visit.VisitManager;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class LevelCommand extends SubCommand {

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        IslandLevelManager levellingManager = plugin.getLevellingManager();
        CooldownManager cooldownManager = plugin.getCooldownManager();
        MessageManager messageManager = plugin.getMessageManager();
        IslandManager islandManager = plugin.getIslandManager();
        SoundManager soundManager = plugin.getSoundManager();
        VisitManager visitManager = plugin.getVisitManager();

        Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"));
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
                messageManager.sendMessage(player,
                        configLoad.getString("Command.Island.Level.Owner.Other.Message"));
                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                return;
            } else if (!islandOwnerUUID.equals(playerDataManager.getPlayerData(player).getOwner())) {
                if (visitManager.hasIsland(islandOwnerUUID)) {
                    Visit visit = visitManager.getIsland(islandOwnerUUID);

                    messageManager.sendMessage(player,
                            configLoad.getString("Command.Island.Level.Level.Message")
                                    .replace("%player", targetPlayerName).replace("%level",
                                    "" + NumberUtil.formatNumberByDecimal(visit.getLevel().getLevel())));
                    soundManager.playSound(player, CompatibleSound.ENTITY_PLAYER_LEVELUP.getSound(), 1.0F, 1.0F);

                    return;
                }

                messageManager.sendMessage(player,
                        configLoad.getString("Command.Island.Level.Owner.Other.Message"));
                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                return;
            }
        } else if (args.length != 0) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Level.Invalid.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

            return;
        }

        Island island = islandManager.getIsland(player);

        if (island == null) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Level.Owner.Yourself.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
        } else {
            player.closeInventory();

            if (!island.getLevel().hasMaterials()) {
                org.bukkit.OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(island.getOwnerUUID());

                if (cooldownManager.hasPlayer(CooldownType.Levelling, offlinePlayer)) {
                    CooldownPlayer cooldownPlayer = cooldownManager.getCooldownPlayer(CooldownType.Levelling,
                            offlinePlayer);
                    Cooldown cooldown = cooldownPlayer.getCooldown();

                    long[] durationTime = NumberUtil.getDuration(cooldown.getTime());

                    if (cooldown.getTime() >= 3600) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Level.Cooldown.Message").replace("%time",
                                        durationTime[1] + " "
                                                + configLoad.getString("Command.Island.Level.Cooldown.Word.Minute")
                                                + " " + durationTime[2] + " "
                                                + configLoad.getString("Command.Island.Level.Cooldown.Word.Minute")
                                                + " " + durationTime[3] + " "
                                                + configLoad.getString("Command.Island.Level.Cooldown.Word.Second")));
                    } else if (cooldown.getTime() >= 60) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Level.Cooldown.Message").replace("%time",
                                        durationTime[2] + " "
                                                + configLoad.getString("Command.Island.Level.Cooldown.Word.Minute")
                                                + " " + durationTime[3] + " "
                                                + configLoad.getString("Command.Island.Level.Cooldown.Word.Second")));
                    } else {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Level.Cooldown.Message").replace("%time",
                                        cooldown.getTime() + " "
                                                + configLoad.getString("Command.Island.Level.Cooldown.Word.Second")));
                    }

                    soundManager.playSound(player,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                    return;
                }

                messageManager.sendMessage(player, configLoad.getString("Command.Island.Level.Processing.Message"));
                soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_YES.getSound(), 1.0F, 1.0F);

                cooldownManager.createPlayer(CooldownType.Levelling,
                        Bukkit.getServer().getOfflinePlayer(island.getOwnerUUID()));
                levellingManager.startScan(player, island);
            } else {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Level.Loading.Message"));
                soundManager.playSound(player, CompatibleSound.BLOCK_CHEST_OPEN.getSound(), 1.0F, 1.0F);
                Levelling.getInstance().open(player);
            }
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "level";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Level.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"levelling", "points"};
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
