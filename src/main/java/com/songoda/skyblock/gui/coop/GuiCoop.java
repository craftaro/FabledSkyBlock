package com.songoda.skyblock.gui.coop;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.gui.AnvilGui;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.utils.TextUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandCoop;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.PermissionManager;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.item.SkullUtil;
import com.songoda.skyblock.utils.player.OfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GuiCoop extends Gui {
    private final SkyBlock plugin;
    private final Island island;
    private final FileConfiguration languageLoad;
    
    public GuiCoop(SkyBlock plugin, Island island, Gui returnGui) {
        super(6, returnGui);
        this.plugin = plugin;
        this.island = island;
        this.languageLoad = plugin.getFileManager()
                .getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration();
        setDefaultItem(null);
        setTitle(TextUtils.formatText(languageLoad.getString("Menu.Coop.Title")));
        paint();
    }
    
    public void paint() {
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        SoundManager soundManager = plugin.getSoundManager();
        PermissionManager permissionManager = plugin.getPermissionManager();
        MessageManager messageManager = plugin.getMessageManager();
    
        Map<UUID, IslandCoop> coopPlayers = island.getCoopPlayers();
        
        if (inventory != null) {
            inventory.clear();
        }
        setActionForRange(0, 0, 1, 8, null);
        
        setButton(0, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(languageLoad.getString("Menu.Coop.Item.Exit.Displayname"))), (event) -> {
            soundManager.playSound(event.player, CompatibleSound.BLOCK_CHEST_CLOSE.getSound(), 1f, 1f);
            event.player.closeInventory();
        });
    
        List<String> addButtonLore = languageLoad.getStringList("Menu.Coop.Item.Information.Lore");
        Collections.replaceAll(addButtonLore, "%coops", "" + coopPlayers.size());
        
        setButton(4, GuiUtils.createButtonItem(CompatibleMaterial.PAINTING, // Add new
                TextUtils.formatText(languageLoad.getString("Menu.Coop.Item.Information.Displayname")),
                TextUtils.formatText(addButtonLore)),
                (event) -> {
                    if ((island.hasRole(IslandRole.Operator, event.player.getUniqueId())
                            && permissionManager.hasPermission(island, "CoopPlayers", IslandRole.Operator))
                            || island.hasRole(IslandRole.Owner, event.player.getUniqueId())) {
    
                        AnvilGui gui = new AnvilGui(event.player, this);
                        gui.setAction((e -> {
                            String playerName = gui.getInputText().trim();
                            guiManager.showGUI(event.player, new GuiCoopChoose(plugin, island, e.gui.getParent(), playerName));
                        }));
                        gui.setTitle(TextUtils.formatText(
                                languageLoad.getString("Menu.Coop.Item.Word.Enter")));
                        guiManager.showGUI(event.player, gui);
                    } else {
                        messageManager.sendMessage(event.player,
                                languageLoad.getString("Command.Island.Coop.Permission.Message"));
                        soundManager.playSound(event.player,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
                    }
        });
        
        setButton(8, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(languageLoad.getString("Menu.Coop.Item.Exit.Displayname"))), (event) -> {
            soundManager.playSound(event.player, CompatibleSound.BLOCK_CHEST_CLOSE.getSound(), 1f, 1f);
            event.player.closeInventory();
        });
        
        for(int i=9; i<18; i++){
            setItem(i, CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem());
        }
        
        if(coopPlayers.size() == 0){
            ItemStack empty = CompatibleMaterial.BARRIER.getItem();
            ItemMeta emptyMeta = empty.getItemMeta();
            emptyMeta.setDisplayName(TextUtils.formatText(languageLoad.getString("Menu.Coop.Item.Nothing.Displayname")));
            empty.setItemMeta(emptyMeta);
            setButton(31, empty, (event) ->
                    soundManager.playSound(event.player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F));
        } else {
            this.pages = (int) Math.max(1, Math.ceil((double) coopPlayers.size() / 36d));
            
            if (page != 1) {
                setButton(5, 2, GuiUtils.createButtonItem(CompatibleMaterial.ARROW,
                        TextUtils.formatText(languageLoad.getString("Menu.Coop.Item.Previous.Displayname"))),
                        (event) -> {
                            page--;
                            paint();
                        });
            }
            
            if (page != pages) {
                setButton(5, 6, GuiUtils.createButtonItem(CompatibleMaterial.ARROW,
                        TextUtils.formatText(languageLoad.getString("Menu.Coop.Item.Next.Displayname"))),
                        (event) -> {
                            page++;
                            paint();
                        });
            }
            
            UUID[] coopUUIDs = new UUID[coopPlayers.size()];
            coopPlayers.keySet().toArray(coopUUIDs);
            
            for (int i = 18; i < (((getRows()-2)*9)+18); i++) {
                int current = ((page-1) * 36) - 18;
                if (current + i < coopPlayers.size()) {
                    UUID uuid = coopUUIDs[current + i];
                    IslandCoop type = (IslandCoop) coopPlayers.values().toArray()[current + i];
                    if (uuid != null) {
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
                        if (im != null) {
                            im.setDisplayName(TextUtils.formatText(languageLoad.getString("Menu.Coop.Item.Coop.Displayname")
                                    .replace("%player", targetPlayerName == null ? "" : targetPlayerName)
                                    .replace("%type", type == IslandCoop.TEMP ?
                                            languageLoad.getString("Menu.Coop.Item.Word.Temp") :
                                            languageLoad.getString("Menu.Coop.Item.Word.Normal"))));
                            im.setLore(TextUtils.formatText(languageLoad.getStringList("Menu.Coop.Item.Coop.Lore")));
                            is.setItemMeta(im);
                        }
    
                        setButton(i, is, e -> {
                            Bukkit.getServer().dispatchCommand(e.player, "island coop " + targetPlayerName);
                            paint();
                        });
                    }
                } else {
                    setItem(i, null);
                }
            }
        }
    }
}
