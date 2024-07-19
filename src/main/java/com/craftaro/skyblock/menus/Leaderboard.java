package com.craftaro.skyblock.menus;

import com.craftaro.core.utils.NumberUtils;
import com.craftaro.core.utils.SkullItemCreator;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.placeholder.Placeholder;
import com.craftaro.skyblock.playerdata.PlayerDataManager;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.utils.item.nInventoryUtil;
import com.craftaro.skyblock.utils.player.OfflinePlayer;
import com.craftaro.skyblock.visit.Visit;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Leaderboard {
    private static Leaderboard instance;

    public static Leaderboard getInstance() {
        if (instance == null) {
            instance = new Leaderboard();
        }

        return instance;
    }

    public void open(Player player) {
        SkyBlock plugin = SkyBlock.getPlugin(SkyBlock.class);

        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        SoundManager soundManager = plugin.getSoundManager();

        if (playerDataManager.hasPlayerData(player)) {
            FileConfiguration configLoad = plugin.getLanguage();

            Viewer viewer = (Viewer) playerDataManager.getPlayerData(player).getViewer();

            nInventoryUtil nInv;
            if (viewer.getType() == Viewer.Type.BROWSE) {
                nInv = new nInventoryUtil(player, event -> {
                    if (playerDataManager.hasPlayerData(player)) {
                        ItemStack is = event.getItem();

                        if ((XMaterial.OAK_FENCE_GATE.isSimilar(is)) && (is.hasItemMeta())
                                && (is.getItemMeta().getDisplayName()
                                .equals(plugin.formatText(configLoad.getString("Menu.Leaderboard." + Viewer.Type.BROWSE.getFriendlyName()
                                        + ".Item.Exit.Displayname"))))) {
                            soundManager.playSound(player, XSound.BLOCK_CHEST_CLOSE);

                            return;
                        } else if ((is.getType() == Material.DIAMOND) && (is.hasItemMeta())
                                && (is.getItemMeta().getDisplayName()
                                .equals(ChatColor.translateAlternateColorCodes('&',
                                        configLoad
                                                .getString("Menu.Leaderboard." + Viewer.Type.BROWSE.getFriendlyName()
                                                        + ".Item.Leaderboard.Displayname")
                                                .replace("%leaderboard", Viewer.Type.LEVEL.getFriendlyName()))))) {
                            playerDataManager.getPlayerData(player).setViewer(new Viewer(Viewer.Type.LEVEL));
                        } else if ((is.getType() == Material.GOLD_INGOT) && (is.hasItemMeta())
                                && (is.getItemMeta().getDisplayName()
                                .equals(ChatColor.translateAlternateColorCodes('&',
                                        configLoad
                                                .getString("Menu.Leaderboard." + Viewer.Type.BROWSE.getFriendlyName()
                                                        + ".Item.Leaderboard.Displayname")
                                                .replace("%leaderboard", Viewer.Type.BANK.getFriendlyName()))))) {
                            playerDataManager.getPlayerData(player).setViewer(new Viewer(Viewer.Type.BANK));
                        } else if ((is.getType() == Material.EMERALD) && (is.hasItemMeta())
                                && (is.getItemMeta().getDisplayName()
                                .equals(ChatColor.translateAlternateColorCodes('&',
                                        configLoad
                                                .getString("Menu.Leaderboard." + Viewer.Type.BROWSE.getFriendlyName()
                                                        + ".Item.Leaderboard.Displayname")
                                                .replace("%leaderboard", Viewer.Type.VOTES.getFriendlyName()))))) {
                            playerDataManager.getPlayerData(player).setViewer(new Viewer(Viewer.Type.VOTES));
                        }

                        soundManager.playSound(player, XSound.BLOCK_NOTE_BLOCK_PLING);

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                    }
                });

                nInv.addItem(nInv.createItem(XMaterial.OAK_FENCE_GATE.parseItem(),
                        configLoad.getString("Menu.Leaderboard." + viewer.getType().getFriendlyName() + ".Item.Exit.Displayname"),
                        null, null, null, null), 0, 4);
                nInv.addItem(
                        nInv.createItem(new ItemStack(Material.DIAMOND), configLoad
                                        .getString(
                                                "Menu.Leaderboard." + viewer.getType().getFriendlyName() + ".Item.Leaderboard.Displayname")
                                        .replace("%leaderboard", Viewer.Type.LEVEL.getFriendlyName()),
                                configLoad.getStringList(
                                        "Menu.Leaderboard." + viewer.getType().getFriendlyName() + ".Item.Leaderboard.Lore"),
                                new Placeholder[]{new Placeholder("%leaderboard", Viewer.Type.LEVEL.getFriendlyName())}, null,
                                null),
                        1);

                if (plugin.getConfiguration().getBoolean("Island.Bank.Enable")) {
                    nInv.addItem(
                            nInv.createItem(new ItemStack(Material.GOLD_INGOT), configLoad
                                            .getString(
                                                    "Menu.Leaderboard." + viewer.getType().getFriendlyName() + ".Item.Leaderboard.Displayname")
                                            .replace("%leaderboard", Viewer.Type.BANK.getFriendlyName()),
                                    configLoad.getStringList(
                                            "Menu.Leaderboard." + viewer.getType().getFriendlyName() + ".Item.Leaderboard.Lore"),
                                    new Placeholder[]{new Placeholder("%leaderboard", Viewer.Type.BANK.getFriendlyName())}, null,
                                    null),
                            2);
                } else {
                    nInv.addItem(
                            nInv.createItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem(), "", null, null, null, null), 2);
                }
                nInv.addItem(
                        nInv.createItem(new ItemStack(Material.EMERALD), configLoad
                                        .getString(
                                                "Menu.Leaderboard." + viewer.getType().getFriendlyName() + ".Item.Leaderboard.Displayname")
                                        .replace("%leaderboard", Viewer.Type.VOTES.getFriendlyName()),
                                configLoad.getStringList(
                                        "Menu.Leaderboard." + viewer.getType().getFriendlyName() + ".Item.Leaderboard.Lore"),
                                new Placeholder[]{new Placeholder("%leaderboard", Viewer.Type.VOTES.getFriendlyName())}, null,
                                null),
                        3);

                nInv.setTitle(plugin.formatText(
                        configLoad.getString("Menu.Leaderboard." + viewer.getType().getFriendlyName() + ".Title")));
                nInv.setType(InventoryType.HOPPER);

            } else {
                nInv = new nInventoryUtil(player, event -> {
                    if (playerDataManager.hasPlayerData(player)) {
                        ItemStack is = event.getItem();

                        if ((XMaterial.OAK_FENCE_GATE.isSimilar(is)) && (is.hasItemMeta())) {
                            if (is.getItemMeta().getDisplayName().equals(plugin.formatText(
                                    configLoad.getString("Menu.Leaderboard.Leaderboard.Item.Exit.Displayname")))) {
                                soundManager.playSound(player, XSound.BLOCK_CHEST_CLOSE);
                            } else if (is.getItemMeta().getDisplayName()
                                    .equals(plugin.formatText(configLoad
                                            .getString("Menu.Leaderboard.Leaderboard.Item.Return.Displayname")))) {
                                if (plugin.getConfiguration().getBoolean("Island.Visitor.Vote")) {
                                    playerDataManager.getPlayerData(player)
                                            .setViewer(new Viewer(Viewer.Type.BROWSE));
                                    soundManager.playSound(player, XSound.ENTITY_ARROW_HIT);

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                                } else {
                                    soundManager.playSound(player, XSound.BLOCK_CHEST_CLOSE);
                                }

                                return;
                            }
                        }

                        int clickedSlot = event.getSlot();
                        int leaderboardPosition = -1;

                        if (clickedSlot == 13) {
                            leaderboardPosition = 0;
                        } else if (clickedSlot == 21) {
                            leaderboardPosition = 1;
                        } else if (clickedSlot == 22) {
                            leaderboardPosition = 2;
                        } else if (clickedSlot == 23) {
                            leaderboardPosition = 3;
                        } else if (clickedSlot == 29) {
                            leaderboardPosition = 4;
                        } else if (clickedSlot == 31) {
                            leaderboardPosition = 5;
                        } else if (clickedSlot == 33) {
                            leaderboardPosition = 6;
                        } else if (clickedSlot == 37) {
                            leaderboardPosition = 7;
                        } else if (clickedSlot == 40) {
                            leaderboardPosition = 8;
                        } else if (clickedSlot == 43) {
                            leaderboardPosition = 9;
                        }

                        if (leaderboardPosition != -1) {
                            List<com.craftaro.skyblock.leaderboard.Leaderboard> leaderboardIslands = plugin
                                    .getLeaderboardManager().getLeaderboard(
                                            com.craftaro.skyblock.leaderboard.Leaderboard.Type.valueOf(viewer.getType().name()));

                            if (leaderboardIslands.size() > leaderboardPosition) {
                                com.craftaro.skyblock.leaderboard.Leaderboard leaderboard = leaderboardIslands.get(leaderboardPosition);
                                Visit visit = leaderboard.getVisit();

                                OfflinePlayer offlinePlayer = new OfflinePlayer(visit.getOwnerUUID());
                                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Bukkit.dispatchCommand(player, "island teleport " + offlinePlayer.getName()));
                            }

                            event.setWillClose(false);
                            event.setWillDestroy(false);

                            return;
                        }

                        soundManager.playSound(player, XSound.ENTITY_CHICKEN_EGG);

                        event.setWillClose(false);
                        event.setWillDestroy(false);
                    }
                });

                if (plugin.getConfiguration()
                        .getBoolean("Island.Visitor.Vote")) {
                    nInv.addItem(nInv.createItem(XMaterial.OAK_FENCE_GATE.parseItem(),
                            configLoad.getString("Menu.Leaderboard.Leaderboard.Item.Return.Displayname"), null, null,
                            null, null), 0, 8);
                } else {
                    nInv.addItem(nInv.createItem(XMaterial.OAK_FENCE_GATE.parseItem(),
                            configLoad.getString("Menu.Leaderboard.Leaderboard.Item.Exit.Displayname"), null, null,
                            null, null), 0, 8);
                }

                List<com.craftaro.skyblock.leaderboard.Leaderboard> leaderboardIslands = plugin
                        .getLeaderboardManager().getLeaderboard(
                                com.craftaro.skyblock.leaderboard.Leaderboard.Type.valueOf(viewer.getType().name()));

                for (com.craftaro.skyblock.leaderboard.Leaderboard leaderboard : leaderboardIslands) {
                    Visit visit = leaderboard.getVisit();

                    int itemSlot = 0;

                    String playerName;
                    String[] playerTexture;

                    org.bukkit.OfflinePlayer targetPlayer = Bukkit.getServer().getOfflinePlayer(visit.getOwnerUUID());

                    if (targetPlayer == null) {
                        OfflinePlayer offlinePlayer = new OfflinePlayer(visit.getOwnerUUID());
                        playerName = offlinePlayer.getName();
                        playerTexture = offlinePlayer.getTexture();
                    }
                    else {
                        playerName = targetPlayer.getName();
                        if (playerDataManager.hasPlayerData(targetPlayer.getUniqueId())) {
                            playerTexture = playerDataManager.getPlayerData(targetPlayer.getUniqueId()).getTexture();
                        } else {
                            playerTexture = new String[]{null, null};
                        }
                    }

                    if (leaderboard.getPosition() == 0) {
                        itemSlot = 13;
                    } else if (leaderboard.getPosition() == 1) {
                        itemSlot = 21;
                    } else if (leaderboard.getPosition() == 2) {
                        itemSlot = 22;
                    } else if (leaderboard.getPosition() == 3) {
                        itemSlot = 23;
                    } else if (leaderboard.getPosition() == 4) {
                        itemSlot = 29;
                    } else if (leaderboard.getPosition() == 5) {
                        itemSlot = 31;
                    } else if (leaderboard.getPosition() == 6) {
                        itemSlot = 33;
                    } else if (leaderboard.getPosition() == 7) {
                        itemSlot = 37;
                    } else if (leaderboard.getPosition() == 8) {
                        itemSlot = 40;
                    } else if (leaderboard.getPosition() == 9) {
                        itemSlot = 43;
                    }

                    List<String> itemLore = new ArrayList<>();

                    for (String itemLoreList : configLoad.getStringList(
                            "Menu.Leaderboard.Leaderboard.Item.Island." + viewer.getType().getFriendlyName() + ".Lore")) {
                        if (itemLoreList.contains("%signature")) {
                            if (visit.getSignature() == null || visit.getSignature().isEmpty()) {
                                itemLore.add(
                                        configLoad.getString("Menu.Leaderboard.Leaderboard.Item.Island.Word.Empty"));
                            } else {
                                itemLore.addAll(visit.getSignature());
                            }
                        } else {
                            itemLore.add(itemLoreList);
                        }
                    }


                    ItemStack phead;
                    if (playerTexture.length >= 1 && playerTexture[0] != null) {
                        phead = SkullItemCreator.byTextureValue(playerTexture[0]);
                    } else {
                        phead = SkullItemCreator.byUuid(visit.getOwnerUUID());
                    }

                    nInv.addItem(
                            nInv.createItem(phead,
                                    configLoad.getString("Menu.Leaderboard.Leaderboard.Item.Island.Displayname")
                                            .replace("%owner", playerName)
                                            .replace("%position", "" + (leaderboard.getPosition() + 1)),
                                    itemLore,
                                    new Placeholder[]{
                                            new Placeholder("%position", "" + (leaderboard.getPosition() + 1)),
                                            new Placeholder("%owner", playerName),
                                            new Placeholder("%level", "" + visit.getLevel().getLevel()),
                                            new Placeholder("%balance", NumberUtils.formatNumber(visit.getBankBalance())),
                                            new Placeholder("%votes", "" + visit.getVoters().size()),
                                            new Placeholder("%members", "" + visit.getMembers())},
                                    null, null),
                            itemSlot);
                }

                int[] itemSlots = new int[]{13, 21, 22, 23, 29, 31, 33, 37, 40, 43};


                for (int i = 0; i < itemSlots.length; i++) {
                    if (!nInv.getItems().containsKey(itemSlots[i])) {
                        ItemStack qhead = SkullItemCreator.byTextureHash("d34e063cafb467a5c8de43ec78619399f369f4a52434da8017a983cdd92516a0");
                        nInv.addItem(nInv.createItem(qhead,
                                        configLoad.getString("Menu.Leaderboard.Leaderboard.Item.Empty.Displayname")
                                                .replace("%position", "" + (i + 1)),
                                        configLoad.getStringList("Menu.Leaderboard.Leaderboard.Item.Empty.Lore"),
                                        new Placeholder[]{new Placeholder("%position", "" + (i + 1))}, null, null),
                                itemSlots[i]);
                    }
                }
                nInv.setTitle(plugin.formatText(configLoad.getString("Menu.Leaderboard.Leaderboard.Title").replace("%leaderboard",
                        viewer.getType().getFriendlyName())));
                nInv.setRows(6);

            }
            Bukkit.getServer().getScheduler().runTask(plugin, nInv::open);
        }
    }

    public static class Viewer {
        private final Type type;

        public Viewer(Type type) {
            this.type = type;
        }

        public Type getType() {
            return this.type;
        }

        public enum Type {
            BROWSE("Browse"),
            LEVEL("Level"),
            BANK("Bank"),
            VOTES("Votes");

            private final String friendlyName;

            Type(String friendlyName) {
                this.friendlyName = friendlyName;
            }

            public String getFriendlyName() {
                return this.friendlyName;
            }
        }
    }
}
