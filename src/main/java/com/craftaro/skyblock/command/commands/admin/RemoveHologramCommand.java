package com.craftaro.skyblock.command.commands.admin;

import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.command.SubCommand;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.config.FileManager.Config;
import com.craftaro.skyblock.hologram.Hologram;
import com.craftaro.skyblock.hologram.HologramType;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.tasks.HologramTask;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class RemoveHologramCommand extends SubCommand {
    public RemoveHologramCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        onCommand(player, args);
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        onCommand(sender, args);
    }

    public void onCommand(CommandSender sender, String[] args) {
        HologramTask hologramManager = this.plugin.getHologramTask();
        MessageManager messageManager = this.plugin.getMessageManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        FileManager fileManager = this.plugin.getFileManager();

        Config config = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length == 1) {
            HologramType hologramType = null;

            switch (args[0].toLowerCase()) {
                case "level":
                    hologramType = HologramType.LEVEL;
                    break;
                case "bank":
                    hologramType = HologramType.BANK;
                    break;
                case "votes":
                    hologramType = HologramType.VOTES;
                    break;
            }

            if (hologramType != null) {
                Config locationsConfig = fileManager.getConfig(new File(this.plugin.getDataFolder(), "locations.yml"));
                FileConfiguration locationsConfigLoad = locationsConfig.getFileConfiguration();

                if (locationsConfigLoad.getString("Location.Hologram.Leaderboard." + hologramType.getFriendlyName()) == null) {
                    messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.RemoveHologram.Set.Message"));
                    soundManager.playSound(sender, XSound.BLOCK_ANVIL_LAND);
                } else {
                    locationsConfigLoad.set("Location.Hologram.Leaderboard." + hologramType.getFriendlyName(), null);

                    try {
                        locationsConfigLoad.save(locationsConfig.getFile());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    Bukkit.getServer().getScheduler().runTask(this.plugin, () -> {
                        HologramType hologramType1 = HologramType.valueOf(args[0].toUpperCase());
                        Hologram hologram = hologramManager.getHologram(hologramType1);

                        if (hologram != null) {
                            hologramManager.removeHologram(hologram);
                        }
                    });

                    messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.RemoveHologram.Removed.Message").replace("%type", hologramType.getFriendlyName()));
                    soundManager.playSound(sender, XSound.BLOCK_NOTE_BLOCK_PLING);
                }

                return;
            }
        }

        messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.RemoveHologram.Invalid.Message"));
        soundManager.playSound(sender, XSound.BLOCK_ANVIL_LAND);
    }

    @Override
    public String getName() {
        return "removehologram";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Admin.RemoveHologram.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String[] getArguments() {
        return new String[]{"level", "bank", "votes"};
    }
}
