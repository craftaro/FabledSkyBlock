package me.goodandevil.skyblock.command.commands.island;

import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.menus.Visit;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.version.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class VisitCommand extends SubCommand {

	@Override
	public void onCommandByPlayer(Player player, String[] args) {
		MessageManager messageManager = skyblock.getMessageManager();
		SoundManager soundManager = skyblock.getSoundManager();

		if (args.length == 0) {
			if (!skyblock.getPlayerDataManager().hasPlayerData(player))
				skyblock.getPlayerDataManager().createPlayerData(player);

			PlayerData playerData = skyblock.getPlayerDataManager().getPlayerData(player);
			playerData.setType(Visit.Type.Default);
			playerData.setSort(Visit.Sort.Default);

			Visit.getInstance().open(player, (Visit.Type) playerData.getType(), (Visit.Sort) playerData.getSort());
			soundManager.playSound(player, Sounds.CHEST_OPEN.bukkitSound(), 1.0F, 1.0F);
		} else if (args.length == 1) {
			Bukkit.getServer().getScheduler().runTask(skyblock, () -> Bukkit.getServer().dispatchCommand(player, "island teleport " + args[0]));
		} else {
			messageManager.sendMessage(player,
					skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"))
							.getFileConfiguration().getString("Command.Island.Visit.Invalid.Message"));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		}
	}

	@Override
	public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
		sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
	}

	@Override
	public String getName() {
		return "visit";
	}

	@Override
	public String getInfoMessagePath() {
		return "Command.Island.Visit.Info.Message";
	}

	@Override
	public String[] getAliases() {
		return new String[] { "warps", "explore" };
	}

	@Override
	public String[] getArguments() {
		return new String[0];
	}
}
