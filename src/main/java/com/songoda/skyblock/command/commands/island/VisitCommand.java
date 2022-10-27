package com.songoda.skyblock.command.commands.island;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.menus.Visit;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.sound.SoundManager;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class VisitCommand extends SubCommand {

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = plugin.getMessageManager();
        SoundManager soundManager = plugin.getSoundManager();

        if (args.length == 0) {
            if (!plugin.getPlayerDataManager().isPlayerDataLoaded(player))
                plugin.getPlayerDataManager().createPlayerData(player);

            PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
            playerData.setType(Visit.Type.Default);
            playerData.setSort(Visit.Sort.Default);

            Visit.getInstance().open(player, (Visit.Type) playerData.getType(), (Visit.Sort) playerData.getSort());
            soundManager.playSound(player, CompatibleSound.BLOCK_CHEST_OPEN.getSound(), 1.0F, 1.0F);
        } else if (args.length == 1) {
            Bukkit.getServer().getScheduler().runTask(plugin, () -> Bukkit.getServer().dispatchCommand(player, "island teleport " + args[0]));
        } else {
            messageManager.sendMessage(player,
                    plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"))
                            .getFileConfiguration().getString("Command.Island.Visit.Invalid.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "visit";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Visit.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"warps", "explore"};
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
