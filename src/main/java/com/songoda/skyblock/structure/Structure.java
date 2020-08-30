package com.songoda.skyblock.structure;

import com.songoda.core.compatibility.CompatibleMaterial;

import java.util.ArrayList;
import java.util.List;

public class Structure implements com.songoda.skyblock.api.structure.Structure {

    private CompatibleMaterial material;

    private final String name;
    private String overworldFile;
    private String netherFile;
    private String endFile;
    private String displayName;

    private boolean permission;

    private final List<String> description;
    private final List<String> commands;

    private double deletionCost;

    public Structure(String name, CompatibleMaterial material, String overworldFile, String netherFile, String endFile,
                     String displayName, boolean permission, List<String> description, List<String> commands,
                     double deletionCost) {
        this.name = name;
        this.material = material;
        this.overworldFile = overworldFile;
        this.netherFile = netherFile;
        this.endFile = endFile;
        this.displayName = displayName;
        this.permission = permission;
        this.description = description;
        this.commands = commands;
        this.deletionCost = deletionCost;
    }

    public String getName() {
        return name;
    }

    public CompatibleMaterial getMaterial() {
        return material;
    }

    public void setMaterial(CompatibleMaterial material) {
        this.material = material;
    }

    public String getOverworldFile() {
        return overworldFile;
    }

    public void setOverworldFile(String file) {
        this.overworldFile = file;
    }

    public String getNetherFile() {
        return netherFile;
    }

    public void setNetherFile(String file) {
        this.netherFile = file;
    }

    public String getEndFile() {
        return endFile;
    }

    public void setEndFile(String file) {
        this.endFile = file;
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
        return "fabledskyblock.island." + name.toLowerCase().replace(" ", "_");
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

    public List<String> getCommands() {
        return commands;
    }

    public void addCommand(String command) {
        commands.add(command);
    }

    public void removeCommand(int index) {
        commands.remove(index);
    }

    public double getDeletionCost() {
        return deletionCost;
    }

    public void setDeletionCost(double deletionCost) {
        this.deletionCost = deletionCost;
    }
}
