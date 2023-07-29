package com.songoda.skyblock.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtil {
    public static String capitalizeUppercaseLetters(String string) {
        StringBuilder stringBuilder = new StringBuilder(string);
        Matcher matcher = Pattern.compile("[A-Z]").matcher(string);
        int extraFeed = 0;

        while (matcher.find()) {
            if (matcher.start() != 0) {
                stringBuilder = stringBuilder.insert(matcher.start() + extraFeed, " ");
                ++extraFeed;
            }
        }

        return stringBuilder.toString();
    }

    public static String capitalizeWord(String str) {
        String[] words = str.split("\\s");
        String capitalizeWord = "";
        for (String w : words) {
            String first = w.substring(0, 1);
            String afterfirst = w.substring(1);
            capitalizeWord += first.toUpperCase() + afterfirst + " ";
        }
        return capitalizeWord.trim();
    }

    public static String color(String input) {
        return input == null ? null : ChatColor.translateAlternateColorCodes('&', input);
    }
}
