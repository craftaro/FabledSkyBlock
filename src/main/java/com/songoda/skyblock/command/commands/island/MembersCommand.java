package com.songoda.skyblock.command.commands.island;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.menus.Members;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.sound.SoundManager;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class MembersCommand extends SubCommand {

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        SoundManager soundManager = plugin.getSoundManager();

        if (plugin.getIslandManager().getIsland(player) == null) {
            plugin.getMessageManager().sendMessage(player,
                    plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"))
                            .getFileConfiguration().getString("Command.Island.Settings.Owner.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
        } else {
            PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
            playerData.setType(Members.Type.Default);
            playerData.setSort(Members.Sort.Default);

            Members.getInstance().open(player, (Members.Type) playerData.getType(),
                    (Members.Sort) playerData.getSort());
            soundManager.playSound(player, CompatibleSound.BLOCK_CHEST_OPEN.getSound(), 1.0F, 1.0F);
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "members";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Members.Info.Message";
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
