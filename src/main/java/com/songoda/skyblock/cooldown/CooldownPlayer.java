package com.songoda.skyblock.cooldown;

import java.util.UUID;

public class CooldownPlayer {
    private UUID uuid;
    private Cooldown cooldown;

    public CooldownPlayer(UUID uuid, Cooldown cooldown) {
        this.uuid = uuid;
        this.cooldown = cooldown;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public Cooldown getCooldown() {
        return this.cooldown;
    }

    public void setCooldown(Cooldown cooldown) {
        this.cooldown = cooldown;
    }
}
