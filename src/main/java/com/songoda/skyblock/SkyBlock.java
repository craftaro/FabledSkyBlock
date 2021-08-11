package com.songoda.skyblock;

import com.songoda.core.SongodaCore;
import com.songoda.core.SongodaPlugin;
import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.ServerProject;
import com.songoda.core.compatibility.ServerVersion;
import com.songoda.core.configuration.Config;
import com.songoda.core.gui.GuiManager;
import com.songoda.core.hooks.LogManager;
import com.songoda.skyblock.api.SkyBlockAPI;
import com.songoda.skyblock.ban.BanManager;
import com.songoda.skyblock.bank.BankManager;
import com.songoda.skyblock.biome.BiomeManager;
import com.songoda.skyblock.challenge.FabledChallenge;
import com.songoda.skyblock.command.CommandManager;
import com.songoda.skyblock.command.commands.SkyBlockCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.confirmation.ConfirmationTask;
import com.songoda.skyblock.cooldown.CooldownManager;
import com.songoda.skyblock.economy.EconomyManager;
import com.songoda.skyblock.generator.GeneratorManager;
import com.songoda.skyblock.invite.InviteManager;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.reward.RewardManager;
import com.songoda.skyblock.leaderboard.LeaderboardManager;
import com.songoda.skyblock.levelling.IslandLevelManager;
import com.songoda.skyblock.limit.LimitationInstanceHandler;
import com.songoda.skyblock.listeners.*;
import com.songoda.skyblock.listeners.hooks.EpicSpawners;
import com.songoda.skyblock.listeners.hooks.UltimateStacker;
import com.songoda.skyblock.localization.LocalizationManager;
import com.songoda.skyblock.menus.admin.Creator;
import com.songoda.skyblock.menus.admin.Generator;
import com.songoda.skyblock.menus.admin.Levelling;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.PermissionManager;
import com.songoda.skyblock.placeholder.PlaceholderManager;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.playtime.PlaytimeTask;
import com.songoda.skyblock.scoreboard.ScoreboardManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.stackable.StackableManager;
import com.songoda.skyblock.structure.StructureManager;
import com.songoda.skyblock.tasks.HologramTask;
import com.songoda.skyblock.tasks.MobNetherWaterTask;
import com.songoda.skyblock.upgrade.UpgradeManager;
import com.songoda.skyblock.usercache.UserCacheManager;
import com.songoda.skyblock.visit.VisitManager;
import com.songoda.skyblock.visit.VisitTask;
import com.songoda.skyblock.world.WorldManager;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.util.List;

public class SkyBlock extends SongodaPlugin {

    private static SkyBlock INSTANCE;

    private FileManager fileManager;
    private final WorldManager worldManager = new WorldManager(this);
    private UserCacheManager userCacheManager;
    private VisitManager visitManager;
    private BanManager banManager;
    private IslandManager islandManager;
    private UpgradeManager upgradeManager;
    private PlayerDataManager playerDataManager;
    private CooldownManager cooldownManager;
    private ScoreboardManager scoreboardManager;
    private InviteManager inviteManager;
    private BiomeManager biomeManager;
    private IslandLevelManager levellingManager;
    private com.songoda.skyblock.economy.EconomyManager economyManager;
    private CommandManager commandManager;
    private StructureManager structureManager;
    private StackableManager stackableManager;
    private SoundManager soundManager;
    private GeneratorManager generatorManager;
    private LeaderboardManager leaderboardManager;
    private PlaceholderManager placeholderManager;
    private MessageManager messageManager;
    private HologramTask hologramTask;
    private MobNetherWaterTask mobNetherWaterTask;
    private LimitationInstanceHandler limitationHandler;
    private LocalizationManager localizationManager;
    private RewardManager rewardManager;
    private FabledChallenge fabledChallenge;
    private BankManager bankManager;
    private PermissionManager permissionManager;

    private Permission vaultPermission;

    private boolean paper;
    private boolean paperAsync;

    private final GuiManager guiManager = new GuiManager(this);

    public static SkyBlock getInstance() {
        return INSTANCE;
    }

    // Add ymlFiles to cache
    private FileConfiguration biomes;
    private FileConfiguration challenges;
    private FileConfiguration config;
    private FileConfiguration generators;
    private FileConfiguration language;
    private FileConfiguration levelling;
    private FileConfiguration limits;
    private FileConfiguration menus;
    private FileConfiguration placeholders;
    private FileConfiguration rewards;
    private FileConfiguration scoreboard;
    private FileConfiguration settings;
    private FileConfiguration stackables;
    private FileConfiguration upgrades;

