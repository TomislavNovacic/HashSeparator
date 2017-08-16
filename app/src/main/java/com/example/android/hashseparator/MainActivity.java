package com.example.android.hashseparator;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String STATUS_OK = "OK";
    private static final String STATUS_NOT_PRESENTED = "NOT PRESENTED";
    private static final int WEBPAGE_LOADER_ID = 1;
    private static final int HASH_LOADER_ID = 2;
    private static final int DATABASE_LOADER_ID = 3;

    EditText url_edittext;
    Button hash_button;
    String insertedURL;
    ArrayList<Webpage> webpageListDatabase;
    ArrayList<Webpage> webpageListSharedPreferences;
    DbHelper db;
    ProgressBar progressBar;
    Webpage webpage;
    String status;
    String networkStatus;
    String exception;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        url_edittext = (EditText) findViewById(R.id.URL_editText);
        hash_button = (Button) findViewById(R.id.generate_hash_button);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        db = new DbHelper(getApplicationContext());

        final Activity activity = this;

        hash_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exception = null;
                insertedURL = url_edittext.getText().toString();
                webpageListDatabase = new ArrayList<>();
                webpageListSharedPreferences = new ArrayList<>();

                status = Controller.checkDatabase(activity, MainActivity.this, db, webpageListDatabase, insertedURL);

                status = Controller.checkSharedPrefs(activity, MainActivity.this, webpageListSharedPreferences, insertedURL);

                if (status.equals(STATUS_NOT_PRESENTED)) {
                    networkStatus = Controller.checkInternetConnection(MainActivity.this);
                    if (networkStatus.equals(STATUS_OK)) {
                        LoaderManager loaderManager = getLoaderManager();
                        loaderManager.restartLoader(WEBPAGE_LOADER_ID, null, webpageLoaderListener);
                    } else {
                        Toast.makeText(getApplicationContext(), "No intenet connection.", Toast.LENGTH_LONG).show();
                    }
                    ;
                }
            }
        });
    }

    private LoaderManager.LoaderCallbacks<String> webpageLoaderListener
            = new LoaderManager.LoaderCallbacks<String>() {
        @Override
        public Loader<String> onCreateLoader(int id, Bundle args) {
            return new WebpageLoader(getApplicationContext(), insertedURL);
        }

        @Override
        public void onLoadFinished(Loader<String> loader, String data) {

            progressBar.setVisibility(View.GONE);
            exception = QueryUtils.catchException();
            if(exception != null) {
                Toast.makeText(getApplicationContext(),exception,Toast.LENGTH_LONG).show();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            LoaderManager HashLoaderManager = getLoaderManager();
            HashLoaderManager.initLoader(HASH_LOADER_ID, null, hashLoaderListener);
        }

        @Override
        public void onLoaderReset(Loader<String> loader) {
        }
    };

    private LoaderManager.LoaderCallbacks<byte[]> hashLoaderListener
            = new LoaderManager.LoaderCallbacks<byte[]>() {
        @Override
        public Loader<byte[]> onCreateLoader(int id, Bundle args) {
            return new HashLoader(getApplicationContext(), insertedURL);
        }

        @Override
        public void onLoadFinished(Loader<byte[]> loader, byte[] hashValue) {
           webpage = new Webpage(insertedURL, hashValue, "");
           LoaderManager DatabaseLoaderManager = getLoaderManager();
           DatabaseLoaderManager.initLoader(DATABASE_LOADER_ID, null, databaseLoaderListener);
        }

        @Override
        public void onLoaderReset(Loader<byte[]> loader) {

        }
    };

    private LoaderManager.LoaderCallbacks<String> databaseLoaderListener
            = new LoaderManager.LoaderCallbacks<String>() {
        @Override
        public Loader<String> onCreateLoader(int id, Bundle args) {
            return new DatabaseLoader(getApplicationContext(), webpage);
        }

        @Override
        public void onLoadFinished(Loader<String> loader, String webpageStorage) {
            webpage.setStorage(webpageStorage);
            Toast.makeText(getApplicationContext(), "URL: " + webpage.getUrl() + "\n" + "Hash code: " + Arrays.toString(webpage.getHash()) + "\n" + "Saved in " + webpage.getStorage(), Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onLoaderReset(Loader<String> loader) {

        }
    };
}
