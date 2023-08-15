package com.craftaro.skyblock.command.commands.admin;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.command.SubCommand;
import com.craftaro.skyblock.menus.admin.Levelling;
import com.craftaro.skyblock.sound.SoundManager;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class LevelCommand extends SubCommand {
    public LevelCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        SoundManager soundManager = this.plugin.getSoundManager();

        Levelling.getInstance().open(player);
        soundManager.playSound(player, XSound.BLOCK_CHEST_OPEN);
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
