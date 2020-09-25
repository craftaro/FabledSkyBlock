package com.songoda.skyblock.command.commands.island;

import com.songoda.core.compatibility.CompatibleSound;
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
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.ChatComponent;
import com.songoda.core.utils.NumberUtils;
import com.songoda.skyblock.utils.NumberUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class DeleteCommand extends SubCommand {

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        CooldownManager cooldownManager = plugin.getCooldownManager();
        MessageManager messageManager = plugin.getMessageManager();
        IslandManager islandManager = plugin.getIslandManager();
        SoundManager soundManager = plugin.getSoundManager();
        FileManager fileManager = plugin.getFileManager();

        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);

        Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        Island island = islandManager.getIsland(player);

        if (island == null) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Delete.Owner.Message"));
            soundManager.playSound(player,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
        } else if (island.hasRole(IslandRole.Owner, player.getUniqueId())) {
            if (fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml"))
                    .getFileConfiguration().getBoolean("Island.Creation.Cooldown.Creation.Enable")
                    && cooldownManager.hasPlayer(CooldownType.Deletion, player)) {
                CooldownPlayer cooldownPlayer = cooldownManager.getCooldownPlayer(CooldownType.Deletion, player);
                Cooldown cooldown = cooldownPlayer.getCooldown();

                if (cooldown.getTime() < 60) {
                    messageManager.sendMessage(player,
                            config.getFileConfiguration().getString("Island.Deletion.Cooldown.Message") // TODO Add language.yml values
                                    .replace("%time", cooldown.getTime() + " " + config.getFileConfiguration()
                                            .getString("Island.Deletion.Cooldown.Word.Second")));
                } else {
                    long[] durationTime = NumberUtil.getDuration(cooldown.getTime());
                    messageManager.sendMessage(player,
                            config.getFileConfiguration().getString("Island.Deletion.Cooldown.Message")
                                    .replace("%time", durationTime[2] + " "
                                            + config.getFileConfiguration()
                                            .getString("Island.Deletion.Cooldown.Word.Minute")
                                            + " " + durationTime[3] + " " + config.getFileConfiguration()
                                            .getString("Island.Deletion.Cooldown.Word.Second")));
                }

                soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                return;
            }
            if (playerData.getConfirmationTime() > 0) {
                messageManager.sendMessage(player,
                        configLoad.getString("Command.Island.Delete.Confirmation.Pending.Message"));
                soundManager.playSound(player, CompatibleSound.ENTITY_IRON_GOLEM_ATTACK.getSound(), 1.0F, 1.0F);
            } else {
                int confirmationTime = fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml"))
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

                soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_YES.getSound(), 1.0F, 1.0F);
            }
        } else {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Delete.Permission.Message"));
            soundManager.playSound(player,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
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
    public String getInfoMessagePath() {
        return "Command.Island.Delete.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"remove", "disband", "reset", "restart"};
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
