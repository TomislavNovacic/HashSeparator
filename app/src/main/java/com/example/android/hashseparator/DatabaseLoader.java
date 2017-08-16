package com.example.android.hashseparator;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class DatabaseLoader extends AsyncTaskLoader<String> {

    private Webpage mWebpage;
    private static final String PREFS_NAME = "SharedPreferences_name";
    private static final String STORAGE_DATABASE = "Database";
    private static final String STORAGE_SHARED_PREFS = "Shared Preferences";
    private static final String SHARED_PREFS_WEBPAGE_LIST = "Webpage List";
    ArrayList<Webpage> webpageListSharedPreferences;
    Context mContext;

    public DatabaseLoader(Context context, Webpage webpage) {
        super(context);
        byte[] hash = webpage.getHash();
        mWebpage = webpage;
        mContext = context;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public String loadInBackground() {
        DbHelper db = new DbHelper(mContext);
        if (mWebpage.getHash()[0] % 2 == 0) {
            mWebpage.setStorage(STORAGE_DATABASE);
            db.addWebpage(mWebpage);
        } else {
            mWebpage.setStorage(STORAGE_SHARED_PREFS);
            SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
            Gson gson = new Gson();
            String json = settings.getString(SHARED_PREFS_WEBPAGE_LIST, "");
            Type type = new TypeToken<List<Webpage>>(){}.getType();
            if (!(json.equals(""))) {
                webpageListSharedPreferences = gson.fromJson(json, type);
            }
            webpageListSharedPreferences.add(mWebpage);
            SharedPreferences.Editor editor = settings.edit();
            Gson gson2 = new Gson();
            String json2 = gson2.toJson(webpageListSharedPreferences);
            editor.putString(SHARED_PREFS_WEBPAGE_LIST, json2);
            editor.apply();
        }
        return mWebpage.getStorage();
    }
}
