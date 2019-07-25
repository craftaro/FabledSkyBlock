package me.goodandevil.skyblock.economy;

import me.goodandevil.skyblock.api.event.player.PlayerWithdrawMoneyEvent;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyManager {

	private Economy economy = null;
	private Permission permission = null;

	public EconomyManager() {
		setup();
	}

	public void setup() {
		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
			RegisteredServiceProvider<Economy> economyRsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);

			if (economyRsp != null)
				economy = economyRsp.getProvider();

			RegisteredServiceProvider<Permission> permissionRsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
			if (permissionRsp != null)
				permission = permissionRsp.getProvider();
		}
	}

	public double getBalance(Player player) {
		return economy == null ? 0.0D : economy.getBalance(player);
	}

	public boolean hasBalance(Player player, double money) {
		return getBalance(player) >= money;
	}

	public void withdraw(Player player, double money) {
		if (economy != null)
			economy.withdrawPlayer(player, money);

		Bukkit.getServer().getPluginManager().callEvent(new PlayerWithdrawMoneyEvent(player, money));
	}

	public void deposit(Player player, double money) {
		if (economy != null) {
			economy.depositPlayer(player, money);
		}

		Bukkit.getServer().getPluginManager().callEvent(new PlayerWithdrawMoneyEvent(player, money));
	}

	public boolean hasPermission(String world, OfflinePlayer offlinePlayer, String perm) {
		if (permission != null)
			return permission.playerHas(world, offlinePlayer, perm);
		return false;
	}

	public boolean isEconomy() {
		return economy != null;
	}

	public boolean isPermission() {
		return permission != null;
	}
}
