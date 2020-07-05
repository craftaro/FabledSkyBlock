package com.songoda.skyblock.api.utils;

import com.songoda.skyblock.api.island.*;
import com.songoda.skyblock.upgrade.Upgrade;
import com.songoda.skyblock.utils.world.WorldBorder;

public final class APIUtil {

    public static com.songoda.skyblock.island.IslandWorld toImplementation(IslandWorld world) {
        switch (world) {
            case NETHER:
                return com.songoda.skyblock.island.IslandWorld.Nether;
            case OVERWORLD:
                return com.songoda.skyblock.island.IslandWorld.Normal;
            case END:
                return com.songoda.skyblock.island.IslandWorld.End;
        }

        return null;
    }

    public static IslandWorld fromImplementation(com.songoda.skyblock.island.IslandWorld world) {
        switch (world) {
            case Nether:
                return IslandWorld.NETHER;
            case Normal:
                return IslandWorld.OVERWORLD;
            case End:
                return IslandWorld.END;
        }

        return null;
    }

    public static com.songoda.skyblock.island.IslandEnvironment toImplementation(IslandEnvironment environment) {
        switch (environment) {
            case ISLAND:
                return com.songoda.skyblock.island.IslandEnvironment.Island;
            case MAIN:
                return com.songoda.skyblock.island.IslandEnvironment.Main;
            case VISITOR:
                return com.songoda.skyblock.island.IslandEnvironment.Visitor;
        }

        return null;
    }

    public static IslandEnvironment fromImplementation(com.songoda.skyblock.island.IslandEnvironment environment) {
        switch (environment) {
            case Island:
                return IslandEnvironment.ISLAND;
            case Main:
                return IslandEnvironment.MAIN;
            case Visitor:
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
                return com.songoda.skyblock.island.IslandRole.Visitor;
            case COOP:
                return com.songoda.skyblock.island.IslandRole.Coop;
            case MEMBER:
                return com.songoda.skyblock.island.IslandRole.Member;
            case OPERATOR:
                return com.songoda.skyblock.island.IslandRole.Operator;
            case OWNER:
                return com.songoda.skyblock.island.IslandRole.Owner;
        }

        return null;
    }

    public static IslandRole fromImplementation(com.songoda.skyblock.island.IslandRole role) {
        switch (role) {
            case Visitor:
                return IslandRole.VISITOR;
            case Coop:
                return IslandRole.COOP;
            case Member:
                return IslandRole.MEMBER;
            case Operator:
                return IslandRole.OPERATOR;
            case Owner:
                return IslandRole.OWNER;
        }

        return null;
    }

    public static Upgrade.Type toImplementation(IslandUpgrade upgrade) {
        switch (upgrade) {
            case CROP:
                return Upgrade.Type.Crop;
            case DROPS:
                return Upgrade.Type.Drops;
            case FLY:
                return Upgrade.Type.Fly;
            case JUMP:
                return Upgrade.Type.Jump;
            case SIZE:
                return Upgrade.Type.Size;
            case SPAWNER:
                return Upgrade.Type.Spawner;
            case SPEED:
                return Upgrade.Type.Speed;
        }

        return null;
    }

    public static IslandUpgrade fromImplementation(Upgrade.Type upgrade) {
        switch (upgrade) {
            case Crop:
                return IslandUpgrade.CROP;
            case Drops:
                return IslandUpgrade.DROPS;
            case Fly:
                return IslandUpgrade.FLY;
            case Jump:
                return IslandUpgrade.JUMP;
            case Size:
                return IslandUpgrade.SIZE;
            case Spawner:
                return IslandUpgrade.SPAWNER;
            case Speed:
                return IslandUpgrade.SPEED;
        }

        return null;
    }

    public static com.songoda.skyblock.island.IslandMessage toImplementation(IslandMessage message) {
        switch (message) {
            case SIGN:
                return com.songoda.skyblock.island.IslandMessage.Sign;
            case SIGNATURE:
                return com.songoda.skyblock.island.IslandMessage.Signature;
            case WELCOME:
                return com.songoda.skyblock.island.IslandMessage.Welcome;
        }

        return null;
    }

    public static IslandMessage fromImplementation(com.songoda.skyblock.island.IslandMessage message) {
        switch (message) {
            case Sign:
                return IslandMessage.SIGN;
            case Signature:
                return IslandMessage.SIGNATURE;
            case Welcome:
                return IslandMessage.WELCOME;
        }

        return null;
    }

    public static WorldBorder.Color toImplementation(IslandBorderColor color) {
        switch (color) {
            case Blue:
                return WorldBorder.Color.Blue;
            case Green:
                return WorldBorder.Color.Green;
            case Red:
                return WorldBorder.Color.Red;
        }

        return null;
    }

    public static IslandBorderColor fromImplementation(WorldBorder.Color color) {
        switch (color) {
            case Blue:
                return IslandBorderColor.Blue;
            case Green:
                return IslandBorderColor.Green;
            case Red:
                return IslandBorderColor.Red;
        }

        return null;
    }
}
