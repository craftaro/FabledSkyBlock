package com.songoda.skyblock.listeners;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.challenge.player.PlayerManager;
import com.songoda.skyblock.cooldown.CooldownManager;
import com.songoda.skyblock.cooldown.CooldownType;
import com.songoda.skyblock.invite.Invite;
import com.songoda.skyblock.invite.InviteManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandCoop;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.scoreboard.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class Quit implements Listener {

    private final SkyBlock plugin;

    public Quit(SkyBlock plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        CooldownManager cooldownManager = plugin.getCooldownManager();
        MessageManager messageManager = plugin.getMessageManager();
        InviteManager inviteManager = plugin.getInviteManager();
        IslandManager islandManager = plugin.getIslandManager();
        ScoreboardManager scoreboardManager = plugin.getScoreboardManager();
        PlayerManager challengePlayerManager = plugin.getFabledChallenge().getPlayerManager();

        PlayerData playerData = playerDataManager.getPlayerData(player);

        try {
            playerData.setLastOnline(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
        } catch (Exception ignored) {}

        Island island = islandManager.getIsland(player);

        if (island != null) {
            Set<UUID> islandMembersOnline = islandManager.getMembersOnline(island);

            if (islandMembersOnline.size() == 1) {
                OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(island.getOwnerUUID());
                cooldownManager.setCooldownPlayer(CooldownType.Levelling, offlinePlayer);
                cooldownManager.removeCooldownPlayer(CooldownType.Levelling, offlinePlayer);

                cooldownManager.setCooldownPlayer(CooldownType.Ownership, offlinePlayer);
                cooldownManager.removeCooldownPlayer(CooldownType.Ownership, offlinePlayer);
            } else if (islandMembersOnline.size() == 2) {
                for (UUID islandMembersOnlineList : islandMembersOnline) {
                    if (!islandMembersOnlineList.equals(player.getUniqueId())) {
                        Player targetPlayer = Bukkit.getServer().getPlayer(islandMembersOnlineList);
                        PlayerData targetPlayerData = playerDataManager.getPlayerData(targetPlayer);

                        if (targetPlayerData.isChat()) {
                            targetPlayerData.setChat(false);
                            messageManager.sendMessage(targetPlayer,
                                    plugin.getLanguage().getString("Island.Chat.Untoggled.Message"));
                        }
                    }
                }
            }

            final Island is = island;
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> islandManager.unloadIsland(is, player));
        }

        cooldownManager.setCooldownPlayer(CooldownType.Biome, player);
        cooldownManager.removeCooldownPlayer(CooldownType.Biome, player);

        cooldownManager.setCooldownPlayer(CooldownType.Creation, player);
        cooldownManager.removeCooldownPlayer(CooldownType.Creation, player);

        cooldownManager.setCooldownPlayer(CooldownType.Deletion, player);
        cooldownManager.removeCooldownPlayer(CooldownType.Deletion, player);

        playerDataManager.savePlayerData(player);
        playerDataManager.unloadPlayerData(player);

        boolean offline = true;
        if(island != null && this.plugin.getConfiguration()
                .getBoolean("Island.Challenge.PerIsland", false)){
            if(island.getRole(IslandRole.Member) != null){
                offline = island.getRole(IslandRole.Member).stream().noneMatch(uuid -> Bukkit.getPlayer(uuid) != null && !Bukkit.getPlayer(uuid).isOnline());
            }
            if(offline && island.getRole(IslandRole.Operator) != null){
                if (island.getRole(IslandRole.Operator).stream().anyMatch(uuid -> Bukkit.getPlayer(uuid) != null && !Bukkit.getPlayer(uuid).isOnline())) {
                    offline = false;
                }
            }
            if (offline && island.getRole(IslandRole.Owner) != null &&
                    island.getRole(IslandRole.Owner).stream().anyMatch(uuid -> Bukkit.getPlayer(uuid) != null && !Bukkit.getPlayer(uuid).isOnline())) {
                offline = false;
            }
        }

        if(offline){
            challengePlayerManager.unloadPlayer(player.getUniqueId());
        }

        for (Island islandList : islandManager.getCoopIslands(player)) {
            if (this.plugin.getConfiguration()
                    .getBoolean("Island.Coop.Unload") || islandList.getCoopType(player.getUniqueId()) == IslandCoop.TEMP) {
                islandList.removeCoopPlayer(player.getUniqueId());
            }
        }

        if (playerData != null && playerData.getIsland() != null && islandManager.containsIsland(playerData.getIsland())) {
            island = islandManager.getIsland(Bukkit.getServer().getOfflinePlayer(playerData.getIsland()));

            if (!island.hasRole(IslandRole.Member, player.getUniqueId())
                    && !island.hasRole(IslandRole.Operator, player.getUniqueId())
                    && !island.hasRole(IslandRole.Owner, player.getUniqueId())) {
                final Island is = island;
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> islandManager.unloadIsland(is, null));
            }
        }

        if (inviteManager.hasInvite(player.getUniqueId())) {
            Invite invite = inviteManager.getInvite(player.getUniqueId());
            Player targetPlayer = Bukkit.getServer().getPlayer(invite.getOwnerUUID());

            if (targetPlayer != null) {
                messageManager.sendMessage(targetPlayer,
                        plugin.getLanguage()
                                .getString("Command.Island.Invite.Invited.Sender.Disconnected.Message")
                                .replace("%player", player.getName()));
                plugin.getSoundManager().playSound(targetPlayer,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
            }

            inviteManager.removeInvite(player.getUniqueId());
        }
        scoreboardManager.unregisterPlayer(player);
        // Unload Challenge
        SkyBlock.getInstance().getFabledChallenge().getPlayerManager().unloadPlayer(event.getPlayer().getUniqueId());
    }
}
