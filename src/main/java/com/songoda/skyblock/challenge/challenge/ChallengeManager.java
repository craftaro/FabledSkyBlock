package com.songoda.skyblock.challenge.challenge;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;

public class ChallengeManager {
	private SkyBlock plugin;
	private HashMap<Integer, ChallengeCategory> categories;

	public ChallengeManager(SkyBlock plugin) {
		this.plugin = plugin;
		categories = new HashMap<>();
		loadChallenges();
	}

	private void loadChallenges() {
		Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "challenges.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		try {
			ConfigurationSection section = configLoad.getConfigurationSection("challenges");
			if (section != null) {
				for (String k : section.getKeys(false)) {
					int id = configLoad.getInt("challenges." + k + ".id");
					String name = ChatColor.translateAlternateColorCodes('&', configLoad.getString("challenges." + k + ".name"));
					ChallengeCategory cc = new ChallengeCategory(id, name, configLoad);
					categories.put(id, cc);
				}
			}
		} catch (IllegalArgumentException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Error while loading challenges:", ex);
			return;
		}
		Bukkit.getConsoleSender().sendMessage("[FabledSkyBlock] " + ChatColor.GREEN + " challenges loaded with " + ChatColor.GOLD
				+ categories.size() + ChatColor.GREEN + " categories");
	}

	public ChallengeCategory getChallenge(int id) {
		return categories.get(id);
	}
}
