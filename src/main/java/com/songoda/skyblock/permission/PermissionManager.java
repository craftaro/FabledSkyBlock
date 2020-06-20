package com.songoda.skyblock.permission;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.permission.event.Stoppable;
import com.songoda.skyblock.permission.permissions.basic.*;
import com.songoda.skyblock.permission.permissions.listening.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerMoveEvent;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class PermissionManager {

    private final SkyBlock plugin;

    private final Map<String, BasicPermission> registeredPermissions = new HashMap<>();
    private List<HandlerWrapper> registeredHandlers = new LinkedList<>();

    public PermissionManager(SkyBlock plugin) {
        this.plugin = plugin;

        // Load default permissions.
        registerPermissions( // TODO Reload them with /is admin reload - Fabrimat
                //Listening
                new StoragePermission(plugin),
                new DragonEggUsePermission(plugin),
                new BeaconPermission(plugin),
                new ProjectilePermission(plugin),
                new DestroyPermission(plugin),
                new AnvilPermission(plugin),
                new BedPermission(plugin),
                new BrewingPermission(plugin),
                new WorkbenchPermission(plugin),
                new DoorPermission(plugin),
                new EnchantPermission(plugin),
                new FurnacePermission(plugin),
                new LeverButtonPermission(plugin),
                new JukeboxPermission(plugin),
                new HopperPermission(plugin),
                new NoteblockPermission(plugin),
                new RedstonePermission(plugin),
                new GatePermission(plugin),
                new DropperDispenserPermission(plugin),
                new BucketPermission(plugin),
                new WaterCollectionPermission(plugin),
                new SpawnEggPermission(plugin),
                new EntityPlacementPermission(plugin),
                new FirePermission(plugin),
                new TramplePermission(plugin),
                new PressurePlatePermission(plugin),
                new CakePermission(plugin),
                new PlacePermission(plugin),
                new LeashPermission(plugin),
                new AnimalBreedingPermission(plugin),
                new MinecartPermission(plugin),
                new BoatPermission(plugin),
                new TradingPermission(plugin),
                new MilkingPermission(plugin),
                new ShearingPermission(plugin),
                new MobRidingPermission(plugin),
                new HorseInventoryPermission(plugin),
                new MobHurtingPermission(plugin),
                new ArmorStandUsePermission(plugin),
                new MonsterHurtingPermission(plugin),
                new PvpPermission(plugin),
                new HangingDestroyPermission(plugin),
                new DamagePermission(plugin),
                new ExplosionsPermission(plugin),
                new MobTamingPermission(plugin),
                new MobGriefingPermission(plugin),
                new ExperienceOrbPickupPermission(plugin),
                new NaturalMobSpawningPermission(),
                new PortalPermission(plugin),
                new ItemPickupPermission(),
                new ItemDropPermission(),
                new FishingPermission(plugin),

                // Basic
                new MemberPermission(),
                new VisitorPermission(),
                new KickPermission(),
                new BiomePermission(),
                new KeepItemsOnDeathPermission(),
                new UnbanPermission(),
                new BanPermission(),
                new BorderPermission(),
                new FireSpreadPermission(),
                new CoopPlayersPermission(),
                new IslandPermission(),
                new LeafDecayPermission(),
                new WeatherPermission(),
                new MainSpawnPermission(),
                new VisitorSpawnPermission());

        if(plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"))
                .getFileConfiguration().getBoolean("Island.Settings.Hunger.Enable")){
            registerPermission(new HungerPermission(plugin));
        }

        registeredHandlers = registeredHandlers.stream().sorted(Comparator.comparingInt(h -> {
            final PermissionHandler permissionHandler = h.getHandler().getAnnotation(PermissionHandler.class);
            return permissionHandler.priority().ordinal();
        })).collect(Collectors.toList());
    }

    private void updateSettingsConfig(BasicPermission permission){
        FileManager.Config settingsConfig = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "settings.yml"));
        FileConfiguration settingsConfigLoad = settingsConfig.getFileConfiguration();

        switch (permission.getType()){
            case GENERIC:
                if(settingsConfigLoad.getString("Settings.Visitor." + permission.getName()) == null){
                    settingsConfigLoad.set("Settings.Visitor." + permission.getName(), true);
                }
                if(settingsConfigLoad.getString("Settings.Member." + permission.getName()) == null){
                    settingsConfigLoad.set("Settings.Member." + permission.getName(), true);
                }
                if(settingsConfigLoad.getString("Settings.Coop." + permission.getName()) == null){
                    settingsConfigLoad.set("Settings.Coop." + permission.getName(), true);
                }
                break;
            case OPERATOR:
                if(settingsConfigLoad.getString("Settings.Operator." + permission.getName()) == null){
                    settingsConfigLoad.set("Settings.Operator." + permission.getName(), true);
                }
                break;
            case ISLAND:
                if(settingsConfigLoad.getString("Settings.Owner." + permission.getName()) == null){
                    settingsConfigLoad.set("Settings.Owner." + permission.getName(), true);
                }
                break;
        }
        try {
            settingsConfigLoad.save(settingsConfig.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean registerPermission(BasicPermission permission) {
        updateSettingsConfig(permission);

        registeredPermissions.put(permission.getName().toUpperCase(), permission);
        Set<Method> methods;
        try {
            Method[] publicMethods = permission.getClass().getMethods();
            methods = new HashSet<>(publicMethods.length, Float.MAX_VALUE);
            methods.addAll(Arrays.asList(publicMethods));
            Collections.addAll(methods, permission.getClass().getDeclaredMethods());
        } catch (NoClassDefFoundError e) {
            return false;
        }
        for (Method method : methods) {
            final PermissionHandler permissionHandler = method.getAnnotation(PermissionHandler.class);
            if (permissionHandler == null) continue;
            registeredHandlers.add(new HandlerWrapper(permission, method));
        }
        return true;
    }

    public boolean registerPermissions(BasicPermission... permissions) {
        for (BasicPermission permission : permissions)
            if (!registerPermission(permission))
                return false;
        return true;
    }

    public boolean processPermission(Cancellable cancellable, Island island) {
        return processPermission(cancellable, null, island);
    }

    public boolean processPermission(Cancellable cancellable, Player player, Location location) {
        return processPermission(cancellable, player, plugin.getIslandManager().getIslandAtLocation(location));
    }

    public boolean processPermission(Cancellable cancellable, Player player, Island island) {
        return processPermission(cancellable, player, island, false);
    }

    public boolean processPermission(Cancellable cancellable, Player player, Island island, boolean reversePermission) {
        if (island == null) return true;

        for (HandlerWrapper wrapper : registeredHandlers) {
            Method handler = wrapper.getHandler();
            if (handler.getParameterTypes()[0] != cancellable.getClass()) continue;

            if (cancellable.isCancelled()) return false;
            if (cancellable instanceof Stoppable && ((Stoppable) cancellable).isStopped()) return true;

            BasicPermission permission = wrapper.getPermission();

            if (hasPermission(player, island, permission, reversePermission))
                continue;

            try {
                handler.invoke(permission, cancellable);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return !cancellable.isCancelled();
    }

    public boolean hasPermission(Player player, Island island, BasicPermission permission, boolean reversePermission){
        if (player == null)
            return island.hasPermission(IslandRole.Owner, permission);

        if (player.hasPermission("fabledskyblock.bypass." + permission.getName().toLowerCase()))
            return !reversePermission;

        FileManager.Config config = SkyBlock.getInstance().getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        switch(island.getRole(player)){
            case Owner:
                if(!configLoad.getBoolean("Island.Settings.OwnersAndOperatorsAsMembers", false)){
                    return island.hasPermission(IslandRole.Owner, permission);
                }
            case Operator:
                if(!configLoad.getBoolean("Island.Settings.OwnersAndOperatorsAsMembers", false)){
                    return island.hasPermission(IslandRole.Operator, permission);
                }
            case Member:
                return island.hasPermission(IslandRole.Member, permission);
            case Coop:
                return island.hasPermission(IslandRole.Coop, permission);
            case Visitor:
                return island.hasPermission(IslandRole.Visitor, permission);
        }
        return false;
    }

    public boolean hasPermission(Player player, Island island, BasicPermission permission) {
        return this.hasPermission(player, island, permission, false);
    }

    public boolean hasPermission(Location location, String permission, IslandRole islandRole) {
        if (location == null)
            return false;
        return plugin.getIslandManager().getIslandAtLocation(location)
                .hasPermission(islandRole, getPermission(permission));
    }

    public boolean hasPermission(Island island, String permission, IslandRole islandRole) {
        return island.hasPermission(islandRole, getPermission(permission));
    }

    public boolean hasPermission(Player player, Island island, String permission) {
        return hasPermission(player, island, getPermission(permission));
    }

    public BasicPermission getPermission(String permissionName) {
        return registeredPermissions.get(permissionName.toUpperCase());
    }

    public List<BasicPermission> getPermissions() {
        return new ArrayList<>(registeredPermissions.values());
    }

    public List<ListeningPermission> getListeningPermissions() {
        return registeredPermissions.values().stream()
                .filter(p -> p instanceof ListeningPermission)
                .map(p -> (ListeningPermission) p)
                .collect(Collectors.toList());
    }
}
