package me.goodandevil.skyblock.api.utils;

import me.goodandevil.skyblock.api.island.IslandEnvironment;
import me.goodandevil.skyblock.api.island.IslandMessage;
import me.goodandevil.skyblock.api.island.IslandRole;
import me.goodandevil.skyblock.api.island.IslandUpgrade;
import me.goodandevil.skyblock.api.island.IslandWorld;
import me.goodandevil.skyblock.island.Location.Environment;
import me.goodandevil.skyblock.island.Location.World;
import me.goodandevil.skyblock.upgrade.Upgrade;

public final class APIUtil {

	public static World toImplementation(IslandWorld world) {
		switch (world) {
		case NETHER:
			return World.Nether;
		case OVERWORLD:
			return World.Normal;
		}

		return null;
	}

	public static IslandWorld fromImplementation(World world) {
		switch (world) {
		case Nether:
			return IslandWorld.NETHER;
		case Normal:
			return IslandWorld.OVERWORLD;
		}

		return null;
	}

	public static Environment toImplementation(IslandEnvironment environment) {
		switch (environment) {
		case ISLAND:
			return Environment.Island;
		case MAIN:
			return Environment.Main;
		case VISITOR:
			return Environment.Visitor;
		}

		return null;
	}

	public static IslandEnvironment fromImplementation(Environment environment) {
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
		case Crop:
			return Upgrade.Type.Crop;
		case Drops:
			return Upgrade.Type.Drops;
		case Fly:
			return Upgrade.Type.Fly;
		case Jump:
			return Upgrade.Type.Jump;
		case Size:
			return Upgrade.Type.Size;
		case Spawner:
			return Upgrade.Type.Spawner;
		case Speed:
			return Upgrade.Type.Speed;
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
}
