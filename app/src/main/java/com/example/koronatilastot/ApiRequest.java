package com.example.koronatilastot;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiRequest extends AsyncTask<URL, Void, String> {
    private static final int CONNECTION_TIMEOUT = 5000;

    @Override
    protected String doInBackground(URL... urls) {
        HttpURLConnection conn = null;
        String response = "";
        BufferedReader bufferedReader = null;

        try {
            conn = (HttpURLConnection)  urls[0].openConnection();
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setReadTimeout(CONNECTION_TIMEOUT);

            conn.setDoInput(true);
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String responseChunk;
                    while ((responseChunk = bufferedReader.readLine()) != null) {
                        response += responseChunk;
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                }
            }


        }
        catch (Exception  e) {
            e.printStackTrace();
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return response;
    }

}
