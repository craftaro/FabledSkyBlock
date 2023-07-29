package com.songoda.skyblock.command.commands.admin;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.stackable.Stackable;
import com.songoda.skyblock.stackable.StackableManager;
import com.songoda.skyblock.utils.StringUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;


public class StackableCommand extends SubCommand {
    public StackableCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        final MessageManager messageManager = this.plugin.getMessageManager();

        if (args.length == 0) {
            player.sendMessage(StringUtil.color("&e/island admin stackable setsize <size> &7- &f&osets the target block's stack size if applicable"));
            return;
        }

        final FileConfiguration messageConfig = this.plugin.getFileManager().getConfig(new File(this.plugin.getDataFolder(), "language.yml")).getFileConfiguration();

        if (args[0].equalsIgnoreCase("setsize")) {

            if (args.length == 1) {
                messageManager.sendMessage(player, messageConfig.getString("Command.Island.Admin.Stackable.Setsize.No-Arguments"));
                return;
            }

            int amount;

            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                messageManager.sendMessage(player, (messageConfig.getString("Command.Island.Admin.Stackable.Setsize.Invalid-Number")).replace("%number%", args[1]));
                return;
            }

            final Block block = player.getTargetBlock(null, 6);
            if (block == null) {
                messageManager.sendMessage(player, messageConfig.getString("Command.Island.Admin.Stackable.Target.None"));
                return;
            }

            final StackableManager stackableManager = this.plugin.getStackableManager();
            final CompatibleMaterial type = CompatibleMaterial.getMaterial(block.getType());

            if (!stackableManager.isStackableMaterial(type)) {
                messageManager.sendMessage(player, messageConfig.getString("Command.Island.Admin.Stackable.Target.Unstackable"));
                return;
            }

            final Location loc = block.getLocation();
            Stackable stack = stackableManager.getStack(loc, type);

            if (amount <= 1) {
                messageManager.sendMessage(player, messageConfig.getString("Command.Island.Admin.Stackable.Target.Remove-Stack"));
                if (stack != null) {
                    stackableManager.removeStack(stack);
                }
                return;
            }

            final int oldSize;

            if (stack == null) {
                stack = new Stackable(loc, type);
                stackableManager.addStack(stack);
                oldSize = 0;
            } else {
                oldSize = stack.getSize();
            }

            stack.setSize(amount);

            String input = messageConfig.getString("Command.Island.Admin.Stackable.Setsize.Success");

            input = input.replace("%old_size%", Integer.toString(oldSize));
            input = input.replace("%new_size%", Integer.toString(amount));

            messageManager.sendMessage(player, input);
        } else {
            messageManager.sendMessage(player, messageConfig.getString("Command.Island.Argument.Unrecognised.Message"));
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "stackable";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Admin.Stackable.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"stackables"};
    }

    @Override
    public String[] getArguments() {
        return new String[]{"setsize"};
    }
}
