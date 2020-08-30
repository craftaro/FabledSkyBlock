package com.songoda.skyblock.listeners;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandEnvironment;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.event.events.PlayerEnterPortalEvent;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.world.LocationUtil;
import com.songoda.skyblock.world.WorldManager;
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

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Portal implements Listener {

    private final SkyBlock plugin;

    private final Map<UUID, Tick> tickCounter = new HashMap<>();

    public Portal(SkyBlock plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        org.bukkit.block.Block from = event.getFrom().getBlock();
        org.bukkit.block.Block to = event.getTo().getBlock();

        IslandManager islandManager = plugin.getIslandManager();

        if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) return;

        Island island = islandManager.getIslandAtLocation(to.getLocation());

        if (island == null) return;

        // Check permissions.
        plugin.getPermissionManager().processPermission(event, player,
                islandManager.getIslandAtLocation(event.getTo()));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityPortalEnter(EntityPortalEnterEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        org.bukkit.block.Block block = event.getLocation().getBlock();

        MessageManager messageManager = plugin.getMessageManager();
        IslandManager islandManager = plugin.getIslandManager();
        SoundManager soundManager = plugin.getSoundManager();
        WorldManager worldManager = plugin.getWorldManager();
        FileManager fileManager = plugin.getFileManager();

        if (!worldManager.isIslandWorld(player.getWorld())) return;

        Island island = islandManager.getIslandAtLocation(player.getLocation());

        if (island == null) return;

        Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        IslandEnvironment spawnEnvironment;
        switch (island.getRole(player)) {
            case Operator:
            case Owner:
            case Member:
            case Coop:
                spawnEnvironment = IslandEnvironment.Main;
                break;

            default:
                spawnEnvironment = IslandEnvironment.Visitor;
        }

        Tick tick;
        if (!tickCounter.containsKey(player.getUniqueId())) {
            tick = tickCounter.put(player.getUniqueId(), new Tick());
        } else {
            tick = tickCounter.get(player.getUniqueId());

            tick.setTick(tick.getTick() + 1);

            if (System.currentTimeMillis() - tick.getLast() < 1000) {
                return;
            } else if (System.currentTimeMillis() - tick.getLast() >= 1000) {
                tick.setLast(System.currentTimeMillis());
            }
            if (tick.getTick() >= 100) {
                messageManager.sendMessage(player, fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Portal.Stuck.Message"));
                soundManager.playSound(player, CompatibleSound.ENTITY_ENDERMAN_TELEPORT.getSound(), 1.0F, 1.0F);
                LocationUtil.teleportPlayerToSpawn(player);
                return;
            }
        }

        if (tick == null) return;

        PlayerEnterPortalEvent playerEnterPortalEvent = new PlayerEnterPortalEvent(player, player.getLocation());
        // Check permissions.
        boolean perms = !plugin.getPermissionManager().processPermission(playerEnterPortalEvent,
                player, island);

        IslandWorld fromWorld = worldManager.getIslandWorld(player.getWorld());
        IslandWorld toWorld = IslandWorld.Normal;

        if (block.getType().equals(CompatibleMaterial.NETHER_PORTAL.getMaterial())) {
            toWorld = fromWorld.equals(IslandWorld.Normal) ? IslandWorld.Nether : IslandWorld.Normal;
        } else if (block.getType().equals(CompatibleMaterial.END_PORTAL.getMaterial())) {
            toWorld = fromWorld.equals(IslandWorld.Normal) ? IslandWorld.End : IslandWorld.Normal;
        }

        if(!perms){
            switch (toWorld) {
                case End:
                case Nether:
                    if (configLoad.getBoolean("Island.World." + toWorld.name() + ".Enable") && island.isRegionUnlocked(player, toWorld)) {
                        teleportPlayerToWorld(player, soundManager, island, spawnEnvironment, tick, toWorld);
                    }
                    break;

                default:
                    IslandWorld toWorldF = toWorld;

                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> PaperLib.teleportAsync(player, island.getLocation(toWorldF, spawnEnvironment)), 1L);
                    soundManager.playSound(player, CompatibleSound.ENTITY_ENDERMAN_TELEPORT.getSound(), 1.0F, 1.0F);
                    player.setFallDistance(0.0F);
                    tick.setTick(1);
                    break;
            }
        } else {
            if(toWorld.equals(IslandWorld.End)){
                player.setVelocity(player.getLocation().getDirection().multiply(-.50).setY(.6f));
            } else if(toWorld.equals(IslandWorld.Nether)) {
                player.setVelocity(player.getLocation().getDirection().multiply(-.50));
            }
        }

    }

    private void teleportPlayerToWorld(Player player, SoundManager soundManager, Island island, IslandEnvironment spawnEnvironment, Tick tick, IslandWorld toWorld) {
        IslandWorld toWorldF = toWorld;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Location loc = island.getLocation(toWorldF, spawnEnvironment);
            if(plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"))
                    .getFileConfiguration().getBoolean("Island.Teleport.SafetyCheck", true)) {
                Location safeLoc = LocationUtil.getSafeLocation(loc);
                if (safeLoc != null) {
                    loc = safeLoc;
                }
            }
            Location finalLoc = loc;
            PaperLib.teleportAsync(player, finalLoc);
        }, 1L);
        soundManager.playSound(player, CompatibleSound.ENTITY_ENDERMAN_TELEPORT.getSound(), 1.0F, 1.0F);
        player.setFallDistance(0.0F);
        tick.setTick(1);
    }

    public static class Tick {
        private int tick = 1;
        private long last = System.currentTimeMillis() - 1001;

        public int getTick() {
            if (System.currentTimeMillis() - last >= 1500) tick = 0;
            return tick;
        }

        public void setTick(int tick) {
            this.tick = tick;
        }

        public long getLast() {
            return last;
        }

        public void setLast(long last) {
            this.last = last;
        }
    }
}
