package me.goodandevil.skyblock.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import com.google.common.base.Preconditions;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.api.island.Island;
import me.goodandevil.skyblock.api.island.IslandRole;
import me.goodandevil.skyblock.api.structure.Structure;
import me.goodandevil.skyblock.api.utils.APIUtil;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;

public class SkyBlockAPI {

	private static SkyBlock implementation;

	/**
	 * @param implementation the implementation to set
	 */
	public static void setImplementation(SkyBlock implementation) {
		if (SkyBlockAPI.implementation != null) {
			throw new IllegalArgumentException("Cannot set API implementation twice");
		}

		SkyBlockAPI.implementation = implementation;
	}

	/**
	 * @return The SkyBlock implementation
	 */
	public static SkyBlock getImplementation() {
		return implementation;
	}

	/**
	 * Set the Biome of an Island
	 */
	public static void setBiome(Island island, Biome biome) {
		Preconditions.checkArgument(island != null, "Cannot set biome to null island");
		Preconditions.checkArgument(biome != null, "Cannot set biome to null biome");
		implementation.getBiomeManager().setBiome(island.getIsland(), biome);
	}

	/**
	 * Calculates the points of an Island to determine what the Island level is
	 */
	public static void calculatePoints(Island island) {
		Preconditions.checkArgument(island != null, "Cannot calculate points to null island");
		implementation.getLevellingManager().calculatePoints(null, island.getIsland());
	}

	/**
	 * @return The Structure for an Island
	 */
	public static Structure getStructure(String structure) {
		return implementation.getStructureManager().getStructure(structure);
	}

	/**
	 * @return true of conditions met, false otherwise
	 */
	public static boolean hasStructure(String structure) {
		return implementation.getStructureManager().containsStructure(structure);
	}

	/**
	 * @return A List of Structures for an Island
	 */
	public static List<Structure> getStructures() {
		List<Structure> structures = new ArrayList<>();

		for (Structure structureList : implementation.getStructureManager().getStructures()) {
			structures.add(structureList);
		}

		return structures;
	}

	/**
	 * Gives Island ownership to a player of their Island
	 */
	public static void giveOwnership(Player player) {
		Preconditions.checkArgument(player != null, "Cannot give ownership to null player");
		Island island = getIsland(player);

		if (island != null && island.getRole(player) != IslandRole.OWNER) {
			implementation.getIslandManager().giveIslandOwnership(player.getUniqueId());
		}
	}

	/**
	 * @return The Visitors occupying an Island
	 */
	public static Set<UUID> getVisitorsAtIsland(Island island) {
		Preconditions.checkArgument(island != null, "Cannot get visitors at island to null island");
		return implementation.getIslandManager().getVisitorsAtIsland(island.getIsland());
	}

	/**
	 * Makes a player a Visitor of an Island
	 */
	public static void visitIsland(Player player, Island island) {
		Preconditions.checkArgument(player != null, "Cannot visit island to null player");
		Preconditions.checkArgument(island != null, "Cannot visit island to null island");
		implementation.getIslandManager().visitIsland(player, island.getIsland());
	}

	/**
	 * Closes an Island from Visitors
	 */
	public static void closeIsland(Island island) {
		Preconditions.checkArgument(island != null, "Cannot closed island to null island");
		implementation.getIslandManager().closeIsland(island.getIsland());
	}

	/**
	 * Checks if a player has permission at an Island for a Setting
	 * 
	 * @return true of conditions met, false otherwise
	 */
	public static boolean hasPermission(Player player, String setting) {
		Preconditions.checkArgument(player != null, "Cannot check permission to null player");

		return implementation.getIslandManager().hasPermission(player, setting);
	}

	/**
	 * Checks the permission of a Setting for a Role at a Location
	 * 
	 * @return true of conditions met, false otherwise
	 */
	public static boolean hasSetting(Location location, IslandRole role, String setting) {
		Preconditions.checkArgument(location != null, "Cannot check setting to null location");
		Preconditions.checkArgument(role != null, "Cannot check setting to null role");

		return implementation.getIslandManager().hasSetting(location, APIUtil.toImplementation(role), setting);
	}

	/**
	 * @return A Set of Members of an Island that are online
	 */
	public static Set<UUID> getMembersOnline(Island island) {
		Preconditions.checkArgument(island != null, "Cannot get online members to null island");
		return implementation.getIslandManager().getMembersOnline(island.getIsland());
	}

