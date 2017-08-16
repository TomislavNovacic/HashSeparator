package com.example.android.hashseparator;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Controller {

    private static final String PREFS_NAME = "SharedPreferences_name";
    private static final String SHARED_PREFS_WEBPAGE_LIST = "Webpage List";
    private static final String STATUS_PRESENTED = "PRESENTED";
    private static final String STATUS_NOT_PRESENTED = "NOT PRESENTED";

    private Controller() {
    }

public static String checkDatabase(Activity activity, Context context, DbHelper db, ArrayList<Webpage> webpageListDatabase, String insertedUrl) {
    final Button hashButton = (Button) activity.findViewById(R.id.generate_hash_button);
    if(db != null) {
        webpageListDatabase = db.getAllWebpages();
    }
    if(!(webpageListDatabase == null)) {
        for (Webpage webpage : webpageListDatabase) {
            if (insertedUrl.equals(webpage.getUrl())) {
                hashButton.setEnabled(false);
                new CountDownTimer(5000, 10) {
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        hashButton.setEnabled(true);
                    }
                }.start();
                Toast.makeText(context, "URL: " + webpage.getUrl() + "\n" + "Hash code: " + Arrays.toString(webpage.getHash()) + "\n" + "Saved in " + webpage.getStorage(), Toast.LENGTH_LONG).show();
                return STATUS_PRESENTED;
            }
        }
    }
    return STATUS_NOT_PRESENTED;
}

public static String checkSharedPrefs(Activity activity, Context context, ArrayList<Webpage> webpageListSharedPreferences, String insertedUrl) {
    final Button hashButton = (Button) activity.findViewById(R.id.generate_hash_button);
    SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
    Gson gson = new Gson();
    String json = settings.getString(SHARED_PREFS_WEBPAGE_LIST, "");
    Type type = new TypeToken<List<Webpage>>(){}.getType();
    if (!(json.equals(""))) {
        webpageListSharedPreferences = gson.fromJson(json, type);
        for (Webpage webpage : webpageListSharedPreferences) {
            if (insertedUrl.equals(webpage.getUrl())) {
                hashButton.setEnabled(false);
                new CountDownTimer(5000, 10) {
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        hashButton.setEnabled(true);
                    }
                }.start();
                Toast.makeText(context, "URL: " + webpage.getUrl() + "\n" + "Hash code: " + Arrays.toString(webpage.getHash()) + "\n" + "Saved in " + webpage.getStorage(), Toast.LENGTH_LONG).show();
                return STATUS_PRESENTED;
            }
        }
    }
    return STATUS_NOT_PRESENTED;
}

public static String checkInternetConnection(Context context) {
    String status;
    ConnectivityManager connMgr = (ConnectivityManager)
            context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    if (networkInfo != null && networkInfo.isConnected()) {
        status = "OK";
    } else {
        status = "NO INTERNET";
    }
    return status;
}
}

