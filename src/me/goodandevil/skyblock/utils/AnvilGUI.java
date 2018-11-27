package me.goodandevil.skyblock.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.utils.version.NMSUtil;

public class AnvilGUI {
	
    private Player player;
    
    private Class<?> BlockPosition;
    private Class<?> PacketPlayOutOpenWindow;
    private Class<?> ContainerAnvil;
    private Class<?> ChatMessage;
    private Class<?> EntityHuman;
    
    private Map<AnvilSlot, ItemStack> items = new HashMap<>();
    private Inventory inv;
    private Listener listener;

    private void loadClasses() {
        BlockPosition = NMSUtil.getNMSClass("BlockPosition");
        PacketPlayOutOpenWindow = NMSUtil.getNMSClass("PacketPlayOutOpenWindow");
        ContainerAnvil = NMSUtil.getNMSClass("ContainerAnvil");
        EntityHuman = NMSUtil.getNMSClass("EntityHuman");
        ChatMessage = NMSUtil.getNMSClass("ChatMessage");
    }

    public AnvilGUI(final Player player, final AnvilClickEventHandler handler) {
        loadClasses();
        this.player = player;
        
        this.listener = new Listener() {
            @EventHandler
            public void onInventoryClick(InventoryClickEvent event) {
                if (event.getWhoClicked() instanceof Player) {

                    if (event.getInventory().equals(inv)) {
                        event.setCancelled(true);

                        ItemStack item = event.getCurrentItem();
                        int slot = event.getRawSlot();
                        String name = "";

                        if (item != null) {
                            if (item.hasItemMeta()) {
                                ItemMeta meta = item.getItemMeta();

                                if (meta.hasDisplayName()) {
                                    name = meta.getDisplayName();
                                }
                            }
                        }

                        AnvilClickEvent clickEvent = new AnvilClickEvent(AnvilSlot.bySlot(slot), name);

                        handler.onAnvilClick(clickEvent);

                        if (clickEvent.getWillClose()) {
                            event.getWhoClicked().closeInventory();
                        }

                        if (clickEvent.getWillDestroy()) {
                            destroy();
                        }
                    }
                }
            }

            @EventHandler
            public void onInventoryClose(InventoryCloseEvent event) {
                if (event.getPlayer() instanceof Player) {
                    Inventory inv = event.getInventory();
                    player.setLevel(player.getLevel() - 1);
                    if (inv.equals(AnvilGUI.this.inv)) {
                        inv.clear();
                        destroy();
                    }
                }
            }

            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent event) {
                if (event.getPlayer().equals(getPlayer())) {
                    player.setLevel(player.getLevel() - 1);
                    destroy();
                }
            }
        };

