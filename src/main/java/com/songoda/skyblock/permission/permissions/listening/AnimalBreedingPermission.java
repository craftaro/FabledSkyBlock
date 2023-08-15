package com.songoda.skyblock.permission.permissions.listening;

import com.craftaro.core.compatibility.ServerVersion;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class AnimalBreedingPermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public AnimalBreedingPermission(SkyBlock plugin) {
        super("AnimalBreeding", XMaterial.WHEAT, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack is = player.getItemInHand();
        Entity entity = event.getRightClicked();

        if (entity.getType() == EntityType.HORSE) {
            if (!(XMaterial.GOLDEN_APPLE.isSimilar(is)
                    || XMaterial.GOLDEN_CARROT.isSimilar(is)
                    || XMaterial.SUGAR.isSimilar(is)
                    || XMaterial.WHEAT.isSimilar(is)
                    || XMaterial.APPLE.isSimilar(is)
                    || XMaterial.HAY_BLOCK.isSimilar(is))) {
                return;
            }
        } else if (entity.getType() == EntityType.SHEEP || entity.getType() == EntityType.COW || entity.getType() == EntityType.MUSHROOM_COW) {
            if (!(XMaterial.WHEAT.isSimilar(is))) {
                return;
            }
        } else if (entity.getType() == EntityType.PIG) {
            if (!(XMaterial.CARROT.isSimilar(is) || XMaterial.POTATO.isSimilar(is))) {
                return;
            }
        } else if (entity.getType() == EntityType.CHICKEN) {
            if (!(XMaterial.WHEAT_SEEDS.isSimilar(is)
                    || XMaterial.PUMPKIN_SEEDS.isSimilar(is) || XMaterial.MELON_SEEDS.isSimilar(is))) {
                if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9)) {
                    if (!(XMaterial.BEETROOT_SEEDS.isSimilar(is))) {
                        return;
                    }
                } else {
                    return;
                }
            }
        } else if (entity.getType() == EntityType.WOLF) {
            if (!(XMaterial.BONE.isSimilar(is)
                    || XMaterial.PORKCHOP.isSimilar(is)
                    || XMaterial.BEEF.isSimilar(is)
                    || XMaterial.CHICKEN.isSimilar(is)
                    || XMaterial.RABBIT.isSimilar(is)
                    || XMaterial.MUTTON.isSimilar(is)
                    || XMaterial.ROTTEN_FLESH.isSimilar(is)
                    || XMaterial.COOKED_PORKCHOP.isSimilar(is)
                    || XMaterial.COOKED_BEEF.isSimilar(is)
                    || XMaterial.COOKED_CHICKEN.isSimilar(is)
                    || XMaterial.COOKED_RABBIT.isSimilar(is)
                    || XMaterial.COOKED_MUTTON.isSimilar(is))) {
                return;
            }
        } else if (entity.getType() == EntityType.OCELOT) {
            if (!(XMaterial.COD.isSimilar(is)
                    || XMaterial.SALMON.isSimilar(is)
                    || XMaterial.TROPICAL_FISH.isSimilar(is)
                    || XMaterial.PUFFERFISH.isSimilar(is))) {
                return;
            }
        } else if (entity.getType() == EntityType.RABBIT) {
            if (!(XMaterial.DANDELION.isSimilar(is)
                    || XMaterial.CARROTS.isSimilar(is)
                    || XMaterial.GOLDEN_CARROT.isSimilar(is))) {
                return;
            }
        } else {
            if (ServerVersion.isServerVersionAbove(ServerVersion.V1_10)) {
                if (entity.getType() == EntityType.LLAMA) {
                    if (!(XMaterial.HAY_BLOCK.isSimilar(is))) {
                        return;
                    }
                } else if (ServerVersion.isServerVersionAbove(ServerVersion.V1_12)) {
                    if (entity.getType() == EntityType.TURTLE) {
                        if (!(XMaterial.SEAGRASS.isSimilar(is))) {
                            return;
                        }
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            } else {
                return;
            }
        }
        cancelAndMessage(event, player, this.plugin, this.messageManager);
    }
}
