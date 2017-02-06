package com.adelelis.takemymoney;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;

public class LoginActivity extends AppCompatActivity implements RequestAPIResultReceiver.Receiver {

    private TextView login, password;
    private Button loginBtn;
    private String url;
    private RequestAPIResultReceiver mReceiver;
    private Activity act = this;

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


    private void connection(String requestUrl){
        mReceiver = new RequestAPIResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, RequestAPIService.class);

        intent.putExtra("requestUrl", requestUrl);
        intent.putExtra("receiver", mReceiver);
        startService(intent);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

        switch (resultCode) {
            case RequestAPIService.STATUS_RUNNING:

                break;
            case RequestAPIService.STATUS_FINISHED:

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

                                Intent sendIntent = new Intent(act, AccountActivity.class);
                                startActivity(sendIntent);

                            } catch (IOException | JSONException e) {
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
        }
    }
}
