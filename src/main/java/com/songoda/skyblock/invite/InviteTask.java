package com.songoda.skyblock.invite;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.ChatComponent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public class InviteTask extends BukkitRunnable {

    private final SkyBlock plugin;
    private final InviteManager inviteManager;

    protected InviteTask(InviteManager inviteManager, SkyBlock plugin) {
        this.inviteManager = inviteManager;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        MessageManager messageManager = plugin.getMessageManager();
        SoundManager soundManager = plugin.getSoundManager();

        FileConfiguration configLoad = this.plugin.getLanguage();

        for (Player all : Bukkit.getOnlinePlayers()) {
            if (inviteManager.hasInvite(all.getUniqueId())) {
                Invite invite = inviteManager.getInvite(all.getUniqueId());
                invite.setTime(invite.getTime() - 1);

                if (invite.getTime() == 0) {
                    Player targetPlayer = Bukkit.getServer().getPlayer(invite.getOwnerUUID());

                    if (targetPlayer != null) {
                        targetPlayer.spigot()
                                .sendMessage(new ChatComponent(
                                        messageManager.replaceMessage(targetPlayer,
                                                configLoad
                                                        .getString(
                                                                "Command.Island.Invite.Invited.Sender.Expired.Message")
                                                        .replace("%player", all.getName()) + "   "),
                                        false, null, null, null)
                                        .addExtra(new ChatComponent(
                                                configLoad
                                                        .getString("Command.Island.Invite.Invited.Word.Resend")
                                                        .toUpperCase(),
                                                true, ChatColor.AQUA,
                                                new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                        "/island invite " + all.getName()),
                                                new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                        new ComponentBuilder(ChatColor
                                                                .translateAlternateColorCodes('&', configLoad
                                                                        .getString(
                                                                                "Command.Island.Invite.Invited.Word.Tutorial")
                                                                        .replace("%action",
                                                                                configLoad.getString(
                                                                                        "Command.Island.Invite.Invited.Word.Resend"))))
                                                                .create()))));
                        soundManager.playSound(targetPlayer,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
                    }

                    messageManager.sendMessage(all,
                            configLoad.getString("Command.Island.Invite.Invited.Target.Expired.Message")
                                    .replace("%player", invite.getSenderName()));
                    soundManager.playSound(all,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                    inviteManager.removeInvite(all.getUniqueId());
                }
            }
        }
    }
}
