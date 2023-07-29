package com.songoda.skyblock.command.commands.island;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.ban.Ban;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.player.OfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class UnbanCommand extends SubCommand {
    public UnbanCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        FileManager fileManager = this.plugin.getFileManager();

        Config config = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length == 1) {
            Island island = islandManager.getIsland(player);

            if (island == null) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Unban.Owner.Message"));
                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            } else if (this.plugin.getConfiguration()
                    .getBoolean("Island.Visitor.Banning")) {
                if (island.hasRole(IslandRole.OWNER, player.getUniqueId())
                        || (island.hasRole(IslandRole.OPERATOR, player.getUniqueId())
                        && this.plugin.getPermissionManager().hasPermission(island, "Unban", IslandRole.OPERATOR))) {
                    Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);

                    UUID targetPlayerUUID;
                    String targetPlayerName;

                    if (targetPlayer == null) {
                        OfflinePlayer targetPlayerOffline = new OfflinePlayer(args[0]);
                        targetPlayerUUID = targetPlayerOffline.getUniqueId();
                        targetPlayerName = targetPlayerOffline.getName();
                    } else {
                        targetPlayerUUID = targetPlayer.getUniqueId();
                        targetPlayerName = targetPlayer.getName();
                    }

                    if (targetPlayerUUID == null) {
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Unban.Found.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                    } else if (targetPlayerUUID.equals(player.getUniqueId())) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Unban.Yourself.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                    } else if (island.hasRole(IslandRole.MEMBER, targetPlayerUUID)
                            || island.hasRole(IslandRole.OPERATOR, targetPlayerUUID)
                            || island.hasRole(IslandRole.OWNER, targetPlayerUUID)) {
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Unban.Member.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                    } else if (!island.getBan().isBanned(targetPlayerUUID)) {
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Unban.Banned.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                    } else {
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Unban.Unbanned.Message")
                                .replace("%player", targetPlayerName));
                        soundManager.playSound(player, CompatibleSound.ENTITY_IRON_GOLEM_ATTACK.getSound(), 1.0F, 1.0F);

                        Ban ban = island.getBan();
                        ban.removeBan(targetPlayerUUID);
                        ban.save();
                    }
                } else {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Unban.Permission.Message"));
                    soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
                }
            } else {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Unban.Disabled.Message"));
                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            }
        } else {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Unban.Invalid.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "unban";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Unban.Info.Message";
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
