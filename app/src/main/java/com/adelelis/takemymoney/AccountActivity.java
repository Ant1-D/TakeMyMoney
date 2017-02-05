package com.adelelis.takemymoney;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class AccountActivity extends AppCompatActivity {

    TextView firstname, lastname, email;
    Button shoppingsListsButton, disconnectButton;
    Activity act = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        JSONObject userInfos = getUserInfos();

        firstname = (TextView) findViewById(R.id.label_firstname);
        lastname = (TextView) findViewById(R.id.label_lastname);
        email = (TextView) findViewById(R.id.label_email);

        disconnectButton = (Button) findViewById(R.id.disconnect);
        shoppingsListsButton = (Button) findViewById(R.id.shoppingsLists);

        try {
            firstname.setText("Prénom : " + userInfos.getString("firstname"));
            lastname.setText("Nom : " + userInfos.getString("lastname"));
            email.setText("Email : " + userInfos.getString("email"));
        } catch (JSONException e) {
            e.printStackTrace();
        }



        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });

        shoppingsListsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent sendIntent = new Intent(act, ShoppingListActivity.class);
            startActivity(sendIntent);
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
            return (JSONObject) new JSONTokener(sb.toString()).nextValue();

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
