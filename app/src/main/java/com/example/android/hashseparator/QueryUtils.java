package com.example.android.hashseparator;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;


public final class QueryUtils {

    Activity activity;

    private QueryUtils() {
    }

    private static URL createUrl(String stringUrl, Context context) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Toast.makeText(context,"Problem building the webpage URL", Toast.LENGTH_LONG).show();
        }
        return url;
    }

    private static String makeHttpRequest(URL url, Context context) throws IOException {
        String response = "";

        if (url == null) {
            return response;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                response = readFromStream(inputStream);
            } else {
                Toast.makeText(context,"Error response code: " + urlConnection.getResponseCode(), Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Toast.makeText(context,"Problem retrieving the webpage HTML.", Toast.LENGTH_LONG).show();

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (inputStream != null) {
                inputStream.close();
            }
        }
        return response;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static String fetchWebpageData(String requestUrl, Context context) {
        URL url = createUrl(requestUrl, context);
        String response = null;
        try {
            response = makeHttpRequest(url, context);
        } catch (IOException e) {
            Toast.makeText(context,"Problem making the HTTP request.", Toast.LENGTH_LONG).show();
        }
        return response;
    }
}
