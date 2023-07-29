package com.songoda.skyblock.command.commands.island.disabled;

import com.craftaro.core.compatibility.CompatibleSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.confirmation.Confirmation;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.ChatComponent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class ResetCommand extends SubCommand {
    public ResetCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        FileManager fileManager = this.plugin.getFileManager();

        PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player);

        Config config = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        Island island = islandManager.getIsland(player);

        if (island == null) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Reset.Owner.Message"));
            soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
        } else if (island.hasRole(IslandRole.OWNER, player.getUniqueId())) {
            if (playerData.getConfirmationTime() > 0) {
                messageManager.sendMessage(player,
                        configLoad.getString("Command.Island.Reset.Confirmation.Pending.Message"));
                soundManager.playSound(player, CompatibleSound.ENTITY_IRON_GOLEM_ATTACK.getSound(), 1.0F, 1.0F);
            } else {
                int confirmationTime = fileManager.getConfig(new File(this.plugin.getDataFolder(), "config.yml"))
                        .getFileConfiguration().getInt("Island.Confirmation.Timeout");

                playerData.setConfirmation(Confirmation.RESET);
                playerData.setConfirmationTime(confirmationTime);

                player.spigot().sendMessage(new ChatComponent(
                        configLoad.getString("Command.Island.Reset.Confirmation.Confirm.Message").replace("%time",
                                "" + confirmationTime) + "   ",
                        false, null, null, null)
                        .addExtra(new ChatComponent(configLoad
                                .getString("Command.Island.Reset.Confirmation.Confirm.Word.Confirm")
                                .toUpperCase(), true, ChatColor.RED,
                                new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/island confirm"),
                                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                                        ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                                "Command.Island.Reset.Confirmation.Confirm.Word.Tutorial")))
                                        .create()))));
                soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_YES.getSound(), 1.0F, 1.0F);
            }
        } else {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Reset.Permission.Message"));
            soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "reset";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Reset.Info.Message";
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
