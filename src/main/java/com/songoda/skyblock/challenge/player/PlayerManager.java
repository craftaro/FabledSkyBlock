package com.songoda.skyblock.challenge.player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.challenge.challenge.Challenge;
import com.songoda.skyblock.challenge.challenge.Challenge.Type;
import com.songoda.skyblock.challenge.challenge.ChallengeCategory;
import com.songoda.skyblock.challenge.challenge.Peer;
import com.songoda.skyblock.config.FileManager.Config;

public class PlayerManager {
	private SkyBlock skyblock;
	private HashMap<UUID, HashMap<Challenge, Integer>> players;
	private File playersDirectory;

	public PlayerManager(SkyBlock skyblock) {
		this.skyblock = skyblock;
		players = new HashMap<>();
		playersDirectory = new File(skyblock.getDataFolder(), "challenge-data");
		if (!playersDirectory.exists())
			playersDirectory.mkdirs();
	}

	public HashMap<Challenge, Integer> getPlayer(UUID uuid) {
		return players.get(uuid);
	}

	/**
	 * Load specific player
	 * 
	 * @param uuid
	 *                 The uuid of specific player
	 */
	public void loadPlayer(UUID uuid) {
		Config config = skyblock.getFileManager().getConfig(new File(playersDirectory, uuid.toString() + ".yml"));
		FileConfiguration fileConfig = config.getFileConfiguration();
		HashMap<Challenge, Integer> challenges = new HashMap<>();
		ConfigurationSection section = fileConfig.getConfigurationSection("challenges");
		Set<String> strs = (section != null) ? section.getKeys(false) : new HashSet<>();
		for (String k : strs) {
			int id = fileConfig.getInt("challenges." + k + ".id");
			ChallengeCategory cc = skyblock.getFabledChallenge().getChallengeManager().getChallenge(id);
			// WTF
			if (cc == null)
				continue;
			ConfigurationSection section2 = fileConfig.getConfigurationSection("challenges." + k + ".challenges");
			if (section2 != null)
				for (String d : section2.getKeys(false)) {
					String key = "challenges." + k + ".challenges." + d;
					int cId = fileConfig.getInt(key + ".id");
					int count = fileConfig.getInt(key + ".count");
					Challenge c = cc.getChallenge(cId);
					if (c == null)
						continue;
					challenges.put(c, count);
				}
		}
		players.put(uuid, challenges);
	}

	/**
	 * Unload specific player
	 * 
	 * @param uuid
	 *                 The uuid of specific player
	 */
	public void unloadPlayer(UUID uuid) {
		players.remove(uuid);
		skyblock.getFileManager().unloadConfig(new File(playersDirectory, uuid.toString() + ".yml"));

	}

	/**
	 * Check if specific player can do specific challenge
	 * 
	 * @param p
	 *              The player
	 * @param c
	 *              The challenge
	 * @return true if specific player can execute specific challenge
	 */
	public boolean canDoChallenge(Player p, Challenge c) {
		if (c == null)
			return false;
		UUID uuid = p.getUniqueId();
		HashMap<Challenge, Integer> done = players.get(uuid);
		if (done == null) {
			// Wtf ?
			loadPlayer(uuid);
			done = players.get(uuid);
		}
		int count = done.getOrDefault(c, 0);
		if (c.getMaxTimes() != 0 && count >= c.getMaxTimes())
			return false;
		// Check if player has required items
		for (Peer<Type, Object> peer : c.getRequires())
			if (!peer.getKey().has(p, peer.getValue()))
				return false;
		return true;
	}

	/**
	 * Perform specific challenge for specific player
	 * 
	 * @param p
	 *              Specific player
	 * @param c
	 *              Specific challenge
	 * @return true if all is good
	 */
	public boolean doChallenge(Player p, Challenge c) {
		if (!canDoChallenge(p, c))
			return false;
		UUID uuid = p.getUniqueId();
		HashMap<Challenge, Integer> done = players.get(uuid);
		int count = done.getOrDefault(c, 0);
		done.put(c, count + 1);
		addChallenge(uuid, c);
		// Take items
		for (Peer<Type, Object> peer : c.getRequires())
			peer.getKey().executeRequire(p, peer.getValue());
		for (Peer<Type, Object> peer : c.getRewards())
			peer.getKey().executeReward(p, peer.getValue());
		// Ok, send message
		String broadcast = ChatColor.translateAlternateColorCodes('&',
				SkyBlock.getInstance().getFileManager()
						.getConfig(new File(SkyBlock.getInstance().getDataFolder(), "language.yml"))
						.getFileConfiguration().getString("Challenge.Broadcast"));
		if (c.isShowInChat())
			Bukkit.broadcastMessage(broadcast.replace("%player", p.getName()).replace("%challenge", c.getName())
					.replace("%amount", Integer.toString(count + 1))
					.replace("%max", Integer.toString(c.getMaxTimes())));
		return true;
	}

	public void addChallenge(UUID uuid, Challenge c) {
		Config config = skyblock.getFileManager().getConfig(new File(playersDirectory, uuid.toString() + ".yml"));
		FileConfiguration fileConfig = config.getFileConfiguration();
		int ccId = c.getCategory().getId();
		int cId = c.getId();
		int count = 1;
		if (fileConfig.contains("challenges." + ccId + ".challenges." + cId + ".count"))
			count = fileConfig.getInt("challenges." + ccId + ".challenges." + cId + ".count") + 1;
		fileConfig.set("challenges." + ccId + ".id", ccId);
		fileConfig.set("challenges." + ccId + ".challenges." + cId + ".id", cId);
		fileConfig.set("challenges." + ccId + ".challenges." + cId + ".count", count);
		try {
			fileConfig.save(new File(playersDirectory, uuid.toString() + ".yml"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Return the number of time specific player has done specific challenge
	 * 
	 * @param uuid
	 *                 The player's uuid
	 * @param c
	 *                 The challenge
	 * @return The number of time specific challenge has been done by player
	 */
	public int getChallengeCount(UUID uuid, Challenge c) {
		HashMap<Challenge, Integer> challenges = players.get(uuid);
		if (challenges != null) {
			return challenges.getOrDefault(c, 0);
		} else {
			// Not connected, check in file
			Config config = skyblock.getFileManager().getConfig(new File(playersDirectory, uuid.toString() + ".yml"));
			FileConfiguration fileConfig = config.getFileConfiguration();
			int ccId = c.getCategory().getId();
			int cId = c.getId();
			if (!fileConfig.contains("challenges." + ccId + ".challenges." + cId + ".count"))
				return 0;
			return fileConfig.getInt("challenges." + ccId + ".challenges." + cId + ".count");
		}
	}
}
