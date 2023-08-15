package com.craftaro.skyblock.command.commands.island;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.command.SubCommand;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.confirmation.Confirmation;
import com.craftaro.skyblock.cooldown.Cooldown;
import com.craftaro.skyblock.cooldown.CooldownManager;
import com.craftaro.skyblock.cooldown.CooldownPlayer;
import com.craftaro.skyblock.cooldown.CooldownType;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.island.IslandRole;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.playerdata.PlayerData;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.utils.ChatComponent;
import com.craftaro.skyblock.utils.NumberUtil;
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
    public DeleteCommand(SkyBlock plugin) {
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

        FileManager.Config config = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        Island island = islandManager.getIsland(player);

        if (island == null) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Delete.Owner.Message"));
            soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);
        } else if (island.hasRole(IslandRole.OWNER, player.getUniqueId())) {
            if (fileManager.getConfig(new File(this.plugin.getDataFolder(), "config.yml"))
                    .getFileConfiguration().getBoolean("Island.Creation.Cooldown.Creation.Enable")
                    && cooldownManager.hasPlayer(CooldownType.DELETION, player)) {
                CooldownPlayer cooldownPlayer = cooldownManager.getCooldownPlayer(CooldownType.DELETION, player);
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

                soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);

                return;
            }
            if (playerData.getConfirmationTime() > 0) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Delete.Confirmation.Pending.Message"));
                soundManager.playSound(player, XSound.ENTITY_IRON_GOLEM_ATTACK);
            } else {
                int confirmationTime = fileManager.getConfig(new File(this.plugin.getDataFolder(), "config.yml"))
                        .getFileConfiguration().getInt("Island.Confirmation.Timeout");

                playerData.setConfirmation(Confirmation.DELETION);
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

                soundManager.playSound(player, XSound.ENTITY_VILLAGER_YES);
            }
        } else {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Delete.Permission.Message"));
            soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);
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
