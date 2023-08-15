package com.songoda.skyblock.command.commands.island;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.core.utils.NumberUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.levelling.IslandLevelManager;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import org.apache.commons.lang.WordUtils;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Optional;

public class ValueCommand extends SubCommand {
    public ValueCommand(SkyBlock plugin) {
        super(plugin);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        IslandLevelManager levellingManager = this.plugin.getLevellingManager();
        MessageManager messageManager = this.plugin.getMessageManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        FileManager fileManager = this.plugin.getFileManager();

        Config config = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (player.getItemInHand() == null) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Value.Hand.Message"));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
        } else {
            Optional<XMaterial> materials = CompatibleMaterial.getMaterial(player.getItemInHand().getType().name());

            if (materials.isPresent() && levellingManager.hasWorth(materials.get())) {
                double worth = levellingManager.getWorth(materials.get());
                double level = worth / (double) this.plugin.getConfiguration().getInt("Island.Levelling.Division");

                messageManager.sendMessage(player,
                        configLoad.getString("Command.Island.Value.Value.Message").replace("%material", WordUtils.capitalizeFully(materials.get().name().toLowerCase().replace("_", " ")))
                                .replace("%points", "" + worth).replace("%level", NumberUtils.formatNumber(level)));
                soundManager.playSound(player, XSound.ENTITY_VILLAGER_YES);
            } else {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Value.None.Message"));
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
            }
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "value";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Value.Info.Message";
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
