package com.songoda.skyblock.visit;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.ban.Ban;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.IslandLevel;
import com.songoda.skyblock.island.IslandLocation;
import com.songoda.skyblock.island.IslandStatus;
import com.songoda.skyblock.island.IslandWorld;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Visit {

    private final SkyBlock plugin;
    private final IslandLevel islandLevel;
    private UUID islandOwnerUUID;
    private String islandOwnerName;
    private IslandLocation[] islandLocations;
    private int islandSize;
    private int islandMembers;
    private int safeLevel;
    private double islandBankBalance;
    private List<String> islandSignature;
    
    private IslandStatus status;

    protected Visit(SkyBlock plugin, UUID islandOwnerUUID, IslandLocation[] islandLocations, int islandSize,
                    int islandMembers, double islandBankBalance, int safeLevel, IslandLevel islandLevel, List<String> islandSignature, IslandStatus status) {
        this.plugin = plugin;
        this.islandOwnerUUID = islandOwnerUUID;
        this.islandLocations = islandLocations;
        this.islandSize = islandSize;
        this.islandMembers = islandMembers;
        this.islandBankBalance = islandBankBalance;
        this.safeLevel = safeLevel;
        this.islandLevel = islandLevel;
        this.islandSignature = islandSignature;
        this.status = status;
    }

    public UUID getOwnerUUID() {
        return islandOwnerUUID;
    }

    public void setOwnerUUID(UUID islandOwnerUUID) {
        this.islandOwnerUUID = islandOwnerUUID;
    }

    public String getOwnerName() {
        return islandOwnerName;
    }

    public void setOwnerName(String islandOwnerName) {
        this.islandOwnerName = islandOwnerName;
    }

    public IslandLocation getLocation(IslandWorld world) {
        switch (world) {
            case End:
                return islandLocations[2];
            case Nether:
                return islandLocations[1];
            case Normal:
                return islandLocations[0];
        }

        return null;
    }

    public int getMembers() {
        return islandMembers;
    }

    public void setMembers(int islandMembers) {
        this.islandMembers = islandMembers;
    }

    public int getSafeLevel() {
        return safeLevel;
    }

    public void setSafeLevel(int safeLevel) {
        this.safeLevel = safeLevel;
    }

    public int getRadius() {
        return islandSize;
    }

    public void setSize(int islandSize) {
        this.islandSize = islandSize;
    }

    public double getBankBalance() {
        return this.islandBankBalance;
    }

    public IslandLevel getLevel() {
        return islandLevel;
    }

    public boolean isVisitor(UUID uuid) {
        return getVisitors().contains(uuid);
    }

    public Set<UUID> getVisitors() {
        Set<UUID> islandVisitors = new HashSet<>();

        for (String islandVisitorList : plugin.getFileManager()
                .getConfig(new File(new File(plugin.getDataFolder().toString() + "/visit-data"),
                        islandOwnerUUID.toString() + ".yml"))
                .getFileConfiguration().getStringList("Visitors")) {
            islandVisitors.add(UUID.fromString(islandVisitorList));
        }

        return islandVisitors;
    }

    public void addVisitor(UUID uuid) {
        List<String> islandVisitors = new ArrayList<>();
        FileConfiguration configLoad = plugin.getFileManager()
                .getConfig(new File(new File(plugin.getDataFolder().toString() + "/visit-data"),
                        islandOwnerUUID.toString() + ".yml"))
                .getFileConfiguration();

        for (String islandVisitorList : configLoad.getStringList("Visitors")) {
            islandVisitors.add(islandVisitorList);
        }

        islandVisitors.add(uuid.toString());
        configLoad.set("Visitors", islandVisitors);
    }

    public void removeVisitor(UUID uuid) {
        List<String> islandVisitors = new ArrayList<>();
        FileConfiguration configLoad = plugin.getFileManager()
                .getConfig(new File(new File(plugin.getDataFolder().toString() + "/visit-data"),
                        islandOwnerUUID.toString() + ".yml"))
                .getFileConfiguration();

        for (String islandVisitorList : configLoad.getStringList("Visitors")) {
            islandVisitors.add(islandVisitorList);
        }

        islandVisitors.remove(uuid.toString());
        configLoad.set("Visitors", islandVisitors);
    }

    public boolean isVoter(UUID uuid) {
        return getVoters().contains(uuid);
    }

    public Set<UUID> getVoters() {
        Set<UUID> islandVoters = new HashSet<>();

        for (String islandVisitorList : plugin.getFileManager()
                .getConfig(new File(new File(plugin.getDataFolder().toString() + "/visit-data"),
                        islandOwnerUUID.toString() + ".yml"))
                .getFileConfiguration().getStringList("Voters")) {
            islandVoters.add(UUID.fromString(islandVisitorList));
        }

        return islandVoters;
    }

    public void addVoter(UUID uuid) {
        List<String> islandVoters = new ArrayList<>();
        FileConfiguration configLoad = plugin.getFileManager()
                .getConfig(new File(new File(plugin.getDataFolder().toString() + "/visit-data"),
                        islandOwnerUUID.toString() + ".yml"))
                .getFileConfiguration();

        islandVoters.addAll(configLoad.getStringList("Voters"));

        islandVoters.add(uuid.toString());
        configLoad.set("Voters", islandVoters);
    }

    public void removeVoter(UUID uuid) {
        List<String> islandVoters = new ArrayList<>();
        FileConfiguration configLoad = plugin.getFileManager()
                .getConfig(new File(new File(plugin.getDataFolder().toString() + "/visit-data"),
                        islandOwnerUUID.toString() + ".yml"))
                .getFileConfiguration();

        for (String islandVoterList : configLoad.getStringList("Voters")) {
            if (!uuid.toString().equals(islandVoterList)) {
                islandVoters.add(islandVoterList);
            }
        }

        configLoad.set("Voters", islandVoters);
    }

    public List<String> getSiganture() {
        return islandSignature;
    }

    public void setSignature(List<String> islandSignature) {
        this.islandSignature = islandSignature;
    }

    public Ban getBan() {
        return plugin.getBanManager().getIsland(getOwnerUUID());
    }

    public void save() {
        FileManager.Config config = plugin.getFileManager().getConfig(new File(
                new File(plugin.getDataFolder().toString() + "/visit-data"), islandOwnerUUID.toString() + ".yml"));

        try {
            config.getFileConfiguration().save(config.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public IslandStatus getStatus() {
        return status;
    }
    
    public void setStatus(IslandStatus status) {
        this.status = status;
    }
}
