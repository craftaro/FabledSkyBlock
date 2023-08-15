package com.craftaro.skyblock.visit;

import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.ban.Ban;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.island.IslandLevel;
import com.craftaro.skyblock.island.IslandLocation;
import com.craftaro.skyblock.island.IslandStatus;
import com.craftaro.skyblock.island.IslandWorld;
import com.eatthepath.uuid.FastUUID;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Visit {
    private final SkyBlock plugin;
    private final IslandLevel islandLevel;
    private UUID islandOwnerUUID;
    private String islandOwnerName;
    private final IslandLocation[] islandLocations;
    private int islandSize;
    private int islandMembers;
    private int safeLevel;
    private final double islandBankBalance;
    private List<String> islandSignature;
    private final Set<UUID> islandVisitors;

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
        this.islandVisitors = new HashSet<>();

        FileConfiguration configLoad = plugin.getFileManager()
                .getConfig(new File(new File(plugin.getDataFolder(), "visit-data"),
                        islandOwnerUUID.toString() + ".yml"))
                .getFileConfiguration();

        for (String visitor : configLoad.getStringList("Visitors")) {
            this.islandVisitors.add(FastUUID.parseUUID(visitor));
        }
    }

    public UUID getOwnerUUID() {
        return this.islandOwnerUUID;
    }

    public void setOwnerUUID(UUID islandOwnerUUID) {
        this.islandOwnerUUID = islandOwnerUUID;
    }

    public String getOwnerName() {
        return this.islandOwnerName;
    }

    public void setOwnerName(String islandOwnerName) {
        this.islandOwnerName = islandOwnerName;
    }

    public IslandLocation getLocation(IslandWorld world) {
        switch (world) {
            case END:
                return this.islandLocations[2];
            case NETHER:
                return this.islandLocations[1];
            case NORMAL:
                return this.islandLocations[0];
        }

        return null;
    }

    public int getMembers() {
        return this.islandMembers;
    }

    public void setMembers(int islandMembers) {
        this.islandMembers = islandMembers;
    }

    public int getSafeLevel() {
        return this.safeLevel;
    }

    public void setSafeLevel(int safeLevel) {
        this.safeLevel = safeLevel;
    }

    public int getRadius() {
        return this.islandSize;
    }

    public void setSize(int islandSize) {
        this.islandSize = islandSize;
    }

    public double getBankBalance() {
        return this.islandBankBalance;
    }

    public IslandLevel getLevel() {
        return this.islandLevel;
    }

    public boolean isVisitor(UUID uuid) {
        return getVisitors().contains(uuid);
    }

    public Set<UUID> getVisitors() {
        return this.islandVisitors;
    }

    public void addVisitor(UUID uuid) {
        this.islandVisitors.add(uuid);
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, this::save);
    }

    public void removeVisitor(UUID uuid) {
        this.islandVisitors.remove(uuid);
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, this::save);
    }

    public boolean isVoter(UUID uuid) {
        return getVoters().contains(uuid);
    }

    public Set<UUID> getVoters() {
        Set<UUID> islandVoters = new HashSet<>();

        for (String islandVisitorList : this.plugin.getFileManager()
                .getConfig(new File(new File(this.plugin.getDataFolder(), "/visit-data"),
                        this.islandOwnerUUID.toString() + ".yml"))
                .getFileConfiguration().getStringList("Voters")) {
            islandVoters.add(FastUUID.parseUUID(islandVisitorList));
        }

        return islandVoters;
    }

    public void addVoter(UUID uuid) {
        FileConfiguration configLoad = this.plugin.getFileManager()
                .getConfig(new File(new File(this.plugin.getDataFolder(), "visit-data"),
                        this.islandOwnerUUID.toString() + ".yml"))
                .getFileConfiguration();

        List<String> islandVoters = new ArrayList<>(configLoad.getStringList("Voters"));

        islandVoters.add(FastUUID.toString(uuid));
        configLoad.set("Voters", islandVoters);
    }

    public void removeVoter(UUID uuid) {
        List<String> islandVoters = new ArrayList<>();
        FileConfiguration configLoad = this.plugin.getFileManager()
                .getConfig(new File(new File(this.plugin.getDataFolder().toString() + "/visit-data"),
                        this.islandOwnerUUID.toString() + ".yml"))
                .getFileConfiguration();

        for (String islandVoterList : configLoad.getStringList("Voters")) {
            if (!FastUUID.toString(uuid).equals(islandVoterList)) {
                islandVoters.add(islandVoterList);
            }
        }

        configLoad.set("Voters", islandVoters);
    }

    /**
     * @deprecated Use {@link #getSignature()} instead.
     */
    @Deprecated
    public List<String> getSiganture() {
        return this.islandSignature;
    }

    public List<String> getSignature() {
        return this.islandSignature;
    }

    public void setSignature(List<String> islandSignature) {
        this.islandSignature = islandSignature;
    }

    public Ban getBan() {
        return this.plugin.getBanManager().getIsland(getOwnerUUID());
    }

    public synchronized void save() {
        FileManager.Config config = this.plugin.getFileManager().getConfig(new File(
                new File(this.plugin.getDataFolder(), "visit-data"), this.islandOwnerUUID.toString() + ".yml"));

        config.getFileConfiguration().set("Visitors", new ArrayList<>(this.islandVisitors.stream().map(UUID::toString).collect(Collectors.toSet())));

        try {
            config.getFileConfiguration().save(config.getFile());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public IslandStatus getStatus() {
        return this.status;
    }

    public void setStatus(IslandStatus status) {
        this.status = status;
    }
}
