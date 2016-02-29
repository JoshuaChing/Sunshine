package com.jchingdev.sunshine.data;

import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by Joshua on 2016-02-28.
 */
public class WeatherContract {

    // code taken from https://github.com/udacity/Sunshine-Version-2/blob/lesson_4_starter_code/data/WeatherContract.java

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    public static final class LocationEntry implements BaseColumns {

        public static final String TABLE_NAME = "location";

        public static final String COLUMN_LOCATION_SETTINGS = "location_settings";
        public static final String COLUMN_CITY_NAME = "city_name";
        public static final String COLUMN_COORD_LAT = "coord_lat";
        public static final String COLUMN_COORD_LONG = "coord_long";

    }

    public static final class WeatherEntry implements BaseColumns {

        public static final String TABLE_NAME = "weather";

        // foreign key to location table
        public static final String COLUMN_LOCATION_KEY = "location_id";

        public static final String COLUMN_WEATHER_ID = "weather_id";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_SHORT_DESC = "short_desc";
        public static final String COLUMN_MIN_TEMP = "min_temp";
        public static final String COLUMN_MAX_TEMP = "max_temp";
        public static final String COLUMN_HUMIDITY = "humidity";
        public static final String COLUMN_PRESSURE = "pressure";
        public static final String COLUMN_WIND_SPEED = "wind_speed";
        public static final String COLUMN_DEGREES = "degrees";

    }
}
