package me.goodandevil.skyblock.command.commands;

import me.goodandevil.skyblock.SkyBlock;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SkyBlockCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        sender.sendMessage("");
        sender.sendMessage(formatText("FabledSkyBlock &7Version " + SkyBlock.getInstance().getDescription().getVersion() + " Created with <3 by &5&l&oSongoda"));
        sender.sendMessage(formatText("&8 - &a/island help &7 - The default help command."));
        sender.sendMessage("");
        return true;
    }

    private String formatText(String string){
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
