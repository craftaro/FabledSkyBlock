package com.songoda.skyblock.challenge.challenge;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class ItemChallenge {
	private Challenge challenge;
	private final boolean show;
	private final int row;
	private final int col;
	private final CompatibleMaterial type;
	private final int amount;
	private final List<String> lore;

	private final String itemTitle;

	public ItemChallenge(boolean show, int row, int col, CompatibleMaterial type, int amount, List<String> lore) {
		this.show = show;
		this.row = row;
		this.col = col;
		this.type = type;
		this.amount = amount;
		this.lore = lore;

		FileManager.Config langConfig = SkyBlock.getInstance().getFileManager()
				.getConfig(new File(SkyBlock.getInstance().getDataFolder(), "language.yml"));
		FileConfiguration langConfigLoad = langConfig.getFileConfiguration();
		itemTitle = langConfigLoad.getString("Challenge.Inventory.Item.Title");
	}

	public ItemStack createItem(UUID player, int amount) {
		FileManager.Config langConfig = SkyBlock.getInstance().getFileManager()
				.getConfig(new File(SkyBlock.getInstance().getDataFolder(), "language.yml"));
		FileConfiguration langConfigLoad = langConfig.getFileConfiguration();
		
		ItemStack is = type.getItem();
		is.setAmount(this.amount);
		// Air
		ItemMeta im = is.getItemMeta();
		if (im != null) {
			String maxAmount;
			if(challenge.getMaxTimes() == Integer.MAX_VALUE) {
				maxAmount = langConfigLoad.getString("Challenge.Inventory.Unlimited.Message");
			} else {
				maxAmount = String.valueOf(challenge.getMaxTimes());
			}
			im.setDisplayName(ChatColor.translateAlternateColorCodes('&',
					itemTitle.replace("%challenge", challenge.getName()).replace("%amount", Integer.toString(amount))
							.replace("%max", maxAmount)));
			im.setLore(lore);
			is.setItemMeta(im);
		}
		return is;
	}

	// GETTERS & SETTERS
	public Challenge getChallenge() {
		return challenge;
	}

	public void setChallenge(Challenge challenge) {
		this.challenge = challenge;
	}

	public boolean isShow() {
		return show;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public CompatibleMaterial getType() {
		return type;
	}

	public int getAmount() {
		return amount;
	}

	public List<String> getLore() {
		return lore;
	}
}
