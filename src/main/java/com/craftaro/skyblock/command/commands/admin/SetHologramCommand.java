package com.craftaro.skyblock.command.commands.admin;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
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
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class SetHologramCommand extends SubCommand {
    public SetHologramCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
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
                fileManager.setLocation(
                        fileManager.getConfig(new File(this.plugin.getDataFolder(), "locations.yml")),
                        "Location.Hologram.Leaderboard." + hologramType.getFriendlyName(), player.getLocation(), true);


                Bukkit.getServer().getScheduler().runTask(this.plugin, () -> {
                    HologramType hologramType1 = HologramType.valueOf(WordUtils.capitalize(args[0].toLowerCase()));
                    Hologram hologram = hologramManager.getHologram(hologramType1);

                    if (hologram != null) {
                        hologramManager.removeHologram(hologram);
                    }
                    hologramManager.spawnHologram(hologramType1);
                });

                messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.SetHologram.Set.Message").replace("%type", hologramType.getFriendlyName()));
                soundManager.playSound(player, XSound.BLOCK_NOTE_BLOCK_PLING);

                return;
            }
        }

        messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.SetHologram.Invalid.Message"));
        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "sethologram";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Admin.SetHologram.Info.Message";
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
