package com.craftaro.skyblock.command.commands.admin;

import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.command.SubCommand;
import com.craftaro.skyblock.config.FileManager.Config;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.playerdata.PlayerData;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.utils.ChatComponent;
import com.craftaro.skyblock.utils.Compression;
import com.craftaro.skyblock.utils.structure.StructureUtil;
import com.craftaro.skyblock.utils.world.LocationUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.logging.Level;

public class StructureCommand extends SubCommand {
    public StructureCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = this.plugin.getMessageManager();
        SoundManager soundManager = this.plugin.getSoundManager();

        Config config = this.plugin.getFileManager().getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            for (String helpLines : configLoad.getStringList("Command.Island.Help.Lines")) {
                if (helpLines.contains("%type")) {
                    helpLines = helpLines.replace("%type", "Structure");
                }

                if (helpLines.contains("%commands")) {
                    String[] sections = helpLines.split("%commands");
                    String prefix = "", suffix = "";

                    if (sections.length >= 1) {
                        prefix = ChatColor.translateAlternateColorCodes('&', sections[0]);
                    }

                    if (sections.length == 2) {
                        suffix = ChatColor.translateAlternateColorCodes('&', sections[1]);
                    }

                    player.spigot()
                            .sendMessage(
                                    new ChatComponent(
                                            prefix.replace("%info",
                                                    ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                                            "Command.Island.Admin.Structure.Tool.Info.Message")))
                                                    + "/island admin structure tool"
                                                    + suffix.replace("%info", ChatColor.translateAlternateColorCodes(
                                                    '&',
                                                    configLoad.getString(
                                                            "Command.Island.Admin.Structure.Tool.Info.Message"))),
                                            false, null, null,
                                            new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                                                    ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                                            "Command.Island.Admin.Structure.Tool.Info.Message")))
                                                    .create())).getTextComponent());
                    player.spigot()
                            .sendMessage(
                                    new ChatComponent(
                                            prefix.replace("%info",
                                                    ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                                            "Command.Island.Admin.Structure.Save.Info.Message")))
                                                    + "/island admin structure save"
                                                    + suffix.replace("%info", ChatColor.translateAlternateColorCodes(
                                                    '&',
                                                    configLoad.getString(
                                                            "Command.Island.Admin.Structure.Save.Info.Message"))),
                                            false, null, null,
                                            new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                                                    ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                                            "Command.Island.Admin.Structure.Save.Info.Message")))
                                                    .create())).getTextComponent());
                    player.spigot()
                            .sendMessage(
                                    new ChatComponent(
                                            prefix.replace("%info",
                                                    ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                                            "Command.Island.Admin.Structure.Convert.Info.Message")))
                                                    + "/island admin structure convert"
                                                    + suffix.replace("%info", ChatColor.translateAlternateColorCodes(
                                                    '&',
                                                    configLoad.getString(
                                                            "Command.Island.Admin.Structure.Convert.Info.Message"))),
                                            false, null, null,
                                            new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                                                    ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                                            "Command.Island.Admin.Structure.Convert.Info.Message")))
                                                    .create())).getTextComponent());
                } else {
                    messageManager.sendMessage(player, helpLines);
                }
            }

            soundManager.playSound(player, XSound.ENTITY_ARROW_HIT);
        } else {
            if (args[0].equalsIgnoreCase("tool")) {
                try {
                    ItemStack is = StructureUtil.getTool();

                    for (ItemStack itemList : player.getInventory().getContents()) {
                        if (itemList != null) {
                            if ((itemList.getType() == is.getType()) && (itemList.hasItemMeta()) && (itemList
                                    .getItemMeta().getDisplayName().equals(is.getItemMeta().getDisplayName()))) {
                                messageManager.sendMessage(player, configLoad
                                        .getString("Command.Island.Admin.Structure.Tool.Inventory.Message"));
                                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                return;
                            }
                        }
                    }

                    player.getInventory().addItem(is);
                    messageManager.sendMessage(player,
                            configLoad.getString("Command.Island.Admin.Structure.Tool.Equiped.Message"));
                    soundManager.playSound(player, XSound.ENTITY_CHICKEN_EGG);
                } catch (Exception e) {
                    messageManager.sendMessage(player,
                            configLoad.getString("Island.Structure.Tool.Material.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                    Bukkit.getServer().getLogger().log(Level.WARNING,
                            "SkyBlock | Error: The defined material in the configuration file for the Structure selection tool could not be found.");
                }

                return;
            } else if (args[0].equalsIgnoreCase("save")) {
                if (args.length == 2) {
                    PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player);

                    Location position1Location = playerData.getArea().getPosition(1);
                    Location position2Location = playerData.getArea().getPosition(2);

                    if (position1Location == null && position2Location == null) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Admin.Structure.Save.Position.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                    } else if ((position1Location == null && position2Location != null)
                            || (position1Location != null && position2Location == null)) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Admin.Structure.Save.Complete.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                    } else if (!position1Location.getWorld().getName()
                            .equals(position2Location.getWorld().getName())) {
                        messageManager.sendMessage(player, configLoad
                                .getString("Command.Island.Admin.Structure.Save.Selection.World.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                    } else if (!player.getWorld().getName().equals(position1Location.getWorld().getName())) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Admin.Structure.Save.Player.World.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                    } else if (!LocationUtil.isInsideArea(player.getLocation(), position1Location,
                            position2Location)) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Admin.Structure.Save.Player.Area.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                    } else {
                        try {
                            File configFile = new File(
                                    this.plugin.getDataFolder() + "/structures/" + args[1] + ".structure");
                            StructureUtil.saveStructure(configFile, player.getLocation(),
                                    StructureUtil.getFixedLocations(position1Location, position2Location));

                            messageManager.sendMessage(player,
                                    configLoad.getString("Command.Island.Admin.Structure.Save.Saved.Successful.Message")
                                            .replace("%name", args[1]));
                            soundManager.playSound(player, XSound.ENTITY_VILLAGER_YES);
                            return;
                        } catch (Exception ex) {
                            messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.Structure.Save.Saved.Failed.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                            ex.printStackTrace();
                        }
                    }
                } else {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.Structure.Save.Invalid.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                }


            } else if (args[0].equalsIgnoreCase("convert")) {
                if (args.length == 2) {
                    File structureFile = new File(new File(this.plugin.getDataFolder().toString() + "/structures"), args[1]);
                    if (!structureFile.exists()) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Admin.Structure.Convert.Invalid.Message")
                                        .replace("%name", args[1]));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                        return;
                    }
                    byte[] content = new byte[(int) structureFile.length()];
                    try {
                        FileInputStream fileInputStream = new FileInputStream(structureFile);
                        fileInputStream.read(content);
                        fileInputStream.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(structureFile, false);
                        fileOutputStream.write(Base64.getEncoder().encode(Compression.decompress(content).getBytes()));
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    } catch (IOException ex) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Admin.Structure.Convert.Converted.Failed.Message")
                                        .replace("%name", args[1]));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                        ex.printStackTrace();
                    }

                    messageManager.sendMessage(player,
                            configLoad.getString("Command.Island.Admin.Structure.Convert.Converted.Successful.Message")
                                    .replace("%name", args[1]));
                    soundManager.playSound(player, XSound.ENTITY_VILLAGER_YES);

                } else {
                    messageManager.sendMessage(player,
                            configLoad.getString("Command.Island.Admin.Structure.Convert.Invalid.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                }

                return;
            } else {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Argument.Unrecognised.Message"));
                soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);
            }
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "structure";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Admin.Structure.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String[] getArguments() {
        return new String[]{"tool", "save", "convert"};
    }
}