    @Override
    public void onPluginLoad() {
        INSTANCE = this;
    }

    @Override
    public void onPluginEnable() {
        if (ServerVersion.isServerVersionAbove(ServerVersion.V1_17) || ServerVersion.isServerVersionBelow(ServerVersion.V1_8)) {
            this.getLogger().warning("This Minecraft version is not officially supported.");
        }

        if (paper = ServerProject.isServer(ServerProject.PAPER)) {
            try {
                Bukkit.spigot().getClass().getMethod("getPaperConfig");
                if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_16)) {
                    paperAsync = true;
                } else {
                    paperAsync = ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13) &&
                            Bukkit.spigot().getPaperConfig().getBoolean("settings.async-chunks.enable", false);
                }
            } catch (NoSuchMethodException ignored) {
                paperAsync = false;
            }
            this.getLogger().info("Enabling Paper hooks");
        }

        // Run Songoda Updater
        SongodaCore.registerPlugin(this, 17, CompatibleMaterial.GRASS_BLOCK);

        // Load Economy
        economyManager = new EconomyManager(this);

        // Load Holograms
        com.songoda.core.hooks.HologramManager.load(this);

        fileManager = new FileManager(this);

        if (!loadConfigs()) {
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        final SkyBlock plugin = this;
        Bukkit.getPluginManager().registerEvents(new Listener() {
            boolean alreadyRun = false;

            @EventHandler
            private void onServerLoaded(ServerLoadEvent e) {
                if (!alreadyRun) {
                    alreadyRun = true;

                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        permissionManager = new PermissionManager(plugin);
                        localizationManager = new LocalizationManager();
                        worldManager.loadWorlds();
                        userCacheManager = new UserCacheManager(plugin);
                        visitManager = new VisitManager(plugin);
                        banManager = new BanManager(plugin);
                        islandManager = new IslandManager(plugin);
                        upgradeManager = new UpgradeManager(plugin);
                        playerDataManager = new PlayerDataManager(plugin);
                        cooldownManager = new CooldownManager(plugin);
                        limitationHandler = new LimitationInstanceHandler();
                        fabledChallenge = new FabledChallenge(plugin);
                        scoreboardManager = new ScoreboardManager(plugin);
                        inviteManager = new InviteManager(plugin);
                        biomeManager = new BiomeManager(plugin);
                        levellingManager = new IslandLevelManager(plugin);
                        commandManager = new CommandManager(plugin);
                        structureManager = new StructureManager(plugin);
                        soundManager = new SoundManager(plugin);

                        if (plugin.config.getBoolean("Island.Generator.Enable")) {
                            generatorManager = new GeneratorManager(plugin);
                        }

                        if (plugin.config.getBoolean("Island.Stackable.Enable")) {
                            stackableManager = new StackableManager(plugin);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> stackableManager.loadSavedStackables(), 5L);
                        }

                        leaderboardManager = new LeaderboardManager(plugin);

                        placeholderManager = new PlaceholderManager(plugin);
                        placeholderManager.registerPlaceholders();

                        messageManager = new MessageManager(plugin);

                        rewardManager = new RewardManager(plugin);
                        rewardManager.loadRewards();

                        bankManager = new BankManager(plugin);

                        if (plugin.config.getBoolean("Island.Task.PlaytimeTask")) {
                            new PlaytimeTask(playerDataManager, islandManager).runTaskTimerAsynchronously(plugin, 0L, 20L);
                        }

                        if (plugin.config.getBoolean("Island.Task.VisitTask")) {
                            new VisitTask(playerDataManager).runTaskTimerAsynchronously(plugin, 0L, 20L);
                        }

                        new ConfirmationTask(playerDataManager).runTaskTimerAsynchronously(plugin, 0L, 20L);

                        // Start Tasks
                        hologramTask = HologramTask.startTask(plugin);
                        mobNetherWaterTask = MobNetherWaterTask.startTask(plugin);

                        PluginManager pluginManager = getServer().getPluginManager();
                        pluginManager.registerEvents(new JoinListeners(plugin), plugin);
                        pluginManager.registerEvents(new QuitListeners(plugin), plugin);
                        pluginManager.registerEvents(new BlockListeners(plugin), plugin);
                        pluginManager.registerEvents(new InteractListeners(plugin), plugin);
                        pluginManager.registerEvents(new EntityListeners(plugin), plugin);
                        pluginManager.registerEvents(new BucketListeners(plugin), plugin);
                        pluginManager.registerEvents(new ProjectileListeners(plugin), plugin);
                        pluginManager.registerEvents(new InventoryListeners(plugin), plugin);
                        pluginManager.registerEvents(new ItemListeners(plugin), plugin);
                        pluginManager.registerEvents(new TeleportListeners(plugin), plugin);
                        pluginManager.registerEvents(new PortalListeners(plugin), plugin);
                        pluginManager.registerEvents(new MoveListeners(plugin), plugin);
                        pluginManager.registerEvents(new DeathListeners(plugin), plugin);
                        pluginManager.registerEvents(new RespawnListeners(plugin), plugin);
                        pluginManager.registerEvents(new ChatListeners(plugin), plugin);
                        pluginManager.registerEvents(new SpawnerListeners(plugin), plugin);
                        pluginManager.registerEvents(new FoodListeners(plugin), plugin);
                        pluginManager.registerEvents(new GrowListeners(plugin), plugin);
                        pluginManager.registerEvents(new PistonListeners(plugin), plugin);
                        pluginManager.registerEvents(new FallBreakListeners(plugin), plugin);
                        pluginManager.registerEvents(new WorldListeners(plugin), plugin);

                        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)) {
                            pluginManager.registerEvents(new SpongeListeners(plugin), plugin);
                        }

                        if (pluginManager.isPluginEnabled("EpicSpawners"))
                            pluginManager.registerEvents(new EpicSpawners(plugin), plugin);
                        if (pluginManager.isPluginEnabled("UltimateStacker"))
                            pluginManager.registerEvents(new UltimateStacker(plugin), plugin);

                        pluginManager.registerEvents(new Levelling(), plugin);
                        pluginManager.registerEvents(new Generator(), plugin);
                        pluginManager.registerEvents(new Creator(), plugin);

                        plugin.getCommand("skyblock").setExecutor(new SkyBlockCommand());

                        if (pluginManager.isPluginEnabled("Vault")) {
                            plugin.vaultPermission = getServer().getServicesManager().getRegistration(Permission.class).getProvider();
                        }

                        switch (plugin.config.getString("Economy.Manager", "Default")) {
                            case "Vault":
                                getEconomyManager().setEconomy("Vault");
                                break;
                            case "PlayerPoints":
                                getEconomyManager().setEconomy("PlayerPoints");
                                break;
                            case "Reserve":
                                getEconomyManager().setEconomy("Reserve");
                                break;
                            default:
                                plugin.getLogger().warning("EconomyManager is default");
                        }

                        LogManager.load();
                    });

                    SkyBlockAPI.setImplementation(INSTANCE);
                }
            }
        }, this);
    }

    @Override
    public void onPluginDisable() {
        if (this.userCacheManager != null)
            this.userCacheManager.onDisable();
        if (this.scoreboardManager != null)
            this.scoreboardManager.disable();
        if (this.islandManager != null)
            this.islandManager.onDisable();
        if (this.visitManager != null)
            this.visitManager.onDisable();
        if (this.banManager != null)
            this.banManager.onDisable();
        if (this.playerDataManager != null)
            this.playerDataManager.onDisable();
        if (this.cooldownManager != null)
            this.cooldownManager.onDisable();
        if (this.hologramTask != null)
            this.hologramTask.onDisable();
        if (this.mobNetherWaterTask != null)
            this.mobNetherWaterTask.onDisable();
        if (this.fabledChallenge != null)
            this.fabledChallenge.onDisable();

        HandlerList.unregisterAll(this);
    }

    @Override
    public void onDataLoad() {
    }

    @Override
    public void onConfigReload() {
        if (!loadConfigs()) this.getLogger().warning("Config are not reload !");
        else this.getLogger().info("Configurations Loaded !");
    }

    @Override
    public List<Config> getExtraConfig() {
        return null;
    }

    private boolean loadConfigs() {
        try {
            biomes = this.getFileManager().getConfig(new File(this.getDataFolder(), "biomes.yml")).getFileConfiguration();
            challenges = this.getFileManager().getConfig(new File(this.getDataFolder(), "challenges.yml")).getFileConfiguration();
            config = this.getFileManager().getConfig(new File(this.getDataFolder(), "config.yml")).getFileConfiguration();
            generators = this.getFileManager().getConfig(new File(this.getDataFolder(), "generators.yml")).getFileConfiguration();
            language = this.getFileManager().getConfig(new File(this.getDataFolder(), "language.yml")).getFileConfiguration();
            levelling = this.getFileManager().getConfig(new File(this.getDataFolder(), "levelling.yml")).getFileConfiguration();
            limits = this.getFileManager().getConfig(new File(this.getDataFolder(), "limits.yml")).getFileConfiguration();
            menus = this.getFileManager().getConfig(new File(this.getDataFolder(), "menus.yml")).getFileConfiguration();
            placeholders = this.getFileManager().getConfig(new File(this.getDataFolder(), "placeholders.yml")).getFileConfiguration();
            rewards = this.getFileManager().getConfig(new File(this.getDataFolder(), "rewards.yml")).getFileConfiguration();
            scoreboard = this.getFileManager().getConfig(new File(this.getDataFolder(), "scoreboard.yml")).getFileConfiguration();
            settings = this.getFileManager().getConfig(new File(this.getDataFolder(), "settings.yml")).getFileConfiguration();
            stackables = this.getFileManager().getConfig(new File(this.getDataFolder(), "stackables.yml")).getFileConfiguration();
            upgrades = this.getFileManager().getConfig(new File(this.getDataFolder(), "upgrades.yml")).getFileConfiguration();
            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public String formatText(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    public UserCacheManager getUserCacheManager() {
        return userCacheManager;
    }

    public VisitManager getVisitManager() {
        return visitManager;
    }

    public BanManager getBanManager() {
        return banManager;
    }

    public BankManager getBankManager() {
        return bankManager;
    }

    public IslandManager getIslandManager() {
        return islandManager;
    }

    public UpgradeManager getUpgradeManager() {
        return upgradeManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public void setScoreboardManager(ScoreboardManager scoreboardManager) {
        this.scoreboardManager = scoreboardManager;
    }

    public InviteManager getInviteManager() {
        return inviteManager;
    }

    public BiomeManager getBiomeManager() {
        return biomeManager;
    }

    public IslandLevelManager getLevellingManager() {
        return levellingManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public StructureManager getStructureManager() {
        return structureManager;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public GeneratorManager getGeneratorManager() {
        return generatorManager;
    }

    public void setGeneratorManager(GeneratorManager generatorManager) {
        this.generatorManager = generatorManager;
    }

    public LeaderboardManager getLeaderboardManager() {
        return leaderboardManager;
    }

    public PlaceholderManager getPlaceholderManager() {
        return placeholderManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public HologramTask getHologramTask() {
        return hologramTask;
    }

    public MobNetherWaterTask getMobNetherWaterTask() {
        return mobNetherWaterTask;
    }

    public StackableManager getStackableManager() {
        return stackableManager;
    }

    public LimitationInstanceHandler getLimitationHandler() {
        return limitationHandler;
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return worldManager.getWorldGeneratorForMapName(worldName);
    }

    public LocalizationManager getLocalizationManager() {
        return localizationManager;
    }

    public RewardManager getRewardManager() {
        return rewardManager;
    }

    public FabledChallenge getFabledChallenge() {
        return fabledChallenge;
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public boolean isPaper() {
        return paper;
    }

    public boolean isPaperAsync() {
        return paperAsync;
    }

    public Permission getVaultPermission() {
        return vaultPermission;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public FileConfiguration getBiomes() {
        return biomes;
    }

    public FileConfiguration getChallenges() {
        return challenges;
    }

    public FileConfiguration getConfiguration() {
        return config;
    }

    public FileConfiguration getGenerators() {
        return generators;
    }

    public FileConfiguration getLanguage() {
        return language;
    }

    public FileConfiguration getLevelling() {
        return levelling;
    }

    public FileConfiguration getLimits() {
        return limits;
    }

    public FileConfiguration getMenus() {
        return menus;
    }

    public FileConfiguration getPlaceholders() {
        return placeholders;
    }

    public FileConfiguration getRewards() {
        return rewards;
    }

    public FileConfiguration getSettings() {
        return settings;
    }

    public FileConfiguration getStackables() {
        return stackables;
    }

    public FileConfiguration getUpgrades() {
        return upgrades;
    }

    public FileConfiguration getScoreboard() {
        return scoreboard;
    }
}
