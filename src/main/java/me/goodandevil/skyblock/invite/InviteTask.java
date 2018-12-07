package me.goodandevil.skyblock.invite;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.ChatComponent;
import me.goodandevil.skyblock.utils.version.Sounds;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class InviteTask extends BukkitRunnable {

	private final SkyBlock skyblock;
	private final InviteManager inviteManager;

	protected InviteTask(InviteManager inviteManager, SkyBlock skyblock) {
		this.inviteManager = inviteManager;
		this.skyblock = skyblock;
	}

	@Override
	public void run() {
		MessageManager messageManager = skyblock.getMessageManager();
		SoundManager soundManager = skyblock.getSoundManager();

		Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

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
						soundManager.playSound(targetPlayer, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
					}

					messageManager.sendMessage(all,
							configLoad.getString("Command.Island.Invite.Invited.Target.Expired.Message")
									.replace("%player", invite.getSenderName()));
					soundManager.playSound(all, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);

					inviteManager.removeInvite(all.getUniqueId());
				}
			}
		}
	}
}
