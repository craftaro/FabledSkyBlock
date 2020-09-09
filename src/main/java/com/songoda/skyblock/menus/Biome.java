package com.songoda.skyblock.menus;

import com.songoda.core.compatibility.CompatibleBiome;
import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.biome.BiomeManager;
import com.songoda.skyblock.cooldown.Cooldown;
import com.songoda.skyblock.cooldown.CooldownManager;
import com.songoda.skyblock.cooldown.CooldownPlayer;
import com.songoda.skyblock.cooldown.CooldownType;
import com.songoda.skyblock.island.*;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.PermissionManager;
import com.songoda.skyblock.placeholder.Placeholder;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.NumberUtil;
import com.songoda.skyblock.utils.item.nInventoryUtil;
import com.songoda.skyblock.utils.version.SBiome;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class Biome {

    private static Biome instance;

    public static Biome getInstance() {
        if (instance == null) {
            instance = new Biome();
        }

        return instance;
    }

    public void open(Player player) {
        SkyBlock plugin = SkyBlock.getInstance();

        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        CooldownManager cooldownManager = plugin.getCooldownManager();
        MessageManager messageManager = plugin.getMessageManager();
        IslandManager islandManager = plugin.getIslandManager();
        PermissionManager permissionManager = plugin.getPermissionManager();
        BiomeManager biomeManager = plugin.getBiomeManager();
        SoundManager soundManager = plugin.getSoundManager();

        if (playerDataManager.hasPlayerData(player)) {
            FileConfiguration langConfig = plugin.getLanguage();

            nInventoryUtil nInv = new nInventoryUtil(player, event -> {
                Island island = islandManager.getIsland(player);

                if (island == null) {
                    messageManager.sendMessage(player,
                            langConfig.getString("Command.Island.Biome.Owner.Message"));
                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                    player.closeInventory();

                    return;
                } else if (!((island.hasRole(IslandRole.Operator, player.getUniqueId())
                        && permissionManager.hasPermission(island, "Biome", IslandRole.Operator))
                        || island.hasRole(IslandRole.Owner, player.getUniqueId()))) {
                    messageManager.sendMessage(player,
                            langConfig.getString("Command.Island.Biome.Permission.Message"));
                    soundManager.playSound(player,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
                    player.closeInventory();

                    return;
                }

                ItemStack is = event.getItem();

                if ((is.getType() == Material.NAME_TAG) && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        langConfig.getString("Menu.Biome.Item.Info.Displayname"))))) {
                    soundManager.playSound(player, CompatibleSound.ENTITY_CHICKEN_EGG.getSound(), 1.0F, 1.0F);

                    event.setWillClose(false);
                    event.setWillDestroy(false);
                } else if ((is.getType() == CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getMaterial())
                        && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        langConfig.getString("Menu.Biome.Item.Barrier.Displayname"))))) {
                    soundManager.playSound(player, CompatibleSound.BLOCK_GLASS_BREAK.getSound(), 1.0F, 1.0F);

                    event.setWillClose(false);
                    event.setWillDestroy(false);
                } else if ((is.getType() == CompatibleMaterial.OAK_FENCE_GATE.getMaterial()) && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        langConfig.getString("Menu.Biome.Item.Exit.Displayname"))))) {
                    soundManager.playSound(player, CompatibleSound.BLOCK_CHEST_CLOSE.getSound(), 1.0F, 1.0F);
                } else {
                    if (is.getItemMeta().hasEnchant(Enchantment.THORNS)) {
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                        event.setWillClose(false);
                        event.setWillDestroy(false);
                    } else {
                        if (cooldownManager.hasPlayer(CooldownType.Biome, player) && !player.hasPermission("fabledskyblock.bypass.cooldown")) {
                            CooldownPlayer cooldownPlayer = cooldownManager.getCooldownPlayer(CooldownType.Biome, player);
                            Cooldown cooldown = cooldownPlayer.getCooldown();

                            if (cooldown.getTime() < 60) {
                                messageManager.sendMessage(player,
                                        langConfig.getString("Island.Biome.Cooldown.Message")
                                                .replace("%time",
                                                        cooldown.getTime() + " " + langConfig
                                                                .getString("Island.Biome.Cooldown.Word.Second")));
                            } else {
                                long[] durationTime = NumberUtil.getDuration(cooldown.getTime());
                                messageManager.sendMessage(player,
                                        langConfig.getString("Island.Biome.Cooldown.Message")
                                                .replace("%time", durationTime[2] + " "
                                                        + langConfig.getString("Island.Biome.Cooldown.Word.Minute")
                                                        + " " + durationTime[3] + " "
                                                        + langConfig.getString("Island.Biome.Cooldown.Word.Second")));
                            }

                            soundManager.playSound(player,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                            event.setWillClose(false);
                            event.setWillDestroy(false);

                            return;
                        }

                        @SuppressWarnings("deprecation")
                        SBiome selectedBiomeType = SBiome.getFromGuiIcon(is.getType(), is.getData().getData());

                        cooldownManager.createPlayer(CooldownType.Biome, player);
                        biomeManager.setBiome(island,IslandWorld.Normal, CompatibleBiome.getBiome(selectedBiomeType.getBiome()), null);
                        island.setBiome(selectedBiomeType.getBiome());
                        island.save();

                        soundManager.playSound(island.getLocation(IslandWorld.Normal, IslandEnvironment.Island),
                                CompatibleSound.ENTITY_GENERIC_SPLASH.getSound(), 1.0F, 1.0F);

                        if (!islandManager.isPlayerAtIsland(island, player, IslandWorld.Normal)) {
                            soundManager.playSound(player, CompatibleSound.ENTITY_GENERIC_SPLASH.getSound(), 1.0F, 1.0F);
                        }

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                    }
                }
            });

            Island island = islandManager.getIsland(player);
            org.bukkit.block.Biome islandBiome = island.getBiome();
            String islandBiomeName = island.getBiomeName();

            nInv.addItem(nInv.createItem(new ItemStack(Material.NAME_TAG),
                    ChatColor.translateAlternateColorCodes('&',
                            langConfig.getString("Menu.Biome.Item.Info.Displayname")),
                    langConfig.getStringList("Menu.Biome.Item.Info.Lore"),
                    new Placeholder[]{new Placeholder("%biome_type", islandBiomeName)}, null, null), 4);

            nInv.addItem(nInv.createItem(CompatibleMaterial.OAK_FENCE_GATE.getItem(),
                    langConfig.getString("Menu.Biome.Item.Exit.Displayname"), null, null, null, null),
                    0, 8);

            nInv.addItem(nInv.createItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem(),
                    plugin.formatText(langConfig.getString("Menu.Biome.Item.Barrier.Displayname")),
                    null, null, null, null),
                    9, 10, 11, 12, 13, 14, 15, 16, 17);

            FileConfiguration settings = plugin.getConfiguration();

            boolean allowNetherBiome = settings.getBoolean("Island.Biome.AllowOtherWorldlyBiomes.Nether");
            boolean allowEndBiome = settings.getBoolean("Island.Biome.AllowOtherWorldlyBiomes.End");

            int slotIndex = 18;
            for (SBiome biome : SBiome.values()) {
                if (!biome.isAvailable())
                    continue;

                if (!allowNetherBiome && biome.equals(SBiome.NETHER))
                    continue;

                if (!allowEndBiome && (biome.equals(SBiome.THE_END) || biome.equals(SBiome.THE_VOID)))
                    continue;

                if (!player.hasPermission("fabledskyblock.biome.*") && !player.hasPermission("fabledskyblock.biome." + biome.name().toLowerCase()))
                    continue;

                if (islandBiome.equals(biome.getBiome())) {
                    nInv.addItem(nInv.createItem(biome.getGuiIcon(),
                            ChatColor.translateAlternateColorCodes('&',
                                    langConfig.getString("Menu.Biome.Item.Biome.Current.Displayname")
                                            .replace("%biome_type", biome.getFormattedBiomeName())),
                            langConfig.getStringList("Menu.Biome.Item.Biome.Current.Lore"), null,
                            new Enchantment[]{Enchantment.THORNS}, new ItemFlag[]{ItemFlag.HIDE_ENCHANTS}),
                            slotIndex);
                } else {
                    nInv.addItem(nInv.createItem(biome.getGuiIcon(),
                            ChatColor.translateAlternateColorCodes('&',
                                    langConfig.getString("Menu.Biome.Item.Biome.Select.Displayname")
                                            .replace("%biome_type", biome.getFormattedBiomeName())),
                            langConfig.getStringList("Menu.Biome.Item.Biome.Select.Lore"), null, null, null),
                            slotIndex);
                }

                slotIndex++;
            }

            nInv.setTitle(ChatColor.translateAlternateColorCodes('&', langConfig.getString("Menu.Biome.Title")));
            nInv.setRows(4);

            Bukkit.getServer().getScheduler().runTask(plugin, () -> nInv.open());
        }
    }
}
