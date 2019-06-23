package me.goodandevil.skyblock.leaderboard;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.economy.EconomyManager;
import me.goodandevil.skyblock.island.IslandWorld;
import me.goodandevil.skyblock.leaderboard.leaderheads.TopBank;
import me.goodandevil.skyblock.leaderboard.leaderheads.TopLevel;
import me.goodandevil.skyblock.leaderboard.leaderheads.TopVotes;
import me.goodandevil.skyblock.visit.Visit;
import me.goodandevil.skyblock.visit.VisitManager;
import me.goodandevil.skyblock.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class LeaderboardManager {

	private final SkyBlock skyblock;

	private List<Leaderboard> leaderboardStorage = new ArrayList<>();

	public LeaderboardManager(SkyBlock skyblock) {
		this.skyblock = skyblock;

		new LeaderboardTask(skyblock).runTaskTimerAsynchronously(skyblock, 0L,
				skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"))
						.getFileConfiguration().getInt("Island.Leaderboard.Reset.Time") * 20);

		resetLeaderboard();
		setupLeaderHeads();
	}

	public void resetLeaderboard() {
		EconomyManager economyManager = skyblock.getEconomyManager();
		VisitManager visitManager = skyblock.getVisitManager();
		WorldManager worldManager = skyblock.getWorldManager();

		visitManager.loadIslands();

		List<LeaderboardPlayer> islandLevels = new ArrayList<>();
		List<LeaderboardPlayer> islandBanks = new ArrayList<>();
		List<LeaderboardPlayer> islandVotes = new ArrayList<>();

		boolean enableExemptions = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"))
				.getFileConfiguration().getBoolean("Island.Leaderboard.Exemptions.Enable");

		for (int i = 0; i < visitManager.getIslands().size(); i++) {
			UUID ownerUUID = (UUID) visitManager.getIslands().keySet().toArray()[i];

			if (enableExemptions && economyManager.hasPermission(worldManager.getWorld(IslandWorld.Normal).getName(),
					Bukkit.getOfflinePlayer(ownerUUID),
					"fabledskyblock.top.exempt"))
				continue;

			Visit visit = visitManager.getIslands().get(ownerUUID);
			islandLevels.add(new LeaderboardPlayer(ownerUUID, visit.getLevel().getLevel()));
			islandBanks.add(new LeaderboardPlayer(ownerUUID, (long)visit.getBankBalance()));
			islandVotes.add(new LeaderboardPlayer(ownerUUID, visit.getVoters().size()));
		}

		islandLevels.sort(Comparator.comparingLong(LeaderboardPlayer::getValue).reversed());
		islandBanks.sort(Comparator.comparingLong(LeaderboardPlayer::getValue).reversed());
		islandVotes.sort(Comparator.comparingLong(LeaderboardPlayer::getValue).reversed());

		for (int i = 0; i < 10; i++) {
			if (!islandVotes.isEmpty() && i < islandVotes.size()) {
				Leaderboard leaderboard = new Leaderboard(Leaderboard.Type.Votes, visitManager.getIsland(islandVotes.get(i).getUUID()), i);
				leaderboardStorage.add(leaderboard);
			}

			if (!islandBanks.isEmpty() && i < islandBanks.size()) {
				Leaderboard leaderboard = new Leaderboard(Leaderboard.Type.Bank, visitManager.getIsland(islandBanks.get(i).getUUID()), i);
				leaderboardStorage.add(leaderboard);
			}

			if (!islandLevels.isEmpty() && i < islandLevels.size()) {
				Leaderboard leaderboard = new Leaderboard(Leaderboard.Type.Level, visitManager.getIsland(islandLevels.get(i).getUUID()), i);
				leaderboardStorage.add(leaderboard);
			}
		}
	}

	public int getPlayerIslandLeaderboardPosition(Player player, Leaderboard.Type type) {
		VisitManager visitManager = skyblock.getVisitManager();
		visitManager.loadIslands();

		List<LeaderboardPlayer> leaderboardPlayers = new ArrayList<>();

		switch (type) {
			case Level:
				for (int i = 0; i < visitManager.getIslands().size(); i++) {
					UUID ownerUUID = (UUID) visitManager.getIslands().keySet().toArray()[i];
					Visit visit = visitManager.getIslands().get(ownerUUID);
					leaderboardPlayers.add(new LeaderboardPlayer(ownerUUID, visit.getLevel().getLevel()));
				}
				break;
			case Bank:
				for (int i = 0; i < visitManager.getIslands().size(); i++) {
					UUID ownerUUID = (UUID) visitManager.getIslands().keySet().toArray()[i];
					Visit visit = visitManager.getIslands().get(ownerUUID);
					leaderboardPlayers.add(new LeaderboardPlayer(ownerUUID, (long)visit.getBankBalance()));
				}
			case Votes:
				for (int i = 0; i < visitManager.getIslands().size(); i++) {
					UUID ownerUUID = (UUID) visitManager.getIslands().keySet().toArray()[i];
					Visit visit = visitManager.getIslands().get(ownerUUID);
					leaderboardPlayers.add(new LeaderboardPlayer(ownerUUID, visit.getVoters().size()));
				}
				break;
		}

		leaderboardPlayers.sort(Comparator.comparingLong(LeaderboardPlayer::getValue).reversed());

		for (int i = 0; i < leaderboardPlayers.size(); i++) {
			if (leaderboardPlayers.get(i).getUUID().equals(player.getUniqueId())) {
				return i + 1;
			}
		}

		return -1;
	}

	public void setupLeaderHeads() {
		if (Bukkit.getServer().getPluginManager().getPlugin("LeaderHeads") != null) {
			new TopLevel(skyblock);
			new TopBank(skyblock);
			new TopVotes(skyblock);
		}
	}

	public void clearLeaderboard() {
		leaderboardStorage.clear();
	}

	public List<Leaderboard> getLeaderboard(Leaderboard.Type type) {
		List<Leaderboard> leaderboardIslands = new ArrayList<>();

		for (Leaderboard leaderboardList : leaderboardStorage) {
			if (leaderboardList.getType() == type) {
				leaderboardIslands.add(leaderboardList);
			}
		}

		return leaderboardIslands;
	}

	public Leaderboard getLeaderboardFromPosition(Leaderboard.Type type, int position) {
		for (Leaderboard leaderboardPlayerList : leaderboardStorage) {
			if (leaderboardPlayerList.getType() == type) {
				if (leaderboardPlayerList.getPosition() == position) {
					return leaderboardPlayerList;
				}
			}
		}

		return null;
	}

	public List<Leaderboard> getLeaderboards() {
		return leaderboardStorage;
	}
}
