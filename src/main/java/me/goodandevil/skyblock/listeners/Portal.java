package me.goodandevil.skyblock.listeners;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandEnvironment;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.IslandWorld;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.utils.world.LocationUtil;
import me.goodandevil.skyblock.world.WorldManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.io.File;

public class Portal implements Listener {

    private final SkyBlock skyblock;

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

    @EventHandler
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

        if (!worldManager.isIslandWorld(player.getWorld())) {
            return;
        }

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

        IslandWorld fromWorld = worldManager.getIslandWorld(player.getWorld());
        IslandWorld toWorld = IslandWorld.Normal;

        if (block.getType().equals(Materials.NETHER_PORTAL.parseMaterial()))
            toWorld = fromWorld.equals(IslandWorld.Normal) ? IslandWorld.Nether : IslandWorld.Normal;
        else if (block.getType().equals(Materials.END_PORTAL.parseMaterial()))
            toWorld = fromWorld.equals(IslandWorld.Normal) ? IslandWorld.End : IslandWorld.Normal;

        switch (toWorld) {
            case Nether:
                if (configLoad.getBoolean("Island.World.Nether.Enable") && island.isRegionUnlocked(player, "Nether")) {
                        player.teleport(island.getLocation(toWorld, spawnEnvironment));
                        soundManager.playSound(player, Sounds.ENDERMAN_TELEPORT.bukkitSound(), 1.0F, 1.0F);
                        player.setFallDistance(0.0F);
                }
                break;

            case End:
                if (configLoad.getBoolean("Island.World.End.Enable") && island.isRegionUnlocked(player, "End")) {
                    player.teleport(island.getLocation(toWorld, spawnEnvironment));
                    soundManager.playSound(player, Sounds.ENDERMAN_TELEPORT.bukkitSound(), 1.0F, 1.0F);
                    player.setFallDistance(0.0F);
                }
                break;

            default:
                player.teleport(island.getLocation(toWorld, spawnEnvironment));
                soundManager.playSound(player, Sounds.ENDERMAN_TELEPORT.bukkitSound(), 1.0F, 1.0F);
                player.setFallDistance(0.0F);
                break;
        }

    }
}
