package me.goodandevil.skyblock.listeners;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.api.SkyBlockAPI;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.IslandRole;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.scoreboard.Scoreboard;
import me.goodandevil.skyblock.scoreboard.ScoreboardManager;

public class Join implements Listener {

	private final SkyBlock skyblock;

	public Join(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		IslandManager islandManager = skyblock.getIslandManager();

		try {
			islandManager.loadIsland(player.getUniqueId());
			islandManager.loadPlayer(player);
		} catch (Exception e) {
			e.printStackTrace();
		}

		PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
		playerDataManager.loadPlayerData(player);

		if (playerDataManager.hasPlayerData(player)) {
			String[] playerTexture;

			try {
				Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
				Method getProfileMethod = entityPlayer.getClass().getMethod("getProfile", new Class<?>[0]);
				GameProfile gameProfile = (GameProfile) getProfileMethod.invoke(entityPlayer);
				Property property = gameProfile.getProperties().get("textures").iterator().next();
				playerTexture = new String[] { property.getSignature(), property.getValue() };
			} catch (Exception e) {
				playerTexture = new String[] {
						"K9P4tCIENYbNpDuEuuY0shs1x7iIvwXi4jUUVsATJfwsAIZGS+9OZ5T2HB0tWBoxRvZNi73Vr+syRdvTLUWPusVXIg+2fhXmQoaNEtnQvQVGQpjdQP0TkZtYG8PbvRxE6Z75ddq+DVx/65OSNHLWIB/D+Rg4vINh4ukXNYttn9QvauDHh1aW7/IkIb1Bc0tLcQyqxZQ3mdglxJfgIerqnlA++Lt7TxaLdag4y1NhdZyd3OhklF5B0+B9zw/qP8QCzsZU7VzJIcds1+wDWKiMUO7+60OSrIwgE9FPamxOQDFoDvz5BOULQEeNx7iFMB+eBYsapCXpZx0zf1bduppBUbbVC9wVhto/J4tc0iNyUq06/esHUUB5MHzdJ0Y6IZJAD/xIw15OLCUH2ntvs8V9/cy5/n8u3JqPUM2zhUGeQ2p9FubUGk4Q928L56l3omRpKV+5QYTrvF+AxFkuj2hcfGQG3VE2iYZO6omXe7nRPpbJlHkMKhE8Xvd1HP4PKpgivSkHBoZ92QEUAmRzZydJkp8CNomQrZJf+MtPiNsl/Q5RQM+8CQThg3+4uWptUfP5dDFWOgTnMdA0nIODyrjpp+bvIJnsohraIKJ7ZDnj4tIp4ObTNKDFC/8j8JHz4VCrtr45mbnzvB2DcK8EIB3JYT7ElJTHnc5BKMyLy5SKzuw=",
						"eyJ0aW1lc3RhbXAiOjE1MjkyNTg0MTE4NDksInByb2ZpbGVJZCI6Ijg2NjdiYTcxYjg1YTQwMDRhZjU0NDU3YTk3MzRlZWQ3IiwicHJvZmlsZU5hbWUiOiJTdGV2ZSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGMxYzc3Y2U4ZTU0OTI1YWI1ODEyNTQ0NmVjNTNiMGNkZDNkMGNhM2RiMjczZWI5MDhkNTQ4Mjc4N2VmNDAxNiJ9LCJDQVBFIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjc2N2Q0ODMyNWVhNTMyNDU2MTQwNmI4YzgyYWJiZDRlMjc1NWYxMTE1M2NkODVhYjA1NDVjYzIifX19" };
			}

			PlayerData playerData = playerDataManager.getPlayerData(player);
			playerData.setTexture(playerTexture[0], playerTexture[1]);
			playerData.save();
		} else {
			playerDataManager.createPlayerData(player);
			playerDataManager.loadPlayerData(player);
		}

		playerDataManager.storeIsland(player);

		skyblock.getBiomeManager().loadPlayer(player);
		skyblock.getCreationManager().loadPlayer(player);

		ScoreboardManager scoreboardManager = skyblock.getScoreboardManager();
		FileManager fileManager = skyblock.getFileManager();

		if (scoreboardManager != null) {
			Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
			Scoreboard scoreboard = new Scoreboard(player);

			if (islandManager.hasIsland(player)) {
				Island island = islandManager.getIsland(playerDataManager.getPlayerData(player).getOwner());

				skyblock.getLevellingManager().loadLevelling(island.getOwnerUUID());

				if (island.getRole(IslandRole.Member).size() == 0 && island.getRole(IslandRole.Operator).size() == 0) {
					scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&',
							config.getFileConfiguration().getString("Scoreboard.Island.Solo.Displayname")));

					if (islandManager.getVisitorsAtIsland(island).size() == 0) {
						scoreboard.setDisplayList(config.getFileConfiguration()
								.getStringList("Scoreboard.Island.Solo.Empty.Displaylines"));
					} else {
						scoreboard.setDisplayList(config.getFileConfiguration()
								.getStringList("Scoreboard.Island.Solo.Occupied.Displaylines"));
					}
				} else {
					scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&',
							config.getFileConfiguration().getString("Scoreboard.Island.Team.Displayname")));

					if (islandManager.getVisitorsAtIsland(island).size() == 0) {
						scoreboard.setDisplayList(config.getFileConfiguration()
								.getStringList("Scoreboard.Island.Team.Empty.Displaylines"));
					} else {
						scoreboard.setDisplayList(config.getFileConfiguration()
								.getStringList("Scoreboard.Island.Team.Occupied.Displaylines"));
					}

					Map<String, String> displayVariables = new HashMap<>();
					displayVariables.put("%owner",
							config.getFileConfiguration().getString("Scoreboard.Island.Team.Word.Owner"));
					displayVariables.put("%operator",
							config.getFileConfiguration().getString("Scoreboard.Island.Team.Word.Operator"));
					displayVariables.put("%member",
							config.getFileConfiguration().getString("Scoreboard.Island.Team.Word.Member"));

					scoreboard.setDisplayVariables(displayVariables);
				}
			} else {
				scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&',
						config.getFileConfiguration().getString("Scoreboard.Tutorial.Displayname")));
				scoreboard.setDisplayList(
						config.getFileConfiguration().getStringList("Scoreboard.Tutorial.Displaylines"));
			}

			scoreboard.run();
			scoreboardManager.storeScoreboard(player, scoreboard);
		}

		Bukkit.broadcastMessage(SkyBlockAPI.getIsland(player).getOwnerUUID().toString());
	}
}
