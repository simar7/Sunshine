package com.example.simar.sunshine;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MyActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            // Create a rootView
            View rootView = inflater.inflate(R.layout.fragment_my, container, false);

            // Add some fake data by using an ArrayList of Strings
            ArrayList FakeForecastData = new ArrayList<String>
                    (Arrays.asList("Today | 30C", "Tomorrow | 20C",
                            "Day After | 10C")  );

            // Create an adapter, it takes: Activity, id of the forecast, id of the forecast_textview and fakeData.
            ArrayAdapter<String> ForecastAdapter = new ArrayAdapter<String>
                    (getActivity(),
                    R.layout.list_item_forecast,
                    R.id.list_item_forecast_textview, FakeForecastData);

            // Get the ID of the ListView by traversing the hierarchy at the rootView Node.
            ListView listView  = (ListView) rootView.findViewById(R.id.listview_forecast);

            // Bind the adapter to the ListView.
            listView.setAdapter(ForecastAdapter);

            return rootView;
        }
    }
}
