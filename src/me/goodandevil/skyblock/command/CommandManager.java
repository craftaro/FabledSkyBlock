package me.goodandevil.skyblock.command;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.command.commands.AcceptCommand;
import me.goodandevil.skyblock.command.commands.BanCommand;
import me.goodandevil.skyblock.command.commands.BansCommand;
import me.goodandevil.skyblock.command.commands.BiomeCommand;
import me.goodandevil.skyblock.command.commands.CancelCommand;
import me.goodandevil.skyblock.command.commands.ChatCommand;
import me.goodandevil.skyblock.command.commands.CloseCommand;
import me.goodandevil.skyblock.command.commands.ConfirmCommand;
import me.goodandevil.skyblock.command.commands.ControlPanelCommand;
import me.goodandevil.skyblock.command.commands.CreateCommand;
import me.goodandevil.skyblock.command.commands.CurrentCommand;
import me.goodandevil.skyblock.command.commands.DeleteCommand;
import me.goodandevil.skyblock.command.commands.DemoteCommand;
import me.goodandevil.skyblock.command.commands.DenyCommand;
import me.goodandevil.skyblock.command.commands.InviteCommand;
import me.goodandevil.skyblock.command.commands.KickAllCommand;
import me.goodandevil.skyblock.command.commands.KickCommand;
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
import me.goodandevil.skyblock.command.commands.VisitCommand;
import me.goodandevil.skyblock.command.commands.VisitorsCommand;
import me.goodandevil.skyblock.command.commands.WeatherCommand;
import me.goodandevil.skyblock.command.commands.admin.GeneratorCommand;
import me.goodandevil.skyblock.command.commands.admin.ReloadCommand;
import me.goodandevil.skyblock.command.commands.admin.StructureCommand;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.menus.ControlPanel;
import me.goodandevil.skyblock.menus.Creator;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.ChatComponent;
import me.goodandevil.skyblock.utils.version.Sounds;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class CommandManager implements CommandExecutor, TabCompleter {
	
	private final Main plugin;
	private HashMap<CommandManager.Type, List<SubCommand>> subCommands = new HashMap<>();
	
	public CommandManager(Main plugin) {
		this.plugin = plugin;
		
		plugin.getCommand("island").setExecutor(this);
		plugin.getCommand("island").setTabCompleter(this);
		
		registerSubCommands();
	}
	
	public void registerSubCommands() {
		Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		List<SubCommand> subCommands = new ArrayList<>();
		subCommands.add(new VisitCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Visit.Info.Message"))));
		subCommands.add(new ControlPanelCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.ControlPanel.Info.Message"))));
		subCommands.add(new CreateCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Create.Info.Message"))));
		subCommands.add(new DeleteCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Delete.Info.Message"))));
		subCommands.add(new TeleportCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Teleport.Info.Message"))));
		subCommands.add(new SetSpawnCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.SetSpawn.Info.Message"))));
		subCommands.add(new AcceptCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Accept.Info.Message"))));
		subCommands.add(new DenyCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Deny.Info.Message"))));
		subCommands.add(new CancelCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Cancel.Info.Message"))));
		subCommands.add(new LeaveCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Leave.Info.Message"))));
		subCommands.add(new PromoteCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Promote.Info.Message"))));
		subCommands.add(new DemoteCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Demote.Info.Message"))));
		subCommands.add(new KickCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Kick.Info.Message"))));
		subCommands.add(new KickAllCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.KickAll.Info.Message"))));
		subCommands.add(new BanCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Ban.Info.Message"))));
		subCommands.add(new BansCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Bans.Info.Message"))));
		subCommands.add(new UnbanCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Unban.Info.Message"))));
		subCommands.add(new BiomeCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Biome.Info.Message"))));
		subCommands.add(new WeatherCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Weather.Info.Message"))));
		//subCommands.add(new RollbackCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Rollback.Info.Message"))));
		subCommands.add(new LevelCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Level.Info.Message"))));
		subCommands.add(new SettingsCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Settings.Info.Message"))));
		subCommands.add(new MembersCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Members.Info.Message"))));
		subCommands.add(new OwnerCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Ownership.Info.Message"))));
		subCommands.add(new ConfirmCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Confirmation.Info.Message"))));
		subCommands.add(new InviteCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Invite.Info.Message"))));
		subCommands.add(new ChatCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Chat.Info.Message"))));
		subCommands.add(new VisitorsCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Visitors.Info.Message"))));
		subCommands.add(new CurrentCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Current.Info.Message"))));
		subCommands.add(new PublicCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Public.Info.Message"))));
		subCommands.add(new OpenCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Open.Info.Message"))));
		subCommands.add(new CloseCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Close.Info.Message"))));
		
		this.subCommands.put(CommandManager.Type.Default, subCommands);
		
		subCommands = new ArrayList<>();
		subCommands.add(new me.goodandevil.skyblock.command.commands.admin.LevelCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Admin.Level.Info.Message"))));
		subCommands.add(new me.goodandevil.skyblock.command.commands.admin.SetSpawnCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Admin.SetSpawn.Info.Message"))));
		subCommands.add(new StructureCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Admin.Structure.Info.Message"))));
		subCommands.add(new me.goodandevil.skyblock.command.commands.admin.CreateCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Admin.Create.Info.Message"))));
		subCommands.add(new GeneratorCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Admin.Generator.Info.Message"))));
		subCommands.add(new DeleteCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Admin.Delete.Info.Message"))));
		subCommands.add(new OwnerCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Admin.Owner.Info.Message"))));
		subCommands.add(new ReloadCommand(plugin).setInfo(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Admin.Reload.Info.Message"))));
		
		this.subCommands.put(CommandManager.Type.Admin, subCommands);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (command.getName().equalsIgnoreCase("island")) {
			new BukkitRunnable() {
				@Override
				public void run() {
					SoundManager soundManager = plugin.getSoundManager();
					FileManager fileManager = plugin.getFileManager();
					
					Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
					FileConfiguration configLoad = config.getFileConfiguration();
					
					if (args.length == 0) {
						if (plugin.getIslandManager().hasIsland(player)) {
							ControlPanel.getInstance().open(player);
						} else {
							Creator.getInstance().open(player);
						}
						
						soundManager.playSound(player, Sounds.CHEST_OPEN.bukkitSound(), 1.0F, 1.0F);
						
						return;
					}
					
					SubCommand subCommand;
					
					if (args[0].equalsIgnoreCase("help")) {
						int page = -1;
						
						if (!fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Command.Help.List")) {
							page = 1;
							
							if (args.length == 2) {
								if (args[1].matches("[0-9]+")) {
									page = Integer.valueOf(args[1]);
								} else {
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Help.Integer.Message")));
									soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
									
									return;
								}
							}
						}
						
						sendHelpCommands(player, CommandManager.Type.Default, page);
						
						return;
					} else if (args[0].equalsIgnoreCase("admin")) {
						if (args.length == 1 || (args.length >= 2 && args[1].equalsIgnoreCase("help"))) {
							int page = -1;
							
							if (!fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Command.Help.List")) {
								page = 1;
								
								if (args.length == 3) {
									if (args[2].matches("[0-9]+")) {
										page = Integer.valueOf(args[2]);
									} else {
										player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Help.Integer.Message")));
										soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
										
										return;
									}
								}
							}
							
							sendHelpCommands(player, CommandManager.Type.Admin, page);

							return;
						}
						
						subCommand = getSubCommand(CommandManager.Type.Admin, args[1]);
					} else {
						subCommand = getSubCommand(CommandManager.Type.Default, args[0]);
					}
					
					if (subCommand == null) {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Argument.Unrecognised.Message")));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
					} else {
						ArrayList<String> arguments = new ArrayList<>();
						arguments.addAll(Arrays.asList(args));
						arguments.remove(args[0]);
						
						if (subCommand.getType() == CommandManager.Type.Admin) {
							arguments.remove(args[1]);
						}
						
						subCommand.onCommand(player, arguments.toArray(new String[0]));
					}
				}
			}.runTaskAsynchronously(plugin);
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
				commandAliases.add("admin");
				
				for (SubCommand subCommandList : subCommands.get(Type.Default)) {
					commandAliases.add(subCommandList.getName());
				}
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("admin")) {
					for (SubCommand subCommandList : subCommands.get(Type.Admin)) {
						commandAliases.add(subCommandList.getName());
					}
				}
			} else if (args.length == 3) {
				if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("structure")) {
					commandAliases.add("tool");
					commandAliases.add("save");
				}
			}
			
			if (commandAliases.size() != 0) {
				return commandAliases;
			}
		}
		
		return null;
	}
	
	public void sendHelpCommands(Player player, CommandManager.Type type, int page) {
		FileManager fileManager = plugin.getFileManager();
		
		Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		int pageSize = 7;
		
		if (page == -1) {
			pageSize = 1000;
		}
		
		int nextEndIndex = subCommands.get(type).size() - page * pageSize, index = page * pageSize - pageSize, endIndex = index >= subCommands.get(type).size() ? subCommands.get(type).size() - 1 : index + pageSize;
		boolean showAlises = fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Command.Help.Aliases.Enable");
		
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
			} else {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', helpLines));
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
		
		plugin.getSoundManager().playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);
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
