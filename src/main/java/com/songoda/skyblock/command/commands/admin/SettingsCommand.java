package com.songoda.skyblock.command.commands.admin;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.gui.permissions.GuiPermissionsSelector;
import com.songoda.skyblock.sound.SoundManager;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SettingsCommand extends SubCommand {
    public SettingsCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        SoundManager soundManager = this.plugin.getSoundManager();

        this.plugin.getGuiManager().showGUI(player, new GuiPermissionsSelector(this.plugin, player, null, null));
        soundManager.playSound(player, XSound.BLOCK_CHEST_OPEN);
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "settings";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Admin.Settings.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"permissions", "perms", "p"};
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
