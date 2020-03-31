package com.songoda.skyblock.command.commands.island;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.invite.Invite;
import com.songoda.skyblock.invite.InviteManager;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class DenyCommand extends SubCommand {

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = skyblock.getMessageManager();
        InviteManager inviteManager = skyblock.getInviteManager();
        SoundManager soundManager = skyblock.getSoundManager();

        Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length == 1) {
            if (inviteManager.hasInvite(player.getUniqueId())) {
                Invite invite = inviteManager.getInvite(player.getUniqueId());
                String playerName = args[0];

                if (invite.getSenderName().equalsIgnoreCase(playerName)) {
                    Player targetPlayer = Bukkit.getServer().getPlayer(invite.getSenderUUID());

                    if (targetPlayer != null) {
                        messageManager.sendMessage(targetPlayer,
                                configLoad.getString("Command.Island.Deny.Denied.Target.Message").replace("%player",
                                        player.getName()));
                        soundManager.playSound(targetPlayer, CompatibleSound.ENTITY_IRON_GOLEM_ATTACK.getSound(), 5.0F, 5.0F);
                    }

                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Deny.Denied.Sender.Message")
                            .replace("%player", invite.getSenderName()));
                    soundManager.playSound(player, CompatibleSound.ENTITY_IRON_GOLEM_ATTACK.getSound(), 5.0F, 5.0F);

                    inviteManager.removeInvite(player.getUniqueId());
                } else {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Deny.Invited.Message"));
                    soundManager.playSound(player,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
                }
            } else {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Deny.Invited.Message"));
                soundManager.playSound(player,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
            }
        } else {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Deny.Invalid.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "deny";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Deny.Info.Message";
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
