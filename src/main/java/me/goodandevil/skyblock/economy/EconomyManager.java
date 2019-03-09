package me.goodandevil.skyblock.economy;

import me.goodandevil.skyblock.api.event.player.PlayerWithdrawMoneyEvent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyManager {

	private Economy economy;

	public EconomyManager() {
		setup();
	}

	public void setup() {
		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
			RegisteredServiceProvider<Economy> registeredServiceProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);

			if (registeredServiceProvider != null)
				economy = registeredServiceProvider.getProvider();
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

	public boolean isEconomy() {
		return Bukkit.getServer().getPluginManager().getPlugin("Vault") != null;
	}
}
