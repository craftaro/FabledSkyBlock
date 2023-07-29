package com.songoda.skyblock.command;

import com.songoda.skyblock.SkyBlock;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

public abstract class SubCommand {
    protected final SkyBlock plugin;
    protected final String info;

    public SubCommand(SkyBlock plugin) {
        this.plugin = plugin;
        this.info = this.plugin.formatText(this.plugin.getLanguage().getString(this.getInfoMessagePath()));
    }

    public abstract void onCommandByPlayer(Player player, String[] args);

    public abstract void onCommandByConsole(ConsoleCommandSender sender, String[] args);

    public abstract String getName();

    public abstract String getInfoMessagePath();

    public abstract String[] getAliases();

    public abstract String[] getArguments();

    public String getInfo() {
        return this.info;
    }

    public boolean hasPermission(Permissible toCheck, boolean isAdmin) {
        if (toCheck.hasPermission("fabledskyblock.*")) {
            return true;
        }

        return isAdmin
                ? toCheck.hasPermission("fabledskyblock.admin.*") || toCheck.hasPermission("fabledskyblock.admin." + this.getName())
                : toCheck.hasPermission("fabledskyblock.island.*") || toCheck.hasPermission("fabledskyblock.island." + this.getName());
    }
}
