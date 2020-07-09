package com.songoda.skyblock.sound;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager.Config;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class SoundManager {

    private final SkyBlock plugin;

    public SoundManager(SkyBlock plugin) {
        this.plugin = plugin;
    }

    public void playSound(CommandSender sender, Sound sound, float volume, float pitch) {
        if (sender instanceof Player) {
            Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"));
            FileConfiguration configLoad = config.getFileConfiguration();

            if (configLoad.getBoolean("Sound.Enable")) {
                Player player = (Player) sender;
                player.playSound(player.getLocation(), sound, volume, pitch);
            }
        }
    }

    public void playSound(Location location, Sound sound, float volume, float pitch) {
        Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (configLoad.getBoolean("Sound.Enable")) {
            location.getWorld().playSound(location, sound, volume, pitch);
        }
    }
}
