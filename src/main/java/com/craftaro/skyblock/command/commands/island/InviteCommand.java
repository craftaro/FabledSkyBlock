package com.craftaro.skyblock.command.commands.island;

import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.api.event.island.IslandInviteEvent;
import com.craftaro.skyblock.api.invite.IslandInvitation;
import com.craftaro.skyblock.command.SubCommand;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.invite.Invite;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.island.IslandRole;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.utils.ChatComponent;
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
    public InviteCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        FileManager fileManager = this.plugin.getFileManager();

        FileManager.Config config = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length == 1) {
            Island island = islandManager.getIsland(player);

            if (island == null) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Invite.Owner.Message"));
                soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);
            } else if (island.hasRole(IslandRole.OWNER, player.getUniqueId())
                    || (island.hasRole(IslandRole.OPERATOR, player.getUniqueId())
                    && this.plugin.getPermissionManager().hasPermission(island, "Invite", IslandRole.OPERATOR))) {
                FileManager.Config mainConfig = fileManager.getConfig(new File(this.plugin.getDataFolder(), "config.yml"));

                if ((island.getRole(IslandRole.MEMBER).size() + island.getRole(IslandRole.OPERATOR).size()
                        + 1) >= island.getMaxMembers(player)) {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Invite.Capacity.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                } else {
                    String playerName = args[0];

                    if (playerName.equalsIgnoreCase(player.getName())) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Invite.Yourself.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                    } else {
                        Player targetPlayer = Bukkit.getServer().getPlayer(playerName);

                        if (targetPlayer == null) {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Command.Island.Invite.Offline.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                        } else if (targetPlayer.getName().equalsIgnoreCase(player.getName())) {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Command.Island.Invite.Yourself.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                        } else if (island.hasRole(IslandRole.MEMBER, targetPlayer.getUniqueId())
                                || island.hasRole(IslandRole.OPERATOR, targetPlayer.getUniqueId())
                                || island.hasRole(IslandRole.OWNER, targetPlayer.getUniqueId())) {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Command.Island.Invite.Member.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                        } else if (this.plugin.getInviteManager().hasInvite(targetPlayer.getUniqueId())) {
                            Invite invite = this.plugin.getInviteManager().getInvite(targetPlayer.getUniqueId());

                            if (invite.getOwnerUUID().equals(island.getOwnerUUID())) {
                                messageManager.sendMessage(player,
                                        configLoad.getString("Command.Island.Invite.Already.Own.Message"));
                                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                            } else {
                                messageManager.sendMessage(player,
                                        configLoad.getString("Command.Island.Invite.Already.Other.Message"));
                                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
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

                            Invite invite = this.plugin.getInviteManager().createInvite(targetPlayer, player,
                                    island.getOwnerUUID(), respondTime);

                            Bukkit.getServer().getPluginManager()
                                    .callEvent(new IslandInviteEvent(island.getAPIWrapper(), new IslandInvitation(targetPlayer, player, invite.getTime())));

                            soundManager.playSound(player, XSound.BLOCK_NOTE_BLOCK_PLING);
                            soundManager.playSound(targetPlayer, XSound.BLOCK_NOTE_BLOCK_PLING);
                        }
                    }
                }
            } else {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Invite.Permission.Message"));
                soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);
            }
        } else {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Invite.Invalid.Message"));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
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
