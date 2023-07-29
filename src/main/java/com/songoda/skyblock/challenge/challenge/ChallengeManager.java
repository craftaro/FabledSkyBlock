package com.songoda.skyblock.challenge.challenge;

import com.songoda.skyblock.SkyBlock;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.logging.Level;

public class ChallengeManager {
    private final SkyBlock plugin;
    private final HashMap<Integer, ChallengeCategory> categories;

    public ChallengeManager(SkyBlock plugin) {
        this.plugin = plugin;
        this.categories = new HashMap<>();
        loadChallenges();
    }

    private void loadChallenges() {
        FileConfiguration configLoad = this.plugin.getChallenges();

        try {
            ConfigurationSection section = configLoad.getConfigurationSection("challenges");
            if (section != null) {
                for (String k : section.getKeys(false)) {
                    int id = configLoad.getInt("challenges." + k + ".id");
                    String name = this.plugin.formatText(configLoad.getString("challenges." + k + ".name"));
                    ChallengeCategory cc = new ChallengeCategory(id, name, configLoad);
                    this.categories.put(id, cc);
                }
            }
        } catch (IllegalArgumentException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Error while loading challenges:", ex);
            return;
        }
        Bukkit.getConsoleSender().sendMessage("[FabledSkyBlock] " + ChatColor.GREEN + " challenges loaded with " + ChatColor.GOLD + this.categories.size() + ChatColor.GREEN + " categories");
    }

    public ChallengeCategory getChallenge(int id) {
        return this.categories.get(id);
    }
}
