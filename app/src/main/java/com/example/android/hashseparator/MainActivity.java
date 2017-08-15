package com.example.android.hashseparator;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "SharedPreferences_name";
    private static final String STORAGE_DATABASE = "Database";
    private static final String STORAGE_SHARED_PREFS = "Shared Preferences";
    private static final String SHARED_PREFS_WEBPAGE_LIST = "Webpage List";

    byte[] hashValue;
    EditText url_edittext;
    Button hash_button;
    String insertedURL;
    ArrayList<Webpage> webpageListDatabase;
    ArrayList<Webpage> webpageListSharedPreferences;
    DbHelper db;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        url_edittext = (EditText) findViewById(R.id.URL_editText);
        hash_button = (Button) findViewById(R.id.generate_hash_button);

        hash_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertedURL = url_edittext.getText().toString();
                if(db != null) {
                    webpageListDatabase = db.getAllWebpages();
                }
                if(!(webpageListDatabase == null)) {
                    for (Webpage webpage : webpageListDatabase) {
                        if (insertedURL.equals(webpage.getUrl())) {
                            hash_button.setEnabled(false);
                            new CountDownTimer(5000, 10) {
                                public void onTick(long millisUntilFinished) {
                                }

                                @Override
                                public void onFinish() {
                                    hash_button.setEnabled(true);
                                }
                            }.start();
                            Toast.makeText(getApplicationContext(), "URL: " + webpage.getUrl() + "\n" + "Hash code: " + Arrays.toString(webpage.getHash()) + "\n" + "Saved in " + webpage.getStorage(), Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }

                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                Gson gson = new Gson();
                String json = settings.getString(SHARED_PREFS_WEBPAGE_LIST, "");
                Type type = new TypeToken<List<Webpage>>(){}.getType();
                if (!(json.equals(""))) {
                webpageListSharedPreferences = gson.fromJson(json, type);
                    for (Webpage webpage : webpageListSharedPreferences) {
                        if (insertedURL.equals(webpage.getUrl())) {
                            hash_button.setEnabled(false);
                            new CountDownTimer(5000, 10) {
                                public void onTick(long millisUntilFinished) {
                                }

                                @Override
                                public void onFinish() {
                                    hash_button.setEnabled(true);
                                }
                            }.start();
                            Toast.makeText(getApplicationContext(), "URL: " + webpage.getUrl() + "\n" + "Hash code: " + Arrays.toString(webpage.getHash()) + "\n" + "Saved in " + webpage.getStorage(), Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }

                WebpageAsyncTask task = new WebpageAsyncTask();
                task.execute(insertedURL);
                db = new DbHelper(getApplicationContext());
            }
        });
        webpageListDatabase = new ArrayList<>();
        webpageListSharedPreferences = new ArrayList<>();
        Handler handler = new Handler();
    }

    private class WebpageAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            return QueryUtils.fetchWebpageData(urls[0], getApplicationContext());
        }

        @Override
        protected void onPostExecute(String data) {
            String exception = QueryUtils.catchException();
            if(exception != null) {
                Toast.makeText(getApplicationContext(),exception,Toast.LENGTH_LONG).show();
            }
            if (data != null && !data.isEmpty()) {
                try {
                    try {
                        hashValue = MD5Hash(data.getBytes("UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                Webpage webpage = new Webpage(insertedURL, hashValue, "");

                Save(webpage);

                Toast.makeText(getApplicationContext(), "URL: " + webpage.getUrl() + "\n" + "Hash code: " + Arrays.toString(webpage.getHash()) + "\n" + "Saved in " + webpage.getStorage(), Toast.LENGTH_LONG).show();
                return;
            }
        }

        private byte[] MD5Hash(byte[] dataBytes) throws NoSuchAlgorithmException {
            if (dataBytes == null) return null;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(dataBytes);
            return md.digest();
        }
        private void Save(Webpage webpage) {
            if (webpage.getHash()[0] % 2 == 0) {
                webpage.setStorage(STORAGE_DATABASE);
                db.addWebpage(webpage);
            } else {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                webpage.setStorage(STORAGE_SHARED_PREFS);
                webpageListSharedPreferences.add(webpage);
                Gson gson = new Gson();
                String json = gson.toJson(webpageListSharedPreferences);
                editor.putString(SHARED_PREFS_WEBPAGE_LIST, json);
                editor.apply();
            }
        }
    }
}
