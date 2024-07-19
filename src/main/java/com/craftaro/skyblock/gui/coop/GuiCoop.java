package com.craftaro.skyblock.gui.coop;

import com.craftaro.core.gui.AnvilGui;
import com.craftaro.core.gui.Gui;
import com.craftaro.core.gui.GuiUtils;
import com.craftaro.core.utils.SkullItemCreator;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.core.utils.TextUtils;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandCoop;
import com.craftaro.skyblock.island.IslandRole;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.permission.PermissionManager;
import com.craftaro.skyblock.playerdata.PlayerDataManager;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.utils.player.OfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
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
        setTitle(TextUtils.formatText(this.languageLoad.getString("Menu.Coop.Title")));
        paint();
    }

    public void paint() {
        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        PermissionManager permissionManager = this.plugin.getPermissionManager();
        MessageManager messageManager = this.plugin.getMessageManager();

        Map<UUID, IslandCoop> coopPlayers = this.island.getCoopPlayers();

        if (this.inventory != null) {
            this.inventory.clear();
        }
        setActionForRange(0, 0, 1, 8, null);

        setButton(0, GuiUtils.createButtonItem(XMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(this.languageLoad.getString("Menu.Coop.Item.Exit.Displayname"))), (event) -> {
            soundManager.playSound(event.player, XSound.BLOCK_CHEST_CLOSE);
            event.player.closeInventory();
        });

        List<String> addButtonLore = this.languageLoad.getStringList("Menu.Coop.Item.Information.Lore");
        Collections.replaceAll(addButtonLore, "%coops", "" + coopPlayers.size());

        setButton(4, GuiUtils.createButtonItem(XMaterial.PAINTING, // Add new
                        TextUtils.formatText(this.languageLoad.getString("Menu.Coop.Item.Information.Displayname")),
                        TextUtils.formatText(addButtonLore)),
                (event) -> {
                    if ((this.island.hasRole(IslandRole.OPERATOR, event.player.getUniqueId())
                            && permissionManager.hasPermission(this.island, "CoopPlayers", IslandRole.OPERATOR))
                            || this.island.hasRole(IslandRole.OWNER, event.player.getUniqueId())) {

                        AnvilGui gui = new AnvilGui(event.player, this);
                        gui.setAction((e -> {
                            String playerName = gui.getInputText().trim();
                            this.guiManager.showGUI(event.player, new GuiCoopChoose(this.plugin, this.island, e.gui.getParent(), playerName));
                        }));
                        gui.setTitle(TextUtils.formatText(
                                this.languageLoad.getString("Menu.Coop.Item.Word.Enter")));
                        this.guiManager.showGUI(event.player, gui);
                    } else {
                        messageManager.sendMessage(event.player,
                                this.languageLoad.getString("Command.Island.Coop.Permission.Message"));
                        soundManager.playSound(event.player, XSound.ENTITY_VILLAGER_NO);
                    }
                });

        setButton(8, GuiUtils.createButtonItem(XMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(this.languageLoad.getString("Menu.Coop.Item.Exit.Displayname"))), (event) -> {
            soundManager.playSound(event.player, XSound.BLOCK_CHEST_CLOSE);
            event.player.closeInventory();
        });

        for (int i = 9; i < 18; ++i) {
            setItem(i, XMaterial.BLACK_STAINED_GLASS_PANE.parseItem());
        }

        if (coopPlayers.isEmpty()) {
            ItemStack empty = XMaterial.BARRIER.parseItem();
            ItemMeta emptyMeta = empty.getItemMeta();
            emptyMeta.setDisplayName(TextUtils.formatText(this.languageLoad.getString("Menu.Coop.Item.Nothing.Displayname")));
            empty.setItemMeta(emptyMeta);
            setButton(31, empty, (event) ->
                    soundManager.playSound(event.player, XSound.BLOCK_ANVIL_LAND));
        } else {
            this.pages = (int) Math.max(1, Math.ceil((double) coopPlayers.size() / 36d));

            if (this.page != 1) {
                setButton(5, 2, GuiUtils.createButtonItem(XMaterial.ARROW,
                                TextUtils.formatText(this.languageLoad.getString("Menu.Coop.Item.Previous.Displayname"))),
                        (event) -> {
                            this.page--;
                            paint();
                        });
            }

            if (this.page != this.pages) {
                setButton(5, 6, GuiUtils.createButtonItem(XMaterial.ARROW,
                                TextUtils.formatText(this.languageLoad.getString("Menu.Coop.Item.Next.Displayname"))),
                        (event) -> {
                            this.page++;
                            paint();
                        });
            }

            UUID[] coopUUIDs = new UUID[coopPlayers.size()];
            coopPlayers.keySet().toArray(coopUUIDs);

            for (int i = 18; i < (((getRows() - 2) * 9) + 18); i++) {
                int current = ((this.page - 1) * 36) - 18;
                if (current + i < coopPlayers.size()) {
                    UUID uuid = coopUUIDs[current + i];
                    IslandCoop type = (IslandCoop) coopPlayers.values().toArray()[current + i];
                    if (uuid != null) {
                        String targetPlayerName;
                        String[] targetPlayerTexture;

                        org.bukkit.OfflinePlayer targetPlayer = Bukkit.getServer().getOfflinePlayer(uuid);

                        if (targetPlayer == null) {
                            OfflinePlayer offlinePlayer = new OfflinePlayer(uuid);
                            targetPlayerName = offlinePlayer.getName();
                            targetPlayerTexture = offlinePlayer.getTexture();
                        } else {
                            targetPlayerName = targetPlayer.getName();

                            if (playerDataManager.hasPlayerData(targetPlayer.getUniqueId())) {
                                targetPlayerTexture = playerDataManager.getPlayerData(targetPlayer.getUniqueId()).getTexture();
                            } else {
                                targetPlayerTexture = new String[]{null, null};
                            }
                        }

                        ItemStack phead;
                        if (targetPlayerTexture.length >= 1 && targetPlayerTexture[0] != null) {
                            phead = SkullItemCreator.byTextureValue(targetPlayerTexture[0]);
                        } else {
                            phead = SkullItemCreator.byUuid(uuid);
                        }

                        ItemMeta pheadmeta = phead.getItemMeta();
                        if (pheadmeta != null) {
                            pheadmeta.setDisplayName(TextUtils.formatText(this.languageLoad.getString("Menu.Coop.Item.Coop.Displayname")
                                    .replace("%player", targetPlayerName == null ? "" : targetPlayerName)
                                    .replace("%type", type == IslandCoop.TEMP ?
                                            this.languageLoad.getString("Menu.Coop.Item.Word.Temp") :
                                            this.languageLoad.getString("Menu.Coop.Item.Word.Normal"))));
                            pheadmeta.setLore(TextUtils.formatText(this.languageLoad.getStringList("Menu.Coop.Item.Coop.Lore")));
                            phead.setItemMeta(pheadmeta);
                        }

                        setButton(i, phead, e -> {
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
