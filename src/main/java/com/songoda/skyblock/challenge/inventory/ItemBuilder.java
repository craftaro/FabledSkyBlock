/**
 * 
 */
package com.songoda.skyblock.challenge.inventory;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class ItemBuilder {
	private Material m;
	private int count;
	private int damage;
	private String name;
	private List<String> lores;
	private String texture;

	private ItemBuilder() {
	}

	public static ItemBuilder of(Material m) {
		return of(m, 1);
	}

	public static ItemBuilder of(Material m, int count) {
		return of(m, count, (short) 0);
	}

	public static ItemBuilder of(Material m, int count, int damage) {
		ItemBuilder ib = new ItemBuilder();
		ib.m = m;
		ib.count = count;
		ib.damage = damage;
		return ib;
	}

	public ItemBuilder name(String name) {
		this.name = name;
		return this;
	}

	public ItemBuilder lore(List<String> lores) {
		this.lores = lores;
		return this;
	}

	public ItemBuilder texture(String texture) {
		this.texture = texture;
		return this;
	}

	public ItemStack build() {
		ItemStack is = new ItemStack(m, count);
		if (is instanceof Damageable) {
			Damageable d = (Damageable) is;
			d.setDamage(damage);
		}
		ItemMeta im = is.hasItemMeta() ? is.getItemMeta() : Bukkit.getItemFactory().getItemMeta(m);
		if (im != null) {
			im.setDisplayName(name);
			im.setLore(lores);
			if (m == Material.PLAYER_HEAD && texture != null && !"".equalsIgnoreCase(texture.trim())) {
				SkullMeta headMeta = (SkullMeta) im;
				GameProfile profile = new GameProfile(UUID.randomUUID(), null);

				profile.getProperties().put("textures", new Property("textures", texture));

				try {
					Field profileField = headMeta.getClass().getDeclaredField("profile");
					profileField.setAccessible(true);
					profileField.set(headMeta, profile);
				} catch (IllegalArgumentException | NoSuchFieldException | SecurityException
						| IllegalAccessException ex) {
					Bukkit.getLogger().log(Level.SEVERE, "Error while setting head texture", ex);
				}
			}
			is.setItemMeta(im);
		}

		return is;
	}
}
