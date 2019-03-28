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
package com.example.android.SurvivalGuide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link Fragment} that displays a list of number vocabulary words.
 */
public class AlertsFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<List<Word>>{
    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int DISASTER_LOADER_ID = 1;

    private static final String LOG_TAG = AlertsFragment.class.getName();

    /**
     * URL for earthquake data from the RELIEFWEB dataset
     */
    private static final String RELIEFWEB_REQUEST_URL =
            "https://api.reliefweb.int/v1/disasters?&appname=SurvivalGuide&filter[field]=status&filter[value][]=current&filter[value][]=alert&filter[operator]=OR&preset=latest&limit=100";

    /**
     * Adapter for the list of words
     */
    private WordAdapter mAdapter;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    /** Find a reference to the SwipeRefreshLayout in the layout */
    private android.support.v4.widget.SwipeRefreshLayout mySwipeRefreshLayout;

    public AlertsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.word_list, container, false);

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) rootView.findViewById(R.id.list);

        mySwipeRefreshLayout = (android.support.v4.widget.SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);

        // Create a new adapter that takes an empty list of earthquakes as input
        mAdapter = new WordAdapter(getActivity(), new ArrayList<Word>(), R.color.category_alerts);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);

        mEmptyStateTextView = (TextView)  getActivity().findViewById(R.id.empty_view);
        earthquakeListView.setEmptyView(mEmptyStateTextView);


        /*
         * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */
        mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");

                // This method performs the actual data-refresh operation.
                // The method calls setRefreshing(false) when it's finished.
                startLoader();
                mySwipeRefreshLayout.setRefreshing(false);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            android.support.v4.app.LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(DISASTER_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator =  getActivity().findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mySwipeRefreshLayout = (android.support.v4.widget.SwipeRefreshLayout) getActivity().findViewById(R.id.swiperefresh);

        /*
         * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */
        mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");

                // This method performs the actual data-refresh operation.
                // The method calls setRefreshing(false) when it's finished.
                startLoader();
                mySwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public AsyncTaskLoader<List<Word>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        Uri baseUri = Uri.parse(RELIEFWEB_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);
        return new DisasterLoader(getContext(),  uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<List<Word>> loader, List<Word> words) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = getActivity().findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Clear the adapter of previous earthquake data
        mAdapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (words != null && !words.isEmpty()) {
            mAdapter.addAll(words);
        }

        //check for internet connection
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null ) {
            //state that there is no internet connection
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        } else if (networkInfo!=null && networkInfo.isConnected()){
            //There is internet but list is still empty
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<List<Word>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    private void startLoader() {
        // Get a reference to the LoaderManager, in order to interact with loaders.
        android.support.v4.app.LoaderManager loaderManager = getLoaderManager();
        Log.i("Information","starLoader() called");
        boolean isConnected = isConnected();
        // If network active start fetching data
        if (isConnected) {
            Log.i("Information","Calling initLoader()");
            loaderManager.initLoader(DISASTER_LOADER_ID, null, this).forceLoad();
        } else {
            mEmptyStateTextView.setText("No internet connection");
        }
    }

    private boolean isConnected() {
        // Check for connectivity status
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}