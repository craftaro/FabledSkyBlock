package com.songoda.skyblock.command.commands.island;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.player.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Set;
import java.util.UUID;

public class WhitelistCommand extends SubCommand {
    public WhitelistCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        FileManager fileManager = this.plugin.getFileManager();
        SoundManager soundManager = this.plugin.getSoundManager();

        FileManager.Config language = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration languageLoad = language.getFileConfiguration();

        FileManager.Config config = this.plugin.getFileManager().getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        Island island = islandManager.getIsland(player);

        if (island == null) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Whitelist.Owner.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
        } else if (island.hasRole(IslandRole.OWNER, player.getUniqueId())
                || (island.hasRole(IslandRole.OPERATOR, player.getUniqueId())
                && this.plugin.getPermissionManager().hasPermission(island, "Visitor", IslandRole.OPERATOR))) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("on")) {
                    messageManager.sendMessage(player, languageLoad.getString("Command.Island.Whitelist.Enabled.Message"));
                    islandManager.whitelistIsland(island);
                } else if (args[0].equalsIgnoreCase("off")) {
                    messageManager.sendMessage(player, languageLoad.getString("Command.Island.Whitelist.Disabled.Message"));
                    islandManager.closeIsland(island);
                } else if (args[0].equalsIgnoreCase("list")) {
                    Set<UUID> whitelistedPlayers = island.getWhitelistedPlayers();
                    if (!whitelistedPlayers.isEmpty()) {
                        messageManager.sendMessage(player, languageLoad.getString("Command.Island.Whitelist.List.Start.Message"));
                        for (UUID uuid : whitelistedPlayers) {
                            messageManager.sendMessage(player, languageLoad.getString("Command.Island.Whitelist.List.List.Message")
                                    .replace("%player", new OfflinePlayer(uuid).getName()));
                        }
                        messageManager.sendMessage(player, languageLoad.getString("Command.Island.Whitelist.List.End.Message"));
                    } else {
                        messageManager.sendMessage(player, languageLoad.getString("Command.Island.Whitelist.List.Empty.Message"));
                    }
                } else { // Invalid args
                    messageManager.sendMessage(player, languageLoad.getString("Command.Island.Whitelist.InvalidArgNumber.Message"));
                }
            } else if (args.length == 2) {
                OfflinePlayer offlinePlayer = new OfflinePlayer(args[1]);
                if (offlinePlayer.getBukkitOfflinePlayer().hasPlayedBefore()) {
                    if (args[0].equalsIgnoreCase("add")) {
                        if (!island.isPlayerWhitelisted(offlinePlayer.getUUID())) {
                            island.addWhitelistedPlayer(offlinePlayer.getUUID());
                            messageManager.sendMessage(player, languageLoad.getString("Command.Island.Whitelist.Added.Message")
                                    .replace("%player", offlinePlayer.getName()));
                        } else {
                            messageManager.sendMessage(player, languageLoad.getString("Command.Island.Whitelist.AlreadyAdded.Message")
                                    .replace("%player", offlinePlayer.getName()));
                        }
                    } else if (args[0].equalsIgnoreCase("remove")) {
                        if (island.isPlayerWhitelisted(offlinePlayer.getUUID())) {
                            island.removeWhitelistedPlayer(offlinePlayer.getUUID());
                            messageManager.sendMessage(player, languageLoad.getString("Command.Island.Whitelist.Removed.Message")
                                    .replace("%player", offlinePlayer.getName()));
                        } else {
                            messageManager.sendMessage(player, languageLoad.getString("Command.Island.Whitelist.NotWhitelisted.Message")
                                    .replace("%player", offlinePlayer.getName()));
                        }
                    } else { // Invalid args
                        messageManager.sendMessage(player, languageLoad.getString("Command.Island.Whitelist.InvalidArgNumber.Message"));
                    }
                } else { // Player not found
                    messageManager.sendMessage(player, languageLoad.getString("Command.Island.Whitelist.PlayerNotFound.Message")
                            .replace("%player", offlinePlayer.getName()));
                }
            } else { // Invalid args
                messageManager.sendMessage(player, languageLoad.getString("Command.Island.Whitelist.InvalidArgNumber.Message"));
            }
        } else {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Whitelist.Permission.Message"));
            soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "whitelist";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Public.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String[] getArguments() {
        return new String[]{"on", "off", "add", "remove", "list"};
    }
}
