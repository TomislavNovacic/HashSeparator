package com.example.android.hashseparator;

import android.app.Activity;
import android.content.AsyncTaskLoader;
import android.content.Context;

public class WebpageLoader extends AsyncTaskLoader<String> {

    private String mUrl;
    private Activity mActivity;

    public WebpageLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public String loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        return QueryUtils.fetchWebpageData(mUrl);
    }
}