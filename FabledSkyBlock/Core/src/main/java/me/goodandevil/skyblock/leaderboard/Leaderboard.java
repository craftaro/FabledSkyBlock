package me.goodandevil.skyblock.leaderboard;

import me.goodandevil.skyblock.visit.Visit;

public class Leaderboard {

	private Type type;
	private final Visit visit;
	private int position;

	public Leaderboard(Type type, Visit visit, int position) {
		this.type = type;
		this.visit = visit;
		this.position = position;
	}

	public Type getType() {
		return type;
	}

	public Visit getVisit() {
		return visit;
	}

	public int getPosition() {
		return position;
	}

	public enum Type {

		Level, Bank, Votes

	}
}
