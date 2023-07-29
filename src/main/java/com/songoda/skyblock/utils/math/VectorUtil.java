package com.songoda.skyblock.utils.math;

import org.bukkit.util.Vector;

public final class VectorUtil {
    public static Vector rotateAroundAxisX(Vector v, double angle) {
        angle = Math.toRadians(angle);

        double y, z, cos, sin;

        cos = Math.cos(angle);
        sin = Math.sin(angle);

        y = v.getY() * cos - v.getZ() * sin;
        z = v.getY() * sin + v.getZ() * cos;

        return v.setY(y).setZ(z);
    }

    public static Vector rotateAroundAxisY(Vector v, double angle) {
        angle = -angle;
        angle = Math.toRadians(angle);

        double x, z, cos, sin;

        cos = Math.cos(angle);
        sin = Math.sin(angle);

        x = v.getX() * cos + v.getZ() * sin;
        z = v.getX() * -sin + v.getZ() * cos;

        return v.setX(x).setZ(z);
    }

    public static Vector rotateAroundAxisZ(Vector v, double angle) {
        angle = Math.toRadians(angle);

        double x, y, cos, sin;

        cos = Math.cos(angle);
        sin = Math.sin(angle);

        x = v.getX() * cos - v.getY() * sin;
        y = v.getX() * sin + v.getY() * cos;

        return v.setX(x).setY(y);
    }
}
