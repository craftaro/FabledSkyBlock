package com.songoda.skyblock.gui.biome;

import com.craftaro.core.compatibility.CompatibleBiome;
import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.gui.Gui;
import com.craftaro.core.gui.GuiUtils;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.core.utils.TextUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.biome.BiomeManager;
import com.songoda.skyblock.cooldown.Cooldown;
import com.songoda.skyblock.cooldown.CooldownManager;
import com.songoda.skyblock.cooldown.CooldownPlayer;
import com.songoda.skyblock.cooldown.CooldownType;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandEnvironment;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

public class GuiBiome extends Gui {
    private final SkyBlock plugin;
    private final Island island;
    private final FileConfiguration languageLoad;
    private final Player player;
    private final IslandWorld world;

    public GuiBiome(SkyBlock plugin, Player player, Island island, IslandWorld world, Gui returnGui, boolean admin) {
        super(6, returnGui);
        this.plugin = plugin;
        this.island = island;
        this.world = world;
        this.player = player;
        FileConfiguration config = plugin.getFileManager()
                .getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration();
        this.languageLoad = plugin.getFileManager()
                .getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration();
        setDefaultItem(null);
        setTitle(TextUtils.formatText(this.languageLoad.getString("Menu.Biome.Title")));
        paint();
    }

    public void paint() {
        SoundManager soundManager = this.plugin.getSoundManager();
        BiomeManager biomeManager = this.plugin.getBiomeManager();
        CooldownManager cooldownManager = this.plugin.getCooldownManager();
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();

        if (this.inventory != null) {
            this.inventory.clear();
        }
        setActionForRange(0, 0, 5, 9, null);

        setButton(0, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(this.languageLoad.getString("Menu.Biome.Item.Exit.Displayname"))), (event) -> {
            soundManager.playSound(event.player, XSound.BLOCK_CHEST_CLOSE);
            event.player.closeInventory();
        });

        setButton(8, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(this.languageLoad.getString("Menu.Biome.Item.Exit.Displayname"))), (event) -> {
            soundManager.playSound(event.player, XSound.BLOCK_CHEST_CLOSE);
            event.player.closeInventory();
        });

        List<String> lore = this.languageLoad.getStringList("Menu.Biome.Item.Info.Lore");
        for (ListIterator<String> i = lore.listIterator(); i.hasNext(); ) {
            i.set(TextUtils.formatText(i.next().replace("%biome_type", this.island.getBiomeName())));
        }

        setItem(4, GuiUtils.createButtonItem(CompatibleMaterial.PAINTING, // Info
                TextUtils.formatText(this.languageLoad.getString("Menu.Biome.Item.Info.Displayname")), lore));

        for (int i = 9; i < 18; i++) {
            setItem(i, CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem());
        }

        List<BiomeIcon> biomes = new ArrayList<>();
        for (CompatibleBiome biome : CompatibleBiome.getCompatibleBiomes()) {
            if (biome.isCompatible()) {
                BiomeIcon icon = new BiomeIcon(this.plugin, biome);
                if (icon.biome != null &&
                        (!icon.permission ||
                                this.player.hasPermission("fabledskyblock.biome." + biome.name().toLowerCase()))) {
                    switch (this.world) {
                        case NORMAL:
                            if (icon.normal) {
                                biomes.add(icon);
                            }
                            break;
                        case NETHER:
                            if (icon.nether) {
                                biomes.add(icon);
                            }
                            break;
                        case END:
                            if (icon.end) {
                                biomes.add(icon);
                            }
                            break;
                    }
                }
            }
        }

        if (!biomes.isEmpty()) {
            biomes.sort(Comparator.comparing(m -> m.biome.name()));

            this.pages = (int) Math.max(1, Math.ceil((double) biomes.size() / 27d));

            if (this.page != 1) {
                setButton(5, 2, GuiUtils.createButtonItem(CompatibleMaterial.ARROW,
                                TextUtils.formatText(this.languageLoad.getString("Menu.Biome.Item.Last.Displayname"))),
                        (event) -> {
                            this.page--;
                            paint();
                        });
            }

            if (this.page != this.pages) {
                setButton(5, 6, GuiUtils.createButtonItem(CompatibleMaterial.ARROW,
                                TextUtils.formatText(this.languageLoad.getString("Menu.Biome.Item.Next.Displayname"))),
                        (event) -> {
                            this.page++;
                            paint();
                        });
            }

            for (int i = 18; i < ((getRows() - 2) * 9) + 9; i++) {
                int current = ((this.page - 1) * 27) - 18;
                if (current + i >= biomes.size()) {
                    setItem(i, null);
                    continue;
                }
                BiomeIcon icon = biomes.get(current + i);
                if (icon == null ||
                        icon.biome == null ||
                        icon.biome.getBiome() == null) {
                    continue;
                }

                if (icon.biome.getBiome() == this.island.getBiome()) {
                    icon.enchant();
                }

                setButton(i, icon.displayItem, event -> {
                    if (cooldownManager.hasPlayer(CooldownType.BIOME, this.player) && !this.player.hasPermission("fabledskyblock.bypass.cooldown")) {
                        CooldownPlayer cooldownPlayer = cooldownManager.getCooldownPlayer(CooldownType.BIOME, this.player);
                        Cooldown cooldown = cooldownPlayer.getCooldown();

                        if (cooldown.getTime() < 60) {
                            messageManager.sendMessage(this.player,
                                    this.languageLoad.getString("Island.Biome.Cooldown.Message")
                                            .replace("%time",
                                                    cooldown.getTime() + " " + this.languageLoad
                                                            .getString("Island.Biome.Cooldown.Word.Second")));
                        } else {
                            long[] durationTime = NumberUtil.getDuration(cooldown.getTime());
                            messageManager.sendMessage(this.player,
                                    this.languageLoad.getString("Island.Biome.Cooldown.Message")
                                            .replace("%time", durationTime[2] + " "
                                                    + this.languageLoad.getString("Island.Biome.Cooldown.Word.Minute")
                                                    + " " + durationTime[3] + " "
                                                    + this.languageLoad.getString("Island.Biome.Cooldown.Word.Second")));
                        }

                        soundManager.playSound(this.player, XSound.ENTITY_VILLAGER_NO);

                        return;
                    }
                    cooldownManager.createPlayer(CooldownType.BIOME, this.player);
                    Bukkit.getScheduler().runTask(this.plugin, () -> {
                        biomeManager.setBiome(this.island, IslandWorld.NORMAL, icon.biome, () -> {
                            if (this.languageLoad.getBoolean("Command.Island.Biome.Completed.Should-Display-Message")) {
                                messageManager.sendMessage(this.player, this.languageLoad.getString("Command.Island.Biome.Completed.Message"));
                                soundManager.playSound(this.player, XSound.ENTITY_VILLAGER_YES);
                            }
                        });
                        this.island.setBiome(icon.biome.getBiome()); // FIXME: A event is fired with has a setBiome method...
                        this.island.save();
                    });

                    soundManager.playSound(this.island.getLocation(IslandWorld.NORMAL, IslandEnvironment.ISLAND), XSound.ENTITY_GENERIC_SPLASH, 1, 1);

                    if (!islandManager.isPlayerAtIsland(this.island, this.player, IslandWorld.NORMAL)) {
                        soundManager.playSound(this.player, XSound.ENTITY_GENERIC_SPLASH);
                    }
                    paint();
                });
            }
        } else {
            setItem(31, CompatibleMaterial.BARRIER.getItem()); // TODO
        }
    }
}
