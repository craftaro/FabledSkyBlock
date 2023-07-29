package com.songoda.skyblock.command;

import com.craftaro.core.compatibility.CompatibleSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.command.commands.admin.AddUpgradeCommand;
import com.songoda.skyblock.command.commands.admin.AdminBank;
import com.songoda.skyblock.command.commands.admin.ChatSpyCommand;
import com.songoda.skyblock.command.commands.admin.GeneratorCommand;
import com.songoda.skyblock.command.commands.admin.LevelScanCommand;
import com.songoda.skyblock.command.commands.admin.ProxyCommand;
import com.songoda.skyblock.command.commands.admin.RefreshHologramsCommand;
import com.songoda.skyblock.command.commands.admin.ReloadCommand;
import com.songoda.skyblock.command.commands.admin.RemoveHologramCommand;
import com.songoda.skyblock.command.commands.admin.RemoveUpgradeCommand;
import com.songoda.skyblock.command.commands.admin.SetAlwaysLoadedCommand;
import com.songoda.skyblock.command.commands.admin.SetBiomeCommand;
import com.songoda.skyblock.command.commands.admin.SetHologramCommand;
import com.songoda.skyblock.command.commands.admin.SetMaxMembers;
import com.songoda.skyblock.command.commands.admin.SetSizeCommand;
import com.songoda.skyblock.command.commands.admin.StackableCommand;
import com.songoda.skyblock.command.commands.admin.StructureCommand;
import com.songoda.skyblock.command.commands.admin.UpdateAllIslandsCommand;
import com.songoda.skyblock.command.commands.island.AcceptCommand;
import com.songoda.skyblock.command.commands.island.BanCommand;
import com.songoda.skyblock.command.commands.island.BankCommand;
import com.songoda.skyblock.command.commands.island.BansCommand;
import com.songoda.skyblock.command.commands.island.BiomeCommand;
import com.songoda.skyblock.command.commands.island.BorderCommand;
import com.songoda.skyblock.command.commands.island.CancelCommand;
import com.songoda.skyblock.command.commands.island.ChallengeCommand;
import com.songoda.skyblock.command.commands.island.ChatCommand;
import com.songoda.skyblock.command.commands.island.CloseCommand;
import com.songoda.skyblock.command.commands.island.ConfirmCommand;
import com.songoda.skyblock.command.commands.island.ControlPanelCommand;
import com.songoda.skyblock.command.commands.island.CoopCommand;
import com.songoda.skyblock.command.commands.island.CreateCommand;
import com.songoda.skyblock.command.commands.island.CurrentCommand;
import com.songoda.skyblock.command.commands.island.DeleteCommand;
import com.songoda.skyblock.command.commands.island.DemoteCommand;
import com.songoda.skyblock.command.commands.island.DenyCommand;
import com.songoda.skyblock.command.commands.island.InformationCommand;
import com.songoda.skyblock.command.commands.island.InviteCommand;
import com.songoda.skyblock.command.commands.island.KickAllCommand;
import com.songoda.skyblock.command.commands.island.KickCommand;
import com.songoda.skyblock.command.commands.island.LeaderboardCommand;
import com.songoda.skyblock.command.commands.island.LeaveCommand;
import com.songoda.skyblock.command.commands.island.LevelCommand;
import com.songoda.skyblock.command.commands.island.MembersCommand;
import com.songoda.skyblock.command.commands.island.OpenCommand;
import com.songoda.skyblock.command.commands.island.OwnerCommand;
import com.songoda.skyblock.command.commands.island.PreviewCommand;
import com.songoda.skyblock.command.commands.island.PromoteCommand;
import com.songoda.skyblock.command.commands.island.PublicCommand;
import com.songoda.skyblock.command.commands.island.ScoreboardCommand;
import com.songoda.skyblock.command.commands.island.SetSpawnCommand;
import com.songoda.skyblock.command.commands.island.SettingsCommand;
import com.songoda.skyblock.command.commands.island.TeleportCommand;
import com.songoda.skyblock.command.commands.island.UnbanCommand;
import com.songoda.skyblock.command.commands.island.UnlockCommand;
import com.songoda.skyblock.command.commands.island.UpgradeCommand;
import com.songoda.skyblock.command.commands.island.ValueCommand;
import com.songoda.skyblock.command.commands.island.VisitCommand;
import com.songoda.skyblock.command.commands.island.VisitorsCommand;
import com.songoda.skyblock.command.commands.island.VoteCommand;
import com.songoda.skyblock.command.commands.island.WeatherCommand;
import com.songoda.skyblock.command.commands.island.WhitelistCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.ChatComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager implements CommandExecutor, TabCompleter {
    private final SkyBlock plugin;
    private List<SubCommand> islandCommands;
    private List<SubCommand> adminCommands;

    public CommandManager(SkyBlock plugin) {
        this.plugin = plugin;

        PluginCommand islandCMD = plugin.getCommand("island");
        if (islandCMD != null) {
            islandCMD.setExecutor(this);
            islandCMD.setTabCompleter(this);
            registerSubCommands();
        }

    }

    public void registerSubCommands() {
        this.islandCommands = Arrays.asList(
                new AcceptCommand(this.plugin),
                new BanCommand(this.plugin),
                new BankCommand(this.plugin),
                new BansCommand(this.plugin),
                new BiomeCommand(this.plugin),
                new BorderCommand(this.plugin),
                new CancelCommand(this.plugin),
                new ChallengeCommand(this.plugin),
                new ChatCommand(this.plugin),
                new CloseCommand(this.plugin),
                new ConfirmCommand(this.plugin),
                new ControlPanelCommand(this.plugin),
                new CoopCommand(this.plugin),
                new CreateCommand(this.plugin),
                new CurrentCommand(this.plugin),
                new DeleteCommand(this.plugin),
                new DemoteCommand(this.plugin),
                new DenyCommand(this.plugin),
                new InformationCommand(this.plugin),
                new InviteCommand(this.plugin),
                new KickAllCommand(this.plugin),
                new KickCommand(this.plugin),
                new LeaderboardCommand(this.plugin),
                new LeaveCommand(this.plugin),
                new LevelCommand(this.plugin),
                new MembersCommand(this.plugin),
                new OpenCommand(this.plugin),
                new OwnerCommand(this.plugin),
                new PreviewCommand(this.plugin),
                new PromoteCommand(this.plugin),
                new PublicCommand(this.plugin),
                new SetSpawnCommand(this.plugin),
                new SettingsCommand(this.plugin),
                new TeleportCommand(this.plugin),
                new UnbanCommand(this.plugin),
                new UnlockCommand(this.plugin),
                new UpgradeCommand(this.plugin),
                new ValueCommand(this.plugin),
                new VisitCommand(this.plugin),
                new VisitorsCommand(this.plugin),
                new VoteCommand(this.plugin),
                new ScoreboardCommand(this.plugin),
                new WeatherCommand(this.plugin),
                new WhitelistCommand(this.plugin)
        );

        this.adminCommands = Arrays.asList(
                new AddUpgradeCommand(this.plugin),
                new com.songoda.skyblock.command.commands.admin.CreateCommand(this.plugin),
                new com.songoda.skyblock.command.commands.admin.DeleteCommand(this.plugin),
                new GeneratorCommand(this.plugin),
                new com.songoda.skyblock.command.commands.admin.LevelCommand(this.plugin),
                new LevelScanCommand(this.plugin),
                new com.songoda.skyblock.command.commands.admin.OwnerCommand(this.plugin),
                new RefreshHologramsCommand(this.plugin),
                new ReloadCommand(this.plugin),
                new RemoveHologramCommand(this.plugin),
                new RemoveUpgradeCommand(this.plugin),
                new SetBiomeCommand(this.plugin),
                new SetAlwaysLoadedCommand(this.plugin),
                new ProxyCommand(this.plugin),
                new SetHologramCommand(this.plugin),
                new SetSizeCommand(this.plugin),
                new com.songoda.skyblock.command.commands.admin.SetSpawnCommand(this.plugin),
                new com.songoda.skyblock.command.commands.admin.SettingsCommand(this.plugin),
                new StructureCommand(this.plugin),
                new com.songoda.skyblock.command.commands.admin.UpgradeCommand(this.plugin),
                new StackableCommand(this.plugin),
                new AdminBank(this.plugin),
                new SetMaxMembers(this.plugin),
                new ChatSpyCommand(this.plugin),
                new UpdateAllIslandsCommand(this.plugin)
        );
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String s, String[] args) {
        if (command.getName().equalsIgnoreCase("island")) {
            MessageManager messageManager = this.plugin.getMessageManager();
            SoundManager soundManager = this.plugin.getSoundManager();

            FileConfiguration languageConfigLoad = this.plugin.getLanguage();
            FileConfiguration mainConfig = this.plugin.getConfiguration();

            Player player = null;

            if (sender instanceof Player) {
                player = (Player) sender;
            }

            if (args.length == 0) {
                if (player == null) {
                    sendConsoleHelpCommands(sender);
                } else {
                    String commandToExecute;
                    String defaultCommand;
                    if (this.plugin.getIslandManager().getIsland(player) == null) {
                        defaultCommand = "island create";
                        commandToExecute = mainConfig.getString("Command.Island.Aliases.NoIsland", defaultCommand);
                    } else {
                        defaultCommand = "island controlpanel";
                        commandToExecute = mainConfig.getString("Command.Island.Aliases.IslandOwned", defaultCommand);
                    }

                    if (commandToExecute.trim().equalsIgnoreCase("island") || commandToExecute.trim().equalsIgnoreCase("is")) {
                        commandToExecute = defaultCommand;
                        Bukkit.getLogger().warning("Cannot redirect /island to /island or /is, would result in an endless loop. Using the default.");
                    }

                    if (commandToExecute.startsWith("/")) {
                        commandToExecute = commandToExecute.substring(1);
                    }

                    String finalCommandToExecute = commandToExecute;
                    Bukkit.getServer().getScheduler().runTask(this.plugin, () -> Bukkit.getServer().dispatchCommand(sender, finalCommandToExecute));
                }

                return true;
            }

            SubCommand subCommand;
            boolean isAdmin;

            if (args[0].equalsIgnoreCase("help")) {
                if (player == null) {
                    sendConsoleHelpCommands(sender);
                } else {
                    boolean canUseHelp = player.hasPermission("fabledskyblock.*")
                            || player.hasPermission("fabledskyblock.island.*")
                            || player.hasPermission("fabledskyblock.island.help");

                    if (!canUseHelp) {
                        messageManager.sendMessage(player, languageConfigLoad.getString("Command.PermissionDenied.Island.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                        return true;
                    }

                    int page = -1;

                    if (!mainConfig.getBoolean("Command.Help.List")) {
                        page = 1;

                        if (args.length == 2) {
                            if (args[1].matches("[0-9]+")) {
                                page = Integer.parseInt(args[1]);
                            } else {
                                messageManager.sendMessage(player,
                                        languageConfigLoad.getString("Command.Island.Help.Integer.Message"));
                                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                return true;
                            }
                        }
                    }

                    sendPlayerIslandHelpCommands(player, page);
                }

                return true;
            } else if (args[0].equalsIgnoreCase("admin")) {
                if (args.length == 1 || args[1].equalsIgnoreCase("help")) {
                    if (player == null) {
                        sendConsoleHelpCommands(sender);
                    } else {
                        boolean canUseHelp = player.hasPermission("fabledskyblock.*")
                                || player.hasPermission("fabledskyblock.admin.*")
                                || player.hasPermission("fabledskyblock.admin.help");

                        if (!canUseHelp) {
                            messageManager.sendMessage(player, languageConfigLoad.getString("Command.PermissionDenied.Admin.Message"));
                            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                            return true;
                        }

                        int page = -1;

                        if (!this.plugin.getConfiguration().getBoolean("Command.Help.List")) {
                            page = 1;

                            if (args.length == 3) {
                                if (args[2].matches("[0-9]+")) {
                                    page = Integer.parseInt(args[2]);
                                } else {
                                    messageManager.sendMessage(player,
                                            languageConfigLoad.getString("Command.Island.Help.Integer.Message"));
                                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F,
                                            1.0F);

                                    return true;
                                }
                            }
                        }

                        sendPlayerAdminHelpCommands(player, page);
                    }

                    return true;
                }

                subCommand = getAdminSubCommand(args[1]);
                isAdmin = true;
            } else {
                subCommand = getIslandSubCommand(args[0]);
                isAdmin = false;
            }

            if (subCommand == null) {
                messageManager.sendMessage(sender, languageConfigLoad.getString("Command.Island.Argument.Unrecognised.Message"));
                soundManager.playSound(sender, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
                return true;
            }

            if (!subCommand.hasPermission(sender, isAdmin)) {
                messageManager.sendMessage(sender, languageConfigLoad.getString("Command.PermissionDenied." + (isAdmin ? "Admin" : "Island") + ".Message"));
                soundManager.playSound(sender, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                return true;
            }

            List<String> arguments = new ArrayList<>(Arrays.asList(args));
            arguments.remove(args[0]);

            if (this.adminCommands.contains(subCommand)) {
                arguments.remove(args[1]);
            }

            if (sender instanceof Player) {
                subCommand.onCommandByPlayer(player, arguments.toArray(new String[0]));
            } else if (sender instanceof ConsoleCommandSender) {
                subCommand.onCommandByConsole((ConsoleCommandSender) sender,
                        arguments.toArray(new String[0]));
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        }

        boolean isAdmin = sender.hasPermission("fabledskyblock.admin.*") || sender.hasPermission("fabledskyblock.*");

        if (command.getName().equalsIgnoreCase("island")) {
            List<String> commandAliases = new ArrayList<>();

            if (args.length == 1) {
                if (args[0] == null || args[0].isEmpty()) {
                    commandAliases.add("admin");

                    for (SubCommand subCommandList : this.islandCommands) {
                        commandAliases.add(subCommandList.getName());
                    }
                } else {
                    if (isAdmin) {
                        if ("admin".contains(args[0].toLowerCase())) {
                            commandAliases.add("admin");
                        }
                    }

                    for (SubCommand subCommandList : this.islandCommands) {
                        if (subCommandList.getName().toLowerCase().contains(args[0].toLowerCase())) {
                            commandAliases.add(subCommandList.getName());
                        }
                    }
                }
            } else if (args.length == 2) {
                if (isAdmin) {
                    if (args[0].equalsIgnoreCase("admin")) {
                        if (args[1] == null || args[1].isEmpty()) {
                            for (SubCommand subCommandList : this.adminCommands) {
                                commandAliases.add(subCommandList.getName());
                            }
                        } else {
                            for (SubCommand subCommandList : this.adminCommands) {
                                if (subCommandList.getName().toLowerCase().contains(args[1].toLowerCase())) {
                                    commandAliases.add(subCommandList.getName());
                                }
                            }
                        }
                    }
                }

                List<String> arguments = getIslandArguments(args[0], args[1]);

                if (!arguments.isEmpty()) {
                    commandAliases.addAll(arguments);
                }
            } else if (args.length == 3) {
                if (isAdmin) {
                    if (args[0].equalsIgnoreCase("admin")) {
                        List<String> arguments = getAdminArguments(args[1], args[2]);

                        if (!arguments.isEmpty()) {
                            commandAliases.addAll(arguments);
                        }
                    }
                }
            }

            if (!commandAliases.isEmpty()) {
                return commandAliases;
            }
        }

        return null;
    }

    public List<String> getIslandArguments(String arg1, String arg2) {
        return this.getArguments(this.islandCommands, arg1, arg2);
    }

    public List<String> getAdminArguments(String arg1, String arg2) {
        return this.getArguments(this.adminCommands, arg1, arg2);
    }

    public List<String> getArguments(List<SubCommand> subCommands, String arg1, String arg2) {
        List<String> arguments = new ArrayList<>();

        for (SubCommand subCommandList : subCommands) {
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

    public void sendPlayerIslandHelpCommands(Player player, int page) {
        this.sendPlayerHelpCommands(player, this.islandCommands, page, false);
    }

    public void sendPlayerAdminHelpCommands(Player player, int page) {
        this.sendPlayerHelpCommands(player, this.adminCommands, page, true);
    }

    public void sendPlayerHelpCommands(Player player, List<SubCommand> subCommands, int page, boolean isAdmin) {
        FileManager fileManager = this.plugin.getFileManager();

        Config config = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        int pageSize = 7;

        int nextEndIndex = subCommands.size() - page * pageSize, index = page * pageSize - pageSize,
                endIndex = index >= subCommands.size() ? subCommands.size() - 1 : index + pageSize;
        boolean showAliases = fileManager.getConfig(new File(this.plugin.getDataFolder(), "config.yml"))
                .getFileConfiguration().getBoolean("Command.Help.Aliases.Enable");

        if (nextEndIndex <= -7) {
            this.plugin.getMessageManager().sendMessage(player, configLoad.getString("Command.Island.Help.Page.Message"));
            this.plugin.getSoundManager().playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

            return;
        }

        String subCommandText = "";

        if (isAdmin) {
            subCommandText = "admin ";
        }

        for (String helpLines : configLoad.getStringList("Command.Island.Help.Lines")) {
            if (helpLines.contains("%type")) {
                helpLines = helpLines.replace("%type", "Admin");
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
                    for (SubCommand subCommand : subCommands) {
                        StringBuilder commandAliases = new StringBuilder();

                        if (showAliases) {
                            for (int i = 0; i < subCommand.getAliases().length; i++) {
                                commandAliases.append("/").append(subCommand.getAliases()[i]);
                            }
                        }

                        player.spigot()
                                .sendMessage(new ChatComponent(
                                        prefix.replace("%info", subCommand.getInfo()) + "/island "
                                                + subCommandText + subCommand.getName() + commandAliases
                                                + suffix.replace("%info", subCommand.getInfo()),
                                        false, null, null,
                                        new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                new ComponentBuilder(subCommand.getInfo()).create()))
                                        .getTextComponent());
                    }
                } else {
                    for (; index < endIndex; index++) {
                        if (subCommands.size() > index) {
                            SubCommand subCommandFromIndex = subCommands.get(index);
                            StringBuilder commandAliases = new StringBuilder();

                            if (showAliases) {
                                for (int i = 0; i < subCommandFromIndex.getAliases().length; i++) {
                                    commandAliases.append("/").append(subCommandFromIndex.getAliases()[i]);
                                }
                            }

                            player.spigot()
                                    .sendMessage(new ChatComponent(
                                            prefix.replace("%info", subCommandFromIndex.getInfo()) + "/island "
                                                    + subCommandText + subCommandFromIndex.getName() + commandAliases
                                                    + suffix.replace("%info", subCommandFromIndex.getInfo()),
                                            false, null, null,
                                            new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                    new ComponentBuilder(subCommandFromIndex.getInfo()).create()))
                                            .getTextComponent());
                        }
                    }
                }
            } else {
                this.plugin.getMessageManager().sendMessage(player, helpLines);
            }
        }

        if (page != -1) {
            if (!(nextEndIndex == 0 || nextEndIndex < 0)) {
                if (page == 1) {
                    player.spigot()
                            .sendMessage(
                                    new ChatComponent(
                                            this.plugin.formatText(configLoad.getString("Command.Island.Help.Word.Next")),
                                            false, null,
                                            new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                    "/island " + subCommandText + "help " + (page + 1)),
                                            null).getTextComponent());
                } else {
                    player.spigot()
                            .sendMessage(
                                    new ChatComponent(
                                            this.plugin.formatText(configLoad.getString("Command.Island.Help.Word.Previous")),
                                            false, null,
                                            new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                    "/island " + subCommandText + "help " + (page - 1)),
                                            null).addExtraChatComponent(
                                                    new ChatComponent(" "
                                                            + ChatColor.translateAlternateColorCodes('&',
                                                            configLoad
                                                                    .getString("Command.Island.Help.Word.Pipe"))
                                                            + " ", false, null, null, null))
                                            .addExtraChatComponent(new ChatComponent(
                                                    ChatColor.translateAlternateColorCodes('&',
                                                            configLoad.getString(
                                                                    "Command.Island.Help.Word.Next")),
                                                    false, null,
                                                    new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                            "/island " + subCommandText + "help " + (page + 1)),
                                                    null))
                                            .getTextComponent());
                }
            } else {
                if (page != 1) {
                    player.spigot()
                            .sendMessage(new ChatComponent(
                                    ChatColor.translateAlternateColorCodes(
                                            '&', configLoad.getString("Command.Island.Help.Word.Previous")),
                                    false, null,
                                    new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                            "/island " + subCommandText + "help " + (page - 1)),
                                    null).getTextComponent());
                }
            }
        }

        this.plugin.getSoundManager().playSound(player, CompatibleSound.ENTITY_ARROW_HIT.getSound(), 1.0F, 1.0F);
    }

    public void sendConsoleHelpCommands(CommandSender sender) {
        sender.sendMessage("SkyBlock - Console Commands");

        String[] commands = {"delete", "owner", "reload", "removehologram", "setsize"};

        for (String commandList : commands) {
            SubCommand subCommand = this.getAdminSubCommand(commandList);
            sender.sendMessage("* /island admin " + subCommand.getName() + " - " + subCommand.getInfo());
        }
    }

    public SubCommand getIslandSubCommand(String cmdName) {
        return this.getSubCommand(this.islandCommands, cmdName);
    }

    public SubCommand getAdminSubCommand(String cmdName) {
        return this.getSubCommand(this.adminCommands, cmdName);
    }

    public SubCommand getSubCommand(List<SubCommand> subCommands, String cmdName) {
        for (SubCommand command : subCommands) {
            if (command.getName().equalsIgnoreCase(cmdName)) {
                return command;
            }

            for (String argList : command.getAliases()) {
                if (argList.equalsIgnoreCase(cmdName)) {
                    return command;
                }
            }
        }

        return null;
    }
}