        Bukkit.getPluginManager().registerEvents(listener, SkyBlock.getInstance());
    }

    public Player getPlayer() {
        return player;
    }

    public void setSlot(AnvilSlot slot, ItemStack item) {
        items.put(slot, item);
    }

    public void open() {
        player.setLevel(player.getLevel() + 1);

        try {
	        Object craftPlayer = NMSUtil.getCraftClass("entity.CraftPlayer").cast(player);
	        Method getHandleMethod = craftPlayer.getClass().getMethod("getHandle", new Class<?>[0]);
	        Object entityPlayer = getHandleMethod.invoke(craftPlayer, new Object[0]);

            Object container;
            
  	      	if (NMSUtil.getVersionNumber() == 7) {
  	      		container = ContainerAnvil.getConstructor(new Class[] { NMSUtil.getNMSClass("PlayerInventory"), NMSUtil.getNMSClass("World"), Integer.TYPE, Integer.TYPE, Integer.TYPE, EntityHuman }).newInstance(new Object[] { NMSUtil.getFieldObject(entityPlayer, NMSUtil.getField(entityPlayer.getClass(), "inventory", false)), NMSUtil.getFieldObject(entityPlayer, NMSUtil.getField(entityPlayer.getClass(), "world", false)), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), entityPlayer });
  	      	} else {
  	      		container = ContainerAnvil.getConstructor(NMSUtil.getNMSClass("PlayerInventory"), NMSUtil.getNMSClass("World"), BlockPosition, EntityHuman).newInstance(NMSUtil.getFieldObject(entityPlayer, NMSUtil.getField(entityPlayer.getClass(), "inventory", false)), NMSUtil.getFieldObject(entityPlayer, NMSUtil.getField(entityPlayer.getClass(), "world", false)), BlockPosition.getConstructor(int.class, int.class, int.class).newInstance(0, 0, 0), entityPlayer);
  	      	}
            
            NMSUtil.getField(NMSUtil.getNMSClass("Container"), "checkReachable", true).set(container, false);
            
            Method getBukkitViewMethod = container.getClass().getMethod("getBukkitView", new Class<?>[0]);
            Object bukkitView = getBukkitViewMethod.invoke(container);
            Method getTopInventoryMethod = bukkitView.getClass().getMethod("getTopInventory", new Class<?>[0]);
            inv = (Inventory) getTopInventoryMethod.invoke(bukkitView);

            for (AnvilSlot slot : items.keySet()) {
                inv.setItem(slot.getSlot(), items.get(slot));
            }
            
            Method nextContainerCounterMethod = entityPlayer.getClass().getMethod("nextContainerCounter", new Class<?>[0]);
            int c = (int) nextContainerCounterMethod.invoke(entityPlayer);
            
            Constructor<?> chatMessageConstructor = ChatMessage.getConstructor(String.class, Object[].class);
            Object packet;
            
            if (NMSUtil.getVersionNumber() == 7) {
            	packet = PacketPlayOutOpenWindow.getConstructor(new Class[] { Integer.TYPE, Integer.TYPE, String.class, Integer.TYPE, Boolean.TYPE, Integer.TYPE }).newInstance(new Object[] { Integer.valueOf(c), Integer.valueOf(8), "Repairing", Integer.valueOf(0), Boolean.valueOf(true), Integer.valueOf(0) });
            } else {
            	packet = PacketPlayOutOpenWindow.getConstructor(int.class, String.class, NMSUtil.getNMSClass("IChatBaseComponent"), int.class).newInstance(c, "minecraft:anvil", chatMessageConstructor.newInstance("Repairing", new Object[]{}), 0);
            }
            
            NMSUtil.sendPacket(player, packet);
            
            Field activeContainerField = NMSUtil.getField(EntityHuman, "activeContainer", true);
            
            if (activeContainerField != null) {
                activeContainerField.set(entityPlayer, container);
                NMSUtil.getField(NMSUtil.getNMSClass("Container"), "windowId", true).set(activeContainerField.get(entityPlayer), c);
                
                Method addSlotListenerMethod = activeContainerField.get(entityPlayer).getClass().getMethod("addSlotListener", NMSUtil.getNMSClass("ICrafting"));
                addSlotListenerMethod.invoke(activeContainerField.get(entityPlayer), entityPlayer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        player = null;
        items = null;

        HandlerList.unregisterAll(listener);

        listener = null;
    }

    public enum AnvilSlot {
        INPUT_LEFT(0),
        INPUT_RIGHT(1),
        OUTPUT(2);

        private int slot;

        private AnvilSlot(int slot) {
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
            return slot;
        }
    }

    public interface AnvilClickEventHandler {
        void onAnvilClick(AnvilClickEvent event);
    }

    public class AnvilClickEvent {
        private AnvilSlot slot;

        private String name;

        private boolean close = true;
        private boolean destroy = true;

        public AnvilClickEvent(AnvilSlot slot, String name) {
            this.slot = slot;
            this.name = name;
        }

        public AnvilSlot getSlot() {
            return slot;
        }

        public String getName() {
            return name;
        }

        public boolean getWillClose() {
            return close;
        }

        public void setWillClose(boolean close) {
            this.close = close;
        }

        public boolean getWillDestroy() {
            return destroy;
        }

        public void setWillDestroy(boolean destroy) {
            this.destroy = destroy;
        }
    }
}
