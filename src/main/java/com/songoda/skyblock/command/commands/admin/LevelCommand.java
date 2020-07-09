package com.songoda.skyblock.command.commands.admin;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.menus.admin.Levelling;
import com.songoda.skyblock.sound.SoundManager;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class LevelCommand extends SubCommand {

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        SoundManager soundManager = plugin.getSoundManager();

        Levelling.getInstance().open(player);
        soundManager.playSound(player, CompatibleSound.BLOCK_CHEST_OPEN.getSound(), 1.0F, 1.0F);
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
        return "Command.Island.Admin.Level.Info.Message";
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
