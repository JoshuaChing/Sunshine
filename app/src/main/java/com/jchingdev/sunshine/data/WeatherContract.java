package com.jchingdev.sunshine.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by Joshua on 2016-02-28.
 */
public class WeatherContract {

    public static final String CONTENT_AUTHORITY = "com.jchingdev.sunshine";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_WEATHER = "weather";
    public static final String PATH_LOCATION = "location";

    // code taken from https://github.com/udacity/Sunshine-Version-2/blob/lesson_4_starter_code/data/WeatherContract.java
    // & https://github.com/udacity/Sunshine-Version-2/compare/4.06_test_weather_table...4.07_start_code_content_provider

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

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static final String TABLE_NAME = "location";

        public static final String COLUMN_LOCATION_SETTING = "location_settings";
        public static final String COLUMN_CITY_NAME = "city_name";
        public static final String COLUMN_COORD_LAT = "coord_lat";
        public static final String COLUMN_COORD_LONG = "coord_long";

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class WeatherEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;

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

        public static Uri buildWeatherUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildWeatherLocation(String locationSetting) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
        }

        public static Uri buildWeatherLocationWithStartDate(String locationSetting, long startDate) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).appendQueryParameter(COLUMN_DATE, Long.toString(normalizeDate(startDate))).build();
        }

        public static Uri buildWeatherLocationWithDate(String locationSetting, long date ) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).appendPath(Long.toString(normalizeDate(date))).build();
        }

        public static String getLocationSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static long getDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static long getStartDateFromUri(Uri uri) {
        String dateString = uri.getQueryParameter(COLUMN_DATE);
            if (dateString != null && dateString.length() > 0) {
                return Long.parseLong(dateString);
            } else {
                return 0;
            }
        }

    }
}
