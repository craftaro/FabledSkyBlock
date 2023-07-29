package com.songoda.skyblock.command.commands.island;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.menus.Members;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.sound.SoundManager;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class MembersCommand extends SubCommand {
    public MembersCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        SoundManager soundManager = this.plugin.getSoundManager();

        if (this.plugin.getIslandManager().getIsland(player) == null) {
            this.plugin.getMessageManager().sendMessage(player,
                    this.plugin.getFileManager().getConfig(new File(this.plugin.getDataFolder(), "language.yml"))
                            .getFileConfiguration().getString("Command.Island.Settings.Owner.Message"));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
        } else {
            PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player);
            playerData.setType(Members.Type.DEFAULT);
            playerData.setSort(Members.Sort.DEFAULT);

            Members.getInstance().open(player, (Members.Type) playerData.getType(), (Members.Sort) playerData.getSort());
            soundManager.playSound(player, XSound.BLOCK_CHEST_OPEN);
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
