package com.craftaro.skyblock.command.commands.island;

import com.craftaro.core.hooks.economies.Economy;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.command.SubCommand;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.menus.Upgrade;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.sound.SoundManager;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class UpgradeCommand extends SubCommand {
    public UpgradeCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = this.plugin.getMessageManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        Economy economy = this.plugin.getEconomyManager().getEconomy();

        FileManager.Config config = this.plugin.getFileManager().getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (this.plugin.getIslandManager().getIsland(player) == null) {
            this.plugin.getMessageManager().sendMessage(player,
                    configLoad.getString("Command.Island.Upgrade.Owner.Message"));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
        } else {
            if (economy == null || !economy.isEnabled()) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Upgrade.Disabled.Message"));
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                return;
            }

            Upgrade.getInstance().open(player);
            soundManager.playSound(player, XSound.BLOCK_CHEST_OPEN);
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "upgrade";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Upgrade.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"upgrades"};
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
