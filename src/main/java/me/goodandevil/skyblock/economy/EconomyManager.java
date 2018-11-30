package me.goodandevil.skyblock.economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;

public class EconomyManager {

	private Economy economy;
	
	public EconomyManager() {
		setupVault();
	}
	
	public void setupVault() {
		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
	        RegisteredServiceProvider<Economy> registeredServiceProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
	        
	        if (registeredServiceProvider != null) {
	        	economy = registeredServiceProvider.getProvider();
	        }
		}
	}
	
	public double getBalance(Player player) {
		if (economy != null) {
			return economy.getBalance(player);
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
		}
	}
	
	public boolean isEconomy() {
		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		
		return true;
	}
}
