package me.goodandevil.skyblock.listeners;

import java.io.File;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketFillEvent;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.island.Location;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.Sounds;

public class Bucket implements Listener {

	private final SkyBlock skyblock;
	
 	public Bucket(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}
	
	@EventHandler
	public void onPlayerBucketFill(PlayerBucketFillEvent event) {
		Player player = event.getPlayer();

		if (event.getBlockClicked().getType() == Material.WATER || event.getBlockClicked().getType() == Materials.LEGACY_STATIONARY_WATER.getPostMaterial() || event.getBlockClicked().getType() == Material.LAVA || event.getBlockClicked().getType() == Materials.LEGACY_STATIONARY_LAVA.getPostMaterial()) {
			if (player.getWorld().getName().equals(skyblock.getWorldManager().getWorld(Location.World.Normal).getName()) || player.getWorld().getName().equals(skyblock.getWorldManager().getWorld(Location.World.Nether).getName())) {
				if (!skyblock.getIslandManager().hasPermission(player, "Bucket")) {
					event.setCancelled(true);
					
					skyblock.getMessageManager().sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
					skyblock.getSoundManager().playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
				}
			}
		}
	}
}
