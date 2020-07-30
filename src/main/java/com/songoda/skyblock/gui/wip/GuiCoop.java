package com.songoda.skyblock.gui.wip;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.utils.TextUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandCoop;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.PermissionManager;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.item.SkullUtil;
import com.songoda.skyblock.utils.player.OfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.Map;
import java.util.UUID;

public class GuiCoop extends Gui {
    private final SkyBlock plugin;
    private final Island island;
    private final FileConfiguration languageLoad;
    
    public GuiCoop(SkyBlock plugin, Island island, Gui returnGui) {
        super(returnGui);
        this.plugin = plugin;
        this.island = island;
        this.languageLoad = plugin.getFileManager()
                .getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration();
        setDefaultItem(null);
        setTitle(TextUtils.formatText("Coop"));
        paint();
    }
    
    public void paint() { // TODO Item to add ban
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        MessageManager messageManager = plugin.getMessageManager();
        IslandManager islandManager = plugin.getIslandManager();
        PermissionManager permissionManager = plugin.getPermissionManager();
        SoundManager soundManager = plugin.getSoundManager();
        
        if (inventory != null)
            inventory.clear();
        setActionForRange(0, 0, 1, 8, null);
        
        setButton(0, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(languageLoad.getString("Menu.Coop.Item.Exit.Displayname"))), (event) -> {
            soundManager.playSound(event.player, CompatibleSound.BLOCK_CHEST_CLOSE.getSound(), 1f, 1f);
            event.player.closeInventory();
        });
        
        setButton(8, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(languageLoad.getString("Menu.Coop.Item.Exit.Displayname"))), (event) -> {
            soundManager.playSound(event.player, CompatibleSound.BLOCK_CHEST_CLOSE.getSound(), 1f, 1f);
            event.player.closeInventory();
        });
        
        for(int i=9; i<18; i++){
            setItem(i, CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem());
        }
    
        Map<UUID, IslandCoop> coopPlayers = island.getCoopPlayers();
        
        if(coopPlayers.size() == 0){
            ItemStack empty = CompatibleMaterial.BARRIER.getItem();
            ItemMeta emptyMeta = empty.getItemMeta();
            emptyMeta.setDisplayName(languageLoad.getString("Menu.Coop.Item.Nothing.Displayname"));
            empty.setItemMeta(emptyMeta);
            setButton(31, empty, (event) ->
                    soundManager.playSound(event.player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F));
        } else {
            this.pages = (int) Math.max(1, Math.ceil((double) coopPlayers.size() / 36d));
            
            if (page != 1)
                setButton(5, 2, GuiUtils.createButtonItem(CompatibleMaterial.ARROW,
                        TextUtils.formatText(languageLoad.getString("Menu.Bank.Item.Last.Displayname"))),
                        (event) -> {
                            page--;
                            paint();
                        });
            
            if (page != pages)
                setButton(5, 6, GuiUtils.createButtonItem(CompatibleMaterial.ARROW,
                        TextUtils.formatText(languageLoad.getString("Menu.Bank.Item.Next.Displayname"))),
                        (event) -> {
                            page++;
                            paint();
                        });
            
            UUID[] coopUUIDs = (UUID[]) coopPlayers.keySet().toArray();
            
            for (int i = 9; i < ((getRows()-2)*9)+18; i++) {
                int current = ((page - 1) * 36) - 18;
                if (current + i >= coopPlayers.size()) {
                    setItem(i, null);
                    continue;
                }
                UUID uuid = coopUUIDs[current + i];
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
                    Bukkit.getServer().dispatchCommand(e.player, "island coop " + playerName); // TODO Command or APIs?
                });
            }
        }
    }
}
