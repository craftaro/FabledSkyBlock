package com.songoda.skyblock.command.commands.island;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.*;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;

import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class SetSpawnCommand extends SubCommand {

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = skyblock.getMessageManager();
        IslandManager islandManager = skyblock.getIslandManager();
        SoundManager soundManager = skyblock.getSoundManager();
        FileManager fileManager = skyblock.getFileManager();

        Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length == 1) {
            Island island = islandManager.getIsland(player);

            if (island == null) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.SetSpawn.Owner.Message"));
                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            } else {
                IslandEnvironment environment;

                if (args[0].equalsIgnoreCase("Main")) {
                    environment = IslandEnvironment.Main;
                } else if (args[0].equalsIgnoreCase("Visitor")) {
                    environment = IslandEnvironment.Visitor;
                } else {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.SetSpawn.Spawn.Message"));
                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                    return;
                }

                if (island.hasRole(IslandRole.Operator, player.getUniqueId())
                        || island.hasRole(IslandRole.Owner, player.getUniqueId())) {
                    if ((island.hasRole(IslandRole.Operator, player.getUniqueId())
                            && (island.getSetting(IslandRole.Operator, environment.name() + "Spawn").getStatus()))
                            || island.hasRole(IslandRole.Owner, player.getUniqueId())) {
                        if (islandManager.isPlayerAtIsland(island, player)) {
                            IslandWorld world = skyblock.getWorldManager().getIslandWorld(player.getWorld());
                            Location location = player.getLocation();

                            if (fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
                                    .getFileConfiguration().getBoolean("Island.Spawn.Protection")) {

                                CompatibleMaterial toCompare = CompatibleMaterial.getMaterial(location.clone().subtract(0.0D, 1.0D, 0.0D).getBlock().getType());

                                if(toCompare == CompatibleMaterial.AIR
                                        || toCompare == CompatibleMaterial.MOVING_PISTON
                                        || toCompare == CompatibleMaterial.ICE
                                        || toCompare == CompatibleMaterial.PISTON_HEAD) {

                                    messageManager.sendMessage(player,
                                            configLoad.getString("Command.Island.SetSpawn.Protection.Block.Message"));
                                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                    return;
                                } else if (!player.getLocation().clone().subtract(0, 0.1, 0).getBlock().getType().isSolid()) {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Command.Island.SetSpawn.Protection.Ground.Message"));
                                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                    return;
                                } else if (location.getBlock().isLiquid()
                                        || location.clone().add(0.0D, 1.0D, 0.0D).getBlock().isLiquid()) {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Command.Island.SetSpawn.Protection.Liquid.Message"));
                                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                    return;
                                } else if (CompatibleMaterial.getMaterial(location.getBlock().getType()) == CompatibleMaterial.NETHER_PORTAL
                                        || CompatibleMaterial.getMaterial(location.clone().add(0.0D, 1.0D, 0.0D).getBlock()
                                        .getType()) == CompatibleMaterial.NETHER_PORTAL) {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Command.Island.SetSpawn.Protection.Portal.Message"));
                                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                    return;
                                } else {
                                    CompatibleMaterial type = CompatibleMaterial.getMaterial(location.getBlock().getType());
                                    if (type.isSolid() && type.isOccluding()) {
                                        location.getBlock().breakNaturally();
                                    }

                                    CompatibleMaterial typeBelow = CompatibleMaterial.getMaterial(location.clone().add(0.0D, 1.0D, 0.0D).getBlock().getType());
                                    if (typeBelow.isSolid() && type.isOccluding()) {
                                        location.clone().add(0.0D, 1.0D, 0.0D).getBlock().breakNaturally();
                                    }

                                    islandManager.removeSpawnProtection(island.getLocation(world, environment));
                                }
                            }

                            Location newSpawnLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
                            island.setLocation(world, environment, newSpawnLocation);

                            messageManager.sendMessage(player,
                                    configLoad.getString("Command.Island.SetSpawn.Set.Message").replace("%spawn",
                                            environment.name().toLowerCase()));
                            soundManager.playSound(player, CompatibleSound.BLOCK_NOTE_BLOCK_PLING.getSound(), 1.0F, 1.0F);

                            return;
                        }

                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.SetSpawn.Island.Message").replace("%spawn",
                                        environment.name().toLowerCase()));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                    } else {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.SetSpawn.Permission.Message").replace("%spawn",
                                        environment.name().toLowerCase()));
                        soundManager.playSound(player,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
                    }
                } else {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.SetSpawn.Role.Message")
                            .replace("%spawn", environment.name().toLowerCase()));
                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                }
            }
        } else {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.SetSpawn.Invalid.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "setspawn";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.SetSpawn.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String[] getArguments() {
        return new String[]{"main", "visitor"};
    }
}
