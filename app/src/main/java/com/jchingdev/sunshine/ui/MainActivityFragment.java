package com.jchingdev.sunshine.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jchingdev.sunshine.R;
import com.jchingdev.sunshine.WeatherDataParser;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
    final private String VALUE_DAYS ="7";
    final private String VALUE_APPID ="b1b15e88fa797225412429c1c50c122a";

    private ArrayAdapter<String> adapter;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        adapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, new ArrayList<String>());

        listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = adapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(intent);
            }
        });

        return rootView;
    }

    public void fetchWeatherData(String location) {
        new FetchDataTask().execute(location);
    }

    // snippet of code taken and modified from https://gist.github.com/anonymous/1c04bf2423579e9d2dcd
    private class FetchDataTask extends AsyncTask<String, Void, List<String>>{

        @Override
        protected List<String> doInBackground(String... params) {

            // early return on no params
            if (params.length == 0) {
                return null;
            }

            String json = getJsonFromUrl(params[0]);

            //early return if json is null
            if (json == null) {
                return null;
            }

            String[] data = null;
            try {
                data = WeatherDataParser.getWeatherDataFromJson(json, 7);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // early return if data is null
            if (data == null) {
                return null;
            }

            List<String> dataList = new ArrayList<>();

            for (int i = 0; i < data.length; i++) {
                dataList.add(data[i]);
            }

            return dataList;
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            if (strings != null) {
                adapter.clear();
                adapter.addAll(strings);
            }
        }
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
            // sample: http://api.openweathermap.org/data/2.5/forecast/daily?q=toronto&mode=json&units=imperial&cnt=7&appid=44db6a862fba0b067b1930da0d769e98
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String unit = pref.getString(getString(R.string.pref_units_key), getString(R.string.pref_units_default));

            Uri uri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(PARAM_QUERY, location)
                    .appendQueryParameter(PARAM_FORMAT, VALUE_FORMAT)
                    .appendQueryParameter(PARAM_UNITS, unit)
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
