package com.craftaro.skyblock.command.commands.island;

import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.command.SubCommand;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.cooldown.Cooldown;
import com.craftaro.skyblock.cooldown.CooldownManager;
import com.craftaro.skyblock.cooldown.CooldownPlayer;
import com.craftaro.skyblock.cooldown.CooldownType;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.menus.Creator;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.structure.Structure;
import com.craftaro.skyblock.utils.NumberUtil;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;

public class CreateCommand extends SubCommand {
    public CreateCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        CooldownManager cooldownManager = this.plugin.getCooldownManager();
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        FileManager fileManager = this.plugin.getFileManager();

        FileManager.Config config = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();
        if (islandManager.getIsland(player) == null) {
            FileManager.Config mainConfig = fileManager.getConfig(new File(this.plugin.getDataFolder(), "config.yml"));

            if (args.length == 1) {
                Structure structure = this.plugin.getStructureManager().getStructure(args[0]);

                if (structure != null && islandManager.createIsland(player, structure)) {
                    messageManager.sendMessage(player, configLoad.getString("Island.Creator.Selector.Created.Message"));
                    soundManager.playSound(player, XSound.BLOCK_NOTE_BLOCK_PLING);
                } else if (structure == null) {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Create.StructureNotFound.Message"));
                    soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);
                }
            } else if (mainConfig.getFileConfiguration().getBoolean("Island.Creation.Menu.Enable")) {
                Creator.getInstance().open(player);
                soundManager.playSound(player, XSound.BLOCK_CHEST_OPEN);
            } else {
                List<Structure> structures = this.plugin.getStructureManager().getStructures();

                if (structures.isEmpty()) {
                    messageManager.sendMessage(player, configLoad.getString("Island.Creator.Selector.None.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                    return;
                } else if (!fileManager
                        .isFileExist(new File(new File(this.plugin.getDataFolder() + "/structures"), structures.get(0).getOverworldFile()))) {
                    messageManager.sendMessage(player,
                            configLoad.getString("Island.Creator.Selector.File.Overworld.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                    return;
                } else if (!fileManager
                        .isFileExist(new File(new File(this.plugin.getDataFolder() + "/structures"), structures.get(0).getNetherFile()))) {
                    messageManager.sendMessage(player,
                            configLoad.getString("Island.Creator.Selector.File.Nether.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                    return;
                } else if (fileManager.getConfig(new File(this.plugin.getDataFolder(), "config.yml"))
                        .getFileConfiguration().getBoolean("Island.Creation.Cooldown.Creation.Enable")
                        && cooldownManager.hasPlayer(CooldownType.CREATION, player)) {
                    CooldownPlayer cooldownPlayer = cooldownManager.getCooldownPlayer(CooldownType.CREATION, player);
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

                    soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);

                    return;
                }

                if (islandManager.createIsland(player, structures.get(0))) {
                    messageManager.sendMessage(player, configLoad.getString("Island.Creator.Selector.Created.Message"));
                    soundManager.playSound(player, XSound.BLOCK_NOTE_BLOCK_PLING);
                }
            }
        } else {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Create.Owner.Message"));
            soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);
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
