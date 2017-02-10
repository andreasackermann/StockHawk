package com.udacity.stockhawk.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by aac on 09.02.17.
 */

public class Helper {

    private final static DecimalFormat percentageFormat;

    private final static DecimalFormat dollarFormat;

    static {
        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");

        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);

    }

    public synchronized  static String formatPercent(float value) {
        return percentageFormat.format(value);
    }

    public synchronized static String formatDollar(float value) {
        return dollarFormat.format(value);
    }


}
