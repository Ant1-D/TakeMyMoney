package com.adelelis.takemymoney;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

public class ShoppingListActivity extends AppCompatActivity implements RequestAPIResultReceiver.Receiver, ShoppingListTransferInterface {

    private String url, token;
    private RequestAPIResultReceiver mReceiver;
    private Activity act = this;
    private Button addSL;
    private String requestType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        try {
            JSONObject userInfos = getUserInfos();
            token = userInfos.getString("token");

            getShoppingListList();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        requestApi(url);

        addSL = (Button) findViewById(R.id.addList);
        addSL.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addShoppingListDialog();
            }
        });
    }

    private void requestApi(String requestUrl){
        mReceiver = new RequestAPIResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, RequestAPIService.class);

        intent.putExtra("requestUrl", requestUrl);
        intent.putExtra("receiver", mReceiver);
        startService(intent);
    }

    private JSONObject getUserInfos(){
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

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
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
                            if (Objects.equals(requestType, "list")){
                                ArrayList<ShoppingList> list = new ArrayList<>();

                                if(jObject.getJSONArray("result") != null && jObject.getJSONArray("result").length() > 0){
                                    JSONArray resultArray = jObject.getJSONArray("result");

                                    for (int i = 0; i < resultArray.length() ; i++){
                                        JSONObject shoppingList = resultArray.getJSONObject(i);
                                        ShoppingList sL = new ShoppingList(shoppingList.getString("id"), shoppingList.getString("name"));
                                        list.add(sL);
                                    }

                                }

                                //instantiate custom adapter
                                ShoppingListCustomAdapter adapter = new ShoppingListCustomAdapter(list, this, this);

                                //handle listview and assign adapter
                                ListView lView = (ListView)findViewById(R.id.shoppingsLists);
                                lView.setAdapter(adapter);
                            }else if (Objects.equals(requestType, "add")){
                                JSONObject resultArray = jObject.getJSONObject("result");

                                Intent sendIntent = new Intent(act, ShoppingListDetailActivity.class);
                                sendIntent.putExtra("idSL", resultArray.getString("id"));
                                sendIntent.putExtra("nameSL", resultArray.getString("name"));

                                startActivity(sendIntent);
                                getShoppingListList();
                            }else if(Objects.equals(requestType, "remove")){
                                getShoppingListList();
                            }

                            break;
                        default:
                            Toast.makeText(act, "", Toast.LENGTH_SHORT).show();

                            break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            break;
        }
    }

    private void getShoppingListList(){
        url = "http://appspaces.fr/esgi/shopping_list/shopping_list/list.php?token="+token;
        requestType = "list";

        requestApi(url);
    }

    private void addShoppingListDialog()
    {
        final Dialog myDialog;
        final EditText SLName;

        myDialog = new Dialog(this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.add_shopping_list);
        myDialog.setCancelable(true);
        Button create = (Button) myDialog.findViewById(R.id.createSLButton);

        SLName = (EditText) myDialog.findViewById(R.id.slName);
        myDialog.show();

        create.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            requestType = "add";
            url = "http://appspaces.fr/esgi/shopping_list/shopping_list/create.php?token="+token+"&name="+SLName.getText().toString();
            requestApi(url);
                myDialog.cancel();
            }
        });

    }

    @Override
    public void setValues(ShoppingList sL, String action) {
        switch (action){
            case "remove":
                url = "http://appspaces.fr/esgi/shopping_list/shopping_list/remove.php?token="+token+"&id="+sL.getId();
                requestType = "remove";

                requestApi(url);
                break;

            case "edit":


                break;
        }
    }
}
