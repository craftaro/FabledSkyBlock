package com.songoda.skyblock.command.commands.island;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.levelling.IslandLevelManager;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.core.utils.NumberUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class ValueCommand extends SubCommand {

    @SuppressWarnings("deprecation")
    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        IslandLevelManager levellingManager = plugin.getLevellingManager();
        MessageManager messageManager = plugin.getMessageManager();
        SoundManager soundManager = plugin.getSoundManager();
        FileManager fileManager = plugin.getFileManager();

        Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (player.getItemInHand() == null) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Value.Hand.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
        } else {
            CompatibleMaterial materials = CompatibleMaterial.getMaterial(player.getItemInHand().getType().name());

            if (materials != null && levellingManager.hasWorth(materials)) {
                double worth = levellingManager.getWorth(materials);
                double level = worth / (double) this.plugin.getConfiguration().getInt("Island.Levelling.Division");

                messageManager.sendMessage(player,
                        configLoad.getString("Command.Island.Value.Value.Message").replace("%material", WordUtils.capitalizeFully(materials.name().toLowerCase().replace("_", " ")))
                                .replace("%points", "" + worth).replace("%level", "" + NumberUtils.formatNumber(level)));
                soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_YES.getSound(), 1.0F, 1.0F);
            } else {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Value.None.Message"));
                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            }
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "value";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Value.Info.Message";
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
