package me.goodandevil.skyblock.economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import me.goodandevil.skyblock.api.event.player.PlayerWithdrawMoneyEvent;
import net.milkbowl.vault.economy.Economy;
import net.nifheim.beelzebu.coins.CoinsAPI;

public class EconomyManager {

	private EconomyPlugin economyPlugin;
	private Economy economy;

	public EconomyManager() {
		setup();
	}

	public void setup() {
		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
			RegisteredServiceProvider<Economy> registeredServiceProvider = Bukkit.getServer().getServicesManager()
					.getRegistration(Economy.class);

			if (registeredServiceProvider != null) {
				economy = registeredServiceProvider.getProvider();
			}

			economyPlugin = EconomyPlugin.Vault;
		} else if (Bukkit.getServer().getPluginManager().getPlugin("Coins") != null) {
			economyPlugin = EconomyPlugin.Coins;
		}
	}

	public double getBalance(Player player) {
		if (economy != null) {
			return economy.getBalance(player);
		} else if (economyPlugin == EconomyPlugin.Coins) {
			return CoinsAPI.getCoins(player.getUniqueId());
		}

		return 0.0D;
	}

	public boolean hasBalance(Player player, double money) {
		if (getBalance(player) >= money) {
			return true;
		}

		return false;
	}

	public void withdraw(Player player, double money) {
		if (economy != null) {
			economy.withdrawPlayer(player, money);
		} else if (economyPlugin == EconomyPlugin.Coins) {
			CoinsAPI.takeCoins(player.getUniqueId(), money);
		}

		Bukkit.getServer().getPluginManager().callEvent(new PlayerWithdrawMoneyEvent(player, money));
	}

	public boolean isEconomy() {
		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null
				&& Bukkit.getServer().getPluginManager().getPlugin("Coins") == null) {
			return false;
		}

		return true;
	}

	public enum EconomyPlugin {

		Vault, Coins;

	}
}
