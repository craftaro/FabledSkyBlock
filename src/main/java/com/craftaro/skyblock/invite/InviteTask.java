package com.craftaro.skyblock.invite;

import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.utils.ChatComponent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class InviteTask extends BukkitRunnable {
    private final SkyBlock plugin;
    private final InviteManager inviteManager;

    protected InviteTask(InviteManager inviteManager, SkyBlock plugin) {
        this.inviteManager = inviteManager;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        MessageManager messageManager = this.plugin.getMessageManager();
        SoundManager soundManager = this.plugin.getSoundManager();

        FileConfiguration configLoad = this.plugin.getLanguage();

        for (Player all : Bukkit.getOnlinePlayers()) {
            if (this.inviteManager.hasInvite(all.getUniqueId())) {
                Invite invite = this.inviteManager.getInvite(all.getUniqueId());
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
                        soundManager.playSound(targetPlayer, XSound.ENTITY_VILLAGER_NO);
                    }

                    messageManager.sendMessage(all,
                            configLoad.getString("Command.Island.Invite.Invited.Target.Expired.Message")
                                    .replace("%player", invite.getSenderName()));
                    soundManager.playSound(all, XSound.ENTITY_VILLAGER_NO);

                    this.inviteManager.removeInvite(all.getUniqueId());
                }
            }
        }
    }
}
