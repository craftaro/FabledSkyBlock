package com.songoda.skyblock.api.invite;

import org.bukkit.entity.Player;

public class IslandInvitation {

    private final Player invited, inviter;
    private final int time;

    public IslandInvitation(Player invited, Player inviter, int time) {
        this.invited = invited;
        this.inviter = inviter;
        this.time = time;
    }

    public Player getInvited() {
        return invited;
    }

    public Player getInviter() {
        return inviter;
    }

    public int getTime() {
        return time;
    }
}
