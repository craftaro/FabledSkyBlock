package com.craftaro.skyblock.challenge.inventory.inv;

import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.challenge.FabledChallenge;
import com.craftaro.skyblock.challenge.challenge.Challenge;
import com.craftaro.skyblock.challenge.challenge.ChallengeCategory;
import com.craftaro.skyblock.challenge.challenge.ItemChallenge;
import com.craftaro.skyblock.challenge.defaultinv.DefaultInventory;
import com.craftaro.skyblock.challenge.defaultinv.Item;
import com.craftaro.skyblock.challenge.inventory.ClickableItem;
import com.craftaro.skyblock.challenge.inventory.Inventory;
import com.craftaro.skyblock.challenge.inventory.InventoryProvider;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.logging.Level;

public class ChallengeInventory implements InventoryProvider {
    public static final String CATEGORY = "ChallengeCategory";
    private final FabledChallenge fc;

    public ChallengeInventory(FabledChallenge fc) {
        this.fc = fc;
    }

    @Override
    public String title(Inventory inv) {
        ChallengeCategory category = (ChallengeCategory) inv.get(CATEGORY);
        return category.getName();
    }

    @Override
    public int rows(Inventory inv) {
        return this.fc.getDefaultInventory().getSize();
    }

    @Override
    public void init(Inventory inv) {
        try {
            // Initialize the inventory
            ChallengeCategory category = (ChallengeCategory) inv.get(CATEGORY);
            HashMap<Challenge, Integer> done = this.fc.getPlayerManager()
                    .getPlayer(inv.getPlayer().getUniqueId());
            if (done == null) {
                // How is that possible
                return;
            }
            // Set background
            DefaultInventory di = this.fc.getDefaultInventory();
            for (int row = 0; row < di.getSize(); row++) {
                for (int col = 0; col < 9; col++) {
                    Item item = di.get(row, col);
                    inv.set(col + 1, row + 1, ClickableItem.of(item.getItemStack(), e -> {
                        if (item.getRedirect() != 0) {
                            ChallengeCategory cat = this.fc.getChallengeManager()
                                    .getChallenge(item.getRedirect());
                            if (cat != null) {
                                // Open inventory
                                this.fc.openChallengeInventory(inv.getPlayer(), cat);
                            }
                        }
                    }));
                }
            }

            // Set challenges
            for (Challenge c : category.getChallenges().values()) {
                ItemChallenge ic = c.getItem();
                if (!ic.isShow()) {
                    continue;
                }
                int count = done.getOrDefault(c, 0);
                ItemStack is = ic.createItem(inv.getPlayer().getUniqueId(), count);
                // If challenge is done, add enchantment to show to player that it's done
                if (count >= c.getMaxTimes()) {
                    is.addUnsafeEnchantment(Enchantment.OXYGEN, 1);
                }
                ItemMeta im = is.getItemMeta();
                im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                is.setItemMeta(im);

                Consumer<InventoryClickEvent> consumer = e -> {
                    // Count the new value
                    int count2 = done.getOrDefault(c, 0);
                    if (count2 >= c.getMaxTimes()) {
                        // Do not continue if maxtimes has been reached
                        return;
                    }
                    Player p = inv.getPlayer();
                    if (this.fc.getPlayerManager().doChallenge(p, c)) {
                        // Ok
                        // Update count
                        count2 = done.getOrDefault(c, 0);
                        // Play sound
                        XSound.ENTITY_PLAYER_LEVELUP.play(p);
                        // Update item
                        ItemStack is2 = ic.createItem(inv.getPlayer().getUniqueId(), count2);
                        // If challenge is done, add enchantment to show to player that it's done
                        if (count2 >= c.getMaxTimes()) {
                            is2.addUnsafeEnchantment(Enchantment.OXYGEN, 1);
                        }
                        ItemMeta im2 = is2.getItemMeta();
                        im2.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        is2.setItemMeta(im2);
                        // Update
                        inv.updateItem(ic.getCol(), ic.getRow(), is2);
                    } else {
                        XSound.BLOCK_GLASS_BREAK.play(p);
                    }
                };
                inv.set(ic.getCol(), ic.getRow(), ClickableItem.of(is, consumer));
            }
        } catch (Exception ex) {
            Bukkit.getLogger().log(Level.SEVERE, "", ex);
        }
    }

    @Override
    public void update(Inventory inv) {
        // Nothing to update here
    }
}
