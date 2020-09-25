package com.songoda.skyblock.command.commands.island;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.cooldown.Cooldown;
import com.songoda.skyblock.cooldown.CooldownManager;
import com.songoda.skyblock.cooldown.CooldownPlayer;
import com.songoda.skyblock.cooldown.CooldownType;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.menus.Creator;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.structure.Structure;
import com.songoda.core.utils.NumberUtils;
import com.songoda.skyblock.utils.NumberUtil;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;

public class CreateCommand extends SubCommand {

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        CooldownManager cooldownManager = plugin.getCooldownManager();
        MessageManager messageManager = plugin.getMessageManager();
        IslandManager islandManager = plugin.getIslandManager();
        SoundManager soundManager = plugin.getSoundManager();
        FileManager fileManager = plugin.getFileManager();

        Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();
        if (islandManager.getIsland(player) == null) {
            Config mainConfig = fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml"));

            if (args.length == 1) {
                Structure structure = plugin.getStructureManager().getStructure(args[0]);

                if (structure != null && islandManager.createIsland(player, structure)) {
                    messageManager.sendMessage(player, configLoad.getString("Island.Creator.Selector.Created.Message"));
                    soundManager.playSound(player, CompatibleSound.BLOCK_NOTE_BLOCK_PLING.getSound(), 1.0F, 1.0F);
                } else if (structure == null) {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Create.StructureNotFound.Message"));
                    soundManager.playSound(player,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
                }
            } else if (mainConfig.getFileConfiguration().getBoolean("Island.Creation.Menu.Enable")) {
                Creator.getInstance().open(player);
                soundManager.playSound(player, CompatibleSound.BLOCK_CHEST_OPEN.getSound(), 1.0F, 1.0F);
            } else {
                List<Structure> structures = plugin.getStructureManager().getStructures();

                if (structures.size() == 0) {
                    messageManager.sendMessage(player, configLoad.getString("Island.Creator.Selector.None.Message"));
                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                    return;
                } else if (!fileManager
                        .isFileExist(new File(new File(plugin.getDataFolder().toString() + "/structures"),
                                structures.get(0).getOverworldFile()))) {
                    messageManager.sendMessage(player,
                            configLoad.getString("Island.Creator.Selector.File.Overworld.Message"));
                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                    return;
                } else if (!fileManager
                        .isFileExist(new File(new File(plugin.getDataFolder().toString() + "/structures"),
                                structures.get(0).getNetherFile()))) {
                    messageManager.sendMessage(player,
                            configLoad.getString("Island.Creator.Selector.File.Nether.Message"));
                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                    return;
                } else if (fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml"))
                        .getFileConfiguration().getBoolean("Island.Creation.Cooldown.Creation.Enable")
                        && cooldownManager.hasPlayer(CooldownType.Creation, player)) {
                    CooldownPlayer cooldownPlayer = cooldownManager.getCooldownPlayer(CooldownType.Creation, player);
                    Cooldown cooldown = cooldownPlayer.getCooldown();

                    if (cooldown.getTime() < 60) {
                        messageManager.sendMessage(player,
                                config.getFileConfiguration().getString("Island.Creator.Selector.Cooldown.Message")
                                        .replace("%time", cooldown.getTime() + " " + config.getFileConfiguration()
                                                .getString("Island.Creator.Selector.Cooldown.Word.Second")));
                    } else {
                        long[] durationTime = NumberUtil.getDuration(cooldown.getTime());
                        messageManager.sendMessage(player,
                                config.getFileConfiguration().getString("Island.Creator.Selector.Cooldown.Message")
                                        .replace("%time", durationTime[2] + " "
                                                + config.getFileConfiguration()
                                                .getString("Island.Creator.Selector.Cooldown.Word.Minute")
                                                + " " + durationTime[3] + " " + config.getFileConfiguration()
                                                .getString("Island.Creator.Selector.Cooldown.Word.Second")));
                    }

                    soundManager.playSound(player,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                    return;
                }

                if (islandManager.createIsland(player, structures.get(0))) {
                    messageManager.sendMessage(player, configLoad.getString("Island.Creator.Selector.Created.Message"));
                    soundManager.playSound(player, CompatibleSound.BLOCK_NOTE_BLOCK_PLING.getSound(), 1.0F, 1.0F);
                }
            }
        } else {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Create.Owner.Message"));
            soundManager.playSound(player,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Create.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"new"};
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
