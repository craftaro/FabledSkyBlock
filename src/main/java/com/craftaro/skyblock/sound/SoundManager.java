package com.craftaro.skyblock.sound;

import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class SoundManager {
    private final SkyBlock plugin;

    public SoundManager(SkyBlock plugin) {
        this.plugin = plugin;
    }

    public void playSound(CommandSender sender, XSound sound) {
        playSound(sender, sound, 1, 1);
    }

    public void playSound(CommandSender sender, XSound sound, float volume, float pitch) {
        if (sender instanceof Player) {
            FileConfiguration configLoad = this.plugin.getConfiguration();

            if (configLoad.getBoolean("Sound.Enable")) {
                Player player = (Player) sender;
                sound.play(player, volume, pitch);
            }
        }
    }

    public void playSound(Location location, XSound sound) {
        playSound(location, sound, 1, 1);
    }

    public void playSound(Location location, XSound sound, float volume, float pitch) {
        if (this.plugin.getConfiguration().getBoolean("Sound.Enable")) {
            sound.play(location, volume, pitch);
        }
    }
}
