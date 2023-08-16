package com.craftaro.skyblock.menus;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.core.utils.ItemUtils;
import com.craftaro.core.utils.NumberUtils;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.placeholder.Placeholder;
import com.craftaro.skyblock.playerdata.PlayerDataManager;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.utils.item.nInventoryUtil;
import com.craftaro.skyblock.utils.player.OfflinePlayer;
import com.craftaro.skyblock.visit.Visit;
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

    private static final String[] steveSkinTexture = new String[]{
            "otpbxDm9B+opW7jEzZF8BVDeZSqaqdF0dyLlnlyMh7Q5ysJFDL48/9J/IOHp8JqNm1oarmVdvxrroy9dlNI2Mz4BVuJM2pcCOJwk2h+aZ4dzNZGxst+MYNPSw+i4sMoYu7OV07UVHrQffolFF7MiaBUst1hFwM07IpTE6UtIQz4rqWisXe9Iz5+ooqX4wj0IB3dPntsh6u5nVlL8acWCBDAW4YqcPt2Y4CKK+KtskjzusjqGAdEO+4lRcW1S0ldo2RNtUHEzZADWQcADjg9KKiKq9QIpIpYURIoIAA+pDGb5Q8L5O6CGI+i1+FxqXbgdBvcm1EG0OPdw9WpSqAxGGeXSwlzjILvlvBzYbd6gnHFBhFO+X7iwRJYNd+qQakjUa6ZwR8NbkpbN3ABb9+6YqVkabaEmgfky3HdORE+bTp/AT6LHqEMQo0xdNkvF9gtFci7RWhFwuTLDvQ1esby1IhlgT+X32CPuVHuxEvPCjN7+lmRz2OyOZ4REo2tAIFUKakqu3nZ0NcF98b87wAdA9B9Qyd2H/rEtUToQhpBjP732Sov6TlJkb8echGYiLL5bu/Q7hum72y4+j2GNnuRiOJtJidPgDqrYMg81GfenfPyS6Ynw6KhdEhnwmJ1FJlJhYvXZyqZwLAV1c26DNYkrTMcFcv3VXmcd5/2Zn9FnZtw=",
            "ewogICJ0aW1lc3RhbXAiIDogMTYyMTcxNTMxMjI5MCwKICAicHJvZmlsZUlkIiA6ICJiNTM5NTkyMjMwY2I0MmE0OWY5YTRlYmYxNmRlOTYwYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJtYXJpYW5hZmFnIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzFhNGFmNzE4NDU1ZDRhYWI1MjhlN2E2MWY4NmZhMjVlNmEzNjlkMTc2OGRjYjEzZjdkZjMxOWE3MTNlYjgxMGIiCiAgICB9CiAgfQp9"
    };
    private static final String[] alexSkinTexture = new String[]{
            "rZvLQoZsgLYaoKqEuASopYAs7IAlZlsGkwagoM8ZX38cP9kalseZrWY5OHZVfoiftdQJ+lGOzkiFfyx6kNJDTZniLrnRa8sd3X6D65ZihT1sOm/RInCwxpS1K0zGCM2h9ErkWswfwaviIf7hJtrwk8/zL0bfzDk2IgX/IBvIZpVoYTfmQsVY9jgSwORrS9ObePGIfFgmThMoZnCYWQMVpS2+yTFA2wnw9hmisQK9UWBU+iBZv55bMmkMcyEuXw1w14DaEu+/M0UGD91LU4GmJLPA9T4GCuIV8GxOcraSVIajki1cMlOBQwIaibB2NE6KAwq1Zh6NnsNYucy6qFM+136lXfBchQ1Nx4FDRZQgt8VRqTMy/OQFpr2nTbWWbRU4gRFpKC3R0518DqUH0Qm612kPWniKku/QzUUBSe1PSVljBaZCyyRx0OB1a1/8MexboKRnPXuTDnmPa9UPfuH4VO0q+qYkjV2KUzP6e5vIP5aQ6USPrMie7MmAHFJzwAMIbLjgkTVx91GWtYqg/t7qBlvrdBRLIPPsy/DSOqa+2+4hABouVCPZrBMCMLzstPPQoqZAyiCqcKb2HqWSU0h9Bhx19yoIcbHCeI3zsQs8PqIBjUL4mO6VQT4lzHy0e3M61Xsdd8S1GtsakSetTvEtMdUwCEDfBA5PRRTLOVYTY+g=",
            "ewogICJ0aW1lc3RhbXAiIDogMTYyMTcxNTQ5ODM0MywKICAicHJvZmlsZUlkIiA6ICIxYTc1ZTNiYmI1NTk0MTc2OTVjMmY4NTY1YzNlMDAzZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJUZXJvZmFyIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzNiNjBhMWY2ZDU2MmY1MmFhZWJiZjE0MzRmMWRlMTQ3OTMzYTNhZmZlMGU3NjRmYTQ5ZWEwNTc1MzY2MjNjZDMiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ=="
    };
    private static final String[] questionMarkSkinTexture = new String[]{
            "gi+wnQt/y4Z6E9rn65iDWmt8vUOM2WXY66XvtydqDJZTzwgFrjVcx2c5YwdzvtOIRtiX2nZt4n2uWesUFKb59xS24YWbxCDXnalHhCpPFcIP58SQbCm9AYp3UPzkcRNWzuV4BddrS608QQZGyIFOUaLPOPasGITZu51VLcOKcTyFOCKu1QE2yRo1orTH8bWfdpE769BB/VYGdny0qJtm1amc12wGiVifMJRutZmYo2ZdA0APhIJVaNsPppNESVcbeBCvk60l4QK43C/p98/QEe5U6UJ6Z6N01pBQcswubMu8lCuPLasep+vX3v2K+Ui9jnTQNreGNIZPWVjf6V1GH4xMbbUVQJsoPdcaXG855VdzyoW+kyHdWYEojSn0qAY/moH6JCLnx6PLCv9mITSvOIUHq8ITet0M7Z9KALY5s6eg6VdA8TvClRy2TTm9tIRt//TJo5JxBoTYujawGNSR7ryODj2UEDQ2xOyWSagxAXZpispdrO5jHxRmBZUwX9vxnAp+CNWxifpu9sINJTlqYsT/KlGOJQC483gv5B6Nm5VBB1DRFmQkohzO6Wc2eDixgEbaU795GlLxrNaFfNjVH6Bwr1e7df2H3nE0P0bexs4wYdWplijn4gPyHwjT2LDBPGFQK3Vo2SlaXfPYbkIHX21c9qaz3eWHpLEXUBQfnWc=",
            "eyJ0aW1lc3RhbXAiOjE1MzE3MTcxNjY3MDAsInByb2ZpbGVJZCI6IjYwNmUyZmYwZWQ3NzQ4NDI5ZDZjZTFkMzMyMWM3ODM4IiwicHJvZmlsZU5hbWUiOiJNSEZfUXVlc3Rpb24iLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2QzNGUwNjNjYWZiNDY3YTVjOGRlNDNlYzc4NjE5Mzk5ZjM2OWY0YTUyNDM0ZGE4MDE3YTk4M2NkZDkyNTE2YTAifX19"
    };

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
                                            com.craftaro.skyblock.leaderboard.Leaderboard.Type.valueOf(viewer.getType().getFriendlyName()));

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
                                com.craftaro.skyblock.leaderboard.Leaderboard.Type.valueOf(viewer.getType().getFriendlyName()));

                for (com.craftaro.skyblock.leaderboard.Leaderboard leaderboard : leaderboardIslands) {
                    Visit visit = leaderboard.getVisit();

                    int itemSlot = 0;

                    String playerName;
                    String[] playerTexture;

                    Player targetPlayer = Bukkit.getServer().getPlayer(visit.getOwnerUUID());

                    if (targetPlayer == null) {
                        OfflinePlayer offlinePlayer = new OfflinePlayer(visit.getOwnerUUID());
                        playerName = offlinePlayer.getName();
                        playerTexture = offlinePlayer.getTexture();
                    } else {
                        playerName = targetPlayer.getName();

                        if (playerDataManager.hasPlayerData(targetPlayer)) {
                            playerTexture = playerDataManager.getPlayerData(targetPlayer).getTexture();
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

                    if (playerTexture[0] == null || playerTexture[1] == null) {
                        if ((visit.getOwnerUUID().hashCode() & 1) != 0) {
                            playerTexture = alexSkinTexture;
                        } else {
                            playerTexture = steveSkinTexture;
                        }
                    }

                    nInv.addItem(
                            nInv.createItem(ItemUtils.getCustomHead(playerTexture[0], playerTexture[1]),
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
                        nInv.addItem(nInv.createItem(ItemUtils.getCustomHead(questionMarkSkinTexture[0], questionMarkSkinTexture[1]),
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
