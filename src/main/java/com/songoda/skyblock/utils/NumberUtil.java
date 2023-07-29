package com.songoda.skyblock.utils;

import java.util.Date;

public final class NumberUtil {
    public static long[] getDuration(int time) {
        long seconds = time % 60;
        long minutes = time % 3600 / 60;
        long hours = time % 86400 / 3600;
        long days = time / 86400;

        return new long[]{days, hours, minutes, seconds};
    }

    public static long[] getDuration(Date startDate, Date endDate) {
        long different = endDate.getTime() - startDate.getTime();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        return new long[]{elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds};
    }
}
