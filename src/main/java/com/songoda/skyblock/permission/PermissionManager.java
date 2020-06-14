package com.songoda.skyblock.permission;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.permission.event.Stoppable;
import com.songoda.skyblock.permission.permissions.basic.*;
import com.songoda.skyblock.permission.permissions.listening.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

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

        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            // Load default permissions.
            registerPermissions(
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
                    new HungerPermission(plugin),
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

            registeredHandlers = registeredHandlers.stream().sorted(Comparator.comparingInt(h -> {
                final PermissionHandler permissionHandler = h.getHandler().getAnnotation(PermissionHandler.class);
                return permissionHandler.priority().ordinal();
            })).collect(Collectors.toList());
        }, 20L);
    }

    public boolean registerPermission(BasicPermission permission) {
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
        if (island == null) return true;

        for (HandlerWrapper wrapper : registeredHandlers) {
            Method handler = wrapper.getHandler();
            if (handler.getParameterTypes()[0] != cancellable.getClass()) continue;

            if (cancellable.isCancelled()) return false;
            if (cancellable instanceof Stoppable && ((Stoppable) cancellable).isStopped()) return true;

            BasicPermission permission = wrapper.getPermission();

            if (permission.overridingCheck() || hasPermission(player, island, permission))
                continue;

            try {
                handler.invoke(permission, cancellable);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public boolean hasPermission(Player player, Island island, BasicPermission permission) {
        if (player == null)
            return island.hasPermission(IslandRole.Owner, permission);

        if (player.hasPermission("fabledskyblock.bypass." + permission.getName().toLowerCase()))
            return true;

        if (island.hasPermission(island.getRole(player), permission))
            return true;

        if (island.isCoopPlayer(player.getUniqueId()) && island.hasPermission(IslandRole.Coop, permission))
            return true;

        return island.hasPermission(IslandRole.Visitor, permission);
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
