package com.craftaro.skyblock.api.utils;

import com.craftaro.core.world.SWorldBorder;
import com.craftaro.skyblock.api.island.IslandBorderColor;
import com.craftaro.skyblock.api.island.IslandUpgrade;
import com.craftaro.skyblock.api.island.IslandWorld;
import com.craftaro.skyblock.island.IslandEnvironment;
import com.craftaro.skyblock.island.IslandMessage;
import com.craftaro.skyblock.island.IslandRole;
import com.craftaro.skyblock.island.IslandStatus;
import com.craftaro.skyblock.upgrade.Upgrade;

public final class APIUtil {
    public static com.craftaro.skyblock.island.IslandWorld toImplementation(IslandWorld world) {
        switch (world) {
            case NETHER:
                return com.craftaro.skyblock.island.IslandWorld.NETHER;
            case OVERWORLD:
                return com.craftaro.skyblock.island.IslandWorld.NORMAL;
            case END:
                return com.craftaro.skyblock.island.IslandWorld.END;
        }

        return null;
    }

    public static IslandWorld fromImplementation(com.craftaro.skyblock.island.IslandWorld world) {
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

    public static IslandEnvironment toImplementation(com.craftaro.skyblock.api.island.IslandEnvironment environment) {
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

    public static com.craftaro.skyblock.api.island.IslandEnvironment fromImplementation(IslandEnvironment environment) {
        switch (environment) {
            case ISLAND:
                return com.craftaro.skyblock.api.island.IslandEnvironment.ISLAND;
            case MAIN:
                return com.craftaro.skyblock.api.island.IslandEnvironment.MAIN;
            case VISITOR:
                return com.craftaro.skyblock.api.island.IslandEnvironment.VISITOR;
        }

        return null;
    }

    public static IslandStatus toImplementation(com.craftaro.skyblock.api.island.IslandStatus status) {
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

    public static com.craftaro.skyblock.api.island.IslandStatus fromImplementation(IslandStatus status) {
        switch (status) {
            case OPEN:
                return com.craftaro.skyblock.api.island.IslandStatus.OPEN;
            case CLOSED:
                return com.craftaro.skyblock.api.island.IslandStatus.CLOSED;
            case WHITELISTED:
                return com.craftaro.skyblock.api.island.IslandStatus.WHITELISTED;
        }

        return null;
    }

    public static IslandRole toImplementation(com.craftaro.skyblock.api.island.IslandRole role) {
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

    public static com.craftaro.skyblock.api.island.IslandRole fromImplementation(IslandRole role) {
        switch (role) {
            case VISITOR:
                return com.craftaro.skyblock.api.island.IslandRole.VISITOR;
            case COOP:
                return com.craftaro.skyblock.api.island.IslandRole.COOP;
            case MEMBER:
                return com.craftaro.skyblock.api.island.IslandRole.MEMBER;
            case OPERATOR:
                return com.craftaro.skyblock.api.island.IslandRole.OPERATOR;
            case OWNER:
                return com.craftaro.skyblock.api.island.IslandRole.OWNER;
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

    public static IslandMessage toImplementation(com.craftaro.skyblock.api.island.IslandMessage message) {
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

    public static com.craftaro.skyblock.api.island.IslandMessage fromImplementation(IslandMessage message) {
        switch (message) {
            case SIGN:
                return com.craftaro.skyblock.api.island.IslandMessage.SIGN;
            case SIGNATURE:
                return com.craftaro.skyblock.api.island.IslandMessage.SIGNATURE;
            case WELCOME:
                return com.craftaro.skyblock.api.island.IslandMessage.WELCOME;
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
