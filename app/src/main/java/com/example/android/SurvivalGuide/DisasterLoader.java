package com.example.android.SurvivalGuide;

import android.content.Context;
import java.util.List;

/**
 * Loads a list of disasters by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class DisasterLoader extends android.support.v4.content.AsyncTaskLoader<List<Word>> {

    /** Tag for log messages */
    private static final String LOG_TAG = DisasterLoader.class.getName();

    /** Query URL */
    private String mUrl;

    /**
     * Constructs a new {@link DisasterLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public DisasterLoader(Context context, String url){
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<Word> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of disasters.
        List<Word> disasters = QueryUtils.fetchDisasterData(mUrl);
        return disasters;
    }

}
