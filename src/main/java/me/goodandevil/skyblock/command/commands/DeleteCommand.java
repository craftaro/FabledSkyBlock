package me.goodandevil.skyblock.command.commands;

import java.io.File;

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
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.IslandRole;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.ChatComponent;
import me.goodandevil.skyblock.utils.version.Sounds;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class DeleteCommand extends SubCommand {

	private final SkyBlock skyblock;
	private String info;

	public DeleteCommand(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}

	@Override
	public void onCommandByPlayer(Player player, String[] args) {
		MessageManager messageManager = skyblock.getMessageManager();
		IslandManager islandManager = skyblock.getIslandManager();
		SoundManager soundManager = skyblock.getSoundManager();
		FileManager fileManager = skyblock.getFileManager();

		PlayerData playerData = skyblock.getPlayerDataManager().getPlayerData(player);

		Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		Island island = islandManager.getIsland(player);

		if (island == null) {
			messageManager.sendMessage(player, configLoad.getString("Command.Island.Delete.Owner.Message"));
			soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
		} else if (island.hasRole(IslandRole.Owner, player.getUniqueId())) {
			if (playerData.getConfirmationTime() > 0) {
				messageManager.sendMessage(player,
						configLoad.getString("Command.Island.Delete.Confirmation.Pending.Message"));
				soundManager.playSound(player, Sounds.IRONGOLEM_HIT.bukkitSound(), 1.0F, 1.0F);
			} else {
				int confirmationTime = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
						.getFileConfiguration().getInt("Island.Confirmation.Timeout");

				playerData.setConfirmation(Confirmation.Deletion);
				playerData.setConfirmationTime(confirmationTime);

				String confirmationMessage = configLoad.getString("Command.Island.Delete.Confirmation.Confirm.Message")
						.replace("%time", "" + confirmationTime);

				if (confirmationMessage.contains("%confirm")) {
					String[] confirmationMessages = confirmationMessage.split("%confirm");

					if (confirmationMessages.length == 0) {
						player.spigot().sendMessage(new ChatComponent(
								configLoad.getString("Command.Island.Delete.Confirmation.Confirm.Word.Confirm")
										.toUpperCase(),
								true, ChatColor.RED, new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/island confirm"),
								new HoverEvent(HoverEvent.Action.SHOW_TEXT,
										new ComponentBuilder(ChatColor.translateAlternateColorCodes('&',
												configLoad.getString(
														"Command.Island.Delete.Confirmation.Confirm.Word.Tutorial")))
																.create())).getTextComponent());
					} else {
						ChatComponent chatComponent = new ChatComponent("", false, null, null, null);

						for (int i = 0; i < confirmationMessages.length; i++) {
							String message = confirmationMessages[i];

							if (message.contains("\n") || message.contains("\\n")) {
								message = message.replace("\\n", "\n");

								for (String messageList : message.split("\n")) {
									chatComponent
											.addExtraChatComponent(
													new ChatComponent(
															messageManager.replaceMessage(player,
																	messageList.replace("%time",
																			"" + confirmationTime)),
															false, null, null, null));

									chatComponent
											.addExtra(new TextComponent(ComponentSerializer.parse("{text: \"\n\"}")));
								}
							} else {
								chatComponent.addExtraChatComponent(new ChatComponent(
										messageManager.replaceMessage(player,
												message.replace("%time", "" + confirmationTime)),
										false, null, null, null));
							}

							if (confirmationMessages.length == 1 || i + 1 != confirmationMessages.length) {
								chatComponent.addExtraChatComponent(new ChatComponent(
										configLoad.getString("Command.Island.Delete.Confirmation.Confirm.Word.Confirm")
												.toUpperCase(),
										true, ChatColor.RED,
										new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/island confirm"),
										new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
												ChatColor.translateAlternateColorCodes('&', configLoad.getString(
														"Command.Island.Delete.Confirmation.Confirm.Word.Tutorial")))
																.create())));
							}
						}

						player.spigot().sendMessage(chatComponent.getTextComponent());
					}
				} else {
					messageManager.sendMessage(player, confirmationMessage.replace("%time", "" + confirmationTime));
				}

				soundManager.playSound(player, Sounds.VILLAGER_YES.bukkitSound(), 1.0F, 1.0F);
			}
		} else {
			messageManager.sendMessage(player, configLoad.getString("Command.Island.Delete.Permission.Message"));
			soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
		}
	}

	@Override
	public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
		sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
	}

	@Override
	public String getName() {
		return "delete";
	}

	@Override
	public String getInfo() {
		return info;
	}

	@Override
	public SubCommand setInfo(String info) {
		this.info = info;

		return this;
	}

	@Override
	public String[] getAliases() {
		return new String[] { "remove", "disband" };
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
