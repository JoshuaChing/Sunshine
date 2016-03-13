package com.jchingdev.sunshine.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jchingdev.sunshine.FetchWeatherTask;
import com.jchingdev.sunshine.R;

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
        new FetchWeatherTask(getActivity(), adapter).execute(location);
    }

}
