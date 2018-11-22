package me.goodandevil.skyblock.structure;

import java.util.ArrayList;
import java.util.List;

import me.goodandevil.skyblock.utils.version.Materials;

public class Structure {

	private Materials materials;
	
	private String name;
	private String file;
	private String displayName;
	
	private boolean permission;
	
	private List<String> description = new ArrayList<>();
	
	public Structure(String name, Materials materials, String file, String displayName, boolean permission, List<String> description) {
		this.name = name;
		this.materials = materials;
		this.file = file;
		this.displayName = displayName;
		this.permission = permission;
		this.description = description;
	}
	
	public String getName() {
		return name;
	}
	
	public Materials getMaterials() {
		return materials;
	}
	
	public void setMaterials(Materials materials) {
		this.materials = materials;
	}
	
	public String getFile() {
		return file;
	}
	
	public void setFile(String file) {
		this.file = file;
	}
	
	public String getDisplayname() {
		return displayName;
	}
	
	public void setDisplayname(String displayName) {
		this.displayName = displayName;
	}
	
	public boolean isPermission() {
		return permission;
	}
	
	public String getPermission() {
		return "skyblock.island." + name.toLowerCase().replace(" ", "_");
	}
	
	public void setPermission(boolean permission) {
		this.permission = permission;
	}
	
	public List<String> getDescription() {
		return description;
	}
	
	public void addLine(String line) {
		description.add(line);
	}
	
	public void removeLine(int index) {
		description.remove(index);
	}
}
