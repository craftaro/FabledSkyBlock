package com.songoda.skyblock.utils;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.utils.version.NMSUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AbstractAnvilGUI {
    private static final Class<?> BlockPositionClass;
    private static final Class<?> PacketPlayOutOpenWindowClass;
    private static final Class<?> IChatBaseComponentClass;
    private static final Class<?> ICraftingClass;
    private static final Class<?> ContainerAnvilClass;
    private static final Class<?> ChatMessageClass;
    private static final Class<?> EntityHumanClass;
    private static final Class<?> ContainerClass;
    private static Class<?> ContainerAccessClass;
    private static final Class<?> WorldClass;
    private static final Class<?> PlayerInventoryClass;
    private static Class<?> ContainersClass;
    private static final Class<?> CraftPlayerClass;

    static {
        BlockPositionClass = NMSUtil.getNMSClass("BlockPosition");
        PacketPlayOutOpenWindowClass = NMSUtil.getNMSClass("PacketPlayOutOpenWindow");
        IChatBaseComponentClass = NMSUtil.getNMSClass("IChatBaseComponent");
        ICraftingClass = NMSUtil.getNMSClass("ICrafting");
        ContainerAnvilClass = NMSUtil.getNMSClass("ContainerAnvil");
        EntityHumanClass = NMSUtil.getNMSClass("EntityHuman");
        ChatMessageClass = NMSUtil.getNMSClass("ChatMessage");
        ContainerClass = NMSUtil.getNMSClass("Container");
        WorldClass = NMSUtil.getNMSClass("World");
        PlayerInventoryClass = NMSUtil.getNMSClass("PlayerInventory");
        CraftPlayerClass = NMSUtil.getCraftClass("entity.CraftPlayer");

        if (NMSUtil.getVersionNumber() > 13) {
            ContainerAccessClass = NMSUtil.getNMSClass("ContainerAccess");
            ContainersClass = NMSUtil.getNMSClass("Containers");
        }
    }

    private Player player;
    private Map<AnvilSlot, ItemStack> items = new HashMap<>();
    private Inventory inv;
    private Listener listener;

    public AbstractAnvilGUI(Player player, AnvilClickEventHandler handler) {
        SkyBlock instance = SkyBlock.getInstance();
        this.player = player;

        this.listener = new Listener() {
            @EventHandler(priority = EventPriority.HIGHEST)
            public void onInventoryClick(InventoryClickEvent event) {
                if (event.getWhoClicked() instanceof Player && event.getInventory().equals(AbstractAnvilGUI.this.inv)) {
                    event.setCancelled(true);

                    ItemStack item = event.getCurrentItem();
                    int slot = event.getRawSlot();

                    if (item == null || item.getType().equals(Material.AIR) || slot != 2)
                        return;

                    String name = "";

                    ItemMeta meta = item.getItemMeta();
                    if (meta != null && meta.hasDisplayName())
                        name = meta.getDisplayName();

                    AnvilClickEvent clickEvent = new AnvilClickEvent(AnvilSlot.bySlot(slot), name);
                    handler.onAnvilClick(clickEvent);

                    if (clickEvent.getWillClose())
                        event.getWhoClicked().closeInventory();

                    if (clickEvent.getWillDestroy())
                        AbstractAnvilGUI.this.destroy();
                }
            }

            @EventHandler(priority = EventPriority.HIGHEST)
            public void onInventoryClose(InventoryCloseEvent event) {
                if (event.getPlayer() instanceof Player && AbstractAnvilGUI.this.inv.equals(event.getInventory())) {
                    Inventory inv = event.getInventory();
                    player.setLevel(player.getLevel() - 1);
                    inv.clear();
                    Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
                        AbstractAnvilGUI.this.destroy();
                    }, 1L);
                }
            }

            @EventHandler(priority = EventPriority.HIGHEST)
            public void onPlayerQuit(PlayerQuitEvent event) {
                if (event.getPlayer().equals(AbstractAnvilGUI.this.player)) {
                    player.setLevel(player.getLevel() - 1);
                    AbstractAnvilGUI.this.destroy();
                }
            }
        };

        Bukkit.getPluginManager().registerEvents(this.listener, instance);
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setSlot(AnvilSlot slot, ItemStack item) {
        this.items.put(slot, item);
    }

    public void open() {
        this.player.setLevel(this.player.getLevel() + 1);

        try {
            Object craftPlayer = CraftPlayerClass.cast(this.player);
            Method getHandleMethod = CraftPlayerClass.getMethod("getHandle");
            Object entityPlayer = getHandleMethod.invoke(craftPlayer);
            Object playerInventory = NMSUtil.getFieldObject(entityPlayer, NMSUtil.getField(entityPlayer.getClass(), "inventory", false));
            Object world = NMSUtil.getFieldObject(entityPlayer, NMSUtil.getField(entityPlayer.getClass(), "world", false));
            Object blockPosition = BlockPositionClass.getConstructor(int.class, int.class, int.class).newInstance(0, 0, 0);

            Object container;

            if (NMSUtil.getVersionNumber() > 13) {
                container = ContainerAnvilClass
                        .getConstructor(int.class, PlayerInventoryClass, ContainerAccessClass)
                        .newInstance(7, playerInventory, ContainerAccessClass.getMethod("at", WorldClass, BlockPositionClass).invoke(null, world, blockPosition));
            } else {
                container = ContainerAnvilClass
                        .getConstructor(PlayerInventoryClass, WorldClass, BlockPositionClass, EntityHumanClass)
                        .newInstance(playerInventory, world, blockPosition, entityPlayer);
            }

            NMSUtil.getField(ContainerClass, "checkReachable", true).set(container, false);

            Method getBukkitViewMethod = container.getClass().getMethod("getBukkitView");
            Object bukkitView = getBukkitViewMethod.invoke(container);
            Method getTopInventoryMethod = bukkitView.getClass().getMethod("getTopInventory");
            this.inv = (Inventory) getTopInventoryMethod.invoke(bukkitView);

            for (AnvilSlot slot : this.items.keySet()) {
                this.inv.setItem(slot.getSlot(), this.items.get(slot));
            }

            Method nextContainerCounterMethod = entityPlayer.getClass().getMethod("nextContainerCounter");
            int c = (int) nextContainerCounterMethod.invoke(entityPlayer);

            Constructor<?> chatMessageConstructor = ChatMessageClass.getConstructor(String.class, Object[].class);
            Object inventoryTitle = chatMessageConstructor.newInstance("Repairing", new Object[]{});

            Object packet;

            if (NMSUtil.getVersionNumber() > 13) {
                packet = PacketPlayOutOpenWindowClass
                        .getConstructor(int.class, ContainersClass, IChatBaseComponentClass)
                        .newInstance(c, ContainersClass.getField("ANVIL").get(null), inventoryTitle);
            } else {
                packet = PacketPlayOutOpenWindowClass
                        .getConstructor(int.class, String.class, IChatBaseComponentClass, int.class)
                        .newInstance(c, "minecraft:anvil", inventoryTitle, 0);
            }

            NMSUtil.sendPacket(this.player, packet);

            Field activeContainerField = NMSUtil.getField(EntityHumanClass, "activeContainer", true);

            if (activeContainerField != null) {
                activeContainerField.set(entityPlayer, container);
                NMSUtil.getField(ContainerClass, "windowId", true).set(activeContainerField.get(entityPlayer), c);
                Method addSlotListenerMethod = activeContainerField.get(entityPlayer).getClass().getMethod("addSlotListener", ICraftingClass);
                addSlotListenerMethod.invoke(activeContainerField.get(entityPlayer), entityPlayer);

                if (NMSUtil.getVersionNumber() > 13) {
                    ContainerClass.getMethod("setTitle", IChatBaseComponentClass).invoke(container, inventoryTitle);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        this.player = null;
        this.items = null;

        HandlerList.unregisterAll(this.listener);

        this.listener = null;
    }

    public enum AnvilSlot {
        INPUT_LEFT(0),
        INPUT_RIGHT(1),
        OUTPUT(2);

        private final int slot;

        AnvilSlot(int slot) {
            this.slot = slot;
        }

        public static AnvilSlot bySlot(int slot) {
            for (AnvilSlot anvilSlot : values()) {
                if (anvilSlot.getSlot() == slot) {
                    return anvilSlot;
                }
            }

            return null;
        }

        public int getSlot() {
            return this.slot;
        }
    }

    @FunctionalInterface
    public interface AnvilClickEventHandler {
        void onAnvilClick(AnvilClickEvent event);
    }

    public class AnvilClickEvent {
        private final AnvilSlot slot;

        private final String name;

        private boolean close = true;
        private boolean destroy = true;

        public AnvilClickEvent(AnvilSlot slot, String name) {
            this.slot = slot;
            this.name = name;
        }

        public AnvilSlot getSlot() {
            return this.slot;
        }

        public String getName() {
            return this.name;
        }

        public boolean getWillClose() {
            return this.close;
        }

        public void setWillClose(boolean close) {
            this.close = close;
        }

        public boolean getWillDestroy() {
            return this.destroy;
        }

        public void setWillDestroy(boolean destroy) {
            this.destroy = destroy;
        }
    }

}