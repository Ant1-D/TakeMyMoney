package com.adelelis.takemymoney;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.File;

public class HomeActivity extends AppCompatActivity {

    private Button loginBtn, subscribeBtn;
    private Activity act = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        loginBtn = (Button) findViewById(R.id.loginButton);
        subscribeBtn = (Button) findViewById(R.id.subscribeButton);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String FILENAME = "user_infos";
                File file = getBaseContext().getFileStreamPath(FILENAME);

                if(file.exists()) {
                    Intent sendIntent = new Intent(act, AccountActivity.class);
                    startActivity(sendIntent);
                } else {
                    Intent sendIntent = new Intent(act, LoginActivity.class);
                    startActivity(sendIntent);
                }
            }
        });

        subscribeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(act, SubscribeActivity.class);
                startActivity(sendIntent);
            }
        });
    }
}
