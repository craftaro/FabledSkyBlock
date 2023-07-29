package com.songoda.skyblock.utils.player;

import com.eatthepath.uuid.FastUUID;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;
import java.util.UUID;

public final class NameFetcher {
    private NameFetcher() {
    }

    public static Names[] getNames(UUID uuid) throws IOException {
        if (uuid == null) {
            return null;
        }

        Names[] names;

        Scanner jsonScanner = new Scanner((new URL("https://api.mojang.com/user/profiles/" + FastUUID.toString(uuid).replaceAll("-", "") + "/names")).openConnection().getInputStream(), "UTF-8");
        names = new Gson().fromJson(jsonScanner.next(), Names[].class);
        jsonScanner.close();

        return names;
    }

    public static class Names {
        public String name;
        public long changedToAt;

        public String getName() {
            return this.name;
        }

        public Date getChangeDate() {
            return new Date(this.changedToAt);
        }
    }
}
