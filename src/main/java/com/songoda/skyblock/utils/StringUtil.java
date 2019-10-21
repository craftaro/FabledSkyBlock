package com.songoda.skyblock.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatColor;

public final class StringUtil {

    public static String capatilizeUppercaseLetters(String string) {
        StringBuilder stringBuilder = new StringBuilder(string);
        Matcher matcher = Pattern.compile("[A-Z]").matcher(string);
        int extraFeed = 0;

        while (matcher.find()) {
            if (matcher.start() != 0) {
                stringBuilder = stringBuilder.insert(matcher.start() + extraFeed, " ");
                extraFeed++;
            }
        }

        return stringBuilder.toString();
    }

    public static String color(String input) {
        return input == null ? null : ChatColor.translateAlternateColorCodes('&', input);
    }
}
