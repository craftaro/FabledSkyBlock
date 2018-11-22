package me.goodandevil.skyblock.leaderboard;

import java.util.ArrayList;
import java.util.List;

public class Leaderboard {

	private String playerName;
	
	private int level;
	private int points;
	private int votes;
	
	private List<String> members;
	
	public Leaderboard(String playerName, int level, int points, int votes, List<String> members) {
		this.playerName = playerName;
		this.level = level;
		this.points = points;
		this.votes = votes;
		this.members = members;
		
		if (members == null) {
			this.members = new ArrayList<>();
		}
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	public int getLevel() {
		return level;
	}
	
	public int getPoints() {
		return points;
	}
	
	public int getVotes() {
		return votes;
	}
	
	public List<String> getMembers() {
		return members;
	}
}
