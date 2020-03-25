package com.songoda.skyblock.command.commands.island;

import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.confirmation.Confirmation;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.structure.Structure;
import com.songoda.skyblock.utils.version.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class PreviewCommand extends SubCommand {

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        FileManager fileManager = skyblock.getFileManager();
        FileManager.Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLang = config.getFileConfiguration();

        if(args.length != 1) {
            skyblock.getMessageManager().sendMessage(player, configLang.getString("Command.Island.Preview.Argument.Count.Message"));
            return;
        }

        PlayerData data = skyblock.getPlayerDataManager().getPlayerData(player);
        Island island = skyblock.getIslandManager().getIsland(Bukkit.getOfflinePlayer(player.getUniqueId()));

        if (args[0].equals("confirm")) {
            if(data.getConfirmation() == Confirmation.Preview && data.getConfirmationTime() > 0) {
                Structure islandStructure = skyblock.getStructureManager().getStructure(island.getStructure());

                if(skyblock.getIslandManager().deleteIsland(island, true)) {
                    island.setDeleted(true);
                    data.setPreview(false);
                    if(player.getGameMode() == GameMode.SPECTATOR) {
                        player.setGameMode(GameMode.SURVIVAL);
                    }

                    Bukkit.getScheduler().runTaskLater(skyblock, () -> {
                        if(skyblock.getIslandManager().createIsland(player, islandStructure)) {
                            skyblock.getMessageManager().sendMessage(player, configLang.getString("Island.Creator.Selector.Created.Message"));
                            skyblock.getSoundManager().playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
                        }
                    }, 30L);
                }
            }
        } else if (args[0].equals("cancel")) {
            if(data.getConfirmation() == Confirmation.Preview && data.getConfirmationTime() > 0) {
                if(skyblock.getIslandManager().deleteIsland(island, true)) {
                    island.setDeleted(true);
                    data.setPreview(false);
                    if (player.getGameMode() == GameMode.SPECTATOR) {
                        player.setGameMode(GameMode.SURVIVAL);
                    }
                }
            }
        } else {
            Structure structure = skyblock.getStructureManager().getStructure(args[0]);
            if(structure == null) {
                skyblock.getMessageManager().sendMessage(player, configLang.getString("Island.Creator.File.Message"));
                return;
            }
            skyblock.getIslandManager().previewIsland(player, skyblock.getStructureManager().getStructure(args[0]));
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "preview";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Preview.Info.Message";
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