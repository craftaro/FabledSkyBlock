package me.goodandevil.skyblock.placeholder;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.Role;

public class EZPlaceholder extends PlaceholderExpansion {
	
    private final SkyBlock skyblock;

    public EZPlaceholder(SkyBlock skyblock) {
        this.skyblock = skyblock;
    }

    @Override
    public String getIdentifier() {
        return "skyblock";
    }
    
    @Override
    public String getPlugin() {
        return null;
    }
    
    @Override
    public String getAuthor() {
        return skyblock.getDescription().getAuthors().get(0);
    }
    
    @Override
    public String getVersion() {
        return skyblock.getDescription().getVersion();
    }

    public String onPlaceholderRequest(Player player, String identifier) {
        if (identifier.equalsIgnoreCase("islands")) {
            return "" + skyblock.getVisitManager().getIslands().size();
        }

        if (player == null) {
            return "";
        }

        IslandManager islandManager = skyblock.getIslandManager();

        Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (islandManager.hasIsland(player)) {
            Island island = islandManager.getIsland(skyblock.getPlayerDataManager().getPlayerData(player).getOwner());
            
            switch (identifier) {
                case "island_size":
                    return value(configLoad, "Placeholder.skyblock_island_size.Non-empty.Message", island.getSize());
                case "island_radius":
                    return value(configLoad, "Placeholder.skyblock_island_radius.Non-empty.Message", island.getSize());
                case "island_level":
                    return value(configLoad, "Placeholder.skyblock_island_level.Non-empty.Message", island.getLevel().getLevel());
                case "island_points":
                    return value(configLoad, "Placeholder.skyblock_island_points.Non-empty.Message", island.getLevel().getPoints());
                case "island_biome":
                    return value(configLoad, "Placeholder.skyblock_island_biome.Non-empty.Message", island.getBiomeName());
                case "island_time":
                    return value(configLoad, "Placeholder.skyblock_island_time.Non-empty.Message", island.getTime());
                case "island_weather":
                    return value(configLoad, "Placeholder.skyblock_island_weather.Non-empty.Message", island.getWeatherName());
                case "island_bans":
                    return value(configLoad, "Placeholder.skyblock_island_bans.Non-empty.Message", island.getBan().getBans().size());
                case "island_members_total":
                    return value(configLoad, "Placeholder.skyblock_island_members_total.Non-empty.Message", (island.getRole(Role.Member).size() + island.getRole(Role.Operator).size() + 1));
                case "island_members":
                    return value(configLoad, "Placeholder.skyblock_island_members.Non-empty.Message", island.getRole(Role.Member).size());
                case "island_operators":
                    return value(configLoad, "Placeholder.skyblock_island_operators.Non-empty.Message", island.getRole(Role.Operator).size());
                case "island_visitors":
                    return value(configLoad, "Placeholder.skyblock_island_visitors.Non-empty.Message", island.getVisitors().size());
                case "island_role":
                    for (Role roleList : Role.values()) {
                        if (island.isRole(roleList, player.getUniqueId())) {
                            return value(configLoad, "Placeholder.skyblock_island_role.Non-empty.Message", roleList.name());
                        }
                    }
                case "island_owner":
                    UUID owner = island.getOwnerUUID();
                    Player target = Bukkit.getServer().getPlayer(owner);

                    if (target == null) {
                        return value(configLoad, "Placeholder.skyblock_island_owner.Non-empty.Other.Message", Bukkit.getServer().getOfflinePlayer(owner).getName());
                    }
                    if (target.getName().equals(player.getName())) {
                        return value(configLoad, "Placeholder.skyblock_island_owner.Non-empty.Yourself.Message", target.getName());
                    } else {
                        return value(configLoad, "Placeholder.skyblock_island_owner.Non-empty.Other.Message", target.getName());
                    }
            }

            return null;
        }
        
        switch (identifier) {
            case "island_size":
                return value(configLoad, "Placeholder.skyblock_island_size.Empty.Message");
            case "island_radius":
                return value(configLoad, "Placeholder.skyblock_island_radius.Empty.Message");
            case "island_level":
                return value(configLoad, "Placeholder.skyblock_island_level.Empty.Message");
            case "island_points":
                return value(configLoad, "Placeholder.skyblock_island_points.Empty.Message");
            case "island_role":
                return value(configLoad, "Placeholder.skyblock_island_role.Empty.Message");
            case "island_owner":
                return value(configLoad, "Placeholder.skyblock_island_owner.Empty.Message");
            case "island_biome":
                return value(configLoad, "Placeholder.skyblock_island_biome.Empty.Message");
            case "island_time":
                return value(configLoad, "Placeholder.skyblock_island_time.Empty.Message");
            case "island_weather":
                return value(configLoad, "Placeholder.skyblock_island_weather.Empty.Message");
            case "island_bans":
                return value(configLoad, "Placeholder.skyblock_island_bans.Empty.Message");
            case "island_members_total":
                return value(configLoad, "Placeholder.skyblock_island_members_total.Empty.Message");
            case "island_members":
                return value(configLoad, "Placeholder.skyblock_island_members.Empty.Message");
            case "island_operators":
                return value(configLoad, "Placeholder.skyblock_island_operators.Empty.Message");
            case "island_visitors":
                return value(configLoad, "Placeholder.skyblock_island_visitors.Empty.Message");
        }

        return null;
    }
    
    private String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
    
    private String value(FileConfiguration configLoad, String string, Object value) {
        return color(configLoad.getString(string).replace("%placeholder", (CharSequence) value));
    }

    private String value(FileConfiguration configLoad, String string) {
        return color(configLoad.getString(string));
    }
}
