package com.songoda.skyblock.invite;

import com.songoda.skyblock.SkyBlock;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InviteManager {
    private final Map<UUID, Invite> inviteStorage = new HashMap<>();

    public InviteManager(SkyBlock plugin) {
        new InviteTask(this, plugin).runTaskTimerAsynchronously(plugin, 0L, 20L);
    }

    public Invite createInvite(Player player, Player sender, UUID owner, int time) {
        Invite invite = new Invite(player, sender, owner, time);
        this.inviteStorage.put(player.getUniqueId(), invite);
        return invite;
    }

    public void removeInvite(UUID uuid) {
        this.inviteStorage.remove(uuid);
    }

    public void tranfer(UUID uuid1, UUID uuid2) {
        Map<UUID, Invite> islandInvites = getInvites();

        for (Invite invite : islandInvites.values()) {
            if (invite.getOwnerUUID().equals(uuid1)) {
                invite.setOwnerUUID(uuid2);
            }
        }
    }

    public Map<UUID, Invite> getInvites() {
        return this.inviteStorage;
    }

    public Invite getInvite(UUID uuid) {
        return this.inviteStorage.get(uuid);
    }

    public boolean hasInvite(UUID uuid) {
        return this.inviteStorage.containsKey(uuid);
    }
}
