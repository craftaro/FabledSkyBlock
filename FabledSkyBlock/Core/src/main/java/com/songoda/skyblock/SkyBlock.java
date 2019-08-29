package com.songoda.skyblock;

import com.songoda.skyblock.api.SkyBlockAPI;
import com.songoda.skyblock.ban.BanManager;
import com.songoda.skyblock.biome.BiomeManager;
import com.songoda.skyblock.command.CommandManager;
import com.songoda.skyblock.command.commands.SkyBlockCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.confirmation.ConfirmationTask;
import com.songoda.skyblock.cooldown.CooldownManager;
import com.songoda.skyblock.economy.EconomyManager;
import com.songoda.skyblock.generator.GeneratorManager;
import com.songoda.skyblock.hologram.HologramManager;
import com.songoda.skyblock.invite.InviteManager;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.leaderboard.LeaderboardManager;
import com.songoda.skyblock.levelling.LevellingManager;
import com.songoda.skyblock.limit.LimitManager;
import com.songoda.skyblock.listeners.*;
import com.songoda.skyblock.menus.Rollback;
import com.songoda.skyblock.menus.admin.Creator;
import com.songoda.skyblock.menus.admin.Generator;
import com.songoda.skyblock.menus.admin.Levelling;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.placeholder.PlaceholderManager;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.playtime.PlaytimeTask;
import com.songoda.skyblock.scoreboard.ScoreboardManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.stackable.StackableManager;
import com.songoda.skyblock.structure.StructureManager;
import com.songoda.skyblock.upgrade.UpgradeManager;
import com.songoda.skyblock.usercache.UserCacheManager;
import com.songoda.skyblock.utils.Metrics;
import com.songoda.skyblock.visit.VisitManager;
import com.songoda.skyblock.visit.VisitTask;
import com.songoda.skyblock.world.WorldManager;
import com.songoda.skyblock.world.generator.VoidGenerator;
import com.songoda.update.Plugin;
import com.songoda.update.SongodaUpdate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class SkyBlock extends JavaPlugin {

    private static SkyBlock instance;

    private FileManager fileManager;
    private WorldManager worldManager;
    private UserCacheManager userCacheManager;
    private VisitManager visitManager;
    private BanManager banManager;
    private IslandManager islandManager;
    private UpgradeManager upgradeManager;
    private PlayerDataManager playerDataManager;
    private CooldownManager cooldownManager;
    private LimitManager limitManager;
    private ScoreboardManager scoreboardManager;
    private InviteManager inviteManager;
    private BiomeManager biomeManager;
    private LevellingManager levellingManager;
    private CommandManager commandManager;
    private StructureManager structureManager;
    private StackableManager stackableManager;
    private SoundManager soundManager;
    private GeneratorManager generatorManager;
    private LeaderboardManager leaderboardManager;
    private PlaceholderManager placeholderManager;
    private MessageManager messageManager;
    private EconomyManager economyManager;
    private HologramManager hologramManager;

    public static SkyBlock getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(formatText("&a============================="));
        console.sendMessage(formatText("&7FabledSkyBlock " + this.getDescription().getVersion() + " by &5Songoda <3&7!"));
        console.sendMessage(formatText("&7Action: &aEnabling&7..."));
        console.sendMessage(formatText("&a============================="));

        instance = this;

        fileManager = new FileManager(this);
        worldManager = new WorldManager(this);
        userCacheManager = new UserCacheManager(this);
        economyManager = new EconomyManager();
        visitManager = new VisitManager(this);
        banManager = new BanManager(this);
        islandManager = new IslandManager(this);
        upgradeManager = new UpgradeManager(this);
        playerDataManager = new PlayerDataManager(this);
        cooldownManager = new CooldownManager(this);
        limitManager = new LimitManager(this);

        if (fileManager.getConfig(new File(getDataFolder(), "config.yml")).getFileConfiguration()
                .getBoolean("Island.Scoreboard.Enable")) {
            scoreboardManager = new ScoreboardManager(this);
        }

        inviteManager = new InviteManager(this);
        biomeManager = new BiomeManager(this);
        levellingManager = new LevellingManager(this);
        commandManager = new CommandManager(this);
        structureManager = new StructureManager(this);
        soundManager = new SoundManager(this);

        if (fileManager.getConfig(new File(getDataFolder(), "config.yml")).getFileConfiguration()
                .getBoolean("Island.Generator.Enable")) {
            generatorManager = new GeneratorManager(this);
        }

        if (fileManager.getConfig(new File(getDataFolder(), "config.yml")).getFileConfiguration()
                .getBoolean("Island.Stackable.Enable")) {
            stackableManager = new StackableManager(this);
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> stackableManager.loadSavedStackables(), 5L);
        }

        leaderboardManager = new LeaderboardManager(this);

        placeholderManager = new PlaceholderManager(this);
        placeholderManager.registerPlaceholders();

        messageManager = new MessageManager(this);
        hologramManager = new HologramManager(this);

        new PlaytimeTask(playerDataManager, islandManager).runTaskTimerAsynchronously(this, 0L, 20L);
        new VisitTask(playerDataManager).runTaskTimerAsynchronously(this, 0L, 20L);
        new ConfirmationTask(playerDataManager).runTaskTimerAsynchronously(this, 0L, 20L);

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new Join(this), this);
        pluginManager.registerEvents(new Quit(this), this);
        pluginManager.registerEvents(new Block(this), this);
        pluginManager.registerEvents(new Interact(this), this);
        pluginManager.registerEvents(new Entity(this), this);
        pluginManager.registerEvents(new Bucket(this), this);
        pluginManager.registerEvents(new Projectile(this), this);
        pluginManager.registerEvents(new Inventory(this), this);
        pluginManager.registerEvents(new Item(this), this);
        pluginManager.registerEvents(new Teleport(this), this);
        pluginManager.registerEvents(new Portal(this), this);
        pluginManager.registerEvents(new Move(this), this);
        pluginManager.registerEvents(new Death(this), this);
        pluginManager.registerEvents(new Respawn(this), this);
        pluginManager.registerEvents(new Chat(this), this);
        pluginManager.registerEvents(new Spawner(this), this);
        pluginManager.registerEvents(new Food(this), this);

        if (pluginManager.isPluginEnabled("EpicSpawners"))
            pluginManager.registerEvents(new EpicSpawners(this), this);
        if (pluginManager.isPluginEnabled("WildStacker"))
            pluginManager.registerEvents(new WildStacker(this), this);
        if (pluginManager.isPluginEnabled("UltimateStacker"))
            pluginManager.registerEvents(new UltimateStacker(this), this);

        pluginManager.registerEvents(new Rollback(), this);
        pluginManager.registerEvents(new Levelling(), this);
        pluginManager.registerEvents(new Generator(), this);
        pluginManager.registerEvents(new Creator(), this);

        this.getCommand("skyblock").setExecutor(new SkyBlockCommand());

        // bStats Metrics
        new Metrics(this);

        // Songoda Updater
        Plugin plugin = new Plugin(this, 17);
        SongodaUpdate.load(plugin);

        SkyBlockAPI.setImplementation(instance);
    }

    @Override
    public void onDisable() {
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(formatText("&a============================="));
        console.sendMessage(formatText("&7FabledSkyBlock " + this.getDescription().getVersion() + " by &5Songoda <3&7!"));
        console.sendMessage(formatText("&7Action: &cDisabling&7..."));
        console.sendMessage(formatText("&a============================="));

        if (this.userCacheManager != null) {
            this.userCacheManager.onDisable();
        }

        if (this.islandManager != null) {
            this.islandManager.onDisable();
        }

        if (this.visitManager != null) {
            this.visitManager.onDisable();
        }

        if (this.banManager != null) {
            this.banManager.onDisable();
        }

        if (this.playerDataManager != null) {
            this.playerDataManager.onDisable();
        }

        if (this.cooldownManager != null) {
            this.cooldownManager.onDisable();
        }

        if (this.hologramManager != null) {
            this.hologramManager.onDisable();
        }

        HandlerList.unregisterAll(this);
    }

    private String formatText(String string) {
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

    public LimitManager getLimitManager() {
        return limitManager;
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

    public LevellingManager getLevellingManager() {
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

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }

    public StackableManager getStackableManager() {
        return stackableManager;
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new VoidGenerator();
    }
}
