package com.craftaro.skyblock.structure;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;

import java.util.List;

public class Structure implements com.craftaro.skyblock.api.structure.Structure {
    private XMaterial material;

    private final String name;
    private String overworldFile;
    private String netherFile;
    private String endFile;
    private String displayName;

    private boolean permission;

    private final List<String> description;
    private final List<String> commands;

    private double deletionCost;

    public Structure(String name, XMaterial material, String overworldFile, String netherFile, String endFile,
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
        return this.name;
    }

    public XMaterial getMaterial() {
        return this.material;
    }

    public void setMaterial(XMaterial material) {
        this.material = material;
    }

    public String getOverworldFile() {
        return this.overworldFile;
    }

    public void setOverworldFile(String file) {
        this.overworldFile = file;
    }

    public String getNetherFile() {
        return this.netherFile;
    }

    public void setNetherFile(String file) {
        this.netherFile = file;
    }

    public String getEndFile() {
        return this.endFile;
    }

    public void setEndFile(String file) {
        this.endFile = file;
    }

    public String getDisplayname() {
        return this.displayName;
    }

    public void setDisplayname(String displayName) {
        this.displayName = displayName;
    }

    public boolean isPermission() {
        return this.permission;
    }

    public String getPermission() {
        return "fabledskyblock.island." + this.name.toLowerCase().replace(" ", "_");
    }

    public void setPermission(boolean permission) {
        this.permission = permission;
    }

    public List<String> getDescription() {
        return this.description;
    }

    public void addLine(String line) {
        this.description.add(line);
    }

    public void removeLine(int index) {
        this.description.remove(index);
    }

    public List<String> getCommands() {
        return this.commands;
    }

    public void addCommand(String command) {
        this.commands.add(command);
    }

    public void removeCommand(int index) {
        this.commands.remove(index);
    }

    public double getDeletionCost() {
        return this.deletionCost;
    }

    public void setDeletionCost(double deletionCost) {
        this.deletionCost = deletionCost;
    }
}
