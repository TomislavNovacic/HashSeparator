package com.example.android.hashseparator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;


public final class QueryUtils {

    private static String exception;

    private QueryUtils() {
    }

    private static URL createUrl(String stringUrl) {
        if(stringUrl.length() >= 8) {
            String firstEightCharacters = stringUrl.substring(0, 8);
            String firstSevenCharacters = stringUrl.substring(0, 7);
            if (!(firstEightCharacters.equals("https://")) && !(firstSevenCharacters.equals("http://"))) {
                StringBuilder sb = new StringBuilder();
                sb.append("https://");
                sb.append(stringUrl);
                stringUrl = sb.toString();
            }
        }
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            exception = "Problem building the webpage URL.";
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
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
                exception = "Error response code: " + urlConnection.getResponseCode();
            }
        } catch (IOException e) {
            exception = "Problem retrieving the webpage HTML.";

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

    public static String fetchWebpageData(String requestUrl) {
        URL url = createUrl(requestUrl);
        String response = null;
        try {
            response = makeHttpRequest(url);
        } catch (IOException e) {
            exception = "Problem making the HTTP request.";
        }
        return response;
    }

    public static String catchException() {
        return exception;
    }
}
