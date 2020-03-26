/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Earthquake>> {

    static final String
//            LOG_TAG = EarthquakeActivity.class.getName(),
            LOADER_TAG = "LOADER_LOGGING",
            USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";

    private ListView earthquakeListView;
    private ImageView iv;
    private ProgressBar progress;

    private EarthquakeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        progress = (ProgressBar) findViewById(R.id.progress);
        earthquakeListView = (ListView) findViewById(R.id.list);
        iv = (ImageView) findViewById(R.id.iv);

        if (earthquakeListView != null) {
            mAdapter = new EarthquakeAdapter(this, new ArrayList<>());
            earthquakeListView.setAdapter(mAdapter);
            earthquakeListView.setOnItemClickListener((adapterView, view, position, l) -> {
                Earthquake earthquake = mAdapter.getItem(position);
                if (earthquake != null)
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(earthquake.getUrl())));
            });
        }

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) getSupportLoaderManager().initLoader(0, null, this);
        else {
            progress.setVisibility(View.GONE);
            iv.setImageResource(R.drawable.no_internet_connection);
            earthquakeListView.setEmptyView(iv);
        }
    }


    /**
     * Menu
     *
     * @param menu preferences
     * @return true
     */
    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * LoaderManager methods
     *
     * @param id
     * @param args
     * @return
     */
    @Override
    public Loader<List<Earthquake>> onCreateLoader(int id, Bundle args) {
        Log.d(LOADER_TAG, "onCreateLoader");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(USGS_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value. For example, the `format=geojson`
        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "50");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        // Return the completed uri `http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&limit=10&minmag=minMagnitude&orderby=time
        return new EarthquakeLoader(this, uriBuilder.toString());

    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakes) {
        Log.d(LOADER_TAG, "onLoadFinished");
        mAdapter.clear();
        if (earthquakes != null && !earthquakes.isEmpty()) mAdapter.addAll(earthquakes);
        progress.setVisibility(View.GONE);
        earthquakeListView.setEmptyView(iv);
    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        Log.d(LOADER_TAG, "onLoaderReset");
        mAdapter.clear();
    }


    /**
     * EarthquakeLoader
     */
    public static class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {

        private String url;

        EarthquakeLoader(Context context, String url) {
            super(context);
            this.url = url;
        }

        @Override
        protected void onStartLoading() {
            Log.d(LOADER_TAG, "onStartLoading");
            forceLoad();
        }

        @Override
        public List<Earthquake> loadInBackground() {
            Log.d(LOADER_TAG, "loadInBackground");
            return url == null ? null : QueryUtils.fetchEarthquakeData(url);
        }
    }
}
