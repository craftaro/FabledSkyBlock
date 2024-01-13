package com.craftaro.skyblock.command.commands.admin;

import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.command.SubCommand;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.config.FileManager.Config;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.island.IslandRole;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.utils.player.OfflinePlayer;
import com.craftaro.skyblock.utils.world.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class DeleteCommand extends SubCommand {
    public DeleteCommand(SkyBlock plugin) {
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
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        FileManager fileManager = this.plugin.getFileManager();

        Config config = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        Player player = null;

        if (sender instanceof Player) {
            player = (Player) sender;
        }

        if (args.length == 1) {
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

            if (targetPlayerUUID == null || !islandManager.isIslandExist(targetPlayerUUID)) {
                messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.Delete.Owner.Message"));
                soundManager.playSound(sender, XSound.BLOCK_ANVIL_LAND);
            } else {
                islandManager.loadIsland(Bukkit.getServer().getOfflinePlayer(targetPlayerUUID));
                Island island = islandManager.getIsland(Bukkit.getServer().getOfflinePlayer(targetPlayerUUID));
                Location spawnLocation = LocationUtil.getSpawnLocation();

                if (spawnLocation != null && islandManager.isLocationAtIsland(island, spawnLocation)) {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.Delete.Spawn.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                    islandManager.unloadIsland(island, null);

                    return;
                }

                for (Player all : Bukkit.getOnlinePlayers()) {
                    if (island.hasRole(IslandRole.MEMBER, all.getUniqueId())
                            || island.hasRole(IslandRole.OPERATOR, all.getUniqueId())) {
                        all.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Confirmation.Deletion.Broadcast.Message")));
                        soundManager.playSound(all, XSound.ENTITY_GENERIC_EXPLODE, 10, 10);
                    }
                }

                island.setDeleted(true);
                islandManager.deleteIsland(island, true);

                messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.Delete.Deleted.Message").replace("%player", targetPlayerName));
                soundManager.playSound(sender, XSound.ENTITY_IRON_GOLEM_ATTACK);
            }
        } else {
            messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.Delete.Invalid.Message"));
            soundManager.playSound(sender, XSound.BLOCK_ANVIL_LAND);
        }
    }

    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Admin.Delete.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"remove", "disband"};
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
