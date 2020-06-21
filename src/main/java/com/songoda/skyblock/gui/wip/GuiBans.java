package com.songoda.skyblock.gui.wip;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.utils.TextUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.ban.BanManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.utils.item.SkullUtil;
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
    private final SkyBlock plugin;
    private final BanManager banManager;
    private final PlayerDataManager playerDataManager;
    private final Island island;
    private final FileConfiguration languageLoad;

    public GuiBans(SkyBlock plugin, Island island, Gui returnGui) {
        super(returnGui);
        this.plugin = plugin;
        this.playerDataManager = plugin.getPlayerDataManager();
        ;
        this.banManager = plugin.getBanManager();
        this.island = island;
        this.languageLoad = plugin.getFileManager()
                .getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration();
        setDefaultItem(null);
        setTitle(TextUtils.formatText("Bans"));
        paint();
    }

    public void paint() {
        if (inventory != null)
            inventory.clear();
        setActionForRange(0, 0, 1, 8, null);

        setButton(0, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(languageLoad.getString("Menu.Bans.Item.Exit.Displayname"))), (event) -> {
            CompatibleSound.BLOCK_CHEST_CLOSE.play(event.player);
            event.player.closeInventory();
        });

        setButton(8, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(languageLoad.getString("Menu.Bans.Item.Exit.Displayname"))), (event) -> {
            CompatibleSound.BLOCK_CHEST_CLOSE.play(event.player);
            event.player.closeInventory();
        });

        for(int i=9; i<18; i++){
            setItem(i, CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem());
        }

        List<UUID> bans = new ArrayList<>(island.getBan().getBans());

        if(bans.size() == 0){
            setItem(31, CompatibleMaterial.BARRIER.getItem());
        } else {
            for (int i = 9; i < ((getRows()-2)*9)+18; i++) { // TODO check dynamic dimension!
                int current = ((page - 1) * 36) - 18;
                if (current + i >= bans.size()) {
                    setItem(i, null);
                    continue;
                }
                UUID uuid = bans.get(current + i);
                if (uuid == null) continue;

                String targetPlayerName;
                String[] targetPlayerTexture;

                Player targetPlayer = Bukkit.getServer().getPlayer(uuid);

                if (targetPlayer == null) {
                    OfflinePlayer offlinePlayer = new OfflinePlayer(uuid);
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

                ItemStack is = SkullUtil.create(targetPlayerTexture[0], targetPlayerTexture[1]);
                ItemMeta im = is.getItemMeta();
                if(im != null){
                    im.setDisplayName(languageLoad.getString("Menu.Bans.Item.Ban.Displayname")
                            .replace("%player", targetPlayerName == null ? "" : targetPlayerName));
                    im.setLore(languageLoad.getStringList("Menu.Bans.Item.Ban.Lore"));
                    is.setItemMeta(im);
                }

                setButton(i, is, e -> {
                    String playerName = ChatColor.stripColor(is.getItemMeta().getDisplayName()); // TODO Check if it actually works
                    Bukkit.getServer().dispatchCommand(e.player, "island unban " + playerName);
                });
            }
        }
    }
}
