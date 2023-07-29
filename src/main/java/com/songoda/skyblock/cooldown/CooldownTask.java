package com.songoda.skyblock.cooldown;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class CooldownTask extends BukkitRunnable {
    private final CooldownManager cooldownManager;

    public CooldownTask(CooldownManager cooldownManager) {
        this.cooldownManager = cooldownManager;
    }

    @Override
    public void run() {
        for (CooldownType cooldownType : CooldownType.getTypes()) {
            List<CooldownPlayer> cooldownPlayers = this.cooldownManager.getCooldownPlayers(cooldownType);

            if (cooldownPlayers == null) return;

            for (CooldownPlayer cooldownPlayer : new ArrayList<>(cooldownPlayers)) {
                Cooldown cooldown = cooldownPlayer.getCooldown();

                cooldown.setTime(cooldown.getTime() - 1);

                if (cooldown.getTime() <= 0) {
                    this.cooldownManager.deletePlayer(cooldownType, Bukkit.getServer().getOfflinePlayer(cooldownPlayer.getUUID()));
                }
            }
        }
    }
}
