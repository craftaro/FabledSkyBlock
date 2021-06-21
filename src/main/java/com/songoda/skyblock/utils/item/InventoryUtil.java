package com.songoda.skyblock.utils.item;
import com.songoda.core.compatibility.ServerVersion;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class InventoryUtil {

    @SuppressWarnings("deprecation")
    public static void removeItem(Inventory inv, int amount, boolean hasDisplayname, Material material) {
        Map<Integer, ? extends ItemStack> ammo = inv.all(material);

        for (Integer index : ammo.keySet()) {
            ItemStack is = ammo.get(index);
            ItemMeta im = is.getItemMeta();

            if (ServerVersion.isServerVersionAbove(ServerVersion.V1_12)) {
                if (((Damageable) im).getDamage() != 0) {
                    continue;
                }
            } else {
                if (is.getDurability() != 0) {
                    continue;
                }
            }

            int removed = Math.min(amount, is.getAmount());
            amount -= removed;

            if (is.getAmount() == removed) {
                inv.setItem(index, null);
            } else {
                is.setAmount(is.getAmount() - removed);
            }

            if (amount <= 0) {
                break;
            }
        }
    }

    @SuppressWarnings("deprecation")
    public static boolean isInventoryFull(Inventory inv, int subtract, int amount, Material material) {
        for (int i = 0; i < inv.getSize() - subtract; i++) {
            ItemStack is = inv.getItem(i);

            if (is == null) {
                return false;
            } else if (is.getType() == material) {
                ItemMeta im = is.getItemMeta();

                if (!im.hasDisplayName()) {
                    if (ServerVersion.isServerVersionAbove(ServerVersion.V1_12)) {
                        if (((Damageable) im).getDamage() != 0) {
                            continue;
                        }
                    } else {
                        if (is.getDurability() != 0) {
                            continue;
                        }
                    }

                    if (is.getAmount() < is.getMaxStackSize() && (is.getAmount() + amount) <= is.getMaxStackSize()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public static void takeItem(Player player, int amount) {
        if (player.getGameMode() == GameMode.CREATIVE) return;

        ItemStack item = player.getInventory().getItemInMainHand();

        int result = item.getAmount() - amount;
        item.setAmount(result);

        player.getInventory().setItemInMainHand(result > 0 ? item : null);
    }
}
