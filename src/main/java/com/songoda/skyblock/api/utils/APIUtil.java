package com.songoda.skyblock.api.utils;

import com.craftaro.core.world.SWorldBorder;
import com.songoda.skyblock.api.island.IslandBorderColor;
import com.songoda.skyblock.api.island.IslandEnvironment;
import com.songoda.skyblock.api.island.IslandMessage;
import com.songoda.skyblock.api.island.IslandRole;
import com.songoda.skyblock.api.island.IslandStatus;
import com.songoda.skyblock.api.island.IslandUpgrade;
import com.songoda.skyblock.api.island.IslandWorld;
import com.songoda.skyblock.upgrade.Upgrade;

public final class APIUtil {
    public static com.songoda.skyblock.island.IslandWorld toImplementation(IslandWorld world) {
        switch (world) {
            case NETHER:
                return com.songoda.skyblock.island.IslandWorld.NETHER;
            case OVERWORLD:
                return com.songoda.skyblock.island.IslandWorld.NORMAL;
            case END:
                return com.songoda.skyblock.island.IslandWorld.END;
        }

        return null;
    }

    public static IslandWorld fromImplementation(com.songoda.skyblock.island.IslandWorld world) {
        switch (world) {
            case NETHER:
                return IslandWorld.NETHER;
            case NORMAL:
                return IslandWorld.OVERWORLD;
            case END:
                return IslandWorld.END;
        }

        return null;
    }

    public static com.songoda.skyblock.island.IslandEnvironment toImplementation(IslandEnvironment environment) {
        switch (environment) {
            case ISLAND:
                return com.songoda.skyblock.island.IslandEnvironment.ISLAND;
            case MAIN:
                return com.songoda.skyblock.island.IslandEnvironment.MAIN;
            case VISITOR:
                return com.songoda.skyblock.island.IslandEnvironment.VISITOR;
        }

        return null;
    }

    public static IslandEnvironment fromImplementation(com.songoda.skyblock.island.IslandEnvironment environment) {
        switch (environment) {
            case ISLAND:
                return IslandEnvironment.ISLAND;
            case MAIN:
                return IslandEnvironment.MAIN;
            case VISITOR:
                return IslandEnvironment.VISITOR;
        }

        return null;
    }

    public static com.songoda.skyblock.island.IslandStatus toImplementation(IslandStatus status) {
        switch (status) {
            case OPEN:
                return com.songoda.skyblock.island.IslandStatus.OPEN;
            case CLOSED:
                return com.songoda.skyblock.island.IslandStatus.CLOSED;
            case WHITELISTED:
                return com.songoda.skyblock.island.IslandStatus.WHITELISTED;
        }

        return null;
    }

    public static IslandStatus fromImplementation(com.songoda.skyblock.island.IslandStatus status) {
        switch (status) {
            case OPEN:
                return IslandStatus.OPEN;
            case CLOSED:
                return IslandStatus.CLOSED;
            case WHITELISTED:
                return IslandStatus.WHITELISTED;
        }

        return null;
    }

    public static com.songoda.skyblock.island.IslandRole toImplementation(IslandRole role) {
        switch (role) {
            case VISITOR:
                return com.songoda.skyblock.island.IslandRole.VISITOR;
            case COOP:
                return com.songoda.skyblock.island.IslandRole.COOP;
            case MEMBER:
                return com.songoda.skyblock.island.IslandRole.MEMBER;
            case OPERATOR:
                return com.songoda.skyblock.island.IslandRole.OPERATOR;
            case OWNER:
                return com.songoda.skyblock.island.IslandRole.OWNER;
        }

        return null;
    }

    public static IslandRole fromImplementation(com.songoda.skyblock.island.IslandRole role) {
        switch (role) {
            case VISITOR:
                return IslandRole.VISITOR;
            case COOP:
                return IslandRole.COOP;
            case MEMBER:
                return IslandRole.MEMBER;
            case OPERATOR:
                return IslandRole.OPERATOR;
            case OWNER:
                return IslandRole.OWNER;
        }

        return null;
    }

    public static Upgrade.Type toImplementation(IslandUpgrade upgrade) {
        switch (upgrade) {
            case CROP:
                return Upgrade.Type.CROP;
            case DROPS:
                return Upgrade.Type.DROPS;
            case FLY:
                return Upgrade.Type.FLY;
            case JUMP:
                return Upgrade.Type.JUMP;
            case SIZE:
                return Upgrade.Type.SIZE;
            case SPAWNER:
                return Upgrade.Type.SPAWNER;
            case SPEED:
                return Upgrade.Type.SPEED;
        }

        return null;
    }

    public static IslandUpgrade fromImplementation(Upgrade.Type upgrade) {
        switch (upgrade) {
            case CROP:
                return IslandUpgrade.CROP;
            case DROPS:
                return IslandUpgrade.DROPS;
            case FLY:
                return IslandUpgrade.FLY;
            case JUMP:
                return IslandUpgrade.JUMP;
            case SIZE:
                return IslandUpgrade.SIZE;
            case SPAWNER:
                return IslandUpgrade.SPAWNER;
            case SPEED:
                return IslandUpgrade.SPEED;
        }

        return null;
    }

    public static com.songoda.skyblock.island.IslandMessage toImplementation(IslandMessage message) {
        switch (message) {
            case SIGN:
                return com.songoda.skyblock.island.IslandMessage.SIGN;
            case SIGNATURE:
                return com.songoda.skyblock.island.IslandMessage.SIGNATURE;
            case WELCOME:
                return com.songoda.skyblock.island.IslandMessage.WELCOME;
        }

        return null;
    }

    public static IslandMessage fromImplementation(com.songoda.skyblock.island.IslandMessage message) {
        switch (message) {
            case SIGN:
                return IslandMessage.SIGN;
            case SIGNATURE:
                return IslandMessage.SIGNATURE;
            case WELCOME:
                return IslandMessage.WELCOME;
        }

        return null;
    }

    public static SWorldBorder.Color toImplementation(IslandBorderColor color) {
        switch (color) {
            case BLUE:
                return SWorldBorder.Color.Blue;
            case GREEN:
                return SWorldBorder.Color.Green;
            case RED:
                return SWorldBorder.Color.Red;
        }

        return null;
    }

    public static IslandBorderColor fromImplementation(SWorldBorder.Color color) {
        switch (color) {
            case Blue:
                return IslandBorderColor.BLUE;
            case Green:
                return IslandBorderColor.GREEN;
            case Red:
                return IslandBorderColor.RED;
        }

        return null;
    }
}
