package com.adelelis.takemymoney;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.adelelis.takemymoney.R.id.login;

public class ShoppingActivity extends AppCompatActivity {

    TextView firstname;
    Button disconnectButton;
    Activity act = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);

        JSONObject userInfos = getUserInfos();

        firstname = (TextView) findViewById(R.id.nom);
        disconnectButton = (Button) findViewById(R.id.disconnect);

        try {
            firstname.setText("Bonjour " + userInfos.getString("firstname"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });


    }


    public JSONObject getUserInfos(){
        try {
            FileInputStream fis = getApplicationContext().openFileInput("user_infos");

            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject object = (JSONObject) new JSONTokener(sb.toString()).nextValue();

            return object;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void disconnect(){
        String FILENAME = "user_infos";
        File file = getBaseContext().getFileStreamPath(FILENAME);
        file.delete();

        Intent sendIntent = new Intent(act, HomeActivity.class);
        startActivity(sendIntent);
    }
}
