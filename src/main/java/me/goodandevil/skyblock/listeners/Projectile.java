package me.goodandevil.skyblock.listeners;

import java.io.File;

import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.island.Location;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.version.Sounds;

public class Projectile implements Listener {

	private final SkyBlock skyblock;

	public Projectile(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}

	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		if (event.getEntity().getShooter() instanceof Player) {
			Player player = (Player) event.getEntity().getShooter();

			MessageManager messageManager = skyblock.getMessageManager();
			SoundManager soundManager = skyblock.getSoundManager();

			if (player.getWorld().getName().equals(skyblock.getWorldManager().getWorld(Location.World.Normal).getName())
					|| player.getWorld().getName()
							.equals(skyblock.getWorldManager().getWorld(Location.World.Nether).getName())) {
				if (event.getEntity() instanceof FishHook) {
					if (!skyblock.getIslandManager().hasPermission(player, "Fishing")) {
						event.setCancelled(true);

						messageManager.sendMessage(player,
								skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"))
										.getFileConfiguration().getString("Island.Settings.Permission.Message"));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
					}

					return;
				}

				if (!skyblock.getIslandManager().hasPermission(player, "Projectile")) {
					event.setCancelled(true);

					messageManager.sendMessage(player,
							skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"))
									.getFileConfiguration().getString("Island.Settings.Permission.Message"));
					soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
				}
			}
		}
	}
}
