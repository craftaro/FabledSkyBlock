package com.songoda.skyblock.challenge.challenge;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;

public class ItemChallenge {
	private Challenge challenge;
	private boolean show;
	private int row;
	private int col;
	private Material type;
	private int amount;
	private List<String> lore;

	private String itemTitle;

	public ItemChallenge(boolean show, int row, int col, Material type, int amount, List<String> lore) {
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
		ItemStack is = new ItemStack(type, this.amount);
		// Air
		ItemMeta im = is.getItemMeta();
		if (im != null) {
			im.setDisplayName(ChatColor.translateAlternateColorCodes('&',
					itemTitle.replace("%challenge", challenge.getName()).replace("%amount", Integer.toString(amount))
							.replace("%max", Integer.toString(challenge.getMaxTimes()))));
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

	public Material getType() {
		return type;
	}

	public int getAmount() {
		return amount;
	}

	public List<String> getLore() {
		return lore;
	}
}
