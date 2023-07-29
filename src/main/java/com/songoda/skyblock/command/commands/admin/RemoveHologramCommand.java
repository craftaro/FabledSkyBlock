package com.songoda.skyblock.command.commands.admin;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.hologram.Hologram;
import com.songoda.skyblock.hologram.HologramType;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.tasks.HologramTask;
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

                if (locationsConfigLoad.getString("Location.Hologram.Leaderboard." + hologramType.name()) == null) {
                    messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.RemoveHologram.Set.Message"));
                    soundManager.playSound(sender, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                } else {
                    locationsConfigLoad.set("Location.Hologram.Leaderboard." + hologramType.name(), null);

                    try {
                        locationsConfigLoad.save(locationsConfig.getFile());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    Bukkit.getServer().getScheduler().runTask(this.plugin, () -> {
                        HologramType hologramType1 = HologramType.valueOf(WordUtils.capitalize(args[0].toLowerCase()));
                        Hologram hologram = hologramManager.getHologram(hologramType1);

                        if (hologram != null) {
                            hologramManager.removeHologram(hologram);
                        }
                    });

                    messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.RemoveHologram.Removed.Message").replace("%type", hologramType.name()));
                    soundManager.playSound(sender, CompatibleSound.BLOCK_NOTE_BLOCK_PLING.getSound(), 1.0F, 1.0F);
                }

                return;
            }
        }

        messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.RemoveHologram.Invalid.Message"));
        soundManager.playSound(sender, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
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
