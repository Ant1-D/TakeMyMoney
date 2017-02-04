package com.adelelis.takemymoney;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity implements ConnectionResultReceiver.Receiver {

    TextView login, password;
    Button loginBtn;
    String url;
    ConnectionResultReceiver mReceiver;
    Activity act = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = (Button) findViewById(R.id.loginButton);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login = (TextView)findViewById(R.id.login);
                password = (TextView)findViewById(R.id.password);
                url = "http://appspaces.fr/esgi/shopping_list/account/login.php?email=" + login.getText().toString() + "&password=" + password.getText().toString();

                connection(url);
            }
        });
    }


    public void connection(String connectUrl){
        mReceiver = new ConnectionResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, ConnectionService.class);

        intent.putExtra("connectUrl", connectUrl);
        intent.putExtra("receiver", mReceiver);
        startService(intent);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

        switch (resultCode) {
            case ConnectionService.STATUS_RUNNING:

                break;
            case ConnectionService.STATUS_FINISHED:

                String results = resultData.getString("result");

                try {
                    JSONObject jObject = new JSONObject(results);
                    int code = Integer.parseInt(jObject.getString("code"));

                    switch (code){
                        case 0:
                            try {
                                JSONObject resultArray = jObject.getJSONObject("result");

                                String FILENAME = "user_infos";
                                FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                                fos.write(resultArray.toString().getBytes());
                                fos.close();

                                Intent sendIntent = new Intent(act, ShoppingActivity.class);
                                startActivity(sendIntent);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            break;
                        default:
                            Toast.makeText(act, "Identifiants invalides.", Toast.LENGTH_SHORT).show();
                            
                            break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            case ConnectionService.STATUS_ERROR:
                /* Handle the error */
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                break;
        }
    }
}
