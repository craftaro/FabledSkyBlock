package com.songoda.skyblock.command.commands.island;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.confirmation.Confirmation;
import com.songoda.skyblock.cooldown.Cooldown;
import com.songoda.skyblock.cooldown.CooldownManager;
import com.songoda.skyblock.cooldown.CooldownPlayer;
import com.songoda.skyblock.cooldown.CooldownType;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.structure.Structure;
import com.songoda.skyblock.utils.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class PreviewCommand extends SubCommand {

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        CooldownManager cooldownManager = plugin.getCooldownManager();
        MessageManager messageManager = plugin.getMessageManager();
        SoundManager soundManager = plugin.getSoundManager();
        FileManager fileManager = plugin.getFileManager();
        FileManager.Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLang = config.getFileConfiguration();

        if(args.length != 1) {
            plugin.getMessageManager().sendMessage(player, configLang.getString("Command.Island.Preview.Argument.Count.Message"));
            return;
        }

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        Island island = plugin.getIslandManager().getIsland(Bukkit.getOfflinePlayer(player.getUniqueId()));

        if (args[0].equals("confirm")) {
            if(data.getConfirmation() == Confirmation.Preview && data.getConfirmationTime() > 0) {
                Structure islandStructure = plugin.getStructureManager().getStructure(island.getStructure());

                if(plugin.getIslandManager().deleteIsland(island, true)) {
                    island.setDeleted(true);
                    data.setPreview(false);
                    if(player.getGameMode() == GameMode.SPECTATOR) {
                        player.setGameMode(GameMode.SURVIVAL);
                    }

                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        if(plugin.getIslandManager().createIsland(player, islandStructure)) {
                            plugin.getMessageManager().sendMessage(player, configLang.getString("Island.Creator.Selector.Created.Message"));
                            plugin.getSoundManager().playSound(player, CompatibleSound.BLOCK_NOTE_BLOCK_PLING.getSound(), 1.0F, 1.0F);
                        }
                    }, 30L);
                }
            }
        } else if (args[0].equals("cancel")) {
            if(data.getConfirmation() == Confirmation.Preview && data.getConfirmationTime() > 0) {
                if(plugin.getIslandManager().deleteIsland(island, true)) {
                    island.setDeleted(true);
                    data.setPreview(false);
                    if (player.getGameMode() == GameMode.SPECTATOR) {
                        player.setGameMode(GameMode.SURVIVAL);
                    }
                }
            }
        } else {
            // Do not preview if cooldown is still active
            if (fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml"))
                    .getFileConfiguration().getBoolean("Island.Preview.Cooldown.Enable")
                    && cooldownManager.hasPlayer(CooldownType.Preview, player)) {
                CooldownPlayer cooldownPlayer = cooldownManager.getCooldownPlayer(CooldownType.Preview, player);
                Cooldown cooldown = cooldownPlayer.getCooldown();

                if (cooldown.getTime() < 60) {
                    messageManager.sendMessage(player,
                            config.getFileConfiguration().getString("Island.Preview.Cooldown.Message")
                                    .replace("%time", cooldown.getTime() + " " + config.getFileConfiguration()
                                            .getString("Island.Preview.Cooldown.Word.Second")));
                } else {
                    long[] durationTime = NumberUtil.getDuration(cooldown.getTime());
                    messageManager.sendMessage(player,
                            config.getFileConfiguration().getString("Island.Preview.Cooldown.Message")
                                    .replace("%time", durationTime[2] + " "
                                            + config.getFileConfiguration()
                                            .getString("Island.Preview.Cooldown.Word.Minute")
                                            + " " + durationTime[3] + " " + config.getFileConfiguration()
                                            .getString("Island.Preview.Cooldown.Word.Second")));
                }

                soundManager.playSound(player,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                return;
            }
        	// Do not preview if user has an island
        	if (island != null) {
                plugin.getMessageManager().sendMessage(player, configLang.getString("Command.Island.Preview.Island.Message"));
                return;
        	}
            Structure structure = plugin.getStructureManager().getStructure(args[0]);
            if(structure == null) {
                plugin.getMessageManager().sendMessage(player, configLang.getString("Command.Island.Preview.File.Message"));
                return;
            }
            plugin.getIslandManager().previewIsland(player, structure);
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "preview";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Preview.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}