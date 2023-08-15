package com.craftaro.skyblock.permission.event;

public interface Stoppable {
    boolean isStopped();

    void setStopped(boolean stopped);
}
