package com.songoda.skyblock.challenge.challenge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.songoda.core.compatibility.CompatibleMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class ChallengeCategory {
	private int id;
	private String name;
	private HashMap<Integer, Challenge> challenges;

	public ChallengeCategory(int id, String name, FileConfiguration config) {
		this.id = id;
		this.name = name;
		challenges = new HashMap<>();
		loadChallenges(config);
	}

	private void loadChallenges(FileConfiguration config) {
		ConfigurationSection section = config.getConfigurationSection("challenges." + id + ".challenges");
		if (section == null)
			// No challenge here
			return;
		Set<String> keys = section.getKeys(false);
		for (String k : keys) {
			String key = "challenges." + id + ".challenges." + k;
			int id = config.getInt(key + ".id");
			if (id == 0)
				throw new IllegalArgumentException("Invalid id at category " + this.name + "(" + this.id
						+ ") at challenge " + name + "(" + id + ")");
			String name = ChatColor.translateAlternateColorCodes('&', config.getString(key + ".name"));
			List<String> require = toColor(config.getStringList(key + ".require"));
			List<String> reward = toColor(config.getStringList(key + ".reward"));
			int maxTimes = config.getInt(key + ".maxtimes");
			boolean showInChat = config.getBoolean(key + ".showInChat");
			// Item
			boolean show = config.getBoolean(key + ".item.show");
			int row = show ? config.getInt(key + ".item.row") : 0;
			int col = show ? config.getInt(key + ".item.col") : 0;
			String strItem = show ? config.getString(key + ".item.item") : "AIR";
			if (strItem == null)
				strItem = "AIR";
			int amount = show ? config.getInt(key + ".item.amount") : 0;
			List<String> lore = show ? toColor(config.getStringList(key + ".item.lore")) : new ArrayList<>();
			if (lore == null)
				lore = new ArrayList<>();
			try {
				// If an Exception occurs, we don't handle it here but in parent class
				CompatibleMaterial compatibleMaterial = CompatibleMaterial.getMaterial(strItem);
				if (compatibleMaterial == null)
					throw new IllegalArgumentException("Item " + strItem + " isn't a correct material");
				ItemChallenge ic = new ItemChallenge(show, row, col, compatibleMaterial, amount, lore);
				Challenge c = new Challenge(this, id, name, maxTimes, showInChat, require, reward, ic);
				challenges.put(id, c);
			} catch (IllegalArgumentException ex) {
				throw new IllegalArgumentException("Exception at category " + this.name + "(" + this.id
						+ ") at challenge " + name + "(" + id + "): " + ex.getMessage());
			}
		}
		Bukkit.getConsoleSender().sendMessage("[FabledSkyBlock] " + ChatColor.GREEN + "Category " + name + ChatColor.GREEN
				+ " loaded with " + ChatColor.GOLD + challenges.size() + ChatColor.GREEN + " challenges");
	}

	private List<String> toColor(List<String> list) {
		List<String> copy = new ArrayList<>();
		if (list == null)
			return copy;
		for (String str : list)
			copy.add(ChatColor.translateAlternateColorCodes('&', str));
		return copy;
	}

	// GETTERS

	public Challenge getChallenge(int id) {
		return challenges.get(id);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public HashMap<Integer, Challenge> getChallenges() {
		return challenges;
	}
}
