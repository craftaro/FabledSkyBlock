package me.goodandevil.skyblock.listeners;

import java.io.File;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketFillEvent;

import me.goodandevil.skyblock.SkyBlock;
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
}
