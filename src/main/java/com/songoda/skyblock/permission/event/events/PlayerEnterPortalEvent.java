package com.songoda.skyblock.permission.event.events;

import com.songoda.skyblock.permission.event.Stoppable;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityPortalEnterEvent;

public class PlayerEnterPortalEvent extends EntityPortalEnterEvent implements Cancellable, Stoppable {

    private boolean isCancelled = false;
    private boolean isStopped = false;

    public PlayerEnterPortalEvent(Player player, Location location) {
        super(player, location);
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    @Override
    public boolean isStopped() {
        return isStopped;
    }

    @Override
    public void setStopped(boolean stopped) {
        isStopped = stopped;
    }
}
