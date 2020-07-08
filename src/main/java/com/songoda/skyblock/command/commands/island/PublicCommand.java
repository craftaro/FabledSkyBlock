package com.songoda.skyblock.command.commands.island;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.island.IslandStatus;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class PublicCommand extends SubCommand {

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = skyblock.getMessageManager();
        IslandManager islandManager = skyblock.getIslandManager();
        SoundManager soundManager = skyblock.getSoundManager();

        Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        Island island = islandManager.getIsland(player);

        if (island == null) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Public.Owner.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
        } else if (island.hasRole(IslandRole.Owner, player.getUniqueId())
                || (island.hasRole(IslandRole.Operator, player.getUniqueId())
                && skyblock.getPermissionManager().hasPermission(island, "Visitor", IslandRole.Operator))) {
            switch (island.getStatus()) {
                case OPEN:
                    islandManager.whitelistIsland(island);
    
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Public.Restricted.Message"));
                    soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_DOOR_CLOSE.getSound(), 1.0F, 1.0F);
                    break;
                case WHITELISTED:
                    islandManager.closeIsland(island);
    
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Public.Private.Message"));
                    soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_DOOR_CLOSE.getSound(), 1.0F, 1.0F);
                    break;
                case CLOSED:
                    island.setStatus(IslandStatus.OPEN);
    
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Public.Public.Message"));
                    soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_DOOR_OPEN.getSound(), 1.0F, 1.0F);
                    break;
            }
        } else {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Public.Permission.Message"));
            soundManager.playSound(player,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "public";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Public.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"pub", "private", "pri", "restricted", "res"};
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
