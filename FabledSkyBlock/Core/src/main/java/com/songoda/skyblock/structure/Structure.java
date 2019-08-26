package com.songoda.skyblock.structure;

import com.songoda.skyblock.utils.version.Materials;

import java.util.ArrayList;
import java.util.List;

public class Structure implements com.songoda.skyblock.api.structure.Structure {

    private Materials materials;

    private String name;
    private String overworldFile;
    private String netherFile;
    private String endFile;
    private String displayName;

    private boolean permission;

    private List<String> description = new ArrayList<>();
    private List<String> commands = new ArrayList<>();

    private double deletionCost;

    public Structure(String name, Materials materials, String overworldFile, String netherFile, String endFile,
                     String displayName, boolean permission, List<String> description, List<String> commands,
                     double deletionCost) {
        this.name = name;
        this.materials = materials;
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

    public Materials getMaterials() {
        return materials;
    }

    public void setMaterials(Materials materials) {
        this.materials = materials;
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
