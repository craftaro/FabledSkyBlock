package com.songoda.skyblock.listeners;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandEnvironment;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.version.Materials;
import com.songoda.skyblock.utils.version.Sounds;
import com.songoda.skyblock.utils.world.LocationUtil;
import com.songoda.skyblock.world.WorldManager;
import org.bukkit.Bukkit;
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

    private final SkyBlock skyblock;

    private Map<UUID, Tick> tickCounter = new HashMap<>();

    public Portal(SkyBlock skyblock) {
        this.skyblock = skyblock;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        org.bukkit.block.Block from = event.getFrom().getBlock();
        org.bukkit.block.Block to = event.getTo().getBlock();

        MessageManager messageManager = skyblock.getMessageManager();
        IslandManager islandManager = skyblock.getIslandManager();
        FileManager fileManager = skyblock.getFileManager();
        SoundManager soundManager = skyblock.getSoundManager();

        Island island = islandManager.getIslandAtLocation(to.getLocation());
        if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ())
            return;

        if (island == null) return;
        if ((to.getType().equals(Materials.NETHER_PORTAL.parseMaterial()) ||
                to.getType().equals(Materials.END_PORTAL.parseMaterial())) &&
                !islandManager.hasPermission(player, player.getLocation(), "Portal")) {
            event.setTo(LocationUtil.getRandomLocation(event.getFrom().getWorld(), 5000, 5000, true, true));
            messageManager.sendMessage(player,
                    fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration()
                            .getString("Island.Settings.Permission.Message"));
            soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityPortalEnter(EntityPortalEnterEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        org.bukkit.block.Block block = event.getLocation().getBlock();

        MessageManager messageManager = skyblock.getMessageManager();
        IslandManager islandManager = skyblock.getIslandManager();
        SoundManager soundManager = skyblock.getSoundManager();
        WorldManager worldManager = skyblock.getWorldManager();
        FileManager fileManager = skyblock.getFileManager();

        if (!worldManager.isIslandWorld(player.getWorld()))
            return;

        Island island = islandManager.getIslandAtLocation(player.getLocation());

        if (island == null) return;

        Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (!islandManager.hasPermission(player, player.getLocation(), "Portal")) {
            messageManager.sendMessage(player,
                    fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration()
                            .getString("Island.Settings.Permission.Message"));
            soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
            return;
        }

        IslandEnvironment spawnEnvironment;
        switch (island.getRole(player)) {
            case Operator:
            case Owner:
            case Member:
            case Coop:
                spawnEnvironment = IslandEnvironment.Island;
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
                messageManager.sendMessage(player,
                        fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration()
                                .getString("Island.Portal.Stuck.Message"));
                soundManager.playSound(player, Sounds.ENDERMAN_TELEPORT.bukkitSound(), 1.0F, 1.0F);
                LocationUtil.teleportPlayerToSpawn(player);
                return;
            }
        }

        if (tick == null) return;

        IslandWorld fromWorld = worldManager.getIslandWorld(player.getWorld());
        IslandWorld toWorld = IslandWorld.Normal;

        if (block.getType().equals(Materials.NETHER_PORTAL.parseMaterial()))
            toWorld = fromWorld.equals(IslandWorld.Normal) ? IslandWorld.Nether : IslandWorld.Normal;
        else if (block.getType().equals(Materials.END_PORTAL.parseMaterial()))
            toWorld = fromWorld.equals(IslandWorld.Normal) ? IslandWorld.End : IslandWorld.Normal;

        switch (toWorld) {
            case Nether:
                if (configLoad.getBoolean("Island.World.Nether.Enable") && island.isRegionUnlocked(player, "Nether")) {
                    IslandWorld toWorldF = toWorld;
                    Bukkit.getScheduler().scheduleSyncDelayedTask(skyblock, () -> player.teleport(island.getLocation(toWorldF, spawnEnvironment)), 1L);
                    soundManager.playSound(player, Sounds.ENDERMAN_TELEPORT.bukkitSound(), 1.0F, 1.0F);
                    player.setFallDistance(0.0F);
                    tick.setTick(1);
                }
                break;

            case End:
                if (configLoad.getBoolean("Island.World.End.Enable") && island.isRegionUnlocked(player, "End")) {
                    IslandWorld toWorldF = toWorld;
                    Bukkit.getScheduler().scheduleSyncDelayedTask(skyblock, () -> player.teleport(island.getLocation(toWorldF, spawnEnvironment)), 1L);
                    soundManager.playSound(player, Sounds.ENDERMAN_TELEPORT.bukkitSound(), 1.0F, 1.0F);
                    player.setFallDistance(0.0F);
                    tick.setTick(1);
                }
                break;

            default:
                IslandWorld toWorldF = toWorld;
                Bukkit.getScheduler().scheduleSyncDelayedTask(skyblock, () -> player.teleport(island.getLocation(toWorldF, spawnEnvironment)), 1L);
                soundManager.playSound(player, Sounds.ENDERMAN_TELEPORT.bukkitSound(), 1.0F, 1.0F);
                player.setFallDistance(0.0F);
                tick.setTick(1);
                break;
        }

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
