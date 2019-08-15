package me.goodandevil.skyblock.economy;

import me.goodandevil.skyblock.api.event.player.PlayerWithdrawMoneyEvent;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.tnemc.core.Reserve;
import net.tnemc.core.economy.EconomyAPI;
import net.tnemc.core.permissions.PermissionsAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.math.BigDecimal;

public class EconomyManager {

	// Vault
	private Economy vaultEconomy = null;
	private Permission vaultPermission = null;

	// Reserve
	private EconomyAPI reserveEconomy = null;
//	private PermissionsAPI reservePermission = null;

	public EconomyManager() {
		setup();
	}

	public void setup() {
		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
			RegisteredServiceProvider<Economy> economyRsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);

			if (economyRsp != null)
				this.vaultEconomy = economyRsp.getProvider();

			RegisteredServiceProvider<Permission> permissionRsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
			if (permissionRsp != null)
				this.vaultPermission = permissionRsp.getProvider();
		} else if (Bukkit.getServer().getPluginManager().getPlugin("Reserve") != null) {
			if (Reserve.instance().economyProvided())
				this.reserveEconomy = Reserve.instance().economy();

//			if (Reserve.instance().permissionsProvided())
//				this.reservePermission = Reserve.instance().permissions();
		}
	}

	public double getBalance(Player player) {
		if (this.vaultEconomy != null)
			return this.vaultEconomy.getBalance(player);

		if (this.reserveEconomy != null)
			return this.reserveEconomy.getHoldings(player.getUniqueId()).doubleValue();

		return 0;
	}

	public boolean hasBalance(Player player, double money) {
		return this.getBalance(player) >= money;
	}

	public void withdraw(Player player, double money) {
		if (this.vaultEconomy != null)
			this.vaultEconomy.withdrawPlayer(player, money);
		else if (this.reserveEconomy != null)
			this.reserveEconomy.removeHoldings(player.getUniqueId(), new BigDecimal(money));

		Bukkit.getServer().getPluginManager().callEvent(new PlayerWithdrawMoneyEvent(player, money));
	}

	public void deposit(Player player, double money) {
		if (this.vaultEconomy != null)
			this.vaultEconomy.depositPlayer(player, money);
		else if (this.reserveEconomy != null)
			this.reserveEconomy.addHoldings(player.getUniqueId(), new BigDecimal(money));

		Bukkit.getServer().getPluginManager().callEvent(new PlayerWithdrawMoneyEvent(player, money));
	}

	public boolean hasPermission(String world, OfflinePlayer offlinePlayer, String perm) {
		if (this.vaultPermission != null)
			return this.vaultPermission.playerHas(world, offlinePlayer, perm);

//		if (this.reservePermission != null) {
//			// TODO
//		}

		return false;
	}

	public boolean isEconomy() {
		return this.vaultEconomy != null || this.reserveEconomy != null;
	}

	public boolean isPermission() {
		return this.vaultPermission != null/* || this.reservePermission != null*/;
	}
}
