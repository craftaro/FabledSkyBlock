package me.goodandevil.skyblock.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

public final class NumberUtil {

	public static String formatNumber(long number) {
		return String.format("%,d", number);
	}

	public static String formatNumberByDecimal(double number) {
		DecimalFormat decimalFormat = new DecimalFormat("0.#");
		String withoutDecimal = decimalFormat.format(number), withDecimal = "";

		if (decimalFormat.getDecimalFormatSymbols().getDigit() == '.') {
			if (withoutDecimal.contains(".")) {
				withDecimal = "." + withoutDecimal.split("\\.")[1];
				withoutDecimal = withoutDecimal.replace(withDecimal, "");
			}
		} else if (decimalFormat.getDecimalFormatSymbols().getDigit() == ',') {
			if (withoutDecimal.contains(",")) {
				withDecimal = "," + withoutDecimal.split(",")[1];
				withoutDecimal = withoutDecimal.replace(withDecimal, "");
			}
		}

		if (withDecimal.equals(".0") || withDecimal.equals(",0")) {
			withDecimal = "";
		}

		long itemCostWithoutDecimalValue = Long.valueOf(withoutDecimal);

		return formatNumber(itemCostWithoutDecimalValue) + withDecimal;
	}

	public static String formatNumberBySuffix(long number) {
		if (number < 1000) {
			return "" + number;
		}

		int exp = (int) (Math.log(number) / Math.log(1000));

		return String.format("%.1f%c", number / Math.pow(1000, exp), "kMGTPE".charAt(exp - 1));
	}

	public static long[] getDuration(int time) {
		long seconds = time % 60;
		long minutes = time % 3600 / 60;
		long hours = time % 86400 / 3600;
		long days = time / 86400;

		return new long[] { days, hours, minutes, seconds };
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

		return new long[] { elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds };
	}
}
