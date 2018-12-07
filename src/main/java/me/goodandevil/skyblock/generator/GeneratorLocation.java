package me.goodandevil.skyblock.generator;

import org.bukkit.block.Block;

import me.goodandevil.skyblock.island.Location;

public class GeneratorLocation {

	private Location.World world;

	private int blockX;
	private int blockY;
	private int blockZ;

	private int liquidX;
	private int liquidY;
	private int liquidZ;

	public GeneratorLocation(Location.World world, Block block, Block liquid) {
		this.world = world;

		this.blockX = block.getLocation().getBlockX();
		this.blockY = block.getLocation().getBlockY();
		this.blockZ = block.getLocation().getBlockZ();

		if (liquid != null) {
			this.liquidX = liquid.getLocation().getBlockX();
			this.liquidY = liquid.getLocation().getBlockY();
			this.liquidZ = liquid.getLocation().getBlockZ();
		}
	}

	public Location.World getWorld() {
		return world;
	}

	public int getBlockX() {
		return blockX;
	}

	public int getBlockY() {
		return blockY;
	}

	public int getBlockZ() {
		return blockZ;
	}

	public int getLiquidX() {
		return liquidX;
	}

	public int getLiquidY() {
		return liquidY;
	}

	public int getLiquidZ() {
		return liquidZ;
	}
}