	/**
	 * @return A Set of Players at an Island
	 */
	public static Set<UUID> getPlayersAtIsland(Island island) {
		Preconditions.checkArgument(island != null, "Cannot get players at island to null island");
		return implementation.getIslandManager().getPlayersAtIsland(island.getIsland());
	}

	/**
	 * Gives the Island Upgrades to a player
	 */
	public static void giveUgrades(Player player, Island island) {
		Preconditions.checkArgument(player != null, "Cannot give upgrades to null player");
		Preconditions.checkArgument(island != null, "Cannot give upgrades to null island");
		implementation.getIslandManager().giveUpgrades(player, island.getIsland());
	}

	/**
	 * Removes the Island Upgrades from a player
	 */
	public static void removeUpgrades(Player player) {
		Preconditions.checkArgument(player != null, "Cannot remove upgrades to null player");
		implementation.getIslandManager().removeUpgrades(player);
	}

	/**
	 * @return A Set of Cooped Players at an Island
	 */
	public static Set<UUID> getCoopPlayersAtIsland(Island island) {
		Preconditions.checkArgument(island != null, "Cannot get coop players to null island");
		return implementation.getIslandManager().getCoopPlayersAtIsland(island.getIsland());
	}

	/**
	 * Creates an Island for a player from a Structure
	 * 
	 * @return true of conditions met, false otherwise
	 */
	public static boolean createIsland(Player player, Structure structure) {
		Preconditions.checkArgument(player != null, "Cannot create island to null player");
		Preconditions.checkArgument(structure != null, "Cannot create island to null structure");

		if (!hasIsland(player)) {
			return implementation.getIslandManager().createIsland(player,
					(me.goodandevil.skyblock.structure.Structure) structure);
		}

		return false;
	}

	/**
	 * Deletes an Island permanently
	 */
	public static void deleteIsland(Island island) {
		Preconditions.checkArgument(island != null, "Cannot delete island to null island");
		implementation.getIslandManager().deleteIsland(island.getIsland());
	}

	/**
	 * @return The Island of a player
	 */
	public static Island getIsland(Player player) {
		Preconditions.checkArgument(player != null, "Cannot get island to null player");
		PlayerDataManager playerDataManager = implementation.getPlayerDataManager();

		if (playerDataManager.hasPlayerData(player)) {
			PlayerData playerData = playerDataManager.getPlayerData(player);

			if (playerData.getOwner() != null) {
				return implementation.getIslandManager().getIsland(playerData.getOwner()).getAPIWrapper();
			}
		}

		return null;
	}

	/**
	 * @return true of conditions met, false otherwise
	 */
	public static boolean hasIsland(OfflinePlayer player) {
		Preconditions.checkArgument(player != null, "Cannot check island to null player");
		return new me.goodandevil.skyblock.utils.OfflinePlayer(player.getUniqueId()).getOwner() != null;
	}

	/**
	 * @return true of conditions met, false otherwise
	 */
	public static boolean isPlayerAtAnIsland(Player player) {
		Preconditions.checkArgument(player != null, "Cannot check to null player");
		PlayerDataManager playerDataManager = implementation.getPlayerDataManager();

		if (playerDataManager.hasPlayerData(player)) {
			PlayerData playerData = playerDataManager.getPlayerData(player);

			if (playerData.getIsland() != null) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Check if a player is occupying an Island
	 * 
	 * @return true of conditions met, false otherwise
	 */
	public static boolean isPlayerAtIsland(Player player, Island island) {
		Preconditions.checkArgument(player != null, "Cannot check to null player");
		Preconditions.checkArgument(island != null, "Cannot check to null island");
		PlayerDataManager playerDataManager = implementation.getPlayerDataManager();

		if (playerDataManager.hasPlayerData(player)) {
			PlayerData playerData = playerDataManager.getPlayerData(player);

			if (playerData.getIsland() != null && playerData.getIsland().equals(island.getOwnerUUID())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @return The Island the player is occupying
	 */
	public static Island getIslandPlayerAt(Player player) {
		Preconditions.checkArgument(player != null, "Cannot get Island to null player");

		PlayerDataManager playerDataManager = implementation.getPlayerDataManager();
		IslandManager islandManager = implementation.getIslandManager();

		if (playerDataManager.hasPlayerData(player)) {
			PlayerData playerData = playerDataManager.getPlayerData(player);

			if (playerData.getIsland() != null && islandManager.hasIsland(playerData.getIsland())) {
				return islandManager.getIsland(playerData.getIsland()).getAPIWrapper();
			}
		}

		return null;
	}
}
