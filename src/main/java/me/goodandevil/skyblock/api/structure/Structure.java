package me.goodandevil.skyblock.api.structure;

import java.util.List;

import me.goodandevil.skyblock.utils.version.Materials;

public interface Structure {

	public String getName();

	public Materials getMaterials();

	public void setMaterials(Materials materials);

	public String getFile();

	public void setFile(String file);

	public String getDisplayname();

	public void setDisplayname(String displayName);

	public boolean isPermission();

	public String getPermission();

	public void setPermission(boolean permission);

	public List<String> getDescription();

	public void addLine(String line);

	public void removeLine(int index);
}
