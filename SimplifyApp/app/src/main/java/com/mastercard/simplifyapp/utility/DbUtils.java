package com.mastercard.simplifyapp.utility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by Cillian on 30/06/2017.
 */

public class DbUtils {

    private static TimeZone timeZone = Calendar.getInstance().getTimeZone();

    //generates random uuid for id storage
    public static UUID generateUUID() {
        return UUID.randomUUID();
    }

    //returns current epoch time
    public static long getCurrentEpochTime() {
        return System.currentTimeMillis() / 1000L;
    }

    //Convert epoch time to formatted time
    public static String epochToDate(long epoch){
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone(timeZone.getDisplayName()));
        return format.format(epoch*1000);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
