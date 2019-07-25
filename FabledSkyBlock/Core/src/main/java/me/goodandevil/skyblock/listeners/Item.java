package me.goodandevil.skyblock.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import me.goodandevil.skyblock.SkyBlock;

@SuppressWarnings("deprecation")
public class Item implements Listener {

	private final SkyBlock skyblock;

	public Item(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();

		if (skyblock.getWorldManager().isIslandWorld(player.getWorld())) {
			if (!skyblock.getIslandManager().hasPermission(player, "ItemDrop")) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();

		if (skyblock.getWorldManager().isIslandWorld(player.getWorld())) {
			if (!skyblock.getIslandManager().hasPermission(player, "ItemPickup")) {
				event.setCancelled(true);
			}
		}
	}
}
