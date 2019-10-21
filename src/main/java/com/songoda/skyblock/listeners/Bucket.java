package com.songoda.skyblock.listeners;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.utils.version.Materials;
import com.songoda.skyblock.utils.version.Sounds;
import com.songoda.skyblock.utils.world.LocationUtil;
import com.songoda.skyblock.world.WorldManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

import java.io.File;

public class Bucket implements Listener {

    private final SkyBlock skyblock;

    public Bucket(SkyBlock skyblock) {
        this.skyblock = skyblock;
    }

    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        org.bukkit.block.Block block = event.getBlockClicked();

        if (event.getBlockClicked().getType() == Material.WATER
                || event.getBlockClicked().getType() == Materials.LEGACY_STATIONARY_WATER.getPostMaterial()
                || event.getBlockClicked().getType() == Material.LAVA
                || event.getBlockClicked().getType() == Materials.LEGACY_STATIONARY_LAVA.getPostMaterial()) {
            if (skyblock.getWorldManager().isIslandWorld(block.getWorld())) {
                if (!skyblock.getIslandManager().hasPermission(player, block.getLocation(), "Bucket")) {
                    event.setCancelled(true);

                    skyblock.getMessageManager().sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"))
                                    .getFileConfiguration().getString("Island.Settings.Permission.Message"));
                    skyblock.getSoundManager().playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        org.bukkit.block.Block block = event.getBlockClicked().getRelative(event.getBlockFace());

        if (skyblock.getWorldManager().isIslandWorld(block.getWorld())) {
            if (!skyblock.getIslandManager().hasPermission(player, block.getLocation(), "Bucket")) {
                event.setCancelled(true);

                skyblock.getMessageManager().sendMessage(player,
                        skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"))
                                .getFileConfiguration().getString("Island.Settings.Permission.Message"));
                skyblock.getSoundManager().playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
            }
        }

        WorldManager worldManager = skyblock.getWorldManager();
        IslandManager islandManager = skyblock.getIslandManager();

        if (!skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Spawn.Protection"))
            return;

        Island island = islandManager.getIslandAtLocation(block.getLocation());
        if (island == null)
            return;

        // Check spawn block protection
        IslandWorld world = worldManager.getIslandWorld(block.getWorld());
        if (LocationUtil.isLocationAffectingIslandSpawn(block.getLocation(), island, world)) {
            event.setCancelled(true);
            skyblock.getMessageManager().sendMessage(player,
                    skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"))
                            .getFileConfiguration().getString("Island.SpawnProtection.Place.Message"));
            skyblock.getSoundManager().playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
        }
    }
}
