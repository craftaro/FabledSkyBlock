package com.songoda.skyblock.challenge;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.challenge.challenge.ChallengeCategory;
import com.songoda.skyblock.challenge.challenge.ChallengeManager;
import com.songoda.skyblock.challenge.defaultinv.DefaultInventory;
import com.songoda.skyblock.challenge.inventory.InventoryManager;
import com.songoda.skyblock.challenge.inventory.inv.ChallengeInventory;
import com.songoda.skyblock.challenge.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FabledChallenge {
	private SkyBlock plugin;
	private ChallengeManager challengeManager;
	private PlayerManager playerManager;
	// I use my own inventory api bc it's hard to implement inventories with the
	private InventoryManager inventoryManager;
	private DefaultInventory defaultInventory;
	private ChallengeInventory challengeInventory;

	public FabledChallenge(SkyBlock plugin) {
		this.plugin = plugin;
		this.defaultInventory = new DefaultInventory(plugin);
		this.challengeManager = new ChallengeManager(plugin);
		this.playerManager = new PlayerManager(plugin);
		this.challengeInventory = new ChallengeInventory(this);
		this.inventoryManager = new InventoryManager(plugin);
		this.inventoryManager.init();
	}

	public void onDisable() {
		this.inventoryManager.closeInventories();
	}

	public void openChallengeInventory(Player p, ChallengeCategory category) {
		if (category == null)
			return;
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			inventoryManager.openInventory(challengeInventory, p, params -> {
				params.put(ChallengeInventory.CATEGORY, category);
			});
		}, 1);
	}

	// GETTERS

	public ChallengeManager getChallengeManager() {
		return challengeManager;
	}

	public PlayerManager getPlayerManager() {
		return playerManager;
	}

	public InventoryManager getInventoryManager() {
		return inventoryManager;
	}

	public DefaultInventory getDefaultInventory() {
		return defaultInventory;
	}
}
