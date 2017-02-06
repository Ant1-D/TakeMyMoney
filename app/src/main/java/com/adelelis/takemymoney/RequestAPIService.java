package com.adelelis.takemymoney;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;


public class RequestAPIService extends IntentService {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;

    public RequestAPIService() {
        super("RequestAPIService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String requestUrl = intent.getStringExtra("requestUrl");

        String temp, response = "";
        HttpURLConnection urlConnection = null;
        URL url;
        JSONObject object;
        InputStream inStream = null;
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        Bundle bundle = new Bundle();

        try {
            /* Service Started */
            receiver.send(STATUS_RUNNING, Bundle.EMPTY);

            // Create the Request API URL
            url = new URL(requestUrl);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            url = uri.toURL();

            // New Url requestApi with the GET method
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.connect();
            inStream = urlConnection.getInputStream();
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));

            while ((temp = bReader.readLine()) != null) {
                response += temp;
            }
            object = (JSONObject) new JSONTokener(response).nextValue();

            /* Status Finished */
            bundle.putString("result", object.toString());
            receiver.send(STATUS_FINISHED, bundle);

        } catch (Exception e) {
            Log.e("Exception", e.toString());
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException ignored) {
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        this.stopSelf();
    }
}
