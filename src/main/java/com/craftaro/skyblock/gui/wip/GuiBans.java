package com.craftaro.skyblock.gui.wip;

import com.craftaro.core.gui.Gui;
import com.craftaro.core.gui.GuiUtils;
import com.craftaro.core.utils.SkullItemCreator;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.core.utils.TextUtils;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.playerdata.PlayerDataManager;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.utils.player.OfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class GuiBans extends Gui {
    private final PlayerDataManager playerDataManager;
    private final SoundManager soundManager;
    private final Island island;
    private final FileConfiguration languageLoad;

    public GuiBans(SkyBlock plugin, Island island, Gui returnGui) {
        super(returnGui);
        this.playerDataManager = plugin.getPlayerDataManager();
        this.soundManager = plugin.getSoundManager();
        this.island = island;
        this.languageLoad = plugin.getFileManager()
                .getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration();
        setDefaultItem(null);
        setTitle(TextUtils.formatText("Bans"));
        paint();
    }

    public void paint() { // TODO Item to add ban
        if (this.inventory != null) {
            this.inventory.clear();
        }
        setActionForRange(0, 0, 1, 8, null);

        setButton(0, GuiUtils.createButtonItem(XMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(this.languageLoad.getString("Menu.Bans.Item.Exit.Displayname"))), (event) -> {
            this.soundManager.playSound(event.player, XSound.BLOCK_CHEST_CLOSE);
            event.player.closeInventory();
        });

        setButton(8, GuiUtils.createButtonItem(XMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(this.languageLoad.getString("Menu.Bans.Item.Exit.Displayname"))), (event) -> {
            this.soundManager.playSound(event.player, XSound.BLOCK_CHEST_CLOSE);
            event.player.closeInventory();
        });

        for (int i = 9; i < 18; i++) {
            setItem(i, XMaterial.BLACK_STAINED_GLASS_PANE.parseItem());
        }

        List<UUID> bans = new ArrayList<>(this.island.getBan().getBans());

        if (bans.isEmpty()) {
            setItem(31, XMaterial.BARRIER.parseItem());
        } else {
            this.pages = (int) Math.max(1, Math.ceil((double) bans.size() / 36d));

            if (this.page != 1) {
                setButton(5, 2, GuiUtils.createButtonItem(XMaterial.ARROW,
                                TextUtils.formatText(this.languageLoad.getString("Menu.Bank.Item.Last.Displayname"))),
                        (event) -> {
                            this.page--;
                            paint();
                        });
            }

            if (this.page != this.pages) {
                setButton(5, 6, GuiUtils.createButtonItem(XMaterial.ARROW,
                                TextUtils.formatText(this.languageLoad.getString("Menu.Bank.Item.Next.Displayname"))),
                        (event) -> {
                            this.page++;
                            paint();
                        });
            }

            for (int i = 9; i < ((getRows() - 2) * 9) + 18; i++) {
                int current = ((this.page - 1) * 36) - 18;
                if (current + i >= bans.size()) {
                    setItem(i, null);
                    continue;
                }
                UUID uuid = bans.get(current + i);
                if (uuid == null) {
                    continue;
                }

                String targetPlayerName;
                String[] targetPlayerTexture;

                org.bukkit.OfflinePlayer targetPlayer = Bukkit.getServer().getOfflinePlayer(uuid);

                if (targetPlayer == null) {
                    OfflinePlayer offlinePlayer = new OfflinePlayer(uuid);
                    targetPlayerName = offlinePlayer.getName();
                    targetPlayerTexture = offlinePlayer.getTexture();
                } else {
                    targetPlayerName = targetPlayer.getName();

                    if (this.playerDataManager.hasPlayerData(targetPlayer.getUniqueId())) {
                        targetPlayerTexture = this.playerDataManager.getPlayerData(targetPlayer.getUniqueId()).getTexture();
                    } else {
                        targetPlayerTexture = new String[]{null, null};
                    }
                }

                ItemStack is;
                if (targetPlayerTexture.length >= 1 && targetPlayerTexture[0] != null) {
                    is = SkullItemCreator.byTextureValue(targetPlayerTexture[0]);
                } else {
                    try {
                        is = SkullItemCreator.byUuid(uuid).get();
                    } catch (InterruptedException | ExecutionException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                ItemMeta im = is.getItemMeta();
                if (im != null) {
                    im.setDisplayName(this.languageLoad.getString("Menu.Bans.Item.Ban.Displayname")
                            .replace("%player", targetPlayerName == null ? "" : targetPlayerName));
                    im.setLore(this.languageLoad.getStringList("Menu.Bans.Item.Ban.Lore"));
                    is.setItemMeta(im);
                }

                setButton(i, is, e -> {
                    String playerName = ChatColor.stripColor(is.getItemMeta().getDisplayName()); // TODO Check if it actually works
                    Bukkit.getServer().dispatchCommand(e.player, "island unban " + playerName); // TODO Command or APIs?
                });
            }
        }
    }
}
