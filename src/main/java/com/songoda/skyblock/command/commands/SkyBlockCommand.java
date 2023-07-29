package com.songoda.skyblock.command.commands;

import com.songoda.skyblock.SkyBlock;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SkyBlockCommand implements CommandExecutor {
    private final SkyBlock plugin;

    public SkyBlockCommand(SkyBlock plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String s, String[] strings) {
        sender.sendMessage("");
        sender.sendMessage(formatText("FabledSkyBlock &7Version " + this.plugin.getDescription().getVersion() + " Created with <3 by &5&l&oSongoda"));
        sender.sendMessage(formatText("&8 - &a/island help &7 - The default help command."));
        sender.sendMessage("");
        return true;
    }

    private String formatText(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
