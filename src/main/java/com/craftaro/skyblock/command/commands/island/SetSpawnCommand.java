package com.craftaro.skyblock.command.commands.island;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.command.SubCommand;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandEnvironment;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.island.IslandRole;
import com.craftaro.skyblock.island.IslandWorld;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.sound.SoundManager;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Optional;

public class SetSpawnCommand extends SubCommand {
    public SetSpawnCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        FileManager fileManager = this.plugin.getFileManager();

        FileManager.Config config = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length == 1) {
            Island island = islandManager.getIsland(player);

            if (island == null) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.SetSpawn.Owner.Message"));
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
            } else {
                if (args[0].equalsIgnoreCase("Main")) {
                    setIslandSpawn(IslandEnvironment.MAIN, island, islandManager, player, configLoad, fileManager, messageManager, soundManager);
                } else if (args[0].equalsIgnoreCase("Visitor")) {
                    setIslandSpawn(IslandEnvironment.VISITOR, island, islandManager, player, configLoad, fileManager, messageManager, soundManager);
                } else if (args[0].equalsIgnoreCase("All")) {
                    setIslandSpawn(IslandEnvironment.MAIN, island, islandManager, player, configLoad, fileManager, messageManager, soundManager);
                    setIslandSpawn(IslandEnvironment.VISITOR, island, islandManager, player, configLoad, fileManager, messageManager, soundManager);
                } else {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.SetSpawn.Spawn.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                }
            }
        } else {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.SetSpawn.Invalid.Message"));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
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
        return new String[]{"main", "visitor", "all"};
    }


    private void setIslandSpawn(IslandEnvironment environment, Island island, IslandManager islandManager, Player player, FileConfiguration configLoad, FileManager fileManager, MessageManager messageManager, SoundManager soundManager) {
        if (island.hasRole(IslandRole.OPERATOR, player.getUniqueId())
                || island.hasRole(IslandRole.OWNER, player.getUniqueId())) {
            if ((island.hasRole(IslandRole.OPERATOR, player.getUniqueId())
                    && (this.plugin.getPermissionManager().hasPermission(island,
                    environment.getFriendlyName() + "Spawn", IslandRole.OPERATOR)))
                    || island.hasRole(IslandRole.OWNER, player.getUniqueId())) {
                if (islandManager.isPlayerAtIsland(island, player)) {
                    IslandWorld world = this.plugin.getWorldManager().getIslandWorld(player.getWorld());
                    Location location = player.getLocation();

                    if (fileManager.getConfig(new File(this.plugin.getDataFolder(), "config.yml"))
                            .getFileConfiguration().getBoolean("Island.Spawn.Protection")) {

                        Optional<XMaterial> toCompare = CompatibleMaterial.getMaterial(location.clone().subtract(0.0D, 1.0D, 0.0D).getBlock().getType());

                        if (toCompare.isPresent() && CompatibleMaterial.isAir(toCompare.get())
                                || toCompare.get() == XMaterial.MOVING_PISTON
                                || toCompare.get() == XMaterial.ICE
                                || toCompare.get() == XMaterial.PISTON_HEAD) {

                            messageManager.sendMessage(player,
                                    configLoad.getString("Command.Island.SetSpawn.Protection.Block.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                            return;
                        } else if (!player.getLocation().clone().subtract(0, 0.1, 0).getBlock().getType().isSolid()) {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Command.Island.SetSpawn.Protection.Ground.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                            return;
                        } else if (location.getBlock().isLiquid()
                                || location.clone().add(0.0D, 1.0D, 0.0D).getBlock().isLiquid()) {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Command.Island.SetSpawn.Protection.Liquid.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                            return;
                        } else if (CompatibleMaterial.getMaterial(location.getBlock().getType()).get() == XMaterial.NETHER_PORTAL
                                || CompatibleMaterial.getMaterial(location.clone().add(0.0D, 1.0D, 0.0D).getBlock()
                                .getType()).get() == XMaterial.NETHER_PORTAL) {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Command.Island.SetSpawn.Protection.Portal.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                            return;
                        } else {
                            Optional<XMaterial> type = CompatibleMaterial.getMaterial(location.getBlock().getType());
                            if (type.isPresent() && type.get().parseMaterial().isSolid() && type.get().parseMaterial().isOccluding()) {
                                location.getBlock().breakNaturally();
                            }

                            Optional<XMaterial> typeBelow = CompatibleMaterial.getMaterial(location.clone().add(0.0D, 1.0D, 0.0D).getBlock().getType());
                            if (typeBelow.isPresent() && typeBelow.get().parseMaterial().isSolid() && type.get().parseMaterial().isOccluding()) {
                                location.clone().add(0.0D, 1.0D, 0.0D).getBlock().breakNaturally();
                            }

                            islandManager.removeSpawnProtection(island.getLocation(world, environment));
                        }
                    }

                    Location newSpawnLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
                    island.setLocation(world, environment, newSpawnLocation);

                    messageManager.sendMessage(player,
                            configLoad.getString("Command.Island.SetSpawn.Set.Message").replace("%spawn", environment.getFriendlyName().toLowerCase()));
                    soundManager.playSound(player, XSound.BLOCK_NOTE_BLOCK_PLING);

                    return;
                }

                messageManager.sendMessage(player,
                        configLoad.getString("Command.Island.SetSpawn.Island.Message").replace("%spawn", environment.getFriendlyName().toLowerCase()));
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
            } else {
                messageManager.sendMessage(player,
                        configLoad.getString("Command.Island.SetSpawn.Permission.Message").replace("%spawn", environment.getFriendlyName().toLowerCase()));
                soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);
            }
        } else {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.SetSpawn.Role.Message")
                    .replace("%spawn", environment.getFriendlyName().toLowerCase()));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
        }
    }
}
