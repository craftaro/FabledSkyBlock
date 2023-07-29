package com.songoda.skyblock.invite;

import org.bukkit.entity.Player;

import java.util.UUID;

public class Invite {
    private final UUID playerUUID;
    private final UUID senderUUID;
    private UUID islandOwnerUUID;
    private final String senderName;
    private int time;

    public Invite(Player player, Player sender, UUID islandOwnerUUID, int time) {
        this.playerUUID = player.getUniqueId();
        this.senderUUID = sender.getUniqueId();
        this.senderName = sender.getName();
        this.islandOwnerUUID = islandOwnerUUID;
        this.time = time;
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public UUID getSenderUUID() {
        return this.senderUUID;
    }

    public UUID getOwnerUUID() {
        return this.islandOwnerUUID;
    }

    public void setOwnerUUID(UUID islandOwnerUUID) {
        this.islandOwnerUUID = islandOwnerUUID;
    }

    public String getSenderName() {
        return this.senderName;
    }

    public int getTime() {
        return this.time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
