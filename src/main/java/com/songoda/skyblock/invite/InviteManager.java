package com.songoda.skyblock.invite;

import com.songoda.skyblock.SkyBlock;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InviteManager {

    private Map<UUID, Invite> inviteStorage = new HashMap<>();

    public InviteManager(SkyBlock skyblock) {
        new InviteTask(this, skyblock).runTaskTimerAsynchronously(skyblock, 0L, 20L);
    }

    public Invite createInvite(Player player, Player sender, UUID owner, int time) {
        Invite invite = new Invite(player, sender, owner, time);
        inviteStorage.put(player.getUniqueId(), invite);

        return invite;
    }

    public void removeInvite(UUID uuid) {
        if (hasInvite(uuid)) {
            inviteStorage.remove(uuid);
        }
    }

    public void tranfer(UUID uuid1, UUID uuid2) {
        Map<UUID, Invite> islandInvites = getInvites();

        for (UUID islandInviteList : islandInvites.keySet()) {
            Invite invite = islandInvites.get(islandInviteList);

            if (invite.getOwnerUUID().equals(uuid1)) {
                invite.setOwnerUUID(uuid2);
            }
        }
    }

    public Map<UUID, Invite> getInvites() {
        return inviteStorage;
    }

    public Invite getInvite(UUID uuid) {
        if (hasInvite(uuid)) {
            return inviteStorage.get(uuid);
        }

        return null;
    }

    public boolean hasInvite(UUID uuid) {
        return inviteStorage.containsKey(uuid);
    }
}
