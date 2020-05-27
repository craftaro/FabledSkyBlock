package com.songoda.skyblock.utils.version;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class NMSUtil {

    private static final String version;
    private static final int versionNumber;
    private static final int versionReleaseNumber;

    static {

        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        version = packageName.substring(packageName.lastIndexOf('.') + 1) + ".";

        String name = version.substring(3);
        versionNumber = Integer.parseInt(name.substring(0, name.length() - 4));

        versionReleaseNumber = Integer.parseInt(version.substring(version.length() - 2).replace(".", ""));

    }

    public static String getVersion() {
        return version;
    }

    public static int getVersionNumber() {
        return versionNumber;
    }

    public static int getVersionReleaseNumber() {
        return versionReleaseNumber;
    }

    public static Class<?> getNMSClass(String className) {
        try {
            String fullName = "net.minecraft.server." + getVersion() + className;
            return Class.forName(fullName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Class<?> getCraftClass(String className) {
        try {
            String fullName = "org.bukkit.craftbukkit." + getVersion() + className;
            return Class.forName(fullName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Field getField(Class<?> clazz, String name, boolean declared) {
        try {
            Field field;

            if (declared) {
                field = clazz.getDeclaredField(name);
            } else {
                field = clazz.getField(name);
            }

            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getFieldObject(Object object, Field field) {
        try {
            return field.get(object);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setField(Object object, String fieldName, Object fieldValue, boolean declared) {
        try {
            Field field;

            if (declared) {
                field = object.getClass().getDeclaredField(fieldName);
            } else {
                field = object.getClass().getField(fieldName);
            }

            field.setAccessible(true);
            field.set(object, fieldValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
