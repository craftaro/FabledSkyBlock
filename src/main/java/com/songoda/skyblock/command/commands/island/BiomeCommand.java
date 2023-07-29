package com.songoda.skyblock.command.commands.island;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.biome.BiomeManager;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.gui.biome.GuiBiome;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class BiomeCommand extends SubCommand {
    public BiomeCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        BiomeManager biomeManager = this.plugin.getBiomeManager();

        Config config = this.plugin.getFileManager().getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        Island island = islandManager.getIsland(player);

        if (island == null) {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Biome.Owner.Message"));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
        } else if ((island.hasRole(IslandRole.OPERATOR, player.getUniqueId())
                && this.plugin.getPermissionManager().hasPermission(island, "Biome", IslandRole.OPERATOR))
                || island.hasRole(IslandRole.OWNER, player.getUniqueId())) {
            if (biomeManager.isUpdating(island)) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Biome.InProgress.Message"));
                soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);
            } else {
                this.plugin.getGuiManager().showGUI(player, new GuiBiome(this.plugin, player, island, IslandWorld.NORMAL, null, false)); // TODO Nether and End support
                soundManager.playSound(player, XSound.BLOCK_CHEST_OPEN);
            }
        } else {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Biome.Permission.Message"));
            soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "biome";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Biome.Info.Message";
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
