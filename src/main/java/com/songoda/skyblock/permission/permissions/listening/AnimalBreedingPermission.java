package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import com.songoda.skyblock.utils.version.NMSUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class AnimalBreedingPermission extends ListeningPermission {

    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public AnimalBreedingPermission(SkyBlock plugin) {
        super("AnimalBreeding", CompatibleMaterial.WHEAT, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack is = player.getItemInHand();
        Entity entity = event.getRightClicked();

        if (entity.getType() == EntityType.HORSE) {
            if (!(CompatibleMaterial.getMaterial(is) == CompatibleMaterial.GOLDEN_APPLE
                    || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.GOLDEN_CARROT
                    || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.SUGAR
                    || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.WHEAT
                    || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.APPLE
                    || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.HAY_BLOCK)) {
                return;
            }
        } else if (entity.getType() == EntityType.SHEEP || entity.getType() == EntityType.COW || entity.getType() == EntityType.MUSHROOM_COW) {
            if (!(CompatibleMaterial.getMaterial(is) == CompatibleMaterial.WHEAT)) {
                return;
            }
        } else if (entity.getType() == EntityType.PIG) {
            if (!(CompatibleMaterial.getMaterial(is) == CompatibleMaterial.CARROT || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.POTATO)) {
                return;
            }
        } else if (entity.getType() == EntityType.CHICKEN) {
            if (!(CompatibleMaterial.getMaterial(is) == CompatibleMaterial.WHEAT_SEEDS
                    || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.PUMPKIN_SEEDS || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.MELON_SEEDS)) {
                if (NMSUtil.getVersionNumber() > 8) {
                    if (!(CompatibleMaterial.getMaterial(is) == CompatibleMaterial.BEETROOT_SEEDS)) {
                        return;
                    }
                } else {
                    return;
                }
            }
        } else if (entity.getType() == EntityType.WOLF) {
            if (!(CompatibleMaterial.getMaterial(is) == CompatibleMaterial.BONE
                    || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.PORKCHOP
                    || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.BEEF
                    || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.CHICKEN
                    || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.RABBIT
                    || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.MUTTON
                    || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.ROTTEN_FLESH
                    || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.COOKED_PORKCHOP
                    || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.COOKED_BEEF
                    || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.COOKED_CHICKEN
                    || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.COOKED_RABBIT
                    || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.COOKED_MUTTON)) {
                return;
            }
        } else if (entity.getType() == EntityType.OCELOT) {
            if (!(CompatibleMaterial.getMaterial(is) == CompatibleMaterial.COD
                    || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.SALMON
                    || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.TROPICAL_FISH
                    || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.PUFFERFISH)) {
                return;
            }
        } else if (entity.getType() == EntityType.RABBIT) {
            if (!(CompatibleMaterial.getMaterial(is) == CompatibleMaterial.DANDELION
                    || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.CARROTS
                    || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.GOLDEN_CARROT)) {
                return;
            }
        } else {
            int NMSVersion = NMSUtil.getVersionNumber();

            if (NMSVersion > 10) {
                if (entity.getType() == EntityType.LLAMA) {
                    if (!(CompatibleMaterial.getMaterial(is) == CompatibleMaterial.HAY_BLOCK)) {
                        return;
                    }
                } else if (NMSVersion > 12) {
                    if (entity.getType() == EntityType.TURTLE) {
                        if (!(CompatibleMaterial.getMaterial(is) == CompatibleMaterial.SEAGRASS)) {
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
        cancelAndMessage(event, player, plugin, messageManager);
    }
}

