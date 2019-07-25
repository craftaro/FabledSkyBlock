package me.goodandevil.skyblock.api.utils;

import me.goodandevil.skyblock.api.island.IslandBorderColor;
import me.goodandevil.skyblock.api.island.IslandEnvironment;
import me.goodandevil.skyblock.api.island.IslandMessage;
import me.goodandevil.skyblock.api.island.IslandRole;
import me.goodandevil.skyblock.api.island.IslandUpgrade;
import me.goodandevil.skyblock.api.island.IslandWorld;
import me.goodandevil.skyblock.upgrade.Upgrade;
import me.goodandevil.skyblock.utils.world.WorldBorder;

public final class APIUtil {

	public static me.goodandevil.skyblock.island.IslandWorld toImplementation(IslandWorld world) {
		switch (world) {
		case NETHER:
			return me.goodandevil.skyblock.island.IslandWorld.Nether;
		case OVERWORLD:
			return me.goodandevil.skyblock.island.IslandWorld.Normal;
		case END:
			return me.goodandevil.skyblock.island.IslandWorld.End;
		}

		return null;
	}

	public static IslandWorld fromImplementation(me.goodandevil.skyblock.island.IslandWorld world) {
		switch (world) {
		case Nether:
			return IslandWorld.NETHER;
		case Normal:
			return IslandWorld.OVERWORLD;
		case End:
			return IslandWorld.END;
		}

		return null;
	}

	public static me.goodandevil.skyblock.island.IslandEnvironment toImplementation(IslandEnvironment environment) {
		switch (environment) {
		case ISLAND:
			return me.goodandevil.skyblock.island.IslandEnvironment.Island;
		case MAIN:
			return me.goodandevil.skyblock.island.IslandEnvironment.Main;
		case VISITOR:
			return me.goodandevil.skyblock.island.IslandEnvironment.Visitor;
		}

		return null;
	}

	public static IslandEnvironment fromImplementation(me.goodandevil.skyblock.island.IslandEnvironment environment) {
		switch (environment) {
		case Island:
			return IslandEnvironment.ISLAND;
		case Main:
			return IslandEnvironment.MAIN;
		case Visitor:
			return IslandEnvironment.VISITOR;
		}

		return null;
	}

	public static me.goodandevil.skyblock.island.IslandRole toImplementation(IslandRole role) {
		switch (role) {
		case VISITOR:
			return me.goodandevil.skyblock.island.IslandRole.Visitor;
		case COOP:
			return me.goodandevil.skyblock.island.IslandRole.Coop;
		case MEMBER:
			return me.goodandevil.skyblock.island.IslandRole.Member;
		case OPERATOR:
			return me.goodandevil.skyblock.island.IslandRole.Operator;
		case OWNER:
			return me.goodandevil.skyblock.island.IslandRole.Owner;
		}

		return null;
	}

	public static IslandRole fromImplementation(me.goodandevil.skyblock.island.IslandRole role) {
		switch (role) {
		case Visitor:
			return IslandRole.VISITOR;
		case Coop:
			return IslandRole.COOP;
		case Member:
			return IslandRole.MEMBER;
		case Operator:
			return IslandRole.OPERATOR;
		case Owner:
			return IslandRole.OWNER;
		}

		return null;
	}

	public static Upgrade.Type toImplementation(IslandUpgrade upgrade) {
		switch (upgrade) {
		case CROP:
			return Upgrade.Type.Crop;
		case DROPS:
			return Upgrade.Type.Drops;
		case FLY:
			return Upgrade.Type.Fly;
		case JUMP:
			return Upgrade.Type.Jump;
		case SIZE:
			return Upgrade.Type.Size;
		case SPAWNER:
			return Upgrade.Type.Spawner;
		case SPEED:
			return Upgrade.Type.Speed;
		}

		return null;
	}

	public static IslandUpgrade fromImplementation(Upgrade.Type upgrade) {
		switch (upgrade) {
		case Crop:
			return IslandUpgrade.CROP;
		case Drops:
			return IslandUpgrade.DROPS;
		case Fly:
			return IslandUpgrade.FLY;
		case Jump:
			return IslandUpgrade.JUMP;
		case Size:
			return IslandUpgrade.SIZE;
		case Spawner:
			return IslandUpgrade.SPAWNER;
		case Speed:
			return IslandUpgrade.SPEED;
		}

		return null;
	}

	public static me.goodandevil.skyblock.island.IslandMessage toImplementation(IslandMessage message) {
		switch (message) {
		case SIGN:
			return me.goodandevil.skyblock.island.IslandMessage.Sign;
		case SIGNATURE:
			return me.goodandevil.skyblock.island.IslandMessage.Signature;
		case WELCOME:
			return me.goodandevil.skyblock.island.IslandMessage.Welcome;
		}

		return null;
	}

	public static IslandMessage fromImplementation(me.goodandevil.skyblock.island.IslandMessage message) {
		switch (message) {
		case Sign:
			return IslandMessage.SIGN;
		case Signature:
			return IslandMessage.SIGNATURE;
		case Welcome:
			return IslandMessage.WELCOME;
		}

		return null;
	}

	public static WorldBorder.Color toImplementation(IslandBorderColor color) {
		switch (color) {
		case Blue:
			return WorldBorder.Color.Blue;
		case Green:
			return WorldBorder.Color.Green;
		case Red:
			return WorldBorder.Color.Red;
		}

		return null;
	}

	public static IslandBorderColor fromImplementation(WorldBorder.Color color) {
		switch (color) {
		case Blue:
			return IslandBorderColor.Blue;
		case Green:
			return IslandBorderColor.Green;
		case Red:
			return IslandBorderColor.Red;
		}

		return null;
	}
}
