package com.songoda.skyblock.utils;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultPermissions {

    // Vault
    private static Permission vaultPermission = null;

    static {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Permission> permissionRsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
            if (permissionRsp != null) vaultPermission = permissionRsp.getProvider();
        }
    }

    public static boolean hasPermission(String world, OfflinePlayer offlinePlayer, String perm) {
        if (vaultPermission != null) return vaultPermission.playerHas(world, offlinePlayer, perm);
        return false;
    }
}
