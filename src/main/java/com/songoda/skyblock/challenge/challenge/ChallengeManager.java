package com.songoda.skyblock.challenge.challenge;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager.Config;

public class ChallengeManager {
	private SkyBlock skyblock;
	private HashMap<Integer, ChallengeCategory> categories;

	public ChallengeManager(SkyBlock skyblock) {
		this.skyblock = skyblock;
		categories = new HashMap<>();
		loadChallenges();
	}

	private void loadChallenges() {
		Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "challenges.yml"));
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
		Bukkit.getLogger().info("[FabledSkyBlock] " + ChatColor.GREEN + " challenges loaded with " + ChatColor.GOLD
				+ categories.size() + ChatColor.GREEN + " categories");
	}

	public ChallengeCategory getChallenge(int id) {
		return categories.get(id);
	}
}
