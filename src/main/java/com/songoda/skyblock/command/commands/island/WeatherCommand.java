package com.songoda.skyblock.command.commands.island;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.menus.Weather;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class WeatherCommand extends SubCommand {

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = skyblock.getMessageManager();
        IslandManager islandManager = skyblock.getIslandManager();
        SoundManager soundManager = skyblock.getSoundManager();

        FileConfiguration configLoad = skyblock.getFileManager()
                .getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration();

        Island island = islandManager.getIsland(player);

        if (island == null) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Weather.Owner.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
        } else if ((island.hasRole(IslandRole.Operator, player.getUniqueId())
                && island.getSetting(IslandRole.Operator, "Weather").getStatus())
                || island.hasRole(IslandRole.Owner, player.getUniqueId())) {
            Weather.getInstance().open(player);
            soundManager.playSound(player, CompatibleSound.BLOCK_CHEST_OPEN.getSound(), 1.0F, 1.0F);
        } else {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Weather.Permission.Message"));
            soundManager.playSound(player,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "weather";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Weather.Info.Message";
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
