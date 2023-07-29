package com.songoda.skyblock.utils.player;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Set;

public final class PlayerUtil {
    private PlayerUtil() {
    }

    /**
     * This method will only parse positive numbers and will skip any non digit.
     *
     * <p>
     * An example:
     * <p>
     * <code>int number = getPositiveNumber("abc-123", 0, "abc-123".length())</code>
     * will return 123
     *
     * @return a positive number for the given input String at the start and end
     * index.
     * @throws IndexOutOfBoundsException if (input.length() < start ||
     *                                   input.length() > end) evaluates to true.
     */
    public static int getPositiveNumber(String input, int start, int end) {
        if (input.length() < start || input.length() > end) {
            throw new IndexOutOfBoundsException("");
        }

        int num = 0;
        for (int i = start; i < end; ++i) {
            final char ch = input.charAt(i);

            if (!Character.isDigit(ch)) {
                continue;
            }

            final int digit = Character.getNumericValue(ch);
            num = num * 10 + digit;
        }
        return num;
    }

    public static int getNumberFromPermission(Player player, String permission, boolean bypassPermission, int def) {
        final Set<PermissionAttachmentInfo> permissions = player.getEffectivePermissions();

        if (bypassPermission && player.hasPermission(permission + ".bypass")) {
            return Integer.MAX_VALUE;
        }

        boolean set = false;
        int highest = 0;

        for (PermissionAttachmentInfo info : permissions) {

            final String perm = info.getPermission();

            if (!perm.startsWith(permission)) {
                continue;
            }

            final int index = perm.lastIndexOf('.');

            if (index == -1 || index == perm.length()) {
                continue;
            }

            final int number = getPositiveNumber(perm, index, perm.length());
            if (number >= highest) {
                highest = number;
                set = true;
            }
        }

        return set ? highest : def;
    }
}
