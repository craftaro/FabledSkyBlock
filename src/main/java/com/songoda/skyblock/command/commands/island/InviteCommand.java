package com.songoda.skyblock.command.commands.island;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.api.event.island.IslandInviteEvent;
import com.songoda.skyblock.api.invite.IslandInvitation;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.invite.Invite;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.ChatComponent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class InviteCommand extends SubCommand {

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = plugin.getMessageManager();
        IslandManager islandManager = plugin.getIslandManager();
        SoundManager soundManager = plugin.getSoundManager();
        FileManager fileManager = plugin.getFileManager();

        Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length == 1) {
            Island island = islandManager.getIsland(player);

            if (island == null) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Invite.Owner.Message"));
                soundManager.playSound(player,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
            } else if (island.hasRole(IslandRole.Owner, player.getUniqueId())
                    || (island.hasRole(IslandRole.Operator, player.getUniqueId())
                    && plugin.getPermissionManager().hasPermission(island, "Invite", IslandRole.Operator))) {
                Config mainConfig = fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml"));

                if ((island.getRole(IslandRole.Member).size() + island.getRole(IslandRole.Operator).size()
                        + 1) >= island.getMaxMembers(player)) {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Invite.Capacity.Message"));
                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                } else {
                    String playerName = args[0];

                    if (playerName.equalsIgnoreCase(player.getName())) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Invite.Yourself.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                    } else {
                        Player targetPlayer = Bukkit.getServer().getPlayer(playerName);

                        if (targetPlayer == null) {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Command.Island.Invite.Offline.Message"));
                            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                        } else if (targetPlayer.getName().equalsIgnoreCase(player.getName())) {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Command.Island.Invite.Yourself.Message"));
                            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                        } else if (island.hasRole(IslandRole.Member, targetPlayer.getUniqueId())
                                || island.hasRole(IslandRole.Operator, targetPlayer.getUniqueId())
                                || island.hasRole(IslandRole.Owner, targetPlayer.getUniqueId())) {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Command.Island.Invite.Member.Message"));
                            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                        } else if (plugin.getInviteManager().hasInvite(targetPlayer.getUniqueId())) {
                            Invite invite = plugin.getInviteManager().getInvite(targetPlayer.getUniqueId());

                            if (invite.getOwnerUUID().equals(island.getOwnerUUID())) {
                                messageManager.sendMessage(player,
                                        configLoad.getString("Command.Island.Invite.Already.Own.Message"));
                                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                            } else {
                                messageManager.sendMessage(player,
                                        configLoad.getString("Command.Island.Invite.Already.Other.Message"));
                                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                            }
                        } else {
                            int respondTime = mainConfig.getFileConfiguration().getInt("Island.Invite.Time");

                            String cancellationMessage = configLoad
                                    .getString("Command.Island.Invite.Invited.Sender.Sent.Message");
                            String timeMessage;

                            if (respondTime < 60) {
                                timeMessage = respondTime + " "
                                        + configLoad.getString("Command.Island.Invite.Invited.Word.Second");
                            } else {
                                timeMessage = respondTime / 60 + " "
                                        + configLoad.getString("Command.Island.Invite.Invited.Word.Minute");
                            }

                            // TODO: Use this same logic wherever a clickable placeholder has to be replaced at
                            String placeholderName = "%cancel";
                            if (cancellationMessage.contains(placeholderName)) {
                                if (cancellationMessage.equals(placeholderName)) {
                                    player.spigot().sendMessage(new ChatComponent(configLoad
                                            .getString("Command.Island.Invite.Invited.Word.Cancel").toUpperCase(), true,
                                            ChatColor.RED,
                                            new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                    "/island cancel " + targetPlayer.getName()),
                                            new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                                                    ChatColor.translateAlternateColorCodes('&', configLoad
                                                            .getString("Command.Island.Invite.Invited.Word.Tutorial")
                                                            .replace("%action", configLoad.getString(
                                                                    "Command.Island.Invite.Invited.Word.Cancel"))))
                                                    .create())).getTextComponent());
                                } else {
                                    ChatComponent chatComponent = new ChatComponent("", false, null, null, null);

                                    String[] messagePieces = cancellationMessage.replace("\\n", "\n").split("\n");
                                    for (int i = 0; i < messagePieces.length; i++) {
                                        String piece = messagePieces[i].replace("%player", targetPlayer.getName()).replace("%time", timeMessage);

                                        if (piece.contains(placeholderName)) {
                                            String before = piece.substring(0, piece.indexOf(placeholderName));
                                            String after = piece.substring(piece.indexOf(placeholderName) + placeholderName.length());

                                            chatComponent.addExtraChatComponent(new ChatComponent(before, false, null, null, null));

                                            chatComponent.addExtraChatComponent(new ChatComponent(
                                                    configLoad.getString("Command.Island.Invite.Invited.Word.Cancel").toUpperCase(),
                                                    true, ChatColor.RED,
                                                    new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                            "/island cancel " + targetPlayer.getName()),
                                                    new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                                                            ChatColor.translateAlternateColorCodes('&', configLoad
                                                                    .getString("Command.Island.Invite.Invited.Word.Tutorial")
                                                                    .replace("%action", configLoad.getString(
                                                                            "Command.Island.Invite.Invited.Word.Cancel"))))
                                                            .create())));

                                            chatComponent.addExtraChatComponent(new ChatComponent(after, false, null, null, null));
                                        } else {
                                            chatComponent.addExtraChatComponent(new ChatComponent(piece, false, null, null, null));
                                        }

                                        if (i != messagePieces.length - 1)
                                            chatComponent.addExtra(new TextComponent(ComponentSerializer.parse("{text: \"\n\"}")));
                                    }

                                    player.spigot().sendMessage(chatComponent.getTextComponent());
                                }
                            } else {
                                messageManager.sendMessage(player, cancellationMessage
                                        .replace("%player", targetPlayer.getName()).replace("%time", timeMessage));
                            }

                            String invitationMessage = configLoad
                                    .getString("Command.Island.Invite.Invited.Target.Received.Message");
                            ChatComponent chatComponent = new ChatComponent("", false, null, null, null);

                            if (invitationMessage.contains("\n") || invitationMessage.contains("\\n")) {
                                invitationMessage = invitationMessage.replace("\\n", "\n");

                                for (String messageList : invitationMessage.split("\n")) {
                                    chatComponent
                                            .addExtra(new ChatComponent(
                                                    messageManager.replaceMessage(player,
                                                            messageList.replace("%player", player.getName())
                                                                    .replace("%time", timeMessage)),
                                                    false, null, null, null));

                                    chatComponent
                                            .addExtra(new TextComponent(ComponentSerializer.parse("{text: \"\n\"}")));
                                }
                            } else {
                                chatComponent
                                        .addExtra(new ChatComponent(
                                                messageManager.replaceMessage(player,
                                                        invitationMessage.replace("%player", player.getName())
                                                                .replace("%time", timeMessage)),
                                                false, null, null, null));
                            }

                            chatComponent.addExtra(new ChatComponent(
                                    configLoad.getString("Command.Island.Invite.Invited.Word.Accept").toUpperCase(),
                                    true, ChatColor.GREEN,
                                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/island accept " + player.getName()),
                                    new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                            new ComponentBuilder(ChatColor.translateAlternateColorCodes('&',
                                                    configLoad.getString("Command.Island.Invite.Invited.Word.Tutorial")
                                                            .replace("%action", configLoad.getString(
                                                                    "Command.Island.Invite.Invited.Word.Accept"))))
                                                    .create())));

                            chatComponent.addExtra(new ChatComponent(" | ", false, ChatColor.DARK_GRAY, null, null));

                            chatComponent.addExtra(new ChatComponent(
                                    configLoad.getString("Command.Island.Invite.Invited.Word.Deny").toUpperCase(), true,
                                    ChatColor.RED,
                                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/island deny " + player.getName()),
                                    new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                            new ComponentBuilder(ChatColor.translateAlternateColorCodes('&',
                                                    configLoad.getString("Command.Island.Invite.Invited.Word.Tutorial")
                                                            .replace("%action", configLoad.getString(
                                                                    "Command.Island.Invite.Invited.Word.Deny"))))
                                                    .create())));

                            targetPlayer.spigot().sendMessage(chatComponent.getTextComponent());

                            Invite invite = plugin.getInviteManager().createInvite(targetPlayer, player,
                                    island.getOwnerUUID(), respondTime);

                            Bukkit.getServer().getPluginManager()
                                    .callEvent(new IslandInviteEvent(island.getAPIWrapper(),
                                            new IslandInvitation(targetPlayer, player, invite.getTime())));

                            soundManager.playSound(player, CompatibleSound.BLOCK_NOTE_BLOCK_PLING.getSound(), 1.0F, 1.0F);
                            soundManager.playSound(targetPlayer, CompatibleSound.BLOCK_NOTE_BLOCK_PLING.getSound(), 1.0F, 1.0F);
                        }
                    }
                }
            } else {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Invite.Permission.Message"));
                soundManager.playSound(player,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
            }
        } else {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Invite.Invalid.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "invite";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Invite.Info.Message";
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
