package com.songoda.skyblock.command.commands.admin;

import com.songoda.core.compatibility.CompatibleBiome;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.biome.BiomeManager;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.StringUtil;
import com.songoda.skyblock.utils.player.OfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class SetBiomeCommand extends SubCommand {
    public SetBiomeCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        onCommand(player, args);
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        onCommand(sender, args);
    }

    public void onCommand(CommandSender sender, String[] args) {
        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        BiomeManager biomeManager = this.plugin.getBiomeManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        FileManager fileManager = this.plugin.getFileManager();

        Config config = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length >= 2) {
            String biomeName = args[1].toUpperCase().trim();

            IslandWorld world = null;
            if (args.length > 2) {
                String worldName = args[2].toUpperCase().trim();
                for (IslandWorld islandWorld : IslandWorld.values()) {
                    if (islandWorld.name().equalsIgnoreCase(worldName)) {
                        world = islandWorld;
                    }
                }
            }

            if (world == null) {
                world = IslandWorld.NORMAL;
            }

            CompatibleBiome biome = null;
            for (CompatibleBiome cbiome : CompatibleBiome.values()) {
                if (cbiome.isCompatible() && cbiome.name().equals(biomeName)) {
                    biome = cbiome;
                    break;
                }
            }

            if (biome != null) {
                Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
                UUID islandOwnerUUID;
                String targetPlayerName;

                if (targetPlayer == null) {
                    OfflinePlayer targetPlayerOffline = new OfflinePlayer(args[0]);
                    islandOwnerUUID = targetPlayerOffline.getOwner();
                    targetPlayerName = targetPlayerOffline.getName();
                } else {
                    islandOwnerUUID = playerDataManager.getPlayerData(targetPlayer).getOwner();
                    targetPlayerName = targetPlayer.getName();
                }

                if (islandOwnerUUID == null) {
                    messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.SetBiome.Island.Owner.Message"));
                    soundManager.playSound(sender, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
                } else {
                    if (islandManager.containsIsland(islandOwnerUUID)) {
                        Island island = islandManager.getIsland(Bukkit.getServer().getOfflinePlayer(islandOwnerUUID));
                        biomeManager.setBiome(island, world, biome, null);
                        if (world == IslandWorld.NORMAL) {
                            island.setBiome(biome.getBiome());
                        }
                    } else {
                        islandManager.loadIsland(Bukkit.getOfflinePlayer(islandOwnerUUID));
                        Island island = islandManager.getIsland(Bukkit.getOfflinePlayer(islandOwnerUUID));
                        if (island == null) {
                            messageManager.sendMessage(sender,
                                    configLoad.getString("Command.Island.Admin.SetBiome.Island.Data.Message"));
                            soundManager.playSound(sender, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                        } else {
                            CompatibleBiome finalBiome = biome;
                            IslandWorld finalWorld = world;
                            biomeManager.setBiome(island, world, biome, () -> {
                                if (finalWorld == IslandWorld.NORMAL) {
                                    island.setBiome(finalBiome.getBiome());
                                }
                            });
                        }
                    }

                    messageManager.sendMessage(sender,
                            configLoad.getString("Command.Island.Admin.SetBiome.Set.Message")
                                    .replace("%player", targetPlayerName)
                                    .replace("%biome", StringUtil.capitalizeWord(biome.getBiome().name().replaceAll("_", " "))));
                    soundManager.playSound(sender, CompatibleSound.BLOCK_NOTE_BLOCK_PLING.getSound(), 1.0F, 1.0F);
                }
            } else {
                messageManager.sendMessage(sender,
                        configLoad.getString("Command.Island.Admin.SetBiome.InvalidBiome.Message"));
                soundManager.playSound(sender, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            }
        } else {
            messageManager.sendMessage(sender,
                    configLoad.getString("Command.Island.Admin.SetBiome.Invalid.Message"));
            soundManager.playSound(sender, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
        }
    }

    @Override
    public String getName() {
        return "setbiome";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Admin.SetBiome.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
