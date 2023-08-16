package com.craftaro.skyblock.listeners;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandEnvironment;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.island.IslandWorld;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.permission.event.events.PlayerEnterPortalEvent;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.utils.world.LocationUtil;
import com.craftaro.skyblock.world.WorldManager;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PortalListeners implements Listener {
    private final SkyBlock plugin;

    private final Map<UUID, Tick> tickCounter = new HashMap<>();

    public PortalListeners(SkyBlock plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        org.bukkit.block.Block from = event.getFrom().getBlock();
        org.bukkit.block.Block to = event.getTo().getBlock();

        if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) {
            return;
        }

        IslandManager islandManager = this.plugin.getIslandManager();
        Island island = islandManager.getIslandAtLocation(to.getLocation());
        if (island == null) {
            return;
        }

        // Check permissions.
        this.plugin.getPermissionManager().processPermission(event, player,
                islandManager.getIslandAtLocation(event.getTo()));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityPortalEnter(EntityPortalEnterEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        org.bukkit.block.Block block = event.getLocation().getBlock();

        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        WorldManager worldManager = this.plugin.getWorldManager();

        if (!worldManager.isIslandWorld(player.getWorld())) {
            return;
        }

        Island island = islandManager.getIslandAtLocation(player.getLocation());

        if (island == null) {
            return;
        }

        FileConfiguration configLoad = this.plugin.getConfiguration();

        IslandEnvironment spawnEnvironment;
        switch (island.getRole(player)) {
            case OPERATOR:
            case OWNER:
            case MEMBER:
            case COOP:
                spawnEnvironment = IslandEnvironment.MAIN;
                break;

            default:
                spawnEnvironment = IslandEnvironment.VISITOR;
        }

        Tick tick;
        if (!this.tickCounter.containsKey(player.getUniqueId())) {
            tick = this.tickCounter.put(player.getUniqueId(), new Tick());
        } else {
            tick = this.tickCounter.get(player.getUniqueId());

            tick.setTick(tick.getTick() + 1);

            if (System.currentTimeMillis() - tick.getLast() < 1000) {
                return;
            } else if (System.currentTimeMillis() - tick.getLast() >= 1000) {
                tick.setLast(System.currentTimeMillis());
            }
            if (tick.getTick() >= 100) {
                messageManager.sendMessage(player, this.plugin.getLanguage().getString("Island.Portal.Stuck.Message"));
                soundManager.playSound(player, XSound.ENTITY_ENDERMAN_TELEPORT);
                LocationUtil.teleportPlayerToSpawn(player);
                return;
            }
        }

        if (tick == null) {
            return;
        }

        PlayerEnterPortalEvent playerEnterPortalEvent = new PlayerEnterPortalEvent(player, player.getLocation());
        // Check permissions.
        boolean perms = !this.plugin.getPermissionManager().processPermission(playerEnterPortalEvent,
                player, island);

        IslandWorld fromWorld = worldManager.getIslandWorld(player.getWorld());
        IslandWorld toWorld = IslandWorld.NORMAL;

        if (block.getType() == XMaterial.NETHER_PORTAL.parseMaterial()) {
            toWorld = fromWorld == IslandWorld.NETHER ? IslandWorld.NORMAL : IslandWorld.NETHER;
        } else if (block.getType() == XMaterial.END_PORTAL.parseMaterial()) {
            toWorld = fromWorld == IslandWorld.END ? IslandWorld.NORMAL : IslandWorld.END;
        }

        if (!perms) {
            switch (toWorld) {
                case END:
                case NETHER:
                    if (configLoad.getBoolean("Island.World." + toWorld.getFriendlyName() + ".Enable") && island.isRegionUnlocked(player, toWorld)) {
                        teleportPlayerToWorld(player, soundManager, island, spawnEnvironment, tick, toWorld);
                    }
                    break;

                default:
                    IslandWorld toWorldF = toWorld;

                    Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> PaperLib.teleportAsync(player, island.getLocation(toWorldF, spawnEnvironment)), 1L);
                    soundManager.playSound(player, XSound.ENTITY_ENDERMAN_TELEPORT);
                    player.setFallDistance(0.0F);
                    tick.setTick(1);
                    break;
            }
        } else {
            if (toWorld == IslandWorld.END) {
                player.setVelocity(player.getLocation().getDirection().multiply(-.50).setY(.6f));
            } else if (toWorld == IslandWorld.NETHER) {
                player.setVelocity(player.getLocation().getDirection().multiply(-.50));
            }
        }

    }

    private void teleportPlayerToWorld(Player player, SoundManager soundManager, Island island, IslandEnvironment spawnEnvironment, Tick tick, IslandWorld toWorld) {
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            Location loc = island.getLocation(toWorld, spawnEnvironment);
            if (this.plugin.getConfiguration().getBoolean("Island.Teleport.SafetyCheck", true)) {
                Location safeLoc = LocationUtil.getSafeLocation(loc);
                if (safeLoc != null) {
                    loc = safeLoc;
                }
            }
            Location finalLoc = loc;
            PaperLib.teleportAsync(player, finalLoc);
        }, 1L);
        soundManager.playSound(player, XSound.ENTITY_ENDERMAN_TELEPORT);
        player.setFallDistance(0.0F);
        tick.setTick(1);
    }

    public static class Tick {
        private int tick = 1;
        private long last = System.currentTimeMillis() - 1001;

        public int getTick() {
            if (System.currentTimeMillis() - this.last >= 1500) {
                this.tick = 0;
            }
            return this.tick;
        }

        public void setTick(int tick) {
            this.tick = tick;
        }

        public long getLast() {
            return this.last;
        }

        public void setLast(long last) {
            this.last = last;
        }
    }
}
