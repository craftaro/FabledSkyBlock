package me.goodandevil.skyblock.command.commands.island;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.command.CommandManager.Type;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.confirmation.Confirmation;
import me.goodandevil.skyblock.cooldown.Cooldown;
import me.goodandevil.skyblock.cooldown.CooldownManager;
import me.goodandevil.skyblock.cooldown.CooldownPlayer;
import me.goodandevil.skyblock.cooldown.CooldownType;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.IslandRole;
import me.goodandevil.skyblock.menus.Ownership;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.ChatComponent;
import me.goodandevil.skyblock.utils.NumberUtil;
import me.goodandevil.skyblock.utils.player.OfflinePlayer;
import me.goodandevil.skyblock.utils.version.Sounds;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class OwnerCommand extends SubCommand {

	@Override
	public void onCommandByPlayer(Player player, String[] args) {
		CooldownManager cooldownManager = skyblock.getCooldownManager();
		MessageManager messageManager = skyblock.getMessageManager();
		IslandManager islandManager = skyblock.getIslandManager();
		SoundManager soundManager = skyblock.getSoundManager();
		FileManager fileManager = skyblock.getFileManager();

		PlayerData playerData = skyblock.getPlayerDataManager().getPlayerData(player);

		Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		Island island = islandManager.getIsland(player);

		if (island == null) {
			messageManager.sendMessage(player, configLoad.getString("Command.Island.Ownership.Owner.Message"));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		} else if (args.length == 0) {
			if (island.hasRole(IslandRole.Owner, player.getUniqueId())) {
				playerData.setType(Ownership.Visibility.Hidden);
				Ownership.getInstance().open(player);
				soundManager.playSound(player, Sounds.CHEST_OPEN.bukkitSound(), 1.0F, 1.0F);

				return;
			}
		} else if (args.length == 1) {
			if (island.hasRole(IslandRole.Owner, player.getUniqueId())) {
				if (playerData.getConfirmationTime() > 0) {
					messageManager.sendMessage(player,
							configLoad.getString("Command.Island.Ownership.Confirmation.Pending.Message"));
					soundManager.playSound(player, Sounds.IRONGOLEM_HIT.bukkitSound(), 1.0F, 1.0F);
				} else {
					UUID targetPlayerUUID;
					String targetPlayerName;

					Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);

					if (targetPlayer == null) {
						OfflinePlayer offlinePlayer = new OfflinePlayer(args[0]);
						targetPlayerUUID = offlinePlayer.getUniqueId();
						targetPlayerName = offlinePlayer.getName();
					} else {
						targetPlayerUUID = targetPlayer.getUniqueId();
						targetPlayerName = targetPlayer.getName();
					}

					if (targetPlayerUUID == null || (!island.hasRole(IslandRole.Member, targetPlayerUUID)
							&& !island.hasRole(IslandRole.Operator, targetPlayerUUID)
							&& !island.hasRole(IslandRole.Owner, targetPlayerUUID))) {
						messageManager.sendMessage(player,
								configLoad.getString("Command.Island.Ownership.Member.Message"));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					} else if (targetPlayerUUID.equals(player.getUniqueId())) {
						messageManager.sendMessage(player,
								configLoad.getString("Command.Island.Ownership.Yourself.Message"));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					} else if (cooldownManager.hasPlayer(CooldownType.Ownership,
							Bukkit.getServer().getOfflinePlayer(island.getOwnerUUID()))) {
						CooldownPlayer cooldownPlayer = cooldownManager.getCooldownPlayer(CooldownType.Ownership,
								Bukkit.getServer().getOfflinePlayer(island.getOwnerUUID()));
						Cooldown cooldown = cooldownPlayer.getCooldown();
						long[] durationTime = NumberUtil.getDuration(cooldown.getTime());

						if (cooldown.getTime() >= 3600) {
							messageManager.sendMessage(player, configLoad
									.getString("Command.Island.Ownership.Cooldown.Message")
									.replace("%time", durationTime[1] + " "
											+ configLoad.getString("Command.Island.Ownership.Cooldown.Word.Minute")
											+ " " + durationTime[2] + " "
											+ configLoad.getString("Command.Island.Ownership.Cooldown.Word.Minute")
											+ " " + durationTime[3] + " "
											+ configLoad.getString("Command.Island.Ownership.Cooldown.Word.Second")));
						} else if (cooldown.getTime() >= 60) {
							messageManager.sendMessage(player, configLoad
									.getString("Command.Island.Ownership.Cooldown.Message")
									.replace("%time", durationTime[2] + " "
											+ configLoad.getString("Command.Island.Ownership.Cooldown.Word.Minute")
											+ " " + durationTime[3] + " "
											+ configLoad.getString("Command.Island.Ownership.Cooldown.Word.Second")));
						} else {
							messageManager.sendMessage(player, configLoad
									.getString("Command.Island.Ownership.Cooldown.Message")
									.replace("%time", cooldown.getTime() + " "
											+ configLoad.getString("Command.Island.Ownership.Cooldown.Word.Second")));
						}

						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);

						return;
					} else {
						int confirmationTime = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
								.getFileConfiguration().getInt("Island.Confirmation.Timeout");

						playerData.setOwnership(targetPlayerUUID);
						playerData.setConfirmation(Confirmation.Ownership);
						playerData.setConfirmationTime(confirmationTime);

						String confirmationMessage = configLoad
								.getString("Command.Island.Ownership.Confirmation.Confirm.Message")
								.replace("%time", "" + confirmationTime);

						if (confirmationMessage.contains("%confirm")) {
							String[] confirmationMessages = confirmationMessage.split("%confirm");

							if (confirmationMessages.length == 0) {
								player.spigot()
										.sendMessage(new ChatComponent(configLoad
												.getString("Command.Island.Ownership.Confirmation.Confirm.Word.Confirm")
												.toUpperCase(), true, ChatColor.RED,
												new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/island confirm"),
												new HoverEvent(HoverEvent.Action.SHOW_TEXT,
														new ComponentBuilder(ChatColor.translateAlternateColorCodes('&',
																configLoad.getString(
																		"Command.Island.Ownership.Confirmation.Confirm.Word.Tutorial")))
																				.create())).getTextComponent());
							} else {
								ChatComponent chatComponent = new ChatComponent("", false, null, null, null);

								for (int i = 0; i < confirmationMessages.length; i++) {
									String message = confirmationMessages[i];

									if (message.contains("\n") || message.contains("\\n")) {
										message = message.replace("\\n", "\n");

										for (String messageList : message.split("\n")) {
											chatComponent.addExtraChatComponent(new ChatComponent(
													messageManager.replaceMessage(player,
															messageList.replace("%player", targetPlayerName)
																	.replace("%time", "" + confirmationTime)),
													false, null, null, null));

											chatComponent.addExtra(
													new TextComponent(ComponentSerializer.parse("{text: \"\n\"}")));
										}
									} else {
										chatComponent
												.addExtraChatComponent(new ChatComponent(
														messageManager.replaceMessage(player,
																message.replace("%player", targetPlayerName)
																		.replace("%time", "" + confirmationTime)),
														false, null, null, null));
									}

									if (confirmationMessages.length == 1 || i + 1 != confirmationMessages.length) {
										chatComponent.addExtraChatComponent(new ChatComponent(
												configLoad.getString(
														"Command.Island.Ownership.Confirmation.Confirm.Word.Confirm")
														.toUpperCase(),
												true, ChatColor.RED,
												new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/island confirm"),
												new HoverEvent(HoverEvent.Action.SHOW_TEXT,
														new ComponentBuilder(ChatColor.translateAlternateColorCodes('&',
																configLoad.getString(
																		"Command.Island.Ownership.Confirmation.Confirm.Word.Tutorial")))
																				.create())));
									}
								}

								player.spigot().sendMessage(chatComponent.getTextComponent());
							}
						} else {
							messageManager.sendMessage(player, confirmationMessage.replace("%player", targetPlayerName)
									.replace("%time", "" + confirmationTime));
						}

						soundManager.playSound(player, Sounds.VILLAGER_YES.bukkitSound(), 1.0F, 1.0F);
					}
				}
			} else {
				if (island.hasPassword()) {
					if (args[0].equalsIgnoreCase(island.getPassword())) {
						for (Player all : Bukkit.getOnlinePlayers()) {
							if ((island.hasRole(IslandRole.Member, all.getUniqueId())
									|| island.hasRole(IslandRole.Operator, all.getUniqueId())
									|| island.hasRole(IslandRole.Owner, all.getUniqueId()))
									&& (!all.getUniqueId().equals(player.getUniqueId()))) {
								all.sendMessage(ChatColor.translateAlternateColorCodes('&',
										configLoad.getString("Command.Island.Ownership.Assigned.Broadcast.Message")
												.replace("%player", player.getName())));
								soundManager.playSound(all, Sounds.ANVIL_USE.bukkitSound(), 1.0F, 1.0F);
							}
						}

						messageManager.sendMessage(player,
								configLoad.getString("Command.Island.Ownership.Assigned.Sender.Message"));
						soundManager.playSound(player, Sounds.ANVIL_USE.bukkitSound(), 1.0F, 1.0F);

						islandManager.giveOwnership(island, player);

						cooldownManager.createPlayer(CooldownType.Ownership,
								Bukkit.getServer().getOfflinePlayer(island.getOwnerUUID()));
					} else {
						messageManager.sendMessage(player,
								configLoad.getString("Command.Island.Ownership.Password.Incorrect.Message"));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					}
				} else {
					messageManager.sendMessage(player,
							configLoad.getString("Command.Island.Ownership.Password.Unset.Message"));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
				}
			}

			return;
		} else {
			messageManager.sendMessage(player, configLoad.getString("Command.Island.Ownership.Invalid.Message"));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		}
	}

	@Override
	public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
		sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
	}

	@Override
	public String getName() {
		return "owner";
	}

	@Override
	public String getInfo() {
		return info;
	}

	@Override
	public String[] getAliases() {
		return new String[] { "ownership", "transfer", "makeleader", "makeowner" };
	}

	@Override
	public String[] getArguments() {
		return new String[0];
	}

	@Override
	public Type getType() {
		return CommandManager.Type.Default;
	}
}
