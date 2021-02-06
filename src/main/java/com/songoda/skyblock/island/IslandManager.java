package com.songoda.skyblock.island;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.eatthepath.uuid.FastUUID;
import com.google.common.base.Preconditions;
import com.songoda.core.compatibility.CompatibleBiome;
import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.compatibility.ServerVersion;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.api.event.island.*;
import com.songoda.skyblock.ban.BanManager;
import com.songoda.skyblock.blockscanner.CachedChunk;
import com.songoda.skyblock.blockscanner.ChunkLoader;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.confirmation.Confirmation;
import com.songoda.skyblock.cooldown.CooldownManager;
import com.songoda.skyblock.cooldown.CooldownType;
import com.songoda.skyblock.invite.Invite;
import com.songoda.skyblock.invite.InviteManager;
import com.songoda.skyblock.island.removal.ChunkDeleteSplitter;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.scoreboard.ScoreboardManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.structure.Structure;
import com.songoda.skyblock.structure.StructureManager;
import com.songoda.skyblock.upgrade.Upgrade;
import com.songoda.skyblock.upgrade.UpgradeManager;
import com.songoda.skyblock.utils.ChatComponent;
import com.songoda.skyblock.utils.player.OfflinePlayer;
import com.songoda.skyblock.utils.player.PlayerUtil;
import com.songoda.skyblock.utils.structure.SchematicUtil;
import com.songoda.skyblock.utils.structure.StructureUtil;
import com.songoda.skyblock.utils.version.NMSUtil;
import com.songoda.skyblock.utils.world.LocationUtil;
import com.songoda.skyblock.utils.world.WorldBorder;
import com.songoda.skyblock.utils.world.block.BlockDegreesType;
import com.songoda.skyblock.visit.VisitManager;
import com.songoda.skyblock.world.WorldManager;
import io.papermc.lib.PaperLib;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class IslandManager {

    private final SkyBlock plugin;

    private final List<IslandPosition> islandPositions = new ArrayList<>();
    private final Map<UUID, UUID> islandProxies = new HashMap<>();
    private final Map<UUID, Island> islandStorage = new ConcurrentHashMap<>();
    private final int offset;

    private HashMap<IslandWorld, Integer> oldSystemIslands;

    public IslandManager(SkyBlock plugin) {
        this.plugin = plugin;

        Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "worlds.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        offset = plugin.getConfiguration().getInt("Island.Creation.Distance", 1200);

        for (IslandWorld worldList : IslandWorld.values()) {
            ConfigurationSection configSection = configLoad.getConfigurationSection("World." + worldList.name() + ".nextAvailableLocation");
            islandPositions.add(new IslandPosition(worldList, configSection.getDouble("x"), configSection.getDouble("z")));
        }

        Bukkit.getOnlinePlayers().forEach(this::loadIsland);
        for (Island island : getIslands().values()) {
            if (island.isAlwaysLoaded())
                loadIslandAtLocation(island.getLocation(IslandWorld.Normal, IslandEnvironment.Island));
        }

        loadIslandPositions();
    }

    public void onDisable() {
        for (int i = 0; i < islandStorage.size(); i++) {
            UUID islandOwnerUUID = (UUID) islandStorage.keySet().toArray()[i];
            Island island = islandStorage.get(islandOwnerUUID);
            island.save();
        }
    }

    public synchronized void saveNextAvailableLocation(IslandWorld world) {
        FileManager fileManager = plugin.getFileManager();
        Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "worlds.yml"));

        File configFile = config.getFile();
        FileConfiguration configLoad = config.getFileConfiguration();
        for (IslandPosition islandPositionList : islandPositions) {
            if (islandPositionList.getWorld() == world) {
                int island_number = (int) configLoad.get("World." + world.name() + ".nextAvailableLocation.island_number");
                ConfigurationSection configSection = configLoad.createSection("World." + world.name() + ".nextAvailableLocation");
                configSection.set("x", islandPositionList.getX());
                configSection.set("z", islandPositionList.getZ());
                configSection.set("island_number", (island_number + 1));
            }
        }
        try {
            configLoad.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void setNextAvailableLocation(IslandWorld world, org.bukkit.Location location) {
        for (IslandPosition islandPositionList : islandPositions) {
            if (islandPositionList.getWorld() == world) {
                islandPositionList.setX(location.getX());
                islandPositionList.setZ(location.getZ());
            }
        }
    }


    public synchronized org.bukkit.Location prepareNextAvailableLocation(IslandWorld world) {
        for (IslandPosition islandPositionList : islandPositions) {
            if (islandPositionList.getWorld() == world) {

                Config config_world = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "worlds.yml"));

                FileConfiguration configLoad_world = config_world.getFileConfiguration();
                FileConfiguration configLoad_config = plugin.getConfiguration();
                int x = (int) configLoad_world.get("World." + world.name() + ".nextAvailableLocation.island_number");
                int islandHeight = configLoad_config.getInt("Island.World." + world.name() + ".IslandSpawnHeight", 72);
                while (true) {
                    double r = Math.floor((Math.sqrt(x + 1) - 1) / 2) + 1;
                    double p = (8 * r * (r - 1)) / 2;
                    double en = r * 2;
                    double a = (x - p) % (r * 8);
                    int posX = 0;
                    int posY = 0;
                    int loc = (int) Math.floor(a / (r * 2));
                    switch (loc) {
                        case 0:
                            posX = (int) (a - r);
                            posY = (int) (-r);
                            break;
                        case 1:
                            posX = (int) r;
                            posY = (int) ((a % en) - r);
                            break;
                        case 2:
                            posX = (int) (r - (a % en));
                            posY = (int) r;
                            break;
                        case 3:
                            posX = (int) (-r);
                            posY = (int) (r - (a % en));
                            break;
                        default:
                            System.err.println("[FabledSkyblock][prepareNextAvailableLocation] Erreur dans la spirale, valeur : " + loc);
                            return null;
                    }
                    posX = posX * offset;
                    posY = posY * offset;
                    islandPositionList.setX(posX);
                    islandPositionList.setZ(posY);
                    // Check if there was an island at this position
                    int oldFormatPos = oldSystemIslands.get(world);
                    Location islandLocation = new org.bukkit.Location(plugin.getWorldManager().getWorld(world), islandPositionList.getX(), islandHeight, islandPositionList.getZ());
                    if (posX == 1200 && posY >= 0 && posY <= oldFormatPos) {
                        // We have to save to avoid having two islands at same location
                        setNextAvailableLocation(world, islandLocation);
                        saveNextAvailableLocation(world);
                        x++;
                        continue;
                    }
                    return islandLocation;
                }
            }
        }

        return null;
    }

    public synchronized boolean createIsland(Player player, Structure structure) {
        ScoreboardManager scoreboardManager = plugin.getScoreboardManager();
        VisitManager visitManager = plugin.getVisitManager();
        FileManager fileManager = plugin.getFileManager();
        BanManager banManager = plugin.getBanManager();

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        long amt = 0;

        if (data != null) {
            final int highest = PlayerUtil.getNumberFromPermission(player, "fabledskyblock.limit.create", true, 2);

            if ((amt = data.getIslandCreationCount()) >= highest) {
                plugin.getLanguage().getString("Island.Creator.Error.MaxCreationMessage");
                return false;
            }
        }

        if (fileManager.getConfig(new File(plugin.getDataFolder(), "locations.yml")).getFileConfiguration().getString("Location.Spawn") == null) {
            plugin.getMessageManager().sendMessage(player, plugin.getLanguage().getString("Island.Creator.Error.Message"));
            plugin.getSoundManager().playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

            return false;
        }

        if (data != null)
            data.setIslandCreationCount(amt + 1);

        Island island = new Island(player);
        island.setStructure(structure.getName());
        islandStorage.put(player.getUniqueId(), island);

        for (IslandWorld worldList : IslandWorld.getIslandWorlds())
            prepareIsland(island, worldList);

        if (!visitManager.hasIsland(island.getOwnerUUID())) {
            visitManager.createIsland(island.getOwnerUUID(),
                    new IslandLocation[]{island.getIslandLocation(IslandWorld.Normal, IslandEnvironment.Island), island.getIslandLocation(IslandWorld.Nether, IslandEnvironment.Island),
                            island.getIslandLocation(IslandWorld.End, IslandEnvironment.Island)},
                    island.getSize(), island.getRole(IslandRole.Member).size() + island.getRole(IslandRole.Operator).size() + 1, island.getBankBalance(), visitManager.getIslandSafeLevel(island.getOwnerUUID()), island.getLevel(),
                    island.getMessage(IslandMessage.Signature), island.getStatus());
        }

        if (!banManager.hasIsland(island.getOwnerUUID())) banManager.createIsland(island.getOwnerUUID());

        FileConfiguration configLoad = this.plugin.getConfiguration();

        if (configLoad.getBoolean("Island.Creation.Cooldown.Creation.Enable") && !player.hasPermission("fabledskyblock.bypass.cooldown") && !player.hasPermission("fabledskyblock.bypass.*")
                && !player.hasPermission("fabledskyblock.*"))
            plugin.getCooldownManager().createPlayer(CooldownType.Creation, player);
        if (configLoad.getBoolean("Island.Deletion.Cooldown.Deletion.Enable") && !player.hasPermission("fabledskyblock.bypass.cooldown") && !player.hasPermission("fabledskyblock.bypass.*")
                && !player.hasPermission("fabledskyblock.*"))
            plugin.getCooldownManager().createPlayer(CooldownType.Deletion, player);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> Bukkit.getServer().getPluginManager().callEvent(new IslandCreateEvent(island.getAPIWrapper(), player)));

        data.setIsland(player.getUniqueId());
        data.setOwner(player.getUniqueId());

        Bukkit.getScheduler().runTask(plugin, () -> {
            scoreboardManager.updatePlayerScoreboardType(player);
        });

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            PaperLib.teleportAsync(player, island.getLocation(IslandWorld.Normal, IslandEnvironment.Main));
            player.setFallDistance(0.0F);
        }, configLoad.getInt("Island.Creation.TeleportTimeout") * 20);

        String biomeName = this.plugin.getConfiguration().getString("Island.Biome.Default.Type").toUpperCase();
        CompatibleBiome cBiome;
        try {
            cBiome = CompatibleBiome.valueOf(biomeName);
        } catch (Exception ex) {
            cBiome = CompatibleBiome.PLAINS;
        }
        final CompatibleBiome compatibleBiome = cBiome;

        Bukkit.getServer().getScheduler().runTaskLater(plugin, () ->
                plugin.getBiomeManager().setBiome(island, IslandWorld.Normal, compatibleBiome, () -> {
            if (structure.getCommands() != null) {
                for (String commandList : structure.getCommands()) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), commandList.replace("%player", player.getName()));
                }
            }
        }), 20L);

        // Recalculate island level after 5 seconds
        if (configLoad.getBoolean("Island.Levelling.ScanAutomatically"))
            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> plugin.getLevellingManager().startScan(null, island), 100L);

        return true;
    }

    public synchronized boolean previewIsland(Player player, Structure structure) {
        FileManager fileManager = plugin.getFileManager();

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        FileConfiguration configLang = plugin.getLanguage();
        FileConfiguration configMain = plugin.getConfiguration();


        if (data != null) {
            final int highest = PlayerUtil.getNumberFromPermission(player, "fabledskyblock.limit.create", true, 2);

            if ((data.getIslandCreationCount()) >= highest) {
                plugin.getMessageManager().sendMessage(player, plugin.getLanguage().getString("Island.Creator.Error.MaxCreationMessage"));
                return false;
            }

        }

        if (fileManager.getConfig(new File(plugin.getDataFolder(), "locations.yml")).getFileConfiguration().getString("Location.Spawn") == null) {
            plugin.getMessageManager().sendMessage(player, configLang.getString("Island.Creator.Error.Message"));
            plugin.getSoundManager().playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

            return false;
        }

        Island island = new Island(player);
        island.setStructure(structure.getName());
        islandStorage.put(player.getUniqueId(), island);

        data.setPreview(true);

        for (IslandWorld worldList : IslandWorld.getIslandWorlds())
            prepareIsland(island, worldList);


        Bukkit.getScheduler().callSyncMethod(SkyBlock.getInstance(), () -> {
            PaperLib.teleportAsync(player, island.getLocation(IslandWorld.Normal, IslandEnvironment.Island));
            player.setGameMode(GameMode.SPECTATOR);
            return true;
        });

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (data.isPreview()) {
                Location spawn = fileManager.getLocation(fileManager.getConfig(new File(plugin.getDataFolder(), "locations.yml")), "Location.Spawn", true);
                PaperLib.teleportAsync(player, spawn);
                player.setGameMode(GameMode.SURVIVAL);
                data.setIsland(null);
                islandStorage.remove(player.getUniqueId(), island);
                deleteIsland(island, true);
                plugin.getMessageManager().sendMessage(player, configLang.getString("Island.Preview.Timeout.Message"));
                data.setPreview(false);
            }
        }, configMain.getInt("Island.Preview.Time") * 20);


        String defaultMessage = configLang.getString("Command.Island.Preview.Confirmation.Message")
                .replaceAll("%time", "" + configMain.get("Island.Preview.Time"));

        defaultMessage = defaultMessage.replace("\\n", "\n");

        for (String message : defaultMessage.split("\n")) {
            ChatComponent confirmation = null, cancelation = null;

            if (message.contains("%confirm")) {
                message = message.replace("%confirm", "");
                confirmation = new ChatComponent(configLang.getString("Command.Island.Preview.Confirmation.Word.Confirm").toUpperCase() + "     ",
                        true, net.md_5.bungee.api.ChatColor.GREEN,
                        new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/island preview confirm"),
                        new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new ComponentBuilder(
                                        net.md_5.bungee.api.ChatColor.translateAlternateColorCodes(
                                                '&',
                                                configLang.getString("Command.Island.Preview.Confirmation.Word.TutorialConfirm")))
                                        .create()
                        ));
            }

            if (message.contains("%cancel")) {
                message = message.replace("%cancel", "");
                cancelation = new ChatComponent(configLang.getString("Command.Island.Preview.Confirmation.Word.Cancel").toUpperCase(),
                        true, net.md_5.bungee.api.ChatColor.GREEN,
                        new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/island preview cancel"),
                        new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new ComponentBuilder(
                                        net.md_5.bungee.api.ChatColor.translateAlternateColorCodes(
                                                '&',
                                                configLang.getString("Command.Island.Preview.Confirmation.Word.TutorialCancel")))
                                        .create()
                        ));
            }

            TextComponent confirmationMessage = new TextComponent(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', message));
            if (confirmation != null) {
                confirmationMessage.addExtra(confirmation.getTextComponent());
            }
            if (cancelation != null) {
                confirmationMessage.addExtra(cancelation.getTextComponent());
            }

            player.spigot().sendMessage(confirmationMessage);

        }

        data.setConfirmation(Confirmation.Preview);
        data.setConfirmationTime(configMain.getInt("Island.Preview.Time"));


        FileConfiguration configLoad = this.plugin.getConfiguration();
        if (configLoad.getBoolean("Island.Preview.Cooldown.Enable") && !player.hasPermission("fabledskyblock.bypass.cooldown")
                && !player.hasPermission("fabledskyblock.bypass.*") && !player.hasPermission("fabledskyblock.*")) {
            plugin.getCooldownManager().createPlayer(CooldownType.Preview, player);
        }

        return true;
    }

    public synchronized void giveOwnership(Island island, org.bukkit.OfflinePlayer player) {
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        CooldownManager cooldownManager = plugin.getCooldownManager();
        FileManager fileManager = plugin.getFileManager();

        if (island.isDeleted()) {
            return;
        }

        if (island.hasRole(IslandRole.Member, player.getUniqueId()) || island.hasRole(IslandRole.Operator, player.getUniqueId())) {
            UUID uuid2 = island.getOwnerUUID();

            island.save();
            island.setOwnerUUID(player.getUniqueId());
            island.getAPIWrapper().setPlayer(player);

            IslandLevel level = island.getLevel();
            level.save();
            level.setOwnerUUID(player.getUniqueId());

            FileConfiguration configLoad = plugin.getConfiguration();

            if (configLoad.getBoolean("Island.Ownership.Password.Reset")) {
                island.setPassword(null);
            }

            File oldCoopDataFile = new File(new File(plugin.getDataFolder().toString() + "/coop-data"), uuid2.toString() + ".yml");
            fileManager.unloadConfig(oldCoopDataFile);

            if (fileManager.isFileExist(oldCoopDataFile)) {
                File newCoopDataFile = new File(new File(plugin.getDataFolder().toString() + "/coop-data"), player.getUniqueId().toString() + ".yml");

                fileManager.unloadConfig(newCoopDataFile);
                oldCoopDataFile.renameTo(newCoopDataFile);
            }

            File oldLevelDataFile = new File(new File(plugin.getDataFolder().toString() + "/level-data"), uuid2.toString() + ".yml");
            File newLevelDataFile = new File(new File(plugin.getDataFolder().toString() + "/level-data"), player.getUniqueId().toString() + ".yml");

            fileManager.unloadConfig(oldLevelDataFile);
            fileManager.unloadConfig(newLevelDataFile);
            oldLevelDataFile.renameTo(newLevelDataFile);

            File oldSettingDataFile = new File(new File(plugin.getDataFolder().toString() + "/setting-data"), uuid2.toString() + ".yml");
            File newSettingDataFile = new File(new File(plugin.getDataFolder().toString() + "/setting-data"), player.getUniqueId().toString() + ".yml");

            fileManager.unloadConfig(oldSettingDataFile);
            fileManager.unloadConfig(newSettingDataFile);
            oldSettingDataFile.renameTo(newSettingDataFile);

            File oldIslandDataFile = new File(new File(plugin.getDataFolder().toString() + "/island-data"), uuid2.toString() + ".yml");
            File newIslandDataFile = new File(new File(plugin.getDataFolder().toString() + "/island-data"), player.getUniqueId().toString() + ".yml");

            fileManager.unloadConfig(oldIslandDataFile);
            fileManager.unloadConfig(newIslandDataFile);
            oldIslandDataFile.renameTo(newIslandDataFile);

            if (this.plugin.getConfiguration()
                    .getBoolean("Island.Challenge.PerIsland", true)){
                File oldChallengeDataFile = new File(new File(plugin.getDataFolder().toString() + "/challenge-data"), uuid2.toString() + ".yml");
                File newChallengeDataFile = new File(new File(plugin.getDataFolder().toString() + "/challenge-data"), player.getUniqueId().toString() + ".yml");

                fileManager.unloadConfig(oldChallengeDataFile);
                fileManager.unloadConfig(newChallengeDataFile);
                oldChallengeDataFile.renameTo(newChallengeDataFile);
            }

            plugin.getVisitManager().transfer(uuid2, player.getUniqueId());
            plugin.getBanManager().transfer(uuid2, player.getUniqueId());
            plugin.getInviteManager().tranfer(uuid2, player.getUniqueId());

            org.bukkit.OfflinePlayer player1 = Bukkit.getServer().getOfflinePlayer(uuid2);

            cooldownManager.transferPlayer(CooldownType.Levelling, player1, player);
            cooldownManager.transferPlayer(CooldownType.Ownership, player1, player);

            if (configLoad.getBoolean("Island.Ownership.Transfer.Operator")) {
                island.setRole(IslandRole.Operator, uuid2);
            } else {
                island.setRole(IslandRole.Member, uuid2);
            }

            if (island.hasRole(IslandRole.Member, player.getUniqueId())) {
                island.removeRole(IslandRole.Member, player.getUniqueId());
            } else {
                island.removeRole(IslandRole.Operator, player.getUniqueId());
            }

            removeIsland(uuid2);
            islandStorage.put(player.getUniqueId(), island);

            Bukkit.getServer().getPluginManager().callEvent(new IslandOwnershipTransferEvent(island.getAPIWrapper(), player));

            ArrayList<UUID> islandMembers = new ArrayList<>();
            islandMembers.addAll(island.getRole(IslandRole.Member));
            islandMembers.addAll(island.getRole(IslandRole.Operator));
            islandMembers.add(player.getUniqueId());

            for (UUID islandMemberList : islandMembers) {
                Player targetPlayer = Bukkit.getServer().getPlayer(islandMemberList);

                if (targetPlayer == null) {
                    File configFile = new File(new File(plugin.getDataFolder().toString() + "/player-data"), islandMemberList.toString() + ".yml");
                    configLoad = YamlConfiguration.loadConfiguration(configFile);
                    configLoad.set("Island.Owner", player.getUniqueId().toString());

                    try {
                        configLoad.save(configFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    PlayerData playerData = playerDataManager.getPlayerData(targetPlayer);
                    playerData.setOwner(player.getUniqueId());
                    playerData.setIsland(player.getUniqueId());
                    playerData.save();
                }
            }
        }
    }

    public synchronized boolean deleteIsland(Island island, boolean force) {
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        CooldownManager cooldownManager = plugin.getCooldownManager();
        FileManager fileManager = plugin.getFileManager();
        WorldManager worldManager = plugin.getWorldManager();

        if (!force) {
            PlayerData data = playerDataManager.getPlayerData(island.getOwnerUUID());

            if (data != null) {

                final Player player = data.getPlayer();

                if (player != null) {

                    long amt = 0;
                    final int highest = PlayerUtil.getNumberFromPermission(player, "fabledskyblock.limit.delete", true, 1);

                    if ((amt = data.getIslandDeletionCount()) >= highest) return false;

                    data.setIslandDeletionCount(amt + 1);
                    data.deleteTransactions();
                }
            }
        }

        FileConfiguration configLoad = plugin.getConfiguration();

        if (configLoad.getBoolean("Island.Deletion.DeleteIsland", true)) {
            startDeletion(island, worldManager);
        }

        plugin.getVisitManager().deleteIsland(island.getOwnerUUID());
        plugin.getBanManager().deleteIsland(island.getOwnerUUID());
        plugin.getVisitManager().removeVisitors(island, VisitManager.Removal.Deleted);

        org.bukkit.OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(island.getOwnerUUID());
        cooldownManager.removeCooldownPlayer(CooldownType.Levelling, offlinePlayer);
        cooldownManager.removeCooldownPlayer(CooldownType.Ownership, offlinePlayer);

        boolean cooldownCreationEnabled = configLoad.getBoolean("Island.Creation.Cooldown.Creation.Enable");
        boolean cooldownDeletionEnabled = configLoad.getBoolean("Island.Creation.Cooldown.Deletion.Enable");
        boolean cooldownPreviewEnabled = configLoad.getBoolean("Island.Preview.Cooldown.Enable");

        for (Player player : Bukkit.getOnlinePlayers()) {
            if ((island.hasRole(IslandRole.Member, player.getUniqueId()) ||
                    island.hasRole(IslandRole.Operator, player.getUniqueId()) ||
                    island.hasRole(IslandRole.Owner, player.getUniqueId())) &&
                    playerDataManager.hasPlayerData(player)) {
                PlayerData playerData = playerDataManager.getPlayerData(player);
                playerData.setOwner(null);
                playerData.setMemberSince(null);
                playerData.setChat(false);
                playerData.save();

                if (isPlayerAtIsland(island, player)) {
                    LocationUtil.teleportPlayerToSpawn(player);
                }

                // TODO - Find a way to delete also offline players
                if (configLoad.getBoolean("Island.Deletion.ClearInventory", false) && !playerData.isPreview()) {
                    player.getInventory().clear();
                }
                if (configLoad.getBoolean("Island.Deletion.ClearEnderChest", false) && !playerData.isPreview()) {
                    player.getEnderChest().clear();
                }

                if (cooldownCreationEnabled) {
                    if (!player.hasPermission("fabledskyblock.bypass.cooldown") && !player.hasPermission("fabledskyblock.bypass.*") && !player.hasPermission("fabledskyblock.*")) {
                        plugin.getCooldownManager().createPlayer(CooldownType.Creation, player);
                    }
                }
                if (cooldownDeletionEnabled) {
                    if (!player.hasPermission("fabledskyblock.bypass.cooldown") && !player.hasPermission("fabledskyblock.bypass.*") && !player.hasPermission("fabledskyblock.*")) {
                        plugin.getCooldownManager().createPlayer(CooldownType.Deletion, player);
                    }
                }
                if (cooldownPreviewEnabled) {
                    if (!player.hasPermission("fabledskyblock.bypass.cooldown") && !player.hasPermission("fabledskyblock.bypass.*") && !player.hasPermission("fabledskyblock.*")) {
                        plugin.getCooldownManager().createPlayer(CooldownType.Preview, player);
                    }
                }
            }

            InviteManager inviteManager = plugin.getInviteManager();

            if (inviteManager.hasInvite(player.getUniqueId())) {
                Invite invite = inviteManager.getInvite(player.getUniqueId());

                if (invite.getOwnerUUID().equals(island.getOwnerUUID())) {
                    inviteManager.removeInvite(player.getUniqueId());
                }
            }
        }

        fileManager.deleteConfig(new File(new File(plugin.getDataFolder().toString() + "/coop-data"), island.getOwnerUUID().toString() + ".yml"));
        fileManager.deleteConfig(new File(new File(plugin.getDataFolder().toString() + "/level-data"), island.getOwnerUUID().toString() + ".yml"));
        fileManager.deleteConfig(new File(new File(plugin.getDataFolder().toString() + "/setting-data"), island.getOwnerUUID().toString() + ".yml"));
        fileManager.deleteConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"), island.getOwnerUUID().toString() + ".yml"));
        if (this.plugin.getConfiguration()
                .getBoolean("Island.Challenge.PerIsland", true)){
            fileManager.deleteConfig(new File(new File(plugin.getDataFolder().toString() + "/challenge-data"), island.getOwnerUUID().toString() + ".yml"));
        }

        Bukkit.getServer().getPluginManager().callEvent(new IslandDeleteEvent(island.getAPIWrapper()));

        islandStorage.remove(island.getOwnerUUID());
        return true;
    }

    private void startDeletion(Island island, WorldManager worldManager) {
        final Map<World, List<CachedChunk>> cachedChunks = new HashMap<>(3);

        for (IslandWorld worldList : IslandWorld.getIslandWorlds()) {

            final Location location = island.getLocation(worldList, IslandEnvironment.Island);

            if (location == null) continue;

            final World world = worldManager.getWorld(worldList);
    
            ChunkLoader.startChunkLoading(island, IslandWorld.Normal, plugin.isPaperAsync(), (chunks) -> {
                cachedChunks.put(world, chunks);
                ChunkDeleteSplitter.startDeletion(cachedChunks);
            }, null);
        }

    }

    public synchronized void deleteIslandData(UUID uuid) {
        FileManager fileManager = plugin.getFileManager();
        fileManager.deleteConfig(new File(plugin.getDataFolder().toString() + "/island-data", FastUUID.toString(uuid) + ".yml"));
        fileManager.deleteConfig(new File(plugin.getDataFolder().toString() + "/ban-data", FastUUID.toString(uuid) + ".yml"));
        fileManager.deleteConfig(new File(plugin.getDataFolder().toString() + "/coop-data", FastUUID.toString(uuid) + ".yml"));
        fileManager.deleteConfig(new File(plugin.getDataFolder().toString() + "/level-data", FastUUID.toString(uuid) + ".yml"));
        fileManager.deleteConfig(new File(plugin.getDataFolder().toString() + "/setting-data", FastUUID.toString(uuid) + ".yml"));
        fileManager.deleteConfig(new File(plugin.getDataFolder().toString() + "/visit-data", FastUUID.toString(uuid) + ".yml"));
        if (this.plugin.getConfiguration()
                .getBoolean("Island.Challenge.PerIsland", true)){
            fileManager.deleteConfig(new File(plugin.getDataFolder().toString() + "/challenge-data", FastUUID.toString(uuid) + ".yml"));
        }
    }

    public void loadIsland(org.bukkit.OfflinePlayer player) {
        VisitManager visitManager = plugin.getVisitManager();
        FileManager fileManager = plugin.getFileManager();
        BanManager banManager = plugin.getBanManager();

        UUID islandOwnerUUID = null;

        Config config = fileManager.getConfig(new File(new File(plugin.getDataFolder().toString() + "/player-data"), player.getUniqueId().toString() + ".yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (isIslandExist(player.getUniqueId())) {
            if (configLoad.getString("Island.Owner") == null || !configLoad.getString("Island.Owner").equals(player.getUniqueId().toString())) {
                deleteIslandData(player.getUniqueId());
                configLoad.set("Island.Owner", null);

                return;
            }

            islandOwnerUUID = player.getUniqueId();
        } else {
            if (configLoad.getString("Island.Owner") != null) {
                islandOwnerUUID = FastUUID.parseUUID(configLoad.getString("Island.Owner"));
            }
        }
    
        if (islandOwnerUUID != null && !containsIsland(islandOwnerUUID)) {
            config = fileManager.getConfig(new File(plugin.getDataFolder().toString() + "/island-data", islandOwnerUUID.toString() + ".yml"));
        
            if (config.getFileConfiguration().getString("Location") == null) {
                deleteIslandData(islandOwnerUUID);
                configLoad.set("Island.Owner", null);
            
                return;
            }
        
            Island island = new Island(Bukkit.getServer().getOfflinePlayer(islandOwnerUUID));
            islandStorage.put(islandOwnerUUID, island);
        
            for (IslandWorld worldList : IslandWorld.getIslandWorlds()) {
                prepareIsland(island, worldList);
            }
        
            if (!visitManager.hasIsland(island.getOwnerUUID())) {
                visitManager.createIsland(island.getOwnerUUID(),
                        new IslandLocation[]{island.getIslandLocation(IslandWorld.Normal, IslandEnvironment.Island), island.getIslandLocation(IslandWorld.Nether, IslandEnvironment.Island),
                                island.getIslandLocation(IslandWorld.End, IslandEnvironment.Island)},
                        island.getSize(), island.getRole(IslandRole.Member).size() + island.getRole(IslandRole.Operator).size() + 1, island.getBankBalance(), visitManager.getIslandSafeLevel(island.getOwnerUUID()),
                        island.getLevel(), island.getMessage(IslandMessage.Signature), island.getStatus());
            }
        
            if (!banManager.hasIsland(island.getOwnerUUID())) {
                banManager.createIsland(island.getOwnerUUID());
            }
        
            Bukkit.getScheduler().runTask(plugin, () ->
                    Bukkit.getServer().getPluginManager().callEvent(new IslandLoadEvent(island.getAPIWrapper())));
        }
    }
    
    public void adjustAllIslandsSize(@Nonnull int diff, @Nullable Runnable callback) {
        FileManager fileManager = plugin.getFileManager();
        File islandConfigDir = new File(plugin.getDataFolder().toString() + "/island-data");
    
        if (!islandConfigDir.exists()) return;
    
        File[] files = islandConfigDir.listFiles();
        if(files == null) return;
    
        for (File file : files) {
            if (file != null && file.getName().contains(".yml") && file.getName().length() > 35) {
                try {
                    UUID islandOwnerUUID = FastUUID.parseUUID(file.getName().split("\\.")[0]);
                    
                    Island island = getIslandByOwner(Bukkit.getOfflinePlayer(islandOwnerUUID));
                    
                    if(island != null) {
                        island.setSize(island.getSize() + diff);
                        island.save();
                    } else {
                        loadIsland(file);
                        island = getIslandByOwner(Bukkit.getOfflinePlayer(islandOwnerUUID));
    
                        island.setSize(island.getSize() + diff);
                        island.save();
                        unloadIsland(island, null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        if(callback != null) {
            callback.run();
        }
    }
    
    public void setAllIslandsSize(@Nonnull int size, @Nullable Runnable callback) {
        File islandConfigDir = new File(plugin.getDataFolder().toString() + "/island-data");
        
        if (!islandConfigDir.exists()) return;
        
        File[] files = islandConfigDir.listFiles();
        if(files == null) return;
        
        for (File file : files) {
            if (file != null && file.getName().contains(".yml") && file.getName().length() > 35) {
                try {
                    UUID islandOwnerUUID = FastUUID.parseUUID(file.getName().split("\\.")[0]);
                    
                    Island island = getIslandByOwner(Bukkit.getOfflinePlayer(islandOwnerUUID));
                    
                    if(island != null) {
                        island.setSize(size);
                        island.save();
                    } else {
                        loadIsland(file);
                        island = getIslandByOwner(Bukkit.getOfflinePlayer(islandOwnerUUID));
                        
                        island.setSize(size);
                        island.save();
                        unloadIsland(island, null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    
        if(callback != null) {
            callback.run();
        }
    }
    
    public void loadIsland(File islandFile) {
        VisitManager visitManager = plugin.getVisitManager();
        FileManager fileManager = plugin.getFileManager();
        BanManager banManager = plugin.getBanManager();
        
        Config config = fileManager.getConfig(islandFile);
        FileConfiguration configLoad = config.getFileConfiguration();
    
        UUID islandOwnerUUID = FastUUID.parseUUID(islandFile.getName().split("\\.")[0]);
    
        if (config.getFileConfiguration().getString("Location") == null) {
            deleteIslandData(islandOwnerUUID);
            configLoad.set("Island.Owner", null);
        
            return;
        }
    
        Island island = new Island(Bukkit.getServer().getOfflinePlayer(islandOwnerUUID));
        islandStorage.put(islandOwnerUUID, island);
    
        for (IslandWorld worldList : IslandWorld.getIslandWorlds()) {
            prepareIsland(island, worldList);
        }
    
        if (!visitManager.hasIsland(island.getOwnerUUID())) {
            visitManager.createIsland(island.getOwnerUUID(),
                    new IslandLocation[]{island.getIslandLocation(IslandWorld.Normal, IslandEnvironment.Island), island.getIslandLocation(IslandWorld.Nether, IslandEnvironment.Island),
                            island.getIslandLocation(IslandWorld.End, IslandEnvironment.Island)},
                    island.getSize(), island.getRole(IslandRole.Member).size() + island.getRole(IslandRole.Operator).size() + 1, island.getBankBalance(), visitManager.getIslandSafeLevel(island.getOwnerUUID()),
                    island.getLevel(), island.getMessage(IslandMessage.Signature), island.getStatus());
        }
    
        if (!banManager.hasIsland(island.getOwnerUUID())) {
            banManager.createIsland(island.getOwnerUUID());
        }
    
        Bukkit.getScheduler().runTask(plugin, () ->
                Bukkit.getServer().getPluginManager().callEvent(new IslandLoadEvent(island.getAPIWrapper())));
    }

    /**
     * The old island position system was not good, it always create islands at x = 1200 and z starting at 0 and increasing by 1200<br />
     * This method will get the nextAvailableLocation for normal, nether and end islands in worlds.yml file
     * to avoid creating island where an existing island was
     */
    public void loadIslandPositions() {
        oldSystemIslands = new HashMap<>();

        FileManager fileManager = plugin.getFileManager();
        Config config = fileManager.getConfig(new File(plugin.getDataFolder().toString() + "/worlds.yml"));
        FileConfiguration fileConfig = config.getFileConfiguration();
        Config config2 = fileManager.getConfig(new File(plugin.getDataFolder().toString() + "/worlds.oldformat.yml"));
        FileConfiguration fileConfig2 = config2.getFileConfiguration();

        // TODO Find a way to automatically
        int normalZ = 0;
        int netherZ = 0;
        int endZ = 0;
        if (!config2.getFile().exists()) {
            // Old data
            Bukkit.getLogger().info("[FabledSkyblock] Old format detected, please wait ...");
            if (fileConfig.contains("World.Normal.nextAvailableLocation"))
                normalZ = fileConfig.getInt("World.Normal.nextAvailableLocation.z");
            if (fileConfig.contains("World.Nether.nextAvailableLocation"))
                netherZ = fileConfig.getInt("World.Nether.nextAvailableLocation.z");
            if (fileConfig.contains("World.End.nextAvailableLocation"))
                endZ = fileConfig.getInt("World.End.nextAvailableLocation.z");
            // Save
            fileConfig2.set("Normal", normalZ);
            fileConfig2.set("Nether", netherZ);
            fileConfig2.set("End", endZ);
            try {
                fileConfig2.save(config2.getFile());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Bukkit.getLogger().info("[FabledSkyblock] Done ! Got normalZ = " + normalZ + ", netherZ = " + netherZ + ", endZ = " + endZ);
        } else {
            // Load datas
            normalZ = fileConfig2.getInt("Normal");
            netherZ = fileConfig2.getInt("Nether");
            endZ = fileConfig2.getInt("End");
        }
        oldSystemIslands.put(IslandWorld.Normal, normalZ);
        oldSystemIslands.put(IslandWorld.Nether, netherZ);
        oldSystemIslands.put(IslandWorld.End, endZ);
    }

    public void loadIslandAtLocation(Location location) {
        FileManager fileManager = plugin.getFileManager();
        File configFile = new File(plugin.getDataFolder().toString() + "/island-data");

        if (!configFile.exists()) return;
        
        File[] files = configFile.listFiles();
        if(files == null) return;

        for (File file : files) {
            if (file != null && file.getName().contains(".yml") && file.getName().length() > 35) {
                try {
                    Config config = new FileManager.Config(fileManager, file);
                    FileConfiguration configLoad = config.getFileConfiguration();

                    int size = 10;
                    if (configLoad.getString("Size") != null) {
                        size = configLoad.getInt("Size");
                    }

                    Location islandLocation = fileManager.getLocation(config, "Location.Normal.Island", false);

                    if (LocationUtil.isLocationInLocationRadius(location, islandLocation, size)) {
                        loadIsland(file);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void unloadIsland(Island island, org.bukkit.OfflinePlayer player) {
        if (island.isAlwaysLoaded()) return;
        ScoreboardManager scoreboardManager = plugin.getScoreboardManager();
        FileManager fileManager = plugin.getFileManager();

        if (island.isDeleted()) return;

        island.save();

        int islandVisitors = getVisitorsAtIsland(island).size();
        boolean unloadIsland = true;

        for (Player loopPlayer : Bukkit.getOnlinePlayers()) {
            if (loopPlayer == null || (player != null && player.getUniqueId().equals(loopPlayer.getUniqueId()))) {
                continue;
            }

            if (island.hasRole(IslandRole.Member, loopPlayer.getUniqueId()) ||
                    island.hasRole(IslandRole.Operator, loopPlayer.getUniqueId()) ||
                    island.hasRole(IslandRole.Owner, loopPlayer.getUniqueId()) ||
                    island.getCoopType(loopPlayer.getUniqueId()) == IslandCoop.NORMAL) {
                scoreboardManager.updatePlayerScoreboardType(loopPlayer);

                unloadIsland = false;
            }
        }

        if (!unloadIsland) {
            return;
        }

        unloadIsland = this.plugin.getConfiguration().getBoolean("Island.Visitor.Unload");

        if (unloadIsland) {
            VisitManager visitManager = plugin.getVisitManager();
            visitManager.removeVisitors(island, VisitManager.Removal.Unloaded);
            visitManager.unloadIsland(island.getOwnerUUID());

            BanManager banManager = plugin.getBanManager();
            banManager.unloadIsland(island.getOwnerUUID());
        } else {
            int nonIslandMembers = islandVisitors - getCoopPlayersAtIsland(island).size();

            if (nonIslandMembers <= 0) {
                if (island.getStatus().equals(IslandStatus.OPEN)) {
                    return;
                } else if (player != null) {
                    removeCoopPlayers(island, player.getUniqueId());
                }
            } else {
                return;
            }
        }

        fileManager.unloadConfig(new File(new File(plugin.getDataFolder().toString() + "/coop-data"), island.getOwnerUUID() + ".yml"));
        fileManager.unloadConfig(new File(new File(plugin.getDataFolder().toString() + "/setting-data"), island.getOwnerUUID() + ".yml"));
        fileManager.unloadConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"), island.getOwnerUUID() + ".yml"));

        if (this.plugin.getConfiguration()
                .getBoolean("Island.Challenge.PerIsland", true)){
            fileManager.unloadConfig(new File(new File(plugin.getDataFolder().toString() + "/challenge-data"), island.getOwnerUUID() + ".yml"));
        }

        islandStorage.remove(island.getOwnerUUID());

        Bukkit.getScheduler().runTask(plugin, () -> {
           Bukkit.getServer().getPluginManager().callEvent(new IslandUnloadEvent(island.getAPIWrapper()));
        });
    }

    public void prepareIsland(Island island, IslandWorld world) {
        WorldManager worldManager = plugin.getWorldManager();
        FileManager fileManager = plugin.getFileManager();

        Config config = fileManager.getConfig(new File(plugin.getDataFolder().toString() + "/island-data", island.getOwnerUUID() + ".yml"));

        if (config.getFileConfiguration().getString("Location." + world.name()) == null) {
            pasteStructure(island, world);
            return;
        }

        for (IslandEnvironment environmentList : IslandEnvironment.values()) {
            org.bukkit.Location location;

            if (environmentList == IslandEnvironment.Island) {
                location = fileManager.getLocation(config, "Location." + world.name() + "." + environmentList.name(), true);
            } else {
                location = fileManager.getLocation(config, "Location." + world.name() + ".Spawn." + environmentList.name(), true);
            }

            island.addLocation(world, environmentList, worldManager.getLocation(location, world));
        }

        Bukkit.getServer().getScheduler().runTask(plugin, () -> removeSpawnProtection(island.getLocation(world, IslandEnvironment.Island)));
    }

    public void resetIsland(Island island) {
        for (IslandWorld worldList : IslandWorld.getIslandWorlds()) {
            if (isIslandWorldUnlocked(island, worldList)) {
                pasteStructure(island, worldList);
            }
        }
    }

    public void pasteStructure(Island island, IslandWorld world) {
        if (!isIslandWorldUnlocked(island, world)) return;

        StructureManager structureManager = plugin.getStructureManager();
        FileManager fileManager = plugin.getFileManager();

        Structure structure;

        if (island.getStructure() != null && !island.getStructure().isEmpty() && structureManager.containsStructure(island.getStructure())) {
            structure = structureManager.getStructure(island.getStructure());
        } else {
            structure = structureManager.getStructures().get(0);
        }

        org.bukkit.Location islandLocation = prepareNextAvailableLocation(world);

        Config config = fileManager.getConfig(new File(plugin.getDataFolder().toString() + "/island-data", island.getOwnerUUID() + ".yml"));

        for (IslandEnvironment environmentList : IslandEnvironment.values()) {
            if (environmentList == IslandEnvironment.Island) {
                island.addLocation(world, environmentList, islandLocation);
                fileManager.setLocation(config, "Location." + world.name() + "." + environmentList.name(), islandLocation, true);
            } else {
                island.addLocation(world, environmentList, islandLocation.clone().add(0.5D, 0.0D, 0.5D));
                fileManager.setLocation(config, "Location." + world.name() + ".Spawn." + environmentList.name(), islandLocation.clone().add(0.5D, 0.0D, 0.5D), true);
            }
        }

        if (this.plugin.getConfiguration().getBoolean("Island.Spawn.Protection")) {
            Bukkit.getServer().getScheduler().runTask(plugin, () -> islandLocation.clone().subtract(0.0D, 1.0D, 0.0D).getBlock().setType(Material.STONE));
        }

        try {
            String structureFileName = null;
            switch (world) {
                case Normal:
                    structureFileName = structure.getOverworldFile();
                    break;
                case Nether:
                    structureFileName = structure.getNetherFile();
                    break;
                case End:
                    structureFileName = structure.getEndFile();
                    break;
            }

            boolean isStructureFile = structureFileName.endsWith(".structure");
            File structureFile = new File(new File(plugin.getDataFolder().toString() + "/" + (isStructureFile ? "structures" : "schematics")), structureFileName);

            Float[] direction;
            if (isStructureFile) {
                direction = StructureUtil.pasteStructure(StructureUtil.loadStructure(structureFile), island.getLocation(world, IslandEnvironment.Island), BlockDegreesType.ROTATE_360);
            } else {
                direction = SchematicUtil.pasteSchematic(structureFile, island.getLocation(world, IslandEnvironment.Island));
            }

            org.bukkit.Location spawnLocation = island.getLocation(world, IslandEnvironment.Main).clone();
            spawnLocation.setYaw(direction[0]);
            spawnLocation.setPitch(direction[1]);
            island.setLocation(world, IslandEnvironment.Main, spawnLocation);
            island.setLocation(world, IslandEnvironment.Visitor, spawnLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setNextAvailableLocation(world, islandLocation);
        saveNextAvailableLocation(world);
    }

    /**
     * Unlocks an island world and pastes the island structure there
     *
     * @param island      The island to unlock for
     * @param islandWorld The island world type to unlock
     */
    public void unlockIslandWorld(Island island, IslandWorld islandWorld) {
        FileManager fileManager = plugin.getFileManager();
        Config islandData = fileManager.getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"), island.getOwnerUUID().toString() + ".yml"));
        FileConfiguration configLoadIslandData = islandData.getFileConfiguration();

        configLoadIslandData.set("Unlocked." + islandWorld.name(), true);

        pasteStructure(island, islandWorld);

        // Recalculate island level after 5 seconds
        if (plugin.getConfiguration().getBoolean("Island.Levelling.ScanAutomatically")) {
            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> plugin.getLevellingManager().startScan(null, island), 100L);
        }
    }

    /**
     * Checks if an island world is unlocked
     *
     * @param island      The island to check
     * @param islandWorld The island world to check
     * @return true if the island world is unlocked, otherwise false
     */
    public boolean isIslandWorldUnlocked(Island island, IslandWorld islandWorld) {
        if (islandWorld == IslandWorld.Normal) return true;

        FileManager fileManager = plugin.getFileManager();
        Config islandData = fileManager.getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"), island.getOwnerUUID().toString() + ".yml"));
        FileConfiguration configLoadIslandData = islandData.getFileConfiguration();
        boolean unlocked = configLoadIslandData.getBoolean("Unlocked." + islandWorld.name());

        if (!unlocked) {
            FileConfiguration configLoad = plugin.getConfiguration();
            double price = configLoad.getDouble("Island.World." + islandWorld.name() + ".UnlockPrice");
            if (price == -1) unlocked = true;
        }

        return unlocked;
    }

    public Set<UUID> getVisitorsAtIsland(Island island) {
        Map<UUID, PlayerData> playerDataStorage = plugin.getPlayerDataManager().getPlayerData();
        Set<UUID> islandVisitors = new HashSet<>();

        for (UUID playerDataStorageList : playerDataStorage.keySet()) {
            PlayerData playerData = playerDataStorage.get(playerDataStorageList);
            UUID islandOwnerUUID = playerData.getIsland();

            if (islandOwnerUUID != null && islandOwnerUUID.equals(island.getOwnerUUID())) {
                if (playerData.getOwner() == null || !playerData.getOwner().equals(island.getOwnerUUID())) {
                    if (Bukkit.getServer().getPlayer(playerDataStorageList) != null) {
                        islandVisitors.add(playerDataStorageList);
                    }
                }
            }
        }

        return islandVisitors;
    }

    public void visitIsland(Player player, Island island) {
        ScoreboardManager scoreboardManager = plugin.getScoreboardManager();
        FileConfiguration configLoad = plugin.getLanguage();

        if (island.hasRole(IslandRole.Member, player.getUniqueId()) || island.hasRole(IslandRole.Operator, player.getUniqueId()) || island.hasRole(IslandRole.Owner, player.getUniqueId())) {
            Location loc = island.getLocation(IslandWorld.Normal, IslandEnvironment.Main);
            if(loc != null){
                PaperLib.teleportAsync(player, loc);
                if(!configLoad.getBoolean("Island.Teleport.FallDamage", true)){
                    player.setFallDistance(0.0F);
                }
            } else {
                player.sendMessage(plugin.formatText(plugin.getLanguage().getString("Island.Teleport.Unsafe.Message")));
            }
        } else {
            int islandVisitors = getVisitorsAtIsland(island).size();

            if (islandVisitors == 0) {
                for (Player loopPlayer : Bukkit.getOnlinePlayers()) {
                    PlayerData targetPlayerData = plugin.getPlayerDataManager().getPlayerData(loopPlayer);

                    if (targetPlayerData != null &&
                            targetPlayerData.getOwner() != null &&
                            targetPlayerData.getOwner().equals(island.getOwnerUUID())) {
                        scoreboardManager.updatePlayerScoreboardType(loopPlayer);
                    }
                }
            }
            Location loc = island.getLocation(IslandWorld.Normal, IslandEnvironment.Visitor);
            if (!player.getGameMode().equals(GameMode.CREATIVE) && !player.getGameMode().equals(GameMode.SPECTATOR)) {
                if(plugin.getConfiguration().getBoolean("Island.Teleport.SafetyCheck", true)) {
                    Location safeLoc = LocationUtil.getSafeLocation(loc);
                    if (safeLoc != null) {
                        loc = safeLoc;
                    }
                }
            }
            if(loc != null){
                PaperLib.teleportAsync(player, loc);
                if(!configLoad.getBoolean("Island.Teleport.FallDamage", true)){
                    player.setFallDistance(0.0F);
                }
            } else {
                player.sendMessage(plugin.formatText(plugin.getLanguage().getString("Command.Island.Teleport.Unsafe.Message")));
            }
            if(!configLoad.getBoolean("Island.Teleport.FallDamage", true)){
                player.setFallDistance(0.0F);
            }

            List<String> islandWelcomeMessage = island.getMessage(IslandMessage.Welcome);

            if (this.plugin.getConfiguration().getBoolean("Island.Visitor.Welcome.Enable") && islandWelcomeMessage.size() != 0) {
                for (String islandWelcomeMessageList : islandWelcomeMessage) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', islandWelcomeMessageList));
                }
            }
        }

        player.closeInventory();
    }

    public void closeIsland(Island island) {
        MessageManager messageManager = plugin.getMessageManager();
        FileConfiguration configLoad = plugin.getLanguage();

        island.setStatus(IslandStatus.CLOSED);

        UUID islandOwnerUUID = island.getOwnerUUID();
        Player islandOwnerPlayer = Bukkit.getServer().getPlayer(islandOwnerUUID);
        String islandOwnerPlayerName;

        if (islandOwnerPlayer == null) {
            islandOwnerPlayerName = new OfflinePlayer(islandOwnerUUID).getName();
        } else {
            islandOwnerPlayerName = islandOwnerPlayer.getName();
        }

        for (UUID visitor : getVisitorsAtIsland(island)) {
            if (!island.isCoopPlayer(visitor)) {
                Player targetPlayer = Bukkit.getServer().getPlayer(visitor);
                LocationUtil.teleportPlayerToSpawn(targetPlayer);
                messageManager.sendMessage(targetPlayer, configLoad.getString("Island.Visit.Closed.Island.Message").replace("%player", islandOwnerPlayerName));
            }
        }
    }
    
    public void whitelistIsland(Island island) {
        MessageManager messageManager = plugin.getMessageManager();
        FileConfiguration configLoad = plugin.getLanguage();
        
        island.setStatus(IslandStatus.WHITELISTED);
        
        UUID islandOwnerUUID = island.getOwnerUUID();
        Player islandOwnerPlayer = Bukkit.getServer().getPlayer(islandOwnerUUID);
        String islandOwnerPlayerName;
        
        if (islandOwnerPlayer == null) {
            islandOwnerPlayerName = new OfflinePlayer(islandOwnerUUID).getName();
        } else {
            islandOwnerPlayerName = islandOwnerPlayer.getName();
        }
        
        for (UUID visitor : getVisitorsAtIsland(island)) {
            if (!island.isCoopPlayer(visitor) && !island.isPlayerWhitelisted(visitor)) {
                Player targetPlayer = Bukkit.getServer().getPlayer(visitor);
                LocationUtil.teleportPlayerToSpawn(targetPlayer);
                messageManager.sendMessage(targetPlayer, configLoad.getString("Island.Visit.Whitelisted.Message").replace("%player", islandOwnerPlayerName)); // TODO
            }
        }
    }

    public Island getIsland(org.bukkit.OfflinePlayer offlinePlayer) {
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();

        UUID uuid = offlinePlayer.getUniqueId();
        if (islandProxies.containsKey(uuid)) uuid = islandProxies.get(uuid);

        // TODO: Find out how this can be fixed without this, for some reason
        // IslandManager tries to load PlayerDataManager before it's even loaded
        if (playerDataManager == null) return null;

        if (islandStorage.containsKey(uuid)) {
            return islandStorage.get(uuid);
        }

        Player player = offlinePlayer.getPlayer();
        if (offlinePlayer.isOnline() && player != null) {

            if (playerDataManager.hasPlayerData(player)) {
                PlayerData playerData = playerDataManager.getPlayerData(player);

                if (playerData.getOwner() != null && islandStorage.containsKey(playerData.getOwner())) {
                    return islandStorage.get(playerData.getOwner());
                }
            }
        } else {
            OfflinePlayer offlinePlayerData = new OfflinePlayer(offlinePlayer.getUniqueId());
            loadIsland(offlinePlayer);

            if (offlinePlayerData.getOwner() != null && islandStorage.containsKey(offlinePlayer.getUniqueId())) {
                return islandStorage.get(offlinePlayerData.getOwner());
            }
        }

        return null;
    }

    public Island getIslandByUUID(UUID islandUUID) {
        for (Island island : islandStorage.values()) {
            if (island.getIslandUUID().equals(islandUUID)) {
                return island;
            }
        }
        return null;
    }

    public void removeIsland(UUID islandOwnerUUID) {
        islandStorage.remove(islandOwnerUUID);
    }

    public Map<UUID, Island> getIslands() {
        return islandStorage;
    }

    public boolean isIslandExist(UUID uuid) {
        return plugin.getFileManager().isFileExist(new File(new File(plugin.getDataFolder().toString() + "/island-data"), FastUUID.toString(uuid) + ".yml"));
    }

    /*
     * public boolean hasIsland(org.bukkit.OfflinePlayer offlinePlayer) { if
     * (offlinePlayer.isOnline()) { PlayerDataManager playerDataManager =
     * skyblock.getPlayerDataManager();
     *
     * Player player = offlinePlayer.getPlayer();
     *
     * if (playerDataManager.hasPlayerData(player)) { PlayerData playerData =
     * playerDataManager.getPlayerData(player);
     *
     * if (playerData.getOwner() != null &&
     * islandStorage.containsKey(playerData.getOwner())) { return true; } } }
     *
     * if (!isIslandExist(offlinePlayer.getUniqueId())) { return new
     * OfflinePlayer(offlinePlayer.getUniqueId()).getOwner() != null; }
     *
     * return false; }
     */

    public boolean containsIsland(UUID uuid) {
        return islandStorage.containsKey(uuid);
    }

    public void removeSpawnProtection(org.bukkit.Location location) {
        Block block = location.getBlock();

        if (CompatibleMaterial.getMaterial(block.getType()) == CompatibleMaterial.MOVING_PISTON) {
            block.setType(Material.AIR);
        }

        block = location.clone().add(0.0D, 1.0D, 0.0D).getBlock();

        if (CompatibleMaterial.getMaterial(block.getType()) == CompatibleMaterial.MOVING_PISTON) {
            block.setType(Material.AIR);
        }
    }

    public Set<UUID> getMembersOnline(Island island) {
        Set<UUID> membersOnline = new HashSet<>();

        for (Player all : Bukkit.getOnlinePlayers()) {
            if (island.hasRole(IslandRole.Member, all.getUniqueId()) || island.hasRole(IslandRole.Operator, all.getUniqueId()) || island.hasRole(IslandRole.Owner, all.getUniqueId())) {
                membersOnline.add(all.getUniqueId());
            }
        }

        return membersOnline;
    }

    public List<Player> getPlayersAtIsland(Island island) {
        List<Player> playersAtIsland = new ArrayList<>();

        if (island != null) {
            for (IslandWorld worldList : IslandWorld.getIslandWorlds()) {
                playersAtIsland.addAll(getPlayersAtIsland(island, worldList));
            }
        }

        return playersAtIsland;
    }

    public List<Player> getPlayersAtIsland(Island island, IslandWorld world) {
        List<Player> playersAtIsland = new ArrayList<>();

        if (island != null) {
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (isPlayerAtIsland(island, all, world)) {
                    playersAtIsland.add(all);
                }
            }
        }

        return playersAtIsland;
    }

    public Island getIslandPlayerAt(Player player) {
        Preconditions.checkArgument(player != null, "Cannot get Island to null player");

        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();

        if (playerDataManager.hasPlayerData(player)) {
            PlayerData playerData = playerDataManager.getPlayerData(player);

            if (playerData.getIsland() != null) {
                org.bukkit.OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(playerData.getIsland());
                Island island = getIsland(offlinePlayer);

                return island;
            }
        }

        return null;
    }

    public boolean isPlayerAtAnIsland(Player player) {
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();

        if (playerDataManager.hasPlayerData(player)) {
            PlayerData playerData = playerDataManager.getPlayerData(player);

            return playerData.getIsland() != null;
        }

        return false;
    }

    public void loadPlayer(Player player) {
        WorldManager worldManager = plugin.getWorldManager();

        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            if (worldManager.isIslandWorld(player.getWorld())) {
                IslandWorld world = worldManager.getIslandWorld(player.getWorld());
                Island island = getIslandAtLocation(player.getLocation());

                if (island != null) {
                    FileConfiguration configLoad = plugin.getConfiguration();

                    if (!island.isWeatherSynchronized()) {
                        player.setPlayerTime(island.getTime(), configLoad.getBoolean("Island.Weather.Time.Cycle"));
                        player.setPlayerWeather(island.getWeather());
                    }

                    updateFlight(player);

                    if (world == IslandWorld.Nether) {
                        if (NMSUtil.getVersionNumber() < 13) {
                            return;
                        }
                    }
    
                    double increment = island.getSize() % 2 != 0 ? 0.5d : 0.0d;
                    
                    if (configLoad.getBoolean("Island.WorldBorder.Enable") && island.isBorder()) {
                        WorldBorder.send(player, island.getBorderColor(), island.getSize(),
                                island.getLocation(worldManager.getIslandWorld(player.getWorld()),
                                        IslandEnvironment.Island).clone().add(increment, 0, increment));
                    } else {
                        WorldBorder.send(player, null, 1.4999992E7D, new org.bukkit.Location(player.getWorld(), 0, 0, 0));
                    }
                }
            }
        });
    }

    public void updateFlightAtIsland(Island island) {
        for (Player player : getPlayersAtIsland(island))
            this.updateFlight(player);
    }

    public void updateFlight(Player player) {
        // The player can fly in other worlds if they are in creative or have another
        // plugin's fly permission.
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR || player.hasPermission("essentials.fly") || player.hasPermission("cmi.command.fly"))
            return;

        // Residence support
        if (Bukkit.getServer().getPluginManager().getPlugin("Residence") != null) {
            ClaimedResidence res = Residence.getInstance().getResidenceManagerAPI().getByLoc(player.getLocation());
            if(res != null){
                if (res.getPermissions().has(Flags.fly, false) || res.getPermissions().has(Flags.nofly, false)) {
                    return;
                }
            }
        }

        Island island = getIslandAtLocation(player.getLocation());

        UpgradeManager upgradeManager = plugin.getUpgradeManager();
        List<Upgrade> flyUpgrades = upgradeManager.getUpgrades(Upgrade.Type.Fly);
        boolean isFlyUpgradeEnabled = flyUpgrades != null && flyUpgrades.size() > 0 && flyUpgrades.get(0).isEnabled();
        boolean setPlayerFlying = false;
        if (isFlyUpgradeEnabled) {
            boolean upgradeEnabled = island != null && island.isUpgrade(Upgrade.Type.Fly);
            setPlayerFlying = upgradeEnabled;
            Bukkit.getServer().getScheduler().runTask(plugin, () -> player.setAllowFlight(upgradeEnabled));
        }

        if (island == null || setPlayerFlying) return;

        boolean hasGlobalFlyPermission = player.hasPermission("fabledskyblock.*") || player.hasPermission("fabledskyblock.fly.*");
        boolean hasOwnIslandFlyPermission = player.hasPermission("fabledskyblock.fly") && island.getRole(player) != null && island.getRole(player) != IslandRole.Visitor;
        if (hasGlobalFlyPermission || hasOwnIslandFlyPermission) {
            WorldManager worldManager = plugin.getWorldManager();
            boolean canFlyInWorld = worldManager.isIslandWorld(player.getWorld());
            Bukkit.getServer().getScheduler().runTask(plugin, () -> player.setAllowFlight(canFlyInWorld));
        }
    }

    public Set<UUID> getCoopPlayersAtIsland(Island island) {
        final Set<UUID> coopPlayersAtIsland = new HashSet<>();

        if (island == null) return coopPlayersAtIsland;

        for (UUID coopUUID : island.getCoopPlayers().keySet()) {

            final Player player = Bukkit.getPlayer(coopUUID);

            if (player == null) continue;

            if (isPlayerAtIsland(island, player)) coopPlayersAtIsland.add(coopUUID);
        }

        return coopPlayersAtIsland;
    }

    public boolean removeCoopPlayers(Island island, UUID uuid) {
        MessageManager messageManager = plugin.getMessageManager();
        SoundManager soundManager = plugin.getSoundManager();

        FileConfiguration configLoad = plugin.getLanguage();

        boolean coopPlayers = island.hasPermission(IslandRole.Operator, plugin.getPermissionManager().getPermission("CoopPlayers"));

        for (Player all : Bukkit.getOnlinePlayers()) {
            if (uuid != null && all.getUniqueId().equals(uuid)) {
                continue;
            }

            if (island.hasRole(IslandRole.Owner, all.getUniqueId())) {
                return false;
            } else if (coopPlayers && island.hasRole(IslandRole.Operator, all.getUniqueId())) {
                return false;
            }
        }

        for (UUID coopPlayerAtIslandList : getCoopPlayersAtIsland(island)) {
            Player targetPlayer = Bukkit.getServer().getPlayer(coopPlayerAtIslandList);

            if (island.getCoopType(coopPlayerAtIslandList) == IslandCoop.NORMAL) continue;

            if (targetPlayer != null) {
                LocationUtil.teleportPlayerToSpawn(targetPlayer);

                if (coopPlayers) {
                    messageManager.sendMessage(targetPlayer, configLoad.getString("Island.Coop.Removed.Operator.Message"));
                } else {
                    messageManager.sendMessage(targetPlayer, configLoad.getString("Island.Coop.Removed.Owner.Message"));
                }

                soundManager.playSound(targetPlayer, CompatibleSound.ENTITY_IRON_GOLEM_ATTACK.getSound(), 1.0F, 1.0F);
            }
        }

        return true;
    }

    public int getIslandSafeLevel(Island island) {
        FileConfiguration configLoad = plugin.getConfiguration();

        int safeLevel = 0;

        Map<String, Boolean> settings = new HashMap<>();
        settings.put("KeepItemsOnDeath", false);
        settings.put("PvP", true);
        settings.put("Damage", true);

        for (String settingList : settings.keySet()) {
            if (configLoad.getBoolean("Island.Settings." + settingList + ".Enable") &&
                    island.hasPermission(IslandRole.Owner, plugin.getPermissionManager().getPermission(settingList)) ==
                            settings.get(settingList)) {
                safeLevel++;
            }
        }

        return safeLevel;
    }

    public void updateBorder(Island island) {
        WorldManager worldManager = plugin.getWorldManager();

        if (island.isBorder()) {
            if (this.plugin.getConfiguration().getBoolean("Island.WorldBorder.Enable")) {
                double increment = island.getSize() % 2 != 0 ? 0.5d : 0.0d;
                
                for (IslandWorld worldList : IslandWorld.getIslandWorlds()) {
                    if (worldList != IslandWorld.Nether || ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)) {
                        for (Player all : getPlayersAtIsland(island)) {
                            WorldBorder.send(all, island.getBorderColor(), island.getSize(), island.getLocation(worldManager.getIslandWorld(all.getWorld()), IslandEnvironment.Island).clone().add(increment, 0, increment));
                        }
                    }
    
                }
            }
        } else {
            for (IslandWorld worldList : IslandWorld.getIslandWorlds()) {
                if (worldList != IslandWorld.Nether || ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)) {
                    for (Player all : getPlayersAtIsland(island)) {
                        WorldBorder.send(all, null, 1.4999992E7D, new Location(all.getWorld(), 0, 0, 0));
                    }
                }
    
            }
        }
    }

    public List<Island> getCoopIslands(Player player) {
        List<Island> islands = new ArrayList<>();

        for (Island island : getIslands().values()) {
            if (island.getCoopPlayers().containsKey(player.getUniqueId())) {
                islands.add(island);
            }
        }

        return islands;
    }

    public Island getIslandAtLocation(org.bukkit.Location location) {
        for (Island island : new ArrayList<>(getIslands().values())) {
            if (isLocationAtIsland(island, location)) {
                return island;
            }
        }

        return null;
    }

    public boolean isPlayerProxyingAnotherPlayer(UUID proxying) {
        return islandProxies.containsKey(proxying);
    }

    public boolean isPlayerProxyingAnotherPlayer(UUID proxying, UUID proxied) {
        return islandProxies.containsKey(proxying) && islandProxies.get(proxying) == proxied;
    }
    
    public UUID getPlayerProxyingAnotherPlayer(UUID proxying) {
        return islandProxies.get(proxying);
    }

    public void addProxiedPlayer(UUID toProxy, UUID proxied) {
        islandProxies.put(toProxy, proxied);
    }

    public void removeProxyingPlayer(UUID toProxy) {
        islandProxies.remove(toProxy);
    }

    public boolean isPlayerAtIsland(Island island, Player player) {
        return isLocationAtIsland(island, player.getLocation());
    }

    public boolean isPlayerAtIsland(Island island, Player player, IslandWorld world) {
        return isLocationAtIsland(island, player.getLocation(), world);
    }

    public boolean isLocationAtIsland(Island island, org.bukkit.Location location) {
        for (IslandWorld worldList : IslandWorld.getIslandWorlds()) {
            if (isLocationAtIsland(island, location, worldList)) {
                return true;
            }
        }

        return false;
    }

    public boolean isLocationAtIsland(Island island, org.bukkit.Location location, IslandWorld world) {
        Location islandLocation = island.getLocation(world, IslandEnvironment.Island);
        if (islandLocation != null && location.getWorld().equals(islandLocation.getWorld())) {
            double locIncrement = island.getSize() % 2d != 0d ? 0.50d + Double.MIN_VALUE : -Double.MIN_VALUE;
            return LocationUtil.isLocationInLocationRadius(
                    islandLocation.clone().add(locIncrement, 0d, locIncrement),
                    LocationUtil.toCenterLocation(location),
                    island.getRadius() + Math.round(locIncrement));
        }
        return false;
    }

    public Island getIslandByOwner(org.bukkit.OfflinePlayer player) {
        if (islandStorage.containsKey(player.getUniqueId())) {
            return islandStorage.get(player.getUniqueId());
        }
        return null;
    }
}
