package me.goodandevil.skyblock.island;

import org.bukkit.World.Environment;

public enum IslandWorld {

	Normal, Nether, End;

	public Environment getEnvironment() {
		switch (this) {
		case End:
			return Environment.THE_END;
		case Nether:
			return Environment.NETHER;
		case Normal:
			return Environment.NORMAL;
		}

		return null;
	}
}
