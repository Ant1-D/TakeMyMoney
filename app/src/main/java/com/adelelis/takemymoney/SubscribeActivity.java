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

public class SubscribeActivity extends AppCompatActivity implements RequestAPIResultReceiver.Receiver {

    private Button subscribeButton;
    private TextView firstname, lastname, login, password;
    private RequestAPIResultReceiver mReceiver;
    private Activity act = this;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);

        subscribeButton = (Button) findViewById(R.id.subscribeButton);

        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstname = (TextView) findViewById(R.id.firstname);
                lastname = (TextView) findViewById(R.id.lastname);
                login = (TextView) findViewById(R.id.login);
                password = (TextView) findViewById(R.id.password);

                if(!firstname.getText().toString().equals("") &&
                   !login.getText().toString().equals("") &&
                   !password.getText().toString().equals("")
                ){
                    url = "http://appspaces.fr/esgi/shopping_list/account/subscribe.php?firstname="+ firstname.getText().toString() +"&lastname="+ lastname.getText().toString() +"&email=" + login.getText().toString() + "&password=" + password.getText().toString();

                    subscribe(url);
                }else{
                    Toast.makeText(act, "Veuillez renseigner les champs obligatoires.", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    void subscribe(String requestUrl){
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
                        case 1:
                            Toast.makeText(act, "Veuillez renseigner les champs obligatoires.", Toast.LENGTH_SHORT).show();

                            break;
                        case 2:
                            Toast.makeText(act, "Un compte existe déjà avec cette email.", Toast.LENGTH_SHORT).show();

                            break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
        }

    }
}
