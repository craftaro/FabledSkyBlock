package me.goodandevil.skyblock;

import com.songoda.update.Plugin;
import com.songoda.update.SongodaUpdate;
import me.goodandevil.skyblock.api.SkyBlockAPI;
import me.goodandevil.skyblock.ban.BanManager;
import me.goodandevil.skyblock.biome.BiomeManager;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.commands.SkyBlockCommand;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.confirmation.ConfirmationTask;
import me.goodandevil.skyblock.cooldown.CooldownManager;
import me.goodandevil.skyblock.economy.EconomyManager;
import me.goodandevil.skyblock.generator.GeneratorManager;
import me.goodandevil.skyblock.hologram.HologramManager;
import me.goodandevil.skyblock.invite.InviteManager;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.leaderboard.LeaderboardManager;
import me.goodandevil.skyblock.levelling.LevellingManager;
import me.goodandevil.skyblock.listeners.*;
import me.goodandevil.skyblock.menus.Rollback;
import me.goodandevil.skyblock.menus.admin.Creator;
import me.goodandevil.skyblock.menus.admin.Generator;
import me.goodandevil.skyblock.menus.admin.Levelling;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.placeholder.PlaceholderManager;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.playtime.PlaytimeTask;
import me.goodandevil.skyblock.scoreboard.ScoreboardManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.stackable.StackableManager;
import me.goodandevil.skyblock.structure.StructureManager;
import me.goodandevil.skyblock.upgrade.UpgradeManager;
import me.goodandevil.skyblock.usercache.UserCacheManager;
import me.goodandevil.skyblock.utils.Metrics;
import me.goodandevil.skyblock.visit.VisitManager;
import me.goodandevil.skyblock.visit.VisitTask;
import me.goodandevil.skyblock.world.WorldManager;
import me.goodandevil.skyblock.world.generator.VoidGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

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

	private String formatText(String string){
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	public static SkyBlock getInstance() {
		return instance;
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
