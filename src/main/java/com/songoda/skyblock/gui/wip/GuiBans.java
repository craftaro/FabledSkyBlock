package com.songoda.skyblock.gui.wip;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.utils.ItemUtils;
import com.songoda.core.utils.TextUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.player.OfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

        setButton(0, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(this.languageLoad.getString("Menu.Bans.Item.Exit.Displayname"))), (event) -> {
            this.soundManager.playSound(event.player, CompatibleSound.BLOCK_CHEST_CLOSE.getSound(), 1f, 1f);
            event.player.closeInventory();
        });

        setButton(8, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(this.languageLoad.getString("Menu.Bans.Item.Exit.Displayname"))), (event) -> {
            this.soundManager.playSound(event.player, CompatibleSound.BLOCK_CHEST_CLOSE.getSound(), 1f, 1f);
            event.player.closeInventory();
        });

        for (int i = 9; i < 18; i++) {
            setItem(i, CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem());
        }

        List<UUID> bans = new ArrayList<>(this.island.getBan().getBans());

        if (bans.isEmpty()) {
            setItem(31, CompatibleMaterial.BARRIER.getItem());
        } else {
            this.pages = (int) Math.max(1, Math.ceil((double) bans.size() / 36d));

            if (this.page != 1) {
                setButton(5, 2, GuiUtils.createButtonItem(CompatibleMaterial.ARROW,
                                TextUtils.formatText(this.languageLoad.getString("Menu.Bank.Item.Last.Displayname"))),
                        (event) -> {
                            this.page--;
                            paint();
                        });
            }

            if (this.page != this.pages) {
                setButton(5, 6, GuiUtils.createButtonItem(CompatibleMaterial.ARROW,
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

                Player targetPlayer = Bukkit.getServer().getPlayer(uuid);

                if (targetPlayer == null) {
                    OfflinePlayer offlinePlayer = new OfflinePlayer(uuid);
                    targetPlayerName = offlinePlayer.getName();
                    targetPlayerTexture = offlinePlayer.getTexture();
                } else {
                    targetPlayerName = targetPlayer.getName();

                    if (this.playerDataManager.hasPlayerData(targetPlayer)) {
                        targetPlayerTexture = this.playerDataManager.getPlayerData(targetPlayer).getTexture();
                    } else {
                        targetPlayerTexture = new String[]{null, null};
                    }
                }

                ItemStack is = ItemUtils.getCustomHead(targetPlayerTexture[0], targetPlayerTexture[1]);
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
