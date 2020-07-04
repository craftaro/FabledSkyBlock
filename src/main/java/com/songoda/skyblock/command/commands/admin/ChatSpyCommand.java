package com.songoda.skyblock.command.commands.admin;

import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class ChatSpyCommand extends SubCommand {
    
    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = skyblock.getMessageManager();
        PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
        FileManager fileManager = skyblock.getFileManager();
    
        FileManager.Config language = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration languageLoad = language.getFileConfiguration();
        
        PlayerData playerData = playerDataManager.getPlayerData(player);
        if(playerData != null) {
            if(playerData.isChatSpy()){
                playerData.setChatSpy(false);
                messageManager.sendMessage(player, languageLoad.getString("Command.Island.Admin.ChatSpy.Disabled.Message"));
            } else {
                playerData.setChatSpy(true);
                messageManager.sendMessage(player, languageLoad.getString("Command.Island.Admin.ChatSpy.Enabled.Message"));
            }
        }
    }
    
    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }
    
    @Override
    public String getName() {
        return "chatspy";
    }
    
    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Admin.ChatSpy.Info.Message";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"spychat", "spy"};
    }
    
    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
