package com.songoda.skyblock.menus;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.placeholder.Placeholder;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.AbstractAnvilGUI;
import com.songoda.skyblock.utils.item.SkullUtil;
import com.songoda.skyblock.utils.item.nInventoryUtil;
import com.songoda.skyblock.utils.player.OfflinePlayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.Set;
import java.util.UUID;

public class Bans {

    private static Bans instance;

    public static Bans getInstance() {
        if (instance == null) {
            instance = new Bans();
        }

        return instance;
    }

    public void open(Player player) {
        SkyBlock skyblock = SkyBlock.getInstance();

        PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
        MessageManager messageManager = skyblock.getMessageManager();
        IslandManager islandManager = skyblock.getIslandManager();
        SoundManager soundManager = skyblock.getSoundManager();
        FileManager fileManager = skyblock.getFileManager();

        if (playerDataManager.hasPlayerData(player)) {
            PlayerData playerData = playerDataManager.getPlayerData(player);
            Island island = skyblock.getIslandManager().getIsland(player);

            Config languageConfig = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
            FileConfiguration configLoad = languageConfig.getFileConfiguration();

            nInventoryUtil nInv = new nInventoryUtil(player, event -> {
                if (playerDataManager.hasPlayerData(player)) {
                    PlayerData playerData1 = playerDataManager.getPlayerData(player);
                    Island island1 = islandManager.getIsland(player);

                    if (island1 == null) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Bans.Owner.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                        return;
                    } else if (!fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
                            .getFileConfiguration().getBoolean("Island.Visitor.Banning")) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Bans.Disabled.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                        return;
                    }

                    ItemStack is = event.getItem();

                    if ((is.getType() == CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getMaterial()) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Bans.Item.Barrier.Displayname"))))) {
                        soundManager.playSound(player, CompatibleSound.BLOCK_GLASS_BREAK.getSound(), 1.0F, 1.0F);

                        event.setWillClose(false);
                        event.setWillDestroy(false);
                    } else if ((is.getType() == CompatibleMaterial.OAK_FENCE_GATE.getMaterial()) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Bans.Item.Exit.Displayname"))))) {
                        soundManager.playSound(player, CompatibleSound.BLOCK_CHEST_CLOSE.getSound(), 1.0F, 1.0F);
                    } else if ((is.getType() == Material.PAINTING) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Bans.Item.Information.Displayname"))))) {
                        soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                        Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> {
                            AbstractAnvilGUI gui = new AbstractAnvilGUI(player, event1 -> {
                                if (event1.getSlot() == AbstractAnvilGUI.AnvilSlot.OUTPUT) {
                                    Bukkit.getServer().dispatchCommand(player,
                                            "island ban " + event1.getName());

                                    event1.setWillClose(true);
                                    event1.setWillDestroy(true);
                                } else {
                                    event1.setWillClose(false);
                                    event1.setWillDestroy(false);
                                }
                            });

                            ItemStack is1 = new ItemStack(Material.NAME_TAG);
                            ItemMeta im = is1.getItemMeta();
                            im.setDisplayName(configLoad.getString("Menu.Bans.Item.Word.Enter"));
                            is1.setItemMeta(im);

                            gui.setSlot(AbstractAnvilGUI.AnvilSlot.INPUT_LEFT, is1);
                            gui.open();
                        }, 1L);
                    } else if ((is.getType() == Material.BARRIER) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Bans.Item.Nothing.Displayname"))))) {
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                        event.setWillClose(false);
                        event.setWillDestroy(false);
                    } else if ((is.getType() == SkullUtil.createItemStack().getType()) && (is.hasItemMeta())) {
                        if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Menu.Bans.Item.Previous.Displayname")))) {
                            playerData1.setPage(playerData1.getPage() - 1);
                            soundManager.playSound(player, CompatibleSound.ENTITY_ARROW_HIT.getSound(), 1.0F, 1.0F);

                            Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player), 1L);
                        } else if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes(
                                '&', configLoad.getString("Menu.Bans.Item.Next.Displayname")))) {
                            playerData1.setPage(playerData1.getPage() + 1);
                            soundManager.playSound(player, CompatibleSound.ENTITY_ARROW_HIT.getSound(), 1.0F, 1.0F);

                            Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player), 1L);
                        } else {
                            if ((island1.hasRole(IslandRole.Operator, player.getUniqueId())
                                    && skyblock.getPermissionManager().hasPermission(island1,"Unban", IslandRole.Operator))
                                    || island1.hasRole(IslandRole.Owner, player.getUniqueId())) {
                                String playerName = ChatColor.stripColor(is.getItemMeta().getDisplayName());
                                Bukkit.getServer().dispatchCommand(player, "island unban " + playerName);

                                Bukkit.getServer().getScheduler().runTaskLater(skyblock,
                                        () -> open(player), 3L);
                            } else {
                                messageManager.sendMessage(player,
                                        configLoad.getString("Command.Island.Bans.Permission.Message"));
                                soundManager.playSound(player,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                                event.setWillClose(false);
                                event.setWillDestroy(false);
                            }
                        }
                    }
                }
            });

            Set<UUID> islandBans = island.getBan().getBans();

            nInv.addItem(nInv.createItem(CompatibleMaterial.OAK_FENCE_GATE.getItem(),
                    configLoad.getString("Menu.Bans.Item.Exit.Displayname"), null, null, null, null), 0, 8);
            nInv.addItem(nInv.createItem(new ItemStack(Material.PAINTING),
                    configLoad.getString("Menu.Bans.Item.Information.Displayname"),
                    configLoad.getStringList("Menu.Bans.Item.Information.Lore"),
                    new Placeholder[]{new Placeholder("%bans", "" + islandBans.size())}, null, null), 4);
            nInv.addItem(
                    nInv.createItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem(),
                            configLoad.getString("Menu.Bans.Item.Barrier.Displayname"), null, null, null, null),
                    9, 10, 11, 12, 13, 14, 15, 16, 17);

            int playerMenuPage = playerData.getPage(), nextEndIndex = islandBans.size() - playerMenuPage * 36;

            if (playerMenuPage != 1) {
                nInv.addItem(nInv.createItem(SkullUtil.create(
                        "ToR1w9ZV7zpzCiLBhoaJH3uixs5mAlMhNz42oaRRvrG4HRua5hC6oyyOPfn2HKdSseYA9b1be14fjNRQbSJRvXF3mlvt5/zct4sm+cPVmX8K5kbM2vfwHJgCnfjtPkzT8sqqg6YFdT35mAZGqb9/xY/wDSNSu/S3k2WgmHrJKirszaBZrZfnVnqITUOgM9TmixhcJn2obeqICv6tl7/Wyk/1W62wXlXGm9+WjS+8rRNB+vYxqKR3XmH2lhAiyVGbADsjjGtBVUTWjq+aPw670SjXkoii0YE8sqzUlMMGEkXdXl9fvGtnWKk3APSseuTsjedr7yq+AkXFVDqqkqcUuXwmZl2EjC2WRRbhmYdbtY5nEfqh5+MiBrGdR/JqdEUL4yRutyRTw8mSUAI6X2oSVge7EdM/8f4HwLf33EO4pTocTqAkNbpt6Z54asLe5Y12jSXbvd2dFsgeJbrslK7e4uy/TK8CXf0BP3KLU20QELYrjz9I70gtj9lJ9xwjdx4/xJtxDtrxfC4Afmpu+GNYA/mifpyP3GDeBB5CqN7btIvEWyVvRNH7ppAqZIPqYJ7dSDd2RFuhAId5Yq98GUTBn+eRzeigBvSi1bFkkEgldfghOoK5WhsQtQbXuBBXITMME3NaWCN6zG7DxspS6ew/rZ8E809Xe0ArllquIZ0sP+k=",
                        "eyJ0aW1lc3RhbXAiOjE0OTU3NTE5MTYwNjksInByb2ZpbGVJZCI6ImE2OGYwYjY0OGQxNDQwMDBhOTVmNGI5YmExNGY4ZGY5IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dMZWZ0Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zZWJmOTA3NDk0YTkzNWU5NTViZmNhZGFiODFiZWFmYjkwZmI5YmU0OWM3MDI2YmE5N2Q3OThkNWYxYTIzIn19fQ=="),
                        configLoad.getString("Menu.Bans.Item.Previous.Displayname"), null, null, null, null), 1);
            }

            if (!(nextEndIndex == 0 || nextEndIndex < 0)) {
                nInv.addItem(nInv.createItem(SkullUtil.create(
                        "wZPrsmxckJn4/ybw/iXoMWgAe+1titw3hjhmf7bfg9vtOl0f/J6YLNMOI0OTvqeRKzSQVCxqNOij6k2iM32ZRInCQyblDIFmFadQxryEJDJJPVs7rXR6LRXlN8ON2VDGtboRTL7LwMGpzsrdPNt0oYDJLpR0huEeZKc1+g4W13Y4YM5FUgEs8HvMcg4aaGokSbvrYRRcEh3LR1lVmgxtbiUIr2gZkR3jnwdmZaIw/Ujw28+Et2pDMVCf96E5vC0aNY0KHTdMYheT6hwgw0VAZS2VnJg+Gz4JCl4eQmN2fs4dUBELIW2Rdnp4U1Eb+ZL8DvTV7ofBeZupknqPOyoKIjpInDml9BB2/EkD3zxFtW6AWocRphn03Z203navBkR6ztCMz0BgbmQU/m8VL/s8o4cxOn+2ppjrlj0p8AQxEsBdHozrBi8kNOGf1j97SDHxnvVAF3X8XDso+MthRx5pbEqpxmLyKKgFh25pJE7UaMSnzH2lc7aAZiax67MFw55pDtgfpl+Nlum4r7CK2w5Xob2QTCovVhu78/6SV7qM2Lhlwx/Sjqcl8rn5UIoyM49QE5Iyf1tk+xHXkIvY0m7q358oXsfca4eKmxMe6DFRjUDo1VuWxdg9iVjn22flqz1LD1FhGlPoqv0k4jX5Q733LwtPPI6VOTK+QzqrmiuR6e8=",
                        "eyJ0aW1lc3RhbXAiOjE0OTM4NjgxMDA2NzMsInByb2ZpbGVJZCI6IjUwYzg1MTBiNWVhMDRkNjBiZTlhN2Q1NDJkNmNkMTU2IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dSaWdodCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ=="),
                        configLoad.getString("Menu.Bans.Item.Next.Displayname"), null, null, null, null), 7);
            }

            if (islandBans.size() == 0) {
                nInv.addItem(
                        nInv.createItem(new ItemStack(Material.BARRIER),
                                configLoad.getString("Menu.Bans.Item.Nothing.Displayname"), null, null, null, null),
                        31);
            } else {
                int index = playerMenuPage * 36 - 36,
                        endIndex = index >= islandBans.size() ? islandBans.size() - 1 : index + 36, inventorySlot = 17;

                for (; index < endIndex; index++) {
                    if (islandBans.size() > index) {
                        inventorySlot++;

                        UUID targetPlayerUUID = (UUID) islandBans.toArray()[index];
                        String targetPlayerName;
                        String[] targetPlayerTexture;

                        Player targetPlayer = Bukkit.getServer().getPlayer(targetPlayerUUID);

                        if (targetPlayer == null) {
                            OfflinePlayer offlinePlayer = new OfflinePlayer(targetPlayerUUID);
                            targetPlayerName = offlinePlayer.getName();
                            targetPlayerTexture = offlinePlayer.getTexture();
                        } else {
                            targetPlayerName = targetPlayer.getName();

                            if (playerDataManager.hasPlayerData(targetPlayer)) {
                                targetPlayerTexture = playerDataManager.getPlayerData(targetPlayer).getTexture();
                            } else {
                                targetPlayerTexture = new String[]{null, null};
                            }
                        }

                        nInv.addItem(
                                nInv.createItem(SkullUtil.create(targetPlayerTexture[0], targetPlayerTexture[1]),
                                        ChatColor.translateAlternateColorCodes('&',
                                                configLoad.getString("Menu.Bans.Item.Ban.Displayname")
                                                        .replace("%player", targetPlayerName == null ? "" : targetPlayerName)),
                                        configLoad.getStringList("Menu.Bans.Item.Ban.Lore"), null, null, null),
                                inventorySlot);
                    }
                }
            }

            nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Bans.Title")));
            nInv.setRows(6);

            Bukkit.getServer().getScheduler().runTask(skyblock, () -> nInv.open());
        }
    }
}
