package com.songoda.skyblock.api.island;

import com.google.common.base.Preconditions;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.api.structure.Structure;
import com.songoda.skyblock.api.utils.APIUtil;
import com.songoda.skyblock.permission.PermissionManager;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IslandManager {



    /**
     * Gives the Island Upgrades to a player
     *
     * @deprecated use {@link #updateFlight(Player)} instead
     */
    @Deprecated
    public void giveUgrades(Player player, Island island) {
        Preconditions.checkArgument(player != null, "Cannot give upgrades to null player");
        Preconditions.checkArgument(island != null, "Cannot give upgrades to null island");

        this.islandManager.updateFlight(player);
    }

    /**
     * Gives Fly to a player if they have permission at an Island
     *
     * @deprecated use {@link #updateFlight(Player)} instead
     */
    @Deprecated
    public void giveFly(Player player, Island island) {
        Preconditions.checkArgument(player != null, "Cannot give upgrades to null player");
        Preconditions.checkArgument(island != null, "Cannot give upgrades to null island");

        this.islandManager.updateFlight(player);
    }

    /**
     * Removes the Island Upgrades from a player
     *
     * @deprecated use {@link #updateFlight(Player)} instead
     */
    @Deprecated
    public void removeUpgrades(Player player) {
        Preconditions.checkArgument(player != null, "Cannot remove upgrades to null player");

        this.islandManager.updateFlight(player);
    }

    /**
     * Updates the flight of a player based on their permissions and current island upgrades
     */
    public void updateFlight(Player player) {
        Preconditions.checkArgument(player != null, "Cannot update flight of a null player");

        this.islandManager.updateFlight(player);
    }

    /**
     * Updates the flight of all players on an island based on their permissions and island upgrades
     */
    public void updateFlightAtIsland(Island island) {
        Preconditions.checkArgument(island != null, "Cannot update flight of a null island");

        this.islandManager.updateFlightAtIsland(island.getIsland());
    }

    /**
     * @return A Set of Cooped Players at an Island
     */
    public Set<UUID> getCoopPlayersAtIsland(Island island) {
        Preconditions.checkArgument(island != null, "Cannot get coop players to null island");

        return this.islandManager.getCoopPlayersAtIsland(island.getIsland());
    }

    /**
     * Removes Coop Players occupying an Island
     */
    public void removeCoopPlayersAtIsland(Island island) {
        Preconditions.checkArgument(island != null, "Cannot remove coop players to null island");
        this.islandManager.removeCoopPlayers(island.getIsland(), null);
    }

    /**
     * Creates an Island for a player from a Structure
     *
     * @return true of conditions met, false otherwise
     */
    public boolean createIsland(Player player, Structure structure) {
        Preconditions.checkArgument(player != null, "Cannot create island to null player");
        Preconditions.checkArgument(structure != null, "Cannot create island to null structure");

        if (!hasIsland(player)) {
            return islandManager.createIsland(player, (com.songoda.skyblock.structure.Structure) structure);
        }

        return false;
    }

    /**
     * Executes the {@link IslandManager#deleteIsland(Island, boolean)} method with <code>island<code> and
     * <code>true<code> as the parameters
     * <p>
     * See {@link IslandManager#deleteIsland(Island, boolean)}
     */
    public void deleteIsland(Island island) {
        Preconditions.checkArgument(island != null, "Cannot delete island to null island");

        this.islandManager.deleteIsland(island.getIsland(), true);
    }
    
    /*
     * If force is set to true, the island will be deleted and no conditions will be
     * checked, else it will only delete the island if the island deletion
     * conditions are met.
     */
    public void deleteIsland(Island island, boolean force) {
        Preconditions.checkArgument(island != null, "Cannot delete island to null island");

        this.islandManager.deleteIsland(island.getIsland(), force);
    }

    /**
     * Check if a player is occupying an Island
     *
     * @return true of conditions met, false otherwise
     */
    public boolean isPlayerAtIsland(Island island, Player player) {
        Preconditions.checkArgument(island != null, "Cannot check to null island");
        Preconditions.checkArgument(player != null, "Cannot check to null player");
        return this.islandManager.isPlayerAtIsland(island.getIsland(), player);
    }

    /**
     * Check if a player is occupying an Island by IslandWorld
     *
     * @return true of conditions met, false otherwise
     */
    public boolean isPlayerAtIsland(Island island, Player player, IslandWorld world) {
        Preconditions.checkArgument(island != null, "Cannot check to null island");
        Preconditions.checkArgument(player != null, "Cannot check to null player");
        Preconditions.checkArgument(world != null, "Cannot check to null world");

        return this.islandManager.isPlayerAtIsland(island.getIsland(), player, APIUtil.toImplementation(world));
    }

    /**
     * Check if a location is at an Island
     *
     * @return true of conditions met, false otherwise
     */
    public boolean isLocationAtIsland(Island island, Location location) {
        Preconditions.checkArgument(island != null, "Cannot check to null island");
        Preconditions.checkArgument(location != null, "Cannot check to null location");

        return this.islandManager.isLocationAtIsland(island.getIsland(), location);
    }

    /**
     * @return The Island at a location
     */
    public Island getIslandAtLocation(Location location) {
        Preconditions.checkArgument(location != null, "Cannot get island to null location");

        com.songoda.skyblock.island.Island island = this.islandManager.getIslandAtLocation(location);

        if (island != null) {
            return island.getAPIWrapper();
        }

        return null;
    }

    /**
     * Check if a location is at an Island by IslandWorld
     *
     * @return true of conditions met, false otherwise
     */
    public boolean isPlayerAtIsland(Island island, Location location, IslandWorld world) {
        Preconditions.checkArgument(island != null, "Cannot check to null island");
        Preconditions.checkArgument(location != null, "Cannot check to null location");
        Preconditions.checkArgument(world != null, "Cannot check to null world");

        return this.islandManager.isLocationAtIsland(island.getIsland(), location, APIUtil.toImplementation(world));
    }

    /**
     * @return The Island the player is occupying
     */
    public Island getIslandPlayerAt(Player player) {
        Preconditions.checkArgument(player != null, "Cannot get Island to null player");

        com.songoda.skyblock.island.Island island = this.islandManager.getIslandPlayerAt(player);

        if (island != null) {
            return island.getAPIWrapper();
        }

        return null;
    }

    /**
     * @return true of conditions met, false otherwise
     */
    public boolean isPlayerAtAnIsland(Player player) {
        Preconditions.checkArgument(player != null, "Cannot check to null player");
        return this.islandManager.isPlayerAtAnIsland(player);
    }

    /**
     * Resets an Island permanently
     */
    public void resetIsland(Island island) {
        Preconditions.checkArgument(island != null, "Cannot reset island to null island");
        this.islandManager.resetIsland(island.getIsland());
    }
}
