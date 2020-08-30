package com.songoda.skyblock.gui.biome;

import com.songoda.core.compatibility.CompatibleBiome;
import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.utils.TextUtils;
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
        setTitle(TextUtils.formatText(languageLoad.getString("Menu.Biome.Title")));
        paint();
    }

    public void paint() {
        SoundManager soundManager = plugin.getSoundManager();
        BiomeManager biomeManager = plugin.getBiomeManager();
        CooldownManager cooldownManager = plugin.getCooldownManager();
        MessageManager messageManager = plugin.getMessageManager();
        IslandManager islandManager = plugin.getIslandManager();

        if (inventory != null)
            inventory.clear();
        setActionForRange(0, 0, 5, 9, null);

        setButton(0, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(languageLoad.getString("Menu.Biome.Item.Exit.Displayname"))), (event) -> {
            soundManager.playSound(event.player, CompatibleSound.BLOCK_CHEST_CLOSE.getSound(), 1f, 1f);
            event.player.closeInventory();
        });

        setButton(8, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE, // Exit
                TextUtils.formatText(languageLoad.getString("Menu.Biome.Item.Exit.Displayname"))), (event) -> {
            soundManager.playSound(event.player, CompatibleSound.BLOCK_CHEST_CLOSE.getSound(), 1f, 1f);
            event.player.closeInventory();
        });
    
        List<String> lore = languageLoad.getStringList("Menu.Biome.Item.Info.Lore");
        for (ListIterator<String> i = lore.listIterator(); i.hasNext(); ) {
            i.set(TextUtils.formatText(i.next().replace("%biome_type", island.getBiomeName())));
        }
        
        setItem(4, GuiUtils.createButtonItem(CompatibleMaterial.PAINTING, // Info
                TextUtils.formatText(languageLoad.getString("Menu.Biome.Item.Info.Displayname")), lore));
        
        for(int i=9; i<18; i++){
            setItem(i, CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem());
        }

        List<BiomeIcon> biomes = new ArrayList<>();
        for(CompatibleBiome biome : CompatibleBiome.getCompatibleBiomes()) {
            if(biome.isCompatible()){
                try { // Hotfix for core misconfiguration
                    biome.getBiome();
                } catch (IllegalArgumentException ex) {
                    continue;
                }
                BiomeIcon icon = new BiomeIcon(plugin,  biome);
                if (icon.biome != null &&
                        (!icon.permission ||
                                player.hasPermission("fabledskyblock.biome." + biome.name().toLowerCase()))) {
                    switch (world) {
                        case Normal:
                            if (icon.normal) {
                                biomes.add(icon);
                            }
                            break;
                        case Nether:
                            if (icon.nether) {
                                biomes.add(icon);
                            }
                            break;
                        case End:
                            if (icon.end) {
                                biomes.add(icon);
                            }
                            break;
                    }
                }
            }
        }

        if(biomes.size() > 0){
            biomes.sort(Comparator.comparing(m -> m.biome.name()));
    
            this.pages = (int) Math.max(1, Math.ceil((double) biomes.size() / 27d));
    
            if (page != 1)
                setButton(5, 2, GuiUtils.createButtonItem(CompatibleMaterial.ARROW,
                        TextUtils.formatText(languageLoad.getString("Menu.Biome.Item.Last.Displayname"))),
                        (event) -> {
                            page--;
                            paint();
                        });
    
            if (page != pages)
                setButton(5, 6, GuiUtils.createButtonItem(CompatibleMaterial.ARROW,
                        TextUtils.formatText(languageLoad.getString("Menu.Biome.Item.Next.Displayname"))),
                        (event) -> {
                            page++;
                            paint();
                        });

            for (int i = 18; i < ((getRows()-2)*9)+9; i++) {
                int current = ((page - 1) * 27) - 18;
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

                if(icon.biome.getBiome().equals(island.getBiome())){
                    icon.enchant();
                }
                
                setButton(i, icon.displayItem, event -> {
                    if (cooldownManager.hasPlayer(CooldownType.Biome, player) && !player.hasPermission("fabledskyblock.bypass.cooldown")) {
                        CooldownPlayer cooldownPlayer = cooldownManager.getCooldownPlayer(CooldownType.Biome, player);
                        Cooldown cooldown = cooldownPlayer.getCooldown();

                        if (cooldown.getTime() < 60) {
                            messageManager.sendMessage(player,
                                    languageLoad.getString("Island.Biome.Cooldown.Message")
                                            .replace("%time",
                                                    cooldown.getTime() + " " + languageLoad
                                                            .getString("Island.Biome.Cooldown.Word.Second")));
                        } else {
                            long[] durationTime = NumberUtil.getDuration(cooldown.getTime());
                            messageManager.sendMessage(player,
                                    languageLoad.getString("Island.Biome.Cooldown.Message")
                                            .replace("%time", durationTime[2] + " "
                                                    + languageLoad.getString("Island.Biome.Cooldown.Word.Minute")
                                                    + " " + durationTime[3] + " "
                                                    + languageLoad.getString("Island.Biome.Cooldown.Word.Second")));
                        }

                        soundManager.playSound(player,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                        return;
                    }
                    cooldownManager.createPlayer(CooldownType.Biome, player);
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        biomeManager.setBiome(island, IslandWorld.Normal, icon.biome, () -> {
                            if(languageLoad.getBoolean("Command.Island.Biome.Completed.Should-Display-Message")){
                                messageManager.sendMessage(player, languageLoad.getString("Command.Island.Biome.Completed.Message"));
                                soundManager.playSound(player,  CompatibleSound.ENTITY_VILLAGER_YES.getSound(), 1.0F, 1.0F);
                            }
                        });
                        island.setBiome(icon.biome.getBiome());
                        island.save();
                    });

                    soundManager.playSound(island.getLocation(IslandWorld.Normal, IslandEnvironment.Island),
                            CompatibleSound.ENTITY_GENERIC_SPLASH.getSound(), 1.0F, 1.0F);

                    if (!islandManager.isPlayerAtIsland(island, player, IslandWorld.Normal)) {
                        soundManager.playSound(player, CompatibleSound.ENTITY_GENERIC_SPLASH.getSound(), 1.0F, 1.0F);
                    }
                    paint();
                });
            }
        } else {
            setItem(31, CompatibleMaterial.BARRIER.getItem()); // TODO
        }
    }
}
