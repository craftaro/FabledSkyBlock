package me.goodandevil.skyblock.generator;

import java.util.List;

import me.goodandevil.skyblock.utils.version.Materials;

public class Generator {

	private String name;
	private Materials materials;
	private List<GeneratorMaterial> generatorMaterials;
	private boolean permission;
	
	public Generator(String name, Materials materials, List<GeneratorMaterial> generatorMaterials, boolean permission) {
		this.name = name;
		this.materials = materials;
		this.generatorMaterials = generatorMaterials;
	}
	
	public String getName() {
		return name;
	}
	
	public Materials getMaterials() {
		return materials;
	}
	
	public List<GeneratorMaterial> getGeneratorMaterials() {
		return generatorMaterials;
	}
	
	public boolean isPermission() {
		return permission;
	}
	
	public String getPermission() {
		return "skyblock.generator." + name.toLowerCase().replace(" ", "_");
	}
	
	public void setPermission(boolean permission) {
		this.permission = permission;
	}
}
