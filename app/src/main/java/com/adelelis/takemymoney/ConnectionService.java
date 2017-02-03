package com.adelelis.takemymoney;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
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
import java.net.URL;

import static android.app.DownloadManager.STATUS_RUNNING;
import static android.app.DownloadManager.STATUS_SUCCESSFUL;
import static android.content.ContentValues.TAG;
import static android.drm.DrmInfoStatus.STATUS_ERROR;


public class ConnectionService extends IntentService {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    public ConnectionService() {
        super("ConnectionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String connectUrl = intent.getStringExtra("connectUrl");

        String temp, response = "";
        HttpURLConnection urlConnection = null;
        URL url = null;
        JSONObject object = null;
        InputStream inStream = null;

        try {
            // Create the Request API URL
            url = new URL(connectUrl);

            // New Url connection with the GET method
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

            Log.d("BUNDLE Return :", object.toString());

            final ResultReceiver receiver = intent.getParcelableExtra("receiver");
            Bundle bundle = new Bundle();

            /* Service Started */
            receiver.send(STATUS_RUNNING, Bundle.EMPTY);

            /* Status Finished */
            bundle.putString("result", object.toString());
            receiver.send(STATUS_FINISHED, bundle);

            /* Sending error message back to activity */
            bundle.putString(Intent.EXTRA_TEXT, "Error message here..");
            receiver.send(STATUS_ERROR, bundle);



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
