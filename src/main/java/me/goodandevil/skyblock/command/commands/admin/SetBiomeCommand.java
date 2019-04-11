package me.goodandevil.skyblock.command.commands.admin;

import me.goodandevil.skyblock.biome.BiomeManager;
import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.NumberUtil;
import me.goodandevil.skyblock.utils.player.OfflinePlayer;
import me.goodandevil.skyblock.utils.version.SBiome;
import me.goodandevil.skyblock.utils.version.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class SetBiomeCommand extends SubCommand {

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        onCommand(player, args);
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        onCommand(sender, args);
    }

    public void onCommand(CommandSender sender, String[] args) {
        PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
        MessageManager messageManager = skyblock.getMessageManager();
        IslandManager islandManager = skyblock.getIslandManager();
        BiomeManager biomeManager = skyblock.getBiomeManager();
        SoundManager soundManager = skyblock.getSoundManager();
        FileManager fileManager = skyblock.getFileManager();

        Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length == 2) {
            String biomeName = args[1].toUpperCase().trim();

            SBiome biome = null;
            for (SBiome sbiome : SBiome.values()) {
                if (sbiome.isAvailable() && sbiome.name().equals(biomeName)) {
                    biome = sbiome;
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
                    messageManager.sendMessage(sender,
                            configLoad.getString("Command.Island.Admin.SetBiome.Island.Owner.Message"));
                    soundManager.playSound(sender, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
                } else {
                    if (islandManager.containsIsland(islandOwnerUUID)) {
                        Island island = islandManager.getIsland(Bukkit.getServer().getOfflinePlayer(islandOwnerUUID));
                        biomeManager.setBiome(island, biome.getBiome());
                        island.setBiome(biome.getBiome());
                    } else {
                        Island island = islandManager.loadIsland(Bukkit.getOfflinePlayer(islandOwnerUUID));
                        if (island == null) {
                            messageManager.sendMessage(sender,
                                    configLoad.getString("Command.Island.Admin.SetBiome.Island.Data.Message"));
                            soundManager.playSound(sender, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
                        } else {
                            biomeManager.setBiome(island, biome.getBiome());
                            island.setBiome(biome.getBiome());
                        }
                    }

                    messageManager.sendMessage(sender,
                            configLoad.getString("Command.Island.Admin.SetBiome.Set.Message")
                                    .replace("%player", targetPlayerName)
                                    .replace("%biome", biome.getFormattedBiomeName()));
                    soundManager.playSound(sender, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
                }
            } else {
                messageManager.sendMessage(sender,
                        configLoad.getString("Command.Island.Admin.SetBiome.InvalidBiome.Message"));
                soundManager.playSound(sender, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
            }
        } else {
            messageManager.sendMessage(sender,
                    configLoad.getString("Command.Island.Admin.SetBiome.Invalid.Message"));
            soundManager.playSound(sender, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
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
