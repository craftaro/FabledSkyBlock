package com.songoda.skyblock.command.commands.island;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.confirmation.Confirmation;
import com.songoda.skyblock.cooldown.Cooldown;
import com.songoda.skyblock.cooldown.CooldownManager;
import com.songoda.skyblock.cooldown.CooldownPlayer;
import com.songoda.skyblock.cooldown.CooldownType;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.menus.Ownership;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.ChatComponent;
import com.songoda.skyblock.utils.NumberUtil;
import com.songoda.skyblock.utils.player.OfflinePlayer;
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
import java.util.UUID;

public class OwnerCommand extends SubCommand {
    public OwnerCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        CooldownManager cooldownManager = this.plugin.getCooldownManager();
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        FileManager fileManager = this.plugin.getFileManager();

        PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player);

        Config config = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        Island island = islandManager.getIsland(player);

        if (island == null) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Ownership.Owner.Message"));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
        } else if (args.length == 0) {
            if (island.hasRole(IslandRole.OWNER, player.getUniqueId())) {
                playerData.setType(Ownership.Visibility.HIDDEN);
                Ownership.getInstance().open(player);
                soundManager.playSound(player, XSound.BLOCK_CHEST_OPEN);

                return;
            }
        } else if (args.length == 1) {
            if (island.hasRole(IslandRole.OWNER, player.getUniqueId())) {
                if (playerData.getConfirmationTime() > 0) {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Ownership.Confirmation.Pending.Message"));
                    soundManager.playSound(player, XSound.ENTITY_IRON_GOLEM_ATTACK);
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

                    if (targetPlayerUUID == null || (!island.hasRole(IslandRole.MEMBER, targetPlayerUUID)
                            && !island.hasRole(IslandRole.OPERATOR, targetPlayerUUID)
                            && !island.hasRole(IslandRole.OWNER, targetPlayerUUID))) {
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Ownership.Member.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                    } else if (targetPlayerUUID.equals(player.getUniqueId())) {
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Ownership.Yourself.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                    } else if (cooldownManager.hasPlayer(CooldownType.OWNERSHIP, Bukkit.getServer().getOfflinePlayer(island.getOwnerUUID()))) {
                        CooldownPlayer cooldownPlayer = cooldownManager.getCooldownPlayer(CooldownType.OWNERSHIP, Bukkit.getServer().getOfflinePlayer(island.getOwnerUUID()));
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

                        soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);

                        return;
                    } else {
                        int confirmationTime = this.plugin.getConfiguration().getInt("Island.Confirmation.Timeout");

                        playerData.setOwnership(targetPlayerUUID);
                        playerData.setConfirmation(Confirmation.OWNERSHIP);
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

                                            chatComponent.addExtra(new TextComponent(ComponentSerializer.parse("{text: \"\n\"}")));
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

                        soundManager.playSound(player, XSound.ENTITY_VILLAGER_YES);
                    }
                }
            } else {
                if (island.hasPassword()) {
                    if (args[0].equalsIgnoreCase(island.getPassword())) {
                        for (Player all : Bukkit.getOnlinePlayers()) {
                            if ((island.hasRole(IslandRole.MEMBER, all.getUniqueId())
                                    || island.hasRole(IslandRole.OPERATOR, all.getUniqueId())
                                    || island.hasRole(IslandRole.OWNER, all.getUniqueId()))
                                    && (!all.getUniqueId().equals(player.getUniqueId()))) {
                                all.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                        configLoad.getString("Command.Island.Ownership.Assigned.Broadcast.Message")
                                                .replace("%player", player.getName())));
                                soundManager.playSound(all, XSound.BLOCK_ANVIL_USE);
                            }
                        }

                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Ownership.Assigned.Sender.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_USE);

                        islandManager.giveOwnership(island, player);

                        cooldownManager.createPlayer(CooldownType.OWNERSHIP,
                                Bukkit.getServer().getOfflinePlayer(island.getOwnerUUID()));
                    } else {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Ownership.Password.Incorrect.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                    }
                } else {
                    messageManager.sendMessage(player,
                            configLoad.getString("Command.Island.Ownership.Password.Unset.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                }
            }

            return;
        } else {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Ownership.Invalid.Message"));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
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
    public String getInfoMessagePath() {
        return "Command.Island.Ownership.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"ownership", "transfer", "makeleader", "makeowner"};
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
