package com.songoda.skyblock.permission.event.events;

import com.songoda.skyblock.permission.event.Stoppable;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class ProjectileLaunchByPlayerEvent extends ProjectileLaunchEvent implements Cancellable, Stoppable {

    private boolean isCancelled = false;
    private boolean isStopped = false;

    public ProjectileLaunchByPlayerEvent(Entity what) {
        super(what);
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
