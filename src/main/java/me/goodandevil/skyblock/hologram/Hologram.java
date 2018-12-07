package me.goodandevil.skyblock.hologram;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

public class Hologram {

	private List<ArmorStand> holograms = new ArrayList<>();

	private HologramType type;
	private Location location;

	public Hologram(HologramType type, Location location, List<String> lines) {
		this.type = type;
		this.location = location;

		for (String lineList : lines) {
			addLine(lineList);
		}
	}

	public void addLine(String text) {
		ArmorStand as = (ArmorStand) location.getWorld().spawnEntity(
				location.clone().add(0.0D, getHeight() + getHeightIncrement(), 0.0D), EntityType.ARMOR_STAND);
		as.setVisible(false);
		as.setGravity(false);
		as.setCustomName(ChatColor.translateAlternateColorCodes('&', text));
		as.setCustomNameVisible(true);

		holograms.add(as);
	}

	public void setLine(int index, String text) {
		if (index < holograms.size()) {
			ArmorStand as = holograms.get(index);

			if (!as.isDead()) {
				as.setCustomName(ChatColor.translateAlternateColorCodes('&', text));
				as.setCustomNameVisible(true);
			}
		}
	}

	public void removeLine(int index) {
		if (index < holograms.size()) {
			ArmorStand as = holograms.get(index);

			if (!as.isDead()) {
				as.remove();
			}

			holograms.remove(index);
		}
	}

	public double getHeight() {
		return -2.0D + (holograms.size() * getHeightIncrement());
	}

	public double getHeightIncrement() {
		return 0.35;
	}

	public HologramType getType() {
		return type;
	}

	public List<ArmorStand> getHolograms() {
		return holograms;
	}
}
