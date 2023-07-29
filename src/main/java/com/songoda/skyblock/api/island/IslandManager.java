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

public class IslandManager {
    private final com.songoda.skyblock.island.IslandManager islandManager;
    private final PermissionManager permissionManager;

    public IslandManager(com.songoda.skyblock.island.IslandManager islandManager) {
        this.islandManager = islandManager;
        this.permissionManager = SkyBlock.getPlugin(SkyBlock.class).getPermissionManager();
    }

    /**
     * @return true of conditions met, false otherwise
     */
    public static boolean hasIsland(OfflinePlayer player) {
        Preconditions.checkArgument(player != null, "Cannot check island to null player");
        return new com.songoda.skyblock.utils.player.OfflinePlayer(player.getUniqueId()).getOwner() != null;
    }

    /**
     * Updates the Island border for players occupying an Island
     */
    public void updateBorder(Island island) {
        Preconditions.checkArgument(island != null, "Cannot update border to null island");

        this.islandManager.updateBorder(island.getIsland());
    }

    /**
     * Gives Island ownership to a player of their Island
     */
    public void giveOwnership(Island island, OfflinePlayer player) {
        Preconditions.checkArgument(island != null, "Cannot give ownership to null island");
        Preconditions.checkArgument(player != null, "Cannot give ownership to null player");

        this.islandManager.giveOwnership(island.getIsland(), player);
    }

    /**
     * @return The Visitors occupying an Island
     */
    public Set<UUID> getVisitorsAtIsland(Island island) {
        Preconditions.checkArgument(island != null, "Cannot get visitors at island to null island");

        return this.islandManager.getVisitorsAtIsland(island.getIsland());
    }

    /**
     * Makes a player a Visitor of an Island
     */
    public void visitIsland(Player player, Island island) {
        Preconditions.checkArgument(player != null, "Cannot visit island to null player");
        Preconditions.checkArgument(island != null, "Cannot visit island to null island");

        this.islandManager.visitIsland(player, island.getIsland());
    }

    /**
     * Closes an Island from Visitors
     */
    public void closeIsland(Island island) {
        Preconditions.checkArgument(island != null, "Cannot closed island to null island");

        this.islandManager.closeIsland(island.getIsland());
    }

    /**
     * @return A Set of Members of an Island that are online
     */
    public Set<UUID> getMembersOnline(Island island) {
        Preconditions.checkArgument(island != null, "Cannot get online members to null island");

        return this.islandManager.getMembersOnline(island.getIsland());
    }

    /**
     * @return A List of Players at an Island
     */
    public List<Player> getPlayersAtIsland(Island island) {
        Preconditions.checkArgument(island != null, "Cannot get players at island to null island");

        return this.islandManager.getPlayersAtIsland(island.getIsland());
    }

    /**
     * @return A List of Players at an Island by IslandWorld
     */
    public List<Player> getPlayersAtIsland(Island island, IslandWorld world) {
        Preconditions.checkArgument(island != null, "Cannot get players at island to null island");
        Preconditions.checkArgument(world != null, "Cannot get players at island to null world");

        return this.islandManager.getPlayersAtIsland(island.getIsland(), APIUtil.toImplementation(world));
    }

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
            return this.islandManager.createIsland(player, (com.songoda.skyblock.structure.Structure) structure);
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

    /**
     * @return The Island of a player
     */
    public Island getIsland(OfflinePlayer player) {
        Preconditions.checkArgument(player != null, "Cannot get island to null player");

        com.songoda.skyblock.island.Island island = this.islandManager.getIsland(player);

        if (island != null) {
            return island.getAPIWrapper();
        }

        return new Island(null, player);
    }

    /**
     * Gets an Island by its UUID
     * Returns null if an Island with the given UUID does not exist
     *
     * @param islandUUID The UUID of the Island
     * @return The Island with the given UUID, or null if one was not found
     */
    public Island getIslandByUUID(UUID islandUUID) {
        Preconditions.checkArgument(islandUUID != null, "Cannot get island with a null UUID");

        com.songoda.skyblock.island.Island island = this.islandManager.getIslandByUUID(islandUUID);

        return island != null ? island.getAPIWrapper() : null;
    }

    /**
     * @return A List of loaded Islands
     */
    public List<Island> getIslands() {
        List<Island> islands = new ArrayList<>();
        for (int i = 0; i < this.islandManager.getIslands().size(); i++) {
            islands.add(this.islandManager.getIslands().get(this.islandManager.getIslands().keySet().toArray()[i]).getAPIWrapper());
        }
        return islands;
    }
}
