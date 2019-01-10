package com.amapolis.RadioactiveDecay.model.utils;

import java.util.concurrent.TimeUnit;

/**
 * This class converts time to an other format.
 */
public class TimeCalc {
    /**
     * Converts seconds to a String with years, days, hours, minutes, seconds, milliseconds and nanoseconds.
     * @param totalSeconds
     * @return
     */
    public static String getTimeAsString(double totalSeconds){
        double years = totalSeconds / 60 / 60 / 24 / 365;
        double days = (years % 1) * 365;
        double hours = (days % 1) * 24;
        double minutes = (hours % 1) * 60;
        double seconds = (minutes % 1 * 60);
        double milliseconds = (seconds % 1 * 1000);
        int nanoseconds = (int) (milliseconds%1 *1000);

        return (int) years  + "y " + (int) days + "d " + (int) hours + "h " + (int) minutes + "min " + (int) seconds + "s " + (int) milliseconds + "ms " + nanoseconds + "µs";
    }
}
