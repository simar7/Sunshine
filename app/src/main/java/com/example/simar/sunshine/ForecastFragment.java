//Fucking hax. Please make sure to use auto-refactor from next time.
package com.example.simar.sunshine;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    ArrayAdapter<String> ForecastAdapter = null;
    String[] weekForecastArray;
    ArrayList ForecastData;

    public ForecastFragment() {
    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int itemID = item.getItemId();
        if(itemID == R.id.action_refresh) {
            FetchWeatherTask weatherTask = new FetchWeatherTask();

            String InputURL = "http://api.openweathermap.org/data/2.5/forecast/daily?q=";
            String InputZIPCode = "94043";
            String InputMode = "json";
            String InputUnits = "metric";
            String InputCount = "7";

            weatherTask.execute(InputURL, InputZIPCode, InputMode, InputUnits, InputCount);
            Log.v("sunshine", "The refresh button was pressed: " + itemID);
            return true;
        }
        // xxx: why do we do this?
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        // Create a rootView
        View rootView = inflater.inflate(R.layout.fragment_my, container, false);

        // Add some fake data by using an ArrayList of Strings
        ArrayList FakeForecastData = new ArrayList<String>
                (Arrays.asList("Today | 30C", "Tomorrow | 20C",
                        "Day After | 10C"));


        if (weekForecastArray == null) {
            Log.v("sunshine", "weekForecastArray was empty");
            ForecastData = FakeForecastData;
        }
        else
            ForecastData = new ArrayList<String>(Arrays.asList(weekForecastArray));

        // Create an adapter, it takes: Activity, id of the forecast, id of the forecast_textview and fakeData.
        ForecastAdapter = new ArrayAdapter<String>
                (getActivity(),
                        R.layout.list_item_forecast,
                        R.id.list_item_forecast_textview, ForecastData);

        // Get the ID of the ListView by traversing the hierarchy at the rootView Node.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);

        // Bind the adapter to the ListView.
        listView.setAdapter(ForecastAdapter);

        return rootView;
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPostExecute(String[] strings) {
            ForecastAdapter.notifyDataSetChanged();
        }

        @Override
        protected String[] doInBackground(String... InputParams) {

            final String InputURL = InputParams[0].toString();
            final String InputZIPCode = InputParams[1].toString();
            final String InputMode = InputParams[2].toString();
            final String InputUnits = InputParams[3].toString();
            final String InputCount = InputParams[4].toString();

            /*
                Networking code snippet.
             */
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {


                Uri builtURI = Uri.parse(InputURL).buildUpon()
                        .appendQueryParameter("q", InputZIPCode)
                        .appendQueryParameter("mode", InputMode)
                        .appendQueryParameter("units", InputUnits)
                        .appendQueryParameter("cnt", InputCount)
                        .build();
                String BuiltURI = builtURI.toString();

            /*
              Could do this way as well, but breaks the rest of the parsing code.
                Uri.Builder URIBuilder = new Uri.Builder();
                URIBuilder.scheme("http")
                        .authority(InputURL)
                        .appendPath(InputZIPCode).appendPath("&mode=")
                        .appendPath(InputMode).appendPath("&units=")
                        .appendPath(InputUnits).appendPath("&cnt=")
                        .appendPath(InputCount);
                String BuiltURI = URIBuilder.build().toString();
             */

                Log.v("sunshine", "Built URI: " + BuiltURI);

                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL(BuiltURI);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    forecastJsonStr = null;
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
                    forecastJsonStr = null;
                    return null;
                }

                WeatherDataParser parserObject = new WeatherDataParser();
                try
                {
                    weekForecastArray = parserObject.getWeatherDataFromJson(forecastJsonStr, Integer.parseInt(InputCount));
                    forecastJsonStr = buffer.toString();
                    Log.v("sunshine", forecastJsonStr);
                    return weekForecastArray;
                }
                catch (JSONException e)
                {
                    Log.e("ForecastFragment", "Error", e);
                    return null;
                }

            } catch (IOException e) {
                Log.e("ForecastFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                forecastJsonStr = null;
                return null;

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                    return null;
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
        }
    }
}
