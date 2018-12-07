package me.goodandevil.skyblock.island;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

public class Islands {

	private Material material;
	private int data;

	private String fileName;
	private String islandName;
	private String displayName;
	private String permission;

	private List<String> description = new ArrayList<>();

	public Islands(Material material, int data, String fileName, String islandName, String displayName,
			String permission, List<String> description) {
		this.material = material;
		this.data = data;
		this.fileName = fileName;
		this.islandName = islandName;
		this.displayName = displayName;
		this.permission = permission;
		this.description = description;
	}

	public Material getMaterial() {
		return material;
	}

	public int getData() {
		return data;
	}

	public String getFileName() {
		return fileName;
	}

	public String getIslandName() {
		return islandName;
	}

	public String getDisplayname() {
		return displayName;
	}

	public String getPermission() {
		return permission;
	}

	public List<String> getDescription() {
		return description;
	}
}
