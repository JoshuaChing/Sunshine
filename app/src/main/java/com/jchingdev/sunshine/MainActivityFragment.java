package com.jchingdev.sunshine;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    final private String LOG_TAG = MainActivityFragment.class.getSimpleName();

    final private String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
    final private String PARAM_QUERY = "q";
    final private String PARAM_FORMAT = "mode";
    final private String PARAM_UNITS = "units";
    final private String PARAM_DAYS = "cnt";
    final private String PARAM_APPID ="appid";

    final private String VALUE_FORMAT = "json";
    final private String VALUE_UNITS = "metric";
    final private String VALUE_DAYS ="7";
    final private String VALUE_APPID ="44db6a862fba0b067b1930da0d769e98";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ArrayList<String> data = populateFakeData();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, data);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(adapter);

        return rootView;
    }

    private ArrayList<String> populateFakeData() {
        ArrayList<String> data = new ArrayList<>();
        data.add("Today - Sunny - 88/63");
        data.add("Tomorrow - Sunny - 88/63");
        data.add("Wed - Sunny - 88/63");
        data.add("Thurs - Sunny - 88/63");
        data.add("Fri - Sunny - 88/63");
        data.add("Sat - Sunny - 88/63");
        data.add("Sun - Sunny - 88/63");
        return data;
    }

    // snippet of code taken and modified from https://gist.github.com/anonymous/1c04bf2423579e9d2dcd
    private class FetchDataTask extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... params) {

            // early return on no params
            if (params.length == 0) {
                return null;
            }

            String json = getJsonFromUrl(params[0]);

            //early return if json is null
            if (json == null) {
                return null;
            }

            System.out.println(json);
            return null;
        }
    }

    public void fetchWeatherData(String location) {
        new FetchDataTask().execute(location);
    }

    private String getJsonFromUrl(String location) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are available at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            Uri uri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(PARAM_QUERY, location)
                    .appendQueryParameter(PARAM_FORMAT, VALUE_FORMAT)
                    .appendQueryParameter(PARAM_UNITS, VALUE_UNITS)
                    .appendQueryParameter(PARAM_DAYS, VALUE_DAYS)
                    .appendQueryParameter(PARAM_APPID, VALUE_APPID)
                    .build();
            URL url = new URL(uri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            return buffer.toString();
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error MalformedURLException: " + e.toString(), e);
            return null;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error IOException: " + e.toString(), e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }
}
