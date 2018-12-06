package me.goodandevil.skyblock.command;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.command.commands.AcceptCommand;
import me.goodandevil.skyblock.command.commands.BanCommand;
import me.goodandevil.skyblock.command.commands.BansCommand;
import me.goodandevil.skyblock.command.commands.BiomeCommand;
import me.goodandevil.skyblock.command.commands.CancelCommand;
import me.goodandevil.skyblock.command.commands.ChatCommand;
import me.goodandevil.skyblock.command.commands.CloseCommand;
import me.goodandevil.skyblock.command.commands.ConfirmCommand;
import me.goodandevil.skyblock.command.commands.ControlPanelCommand;
import me.goodandevil.skyblock.command.commands.CoopCommand;
import me.goodandevil.skyblock.command.commands.CreateCommand;
import me.goodandevil.skyblock.command.commands.CurrentCommand;
import me.goodandevil.skyblock.command.commands.DeleteCommand;
import me.goodandevil.skyblock.command.commands.DemoteCommand;
import me.goodandevil.skyblock.command.commands.DenyCommand;
import me.goodandevil.skyblock.command.commands.InformationCommand;
import me.goodandevil.skyblock.command.commands.InviteCommand;
import me.goodandevil.skyblock.command.commands.KickAllCommand;
import me.goodandevil.skyblock.command.commands.KickCommand;
import me.goodandevil.skyblock.command.commands.LeaderboardCommand;
import me.goodandevil.skyblock.command.commands.LeaveCommand;
import me.goodandevil.skyblock.command.commands.LevelCommand;
import me.goodandevil.skyblock.command.commands.MembersCommand;
import me.goodandevil.skyblock.command.commands.OpenCommand;
import me.goodandevil.skyblock.command.commands.OwnerCommand;
import me.goodandevil.skyblock.command.commands.PromoteCommand;
import me.goodandevil.skyblock.command.commands.PublicCommand;
import me.goodandevil.skyblock.command.commands.SetSpawnCommand;
import me.goodandevil.skyblock.command.commands.SettingsCommand;
import me.goodandevil.skyblock.command.commands.TeleportCommand;
import me.goodandevil.skyblock.command.commands.UnbanCommand;
import me.goodandevil.skyblock.command.commands.UpgradeCommand;
import me.goodandevil.skyblock.command.commands.VisitCommand;
import me.goodandevil.skyblock.command.commands.VisitorsCommand;
import me.goodandevil.skyblock.command.commands.VoteCommand;
import me.goodandevil.skyblock.command.commands.WeatherCommand;
import me.goodandevil.skyblock.command.commands.admin.GeneratorCommand;
import me.goodandevil.skyblock.command.commands.admin.ReloadCommand;
import me.goodandevil.skyblock.command.commands.admin.RemoveHologramCommand;
import me.goodandevil.skyblock.command.commands.admin.SetHologramCommand;
import me.goodandevil.skyblock.command.commands.admin.SetSizeCommand;
import me.goodandevil.skyblock.command.commands.admin.StructureCommand;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.menus.ControlPanel;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.ChatComponent;
import me.goodandevil.skyblock.utils.version.Sounds;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class CommandManager implements CommandExecutor, TabCompleter {
	
	private final SkyBlock skyblock;
	private HashMap<CommandManager.Type, List<SubCommand>> subCommands = new HashMap<>();
	
	public CommandManager(SkyBlock skyblock) {
		this.skyblock = skyblock;
		
		skyblock.getCommand("island").setExecutor(this);
		skyblock.getCommand("island").setTabCompleter(this);
		
		registerSubCommands();
	}
	
	public void registerSubCommands() {
		Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		List<SubCommand> subCommands = new ArrayList<>();
		subCommands.add(new VisitCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Visit.Info.Message"))));
		subCommands.add(new VoteCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Vote.Info.Message"))));
		subCommands.add(new ControlPanelCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.ControlPanel.Info.Message"))));
		subCommands.add(new UpgradeCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Upgrade.Info.Message"))));
		subCommands.add(new LeaderboardCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Leaderboard.Info.Message"))));
		subCommands.add(new CreateCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Create.Info.Message"))));
		subCommands.add(new DeleteCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Delete.Info.Message"))));
		subCommands.add(new TeleportCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Teleport.Info.Message"))));
		subCommands.add(new SetSpawnCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.SetSpawn.Info.Message"))));
		subCommands.add(new AcceptCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Accept.Info.Message"))));
		subCommands.add(new DenyCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Deny.Info.Message"))));
		subCommands.add(new CancelCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Cancel.Info.Message"))));
		subCommands.add(new LeaveCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Leave.Info.Message"))));
		subCommands.add(new PromoteCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Promote.Info.Message"))));
		subCommands.add(new DemoteCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Demote.Info.Message"))));
		subCommands.add(new KickCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Kick.Info.Message"))));
		subCommands.add(new KickAllCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.KickAll.Info.Message"))));
		subCommands.add(new BanCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Ban.Info.Message"))));
		subCommands.add(new BansCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Bans.Info.Message"))));
		subCommands.add(new UnbanCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Unban.Info.Message"))));
		subCommands.add(new BiomeCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Biome.Info.Message"))));
		subCommands.add(new WeatherCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Weather.Info.Message"))));
		//subCommands.add(new RollbackCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Rollback.Info.Message"))));
		subCommands.add(new LevelCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Level.Info.Message"))));
		subCommands.add(new SettingsCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Settings.Info.Message"))));
		subCommands.add(new InformationCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Information.Info.Message"))));
		subCommands.add(new CoopCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Coop.Info.Message"))));
		subCommands.add(new MembersCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Members.Info.Message"))));
		subCommands.add(new OwnerCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Ownership.Info.Message"))));
		subCommands.add(new ConfirmCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Confirmation.Info.Message"))));
		subCommands.add(new InviteCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Invite.Info.Message"))));
		subCommands.add(new ChatCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Chat.Info.Message"))));
		subCommands.add(new VisitorsCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Visitors.Info.Message"))));
		subCommands.add(new CurrentCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Current.Info.Message"))));
		subCommands.add(new PublicCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Public.Info.Message"))));
		subCommands.add(new OpenCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Open.Info.Message"))));
		subCommands.add(new CloseCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Close.Info.Message"))));
		
		this.subCommands.put(CommandManager.Type.Default, subCommands);
		
		subCommands = new ArrayList<>();
		subCommands.add(new me.goodandevil.skyblock.command.commands.admin.SetSpawnCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Admin.SetSpawn.Info.Message"))));
		subCommands.add(new SetHologramCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Admin.SetHologram.Info.Message"))));
		subCommands.add(new RemoveHologramCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Admin.RemoveHologram.Info.Message"))));
		subCommands.add(new SetSizeCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Admin.SetSize.Info.Message"))));
		subCommands.add(new me.goodandevil.skyblock.command.commands.admin.CreateCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Admin.Create.Info.Message"))));
		subCommands.add(new me.goodandevil.skyblock.command.commands.admin.UpgradeCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Admin.Upgrade.Info.Message"))));
		subCommands.add(new GeneratorCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Admin.Generator.Info.Message"))));
		subCommands.add(new me.goodandevil.skyblock.command.commands.admin.LevelCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Admin.Level.Info.Message"))));
		subCommands.add(new me.goodandevil.skyblock.command.commands.admin.SettingsCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Admin.Settings.Info.Message"))));
		subCommands.add(new StructureCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Admin.Structure.Info.Message"))));
		subCommands.add(new me.goodandevil.skyblock.command.commands.admin.DeleteCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Admin.Delete.Info.Message"))));
		subCommands.add(new OwnerCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Admin.Owner.Info.Message"))));
		subCommands.add(new ReloadCommand(skyblock).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Admin.Reload.Info.Message"))));
		
		this.subCommands.put(CommandManager.Type.Admin, subCommands);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (command.getName().equalsIgnoreCase("island")) {
			new BukkitRunnable() {
				@Override
				public void run() {
					MessageManager messageManager = skyblock.getMessageManager();
					SoundManager soundManager = skyblock.getSoundManager();
					FileManager fileManager = skyblock.getFileManager();
					
					Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
					FileConfiguration configLoad = config.getFileConfiguration();
					
					Player player = null;
					
					if (sender instanceof Player) {
						player = (Player) sender;
					}
					
					if (args.length == 0) {
						if (player == null) {
							sendConsoleHelpCommands(sender);
						} else {
							if (skyblock.getIslandManager().hasIsland(player)) {
								ControlPanel.getInstance().open(player);
								soundManager.playSound(player, Sounds.CHEST_OPEN.bukkitSound(), 1.0F, 1.0F);
							} else {
								Bukkit.getServer().getScheduler().runTask(skyblock, new Runnable() {
									@Override
									public void run() {
										Bukkit.getServer().dispatchCommand(sender, "island create");
									}
								});
							}
						}
						
						return;
					}
					
					SubCommand subCommand;
					
					if (args[0].equalsIgnoreCase("help")) {
						if (player == null) {
							sendConsoleHelpCommands(sender);
						} else {
							int page = -1;
							
							if (!fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Command.Help.List")) {
								page = 1;
								
								if (args.length == 2) {
									if (args[1].matches("[0-9]+")) {
										page = Integer.valueOf(args[1]);
									} else {
										messageManager.sendMessage(player, configLoad.getString("Command.Island.Help.Integer.Message"));
										soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
										
										return;
									}
								}
							}
							
							sendPlayerHelpCommands(player, CommandManager.Type.Default, page);
						}
						
						return;
					} else if (args[0].equalsIgnoreCase("admin")) {
						if (args.length == 1 || (args.length >= 2 && args[1].equalsIgnoreCase("help"))) {
							if (player == null || player.hasPermission("skyblock.admin") || player.hasPermission("skyblock.admin.*") || player.hasPermission("skyblock.*")) {
								if (player == null) {
									sendConsoleHelpCommands(sender);
								} else {
									int page = -1;
									
									if (!fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Command.Help.List")) {
										page = 1;
										
										if (args.length == 3) {
											if (args[2].matches("[0-9]+")) {
												page = Integer.valueOf(args[2]);
											} else {
												messageManager.sendMessage(player, configLoad.getString("Command.Island.Help.Integer.Message"));
												soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
												
												return;
											}
										}
									}
									
									sendPlayerHelpCommands(player, CommandManager.Type.Admin, page);
								}
							} else {
								messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.Help.Permission.Message"));
								soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
							}

							return;
						}
						
						subCommand = getSubCommand(CommandManager.Type.Admin, args[1]);
					} else {
						subCommand = getSubCommand(CommandManager.Type.Default, args[0]);
					}
					
					if (subCommand == null) {
						messageManager.sendMessage(sender, configLoad.getString("Command.Island.Argument.Unrecognised.Message"));
						soundManager.playSound(sender, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
					} else {
						ArrayList<String> arguments = new ArrayList<>();
						arguments.addAll(Arrays.asList(args));
						arguments.remove(args[0]);
						
						if (subCommand.getType() == CommandManager.Type.Admin) {
							arguments.remove(args[1]);
						}
						
						if (sender instanceof Player) {
							subCommand.onCommandByPlayer(player, arguments.toArray(new String[0]));
						} else if (sender instanceof ConsoleCommandSender) {
							subCommand.onCommandByConsole((ConsoleCommandSender)sender, arguments.toArray(new String[0]));
						}
					}
				}
			}.runTaskAsynchronously(skyblock);
		}
		
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
		if (!(sender instanceof Player)) {
			return null;
		}
		
		if (command.getName().equalsIgnoreCase("island")) {
			List<String> commandAliases = new ArrayList<>();
			
			if (args.length == 1) {
				if (args[0] == null || args[0].isEmpty()) {
					commandAliases.add("admin");
					
					for (SubCommand subCommandList : subCommands.get(Type.Default)) {
						commandAliases.add(subCommandList.getName());
					}
				} else {
					if (sender.hasPermission("skyblock.admin") || sender.hasPermission("skyblock.admin.*") || sender.hasPermission("skyblock.*")) {
						if ("admin".contains(args[0].toLowerCase())) {
							commandAliases.add("admin");
						}
					}
					
					for (SubCommand subCommandList : subCommands.get(Type.Default)) {
						if (subCommandList.getName().toLowerCase().contains(args[0].toLowerCase())) {
							commandAliases.add(subCommandList.getName());
						}
					}
				}
			} else if (args.length == 2) {
				if (sender.hasPermission("skyblock.admin") || sender.hasPermission("skyblock.admin.*") || sender.hasPermission("skyblock.*")) {
					if (args[0].equalsIgnoreCase("admin")) {
						if (args[1] == null || args[1].isEmpty()) {
							for (SubCommand subCommandList : subCommands.get(Type.Admin)) {
								commandAliases.add(subCommandList.getName());
							}
						} else {
							for (SubCommand subCommandList : subCommands.get(Type.Admin)) {
								if (subCommandList.getName().toLowerCase().contains(args[1].toLowerCase())) {
									commandAliases.add(subCommandList.getName());
								}
							}
						}
					}
				}
				
				List<String> arguments = getArguments(Type.Default, args[0], args[1]);
				
				if (arguments.size() != 0) {
					commandAliases.addAll(arguments);
				}
			} else if (args.length == 3) {
				if (sender.hasPermission("skyblock.admin") || sender.hasPermission("skyblock.admin.*") || sender.hasPermission("skyblock.*")) {
					if (args[0].equalsIgnoreCase("admin")) {
						List<String> arguments = getArguments(Type.Admin, args[1], args[2]);
						
						if (arguments.size() != 0) {
							commandAliases.addAll(arguments);
						}
					}
				}
			}
			
			if (commandAliases.size() != 0) {
				return commandAliases;
			}
		}
		
		return null;
	}
	
	public List<String> getArguments(Type type, String arg1, String arg2) {
		List<String> arguments = new ArrayList<>();
		
		for (SubCommand subCommandList : subCommands.get(type)) {
			if (arg1.equalsIgnoreCase(subCommandList.getName())) {
				if (arg2 == null || arg2.isEmpty()) {
					arguments.addAll(Arrays.asList(subCommandList.getArguments()));
				} else {
					for (String argumentList : subCommandList.getArguments()) {
						if (argumentList.contains(arg2.toLowerCase())) {
							arguments.add(argumentList);
							
							break;
						}
					}
				}
				
				break;
			}
		}
		
		return arguments;
	}
	
	public void sendPlayerHelpCommands(Player player, CommandManager.Type type, int page) {
		FileManager fileManager = skyblock.getFileManager();
		
		Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		int pageSize = 7;
		
		int nextEndIndex = subCommands.get(type).size() - page * pageSize, index = page * pageSize - pageSize, endIndex = index >= subCommands.get(type).size() ? subCommands.get(type).size() - 1 : index + pageSize;
		boolean showAlises = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Command.Help.Aliases.Enable");
		
		String subCommandText = "";
		
		if (type == CommandManager.Type.Admin) {
			subCommandText = "admin ";
		}
		
		for (String helpLines : configLoad.getStringList("Command.Island.Help.Lines")) {
			if (helpLines.contains("%type")) {
				helpLines = helpLines.replace("%type", type.name());
			}
			
			if (helpLines.contains("%commands")) {
				String[] sections = helpLines.split("%commands");
				String prefix = "", suffix = "";
				
				if (sections.length >= 1) {
					prefix = ChatColor.translateAlternateColorCodes('&', sections[0]);
				}
				
				if (sections.length == 2) {
					suffix = ChatColor.translateAlternateColorCodes('&', sections[1]);
				}
				
				if (page == -1) {
					for (int i = 0; i < subCommands.get(type).size(); i++) {
						SubCommand subCommandFromIndex = subCommands.get(type).get(i);
						String commandAliases = "";
						
						if (showAlises) {
							for (int i1 = 0; i1 < subCommandFromIndex.getAliases().length; i1++) {
								if (i1 == 0) {
									commandAliases = "/";
								}
								
								if (i1 == subCommandFromIndex.getAliases().length-1) {
									commandAliases = commandAliases + subCommandFromIndex.getAliases()[i1];
								} else {
									commandAliases = commandAliases + subCommandFromIndex.getAliases()[i1] + "/";
								}
							}
						}
						
						player.spigot().sendMessage(new ChatComponent(prefix.replace("%info", subCommandFromIndex.getInfo()) + "/island " + subCommandText + subCommandFromIndex.getName() + commandAliases + suffix.replace("%info", subCommandFromIndex.getInfo()), false, null, null, new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(subCommandFromIndex.getInfo()).create())).getTextComponent());
					}
				} else {
					for (; index < endIndex; index++) {
						if (subCommands.get(type).size() > index) {
							SubCommand subCommandFromIndex = subCommands.get(type).get(index);
							String commandAliases = "";
							
							if (showAlises) {
								for (int i = 0; i < subCommandFromIndex.getAliases().length; i++) {
									if (i == 0) {
										commandAliases = "/";
									}
									
									if (i == subCommandFromIndex.getAliases().length) {
										commandAliases = commandAliases + subCommandFromIndex.getAliases()[i];
									} else {
										commandAliases = commandAliases + subCommandFromIndex.getAliases()[i] + "/";
									}
								}
							}
							
							player.spigot().sendMessage(new ChatComponent(prefix.replace("%info", subCommandFromIndex.getInfo()) + "/island " + subCommandText + subCommandFromIndex.getName() + commandAliases + suffix.replace("%info", subCommandFromIndex.getInfo()), false, null, null, new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(subCommandFromIndex.getInfo()).create())).getTextComponent());
						}
					}	
				}
			} else {
				skyblock.getMessageManager().sendMessage(player, helpLines);
			}
		}
		
		if (page != -1) {
			if (!(nextEndIndex == 0 || nextEndIndex < 0)) {
				if (page == 1) {
					player.spigot().sendMessage(new ChatComponent(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Help.Word.Next")), false, null, new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/island " + subCommandText + "help " + (page + 1)), null).getTextComponent());
				} else {
					player.spigot().sendMessage(new ChatComponent(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Help.Word.Previous")), false, null, new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/island " + subCommandText + "help " + (page - 1)), null).addExtraChatComponent(new ChatComponent(" " + ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Help.Word.Pipe")) + " ", false, null, null, null)).addExtraChatComponent(new ChatComponent(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Help.Word.Next")), false, null, new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/island " + subCommandText + "help " + (page + 1)), null)).getTextComponent());
				}
			} else {
				if (page != 1) {
					player.spigot().sendMessage(new ChatComponent(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Help.Word.Previous")), false, null, new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/island " + subCommandText + "help " + (page - 1)), null).getTextComponent());
				}
			}	
		}
		
		skyblock.getSoundManager().playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);
	}
	
	public void sendConsoleHelpCommands(CommandSender sender) {
		sender.sendMessage("SkyBlock - Console Commands");
		
		String[] commands = { "delete", "owner", "reload", "removehologram", "setsize" };
		
		for (String commandList : commands) {
			SubCommand subCommand = getSubCommand(CommandManager.Type.Admin, commandList);
			sender.sendMessage("* /island admin " + subCommand.getName() + " - " + subCommand.getInfo());
		}
	}
	
	public SubCommand getSubCommand(CommandManager.Type type, String arg) {
		for (SubCommand subCommandList : subCommands.get(type)) {
			if (subCommandList.getName().equalsIgnoreCase(arg)) {
				return subCommandList;
			}
			
			for (String argList : subCommandList.getAliases()) {
				if (argList.equalsIgnoreCase(arg)) {
					return subCommandList;
				}
			}
		}
		
		return null;
	}
	
	public enum Type {
		
		Default,
		Admin;
		
	}
}
