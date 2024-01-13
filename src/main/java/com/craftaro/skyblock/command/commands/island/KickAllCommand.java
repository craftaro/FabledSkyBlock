package com.craftaro.skyblock.command.commands.island;

import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.api.event.island.IslandKickEvent;
import com.craftaro.skyblock.api.utils.APIUtil;
import com.craftaro.skyblock.command.SubCommand;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.island.IslandRole;
import com.craftaro.skyblock.island.IslandStatus;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.utils.world.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Set;
import java.util.UUID;

public class KickAllCommand extends SubCommand {
    public KickAllCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        SoundManager soundManager = this.plugin.getSoundManager();

        FileManager.Config config = this.plugin.getFileManager().getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        Island island = islandManager.getIsland(player);

        if (island == null) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.KickAll.Owner.Message"));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
        } else if (island.hasRole(IslandRole.OWNER, player.getUniqueId())
                || (island.hasRole(IslandRole.OPERATOR, player.getUniqueId())
                && this.plugin.getPermissionManager().hasPermission(island, "Kick", IslandRole.OPERATOR))) {
            if (island.getStatus() != IslandStatus.CLOSED) {
                Set<UUID> islandVisitors = islandManager.getVisitorsAtIsland(island);

                if (islandVisitors.isEmpty()) {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.KickAll.Visitors.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                } else {
                    for (UUID islandVisitorList : islandVisitors) {
                        Player targetPlayer = Bukkit.getServer().getPlayer(islandVisitorList);

                        if (targetPlayer != null && targetPlayer.hasPermission("fabledskyblock.bypass.kick")) {
                            continue;
                        }

                        IslandKickEvent islandKickEvent = new IslandKickEvent(island.getAPIWrapper(),
                                APIUtil.fromImplementation(IslandRole.VISITOR),
                                Bukkit.getServer().getOfflinePlayer(islandVisitorList), player);
                        Bukkit.getServer().getPluginManager().callEvent(islandKickEvent);

                        if (!islandKickEvent.isCancelled()) {
                            LocationUtil.teleportPlayerToSpawn(targetPlayer);

                            messageManager.sendMessage(targetPlayer,
                                    configLoad.getString("Command.Island.KickAll.Kicked.Target.Message")
                                            .replace("%player", player.getName()));
                            soundManager.playSound(targetPlayer, XSound.ENTITY_IRON_GOLEM_ATTACK);
                        }
                    }

                    messageManager.sendMessage(player, configLoad.getString("Command.Island.KickAll.Kicked.Sender.Message").replace("%visitors", "" + islandVisitors.size()));
                    soundManager.playSound(player, XSound.ENTITY_IRON_GOLEM_ATTACK);
                }
            } else {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.KickAll.Closed.Message"));
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
            }
        } else {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.KickAll.Role.Message"));
            soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "expel";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.KickAll.Info.Message";
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
