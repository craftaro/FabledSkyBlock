package me.goodandevil.skyblock.leaderboard;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.visit.Visit;
import me.goodandevil.skyblock.visit.VisitManager;

public class LeaderboardManager {
	
	private final SkyBlock skyblock;
	
	private List<Leaderboard> leaderboardStorage = new ArrayList<>();
	
	public LeaderboardManager(SkyBlock skyblock) {
		this.skyblock = skyblock;
		
		new LeaderboardTask(skyblock).runTaskTimerAsynchronously(skyblock, 0L, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getInt("Island.Leaderboard.Reset.Time") * 20);
		
		resetLeaderboard();
	}
	
	public void resetLeaderboard() {
		VisitManager visitManager = skyblock.getVisitManager();
		
		List<LeaderboardPlayer> islandLevels = new ArrayList<>();
		List<LeaderboardPlayer> islandVotes = new ArrayList<>();
		
		for (int i = 0; i < visitManager.getIslands().size(); i++) {
			UUID ownerUUID = (UUID) visitManager.getIslands().keySet().toArray()[i];
			Visit visit = visitManager.getIslands().get(ownerUUID);
			islandLevels.add(new LeaderboardPlayer(ownerUUID, visit.getLevel().getLevel()));
			islandVotes.add(new LeaderboardPlayer(ownerUUID, visit.getVoters().size()));
		}
		
		islandLevels.sort(new Comparator<LeaderboardPlayer>() {
			@Override
			public int compare(LeaderboardPlayer leaderboardPlayer1, LeaderboardPlayer leaderboardPlayer2) {
				return Integer.valueOf(leaderboardPlayer2.getValue()).compareTo(leaderboardPlayer1.getValue());
			}
		});
		
		islandVotes.sort(new Comparator<LeaderboardPlayer>() {
			@Override
			public int compare(LeaderboardPlayer leaderboardPlayer1, LeaderboardPlayer leaderboardPlayer2) {
				return Integer.valueOf(leaderboardPlayer2.getValue()).compareTo(leaderboardPlayer1.getValue());
			}
		});
		
		for (int i = 0; i < 10; i++) {
			if (islandLevels.size() != 0 && i <= islandLevels.size()-1) {
				leaderboardStorage.add(new Leaderboard(Leaderboard.Type.Level, visitManager.getIsland((UUID) islandLevels.get(i).getUUID()), i));
			}
			
			if (islandVotes.size() != 0 && i <= islandVotes.size()-1) {
				leaderboardStorage.add(new Leaderboard(Leaderboard.Type.Votes, visitManager.getIsland((UUID) islandLevels.get(i).getUUID()), i));
			}
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
	
	public List<Leaderboard> getLeaderboards() {
		return leaderboardStorage;
	}
}
