package com.songoda.skyblock.command.commands.island;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.menus.ControlPanel;
import com.songoda.skyblock.sound.SoundManager;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class ControlPanelCommand extends SubCommand {

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        SoundManager soundManager = plugin.getSoundManager();

        Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (plugin.getIslandManager().getIsland(player) == null) {
            plugin.getMessageManager().sendMessage(player,
                    configLoad.getString("Command.Island.ControlPanel.Owner.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
        } else {
            ControlPanel.getInstance().open(player);
            soundManager.playSound(player, CompatibleSound.BLOCK_CHEST_OPEN.getSound(), 1.0F, 1.0F);
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "controlpanel";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.ControlPanel.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"cp"};
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
