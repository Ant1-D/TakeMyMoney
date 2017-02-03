package com.adelelis.takemymoney;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity implements ConnectionResultReceiver.Receiver {

    TextView login, password;
    Button loginBtn;
    String url;

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

                Log.d("URL", url);

                connection(url);
            }
        });
    }


    public void connection(String connectUrl){
        Intent intent = new Intent(this, ConnectionService.class);
        intent.putExtra("connectUrl", connectUrl);
        startService(intent);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case ConnectionService.STATUS_RUNNING:

                break;
            case ConnectionService.STATUS_FINISHED:

                String results = resultData.getString("result");

                Log.d("Return :", results);



                break;
            case ConnectionService.STATUS_ERROR:
                /* Handle the error */
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                break;
        }
    }
}
