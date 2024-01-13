package com.craftaro.skyblock.command.commands.island;

import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.command.SubCommand;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.invite.Invite;
import com.craftaro.skyblock.invite.InviteManager;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.sound.SoundManager;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class DenyCommand extends SubCommand {
    public DenyCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = this.plugin.getMessageManager();
        InviteManager inviteManager = this.plugin.getInviteManager();
        SoundManager soundManager = this.plugin.getSoundManager();

        FileManager.Config config = this.plugin.getFileManager().getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
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
                        soundManager.playSound(targetPlayer, XSound.ENTITY_IRON_GOLEM_ATTACK, 5, 5);
                    }

                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Deny.Denied.Sender.Message")
                            .replace("%player", invite.getSenderName()));
                    soundManager.playSound(player, XSound.ENTITY_IRON_GOLEM_ATTACK, 5, 5);

                    inviteManager.removeInvite(player.getUniqueId());
                } else {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Deny.Invited.Message"));
                    soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);
                }
            } else {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Deny.Invited.Message"));
                soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);
            }
        } else {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Deny.Invalid.Message"));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
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
