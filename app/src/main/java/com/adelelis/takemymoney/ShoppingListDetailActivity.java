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
import android.widget.TextView;
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

public class ShoppingListDetailActivity extends AppCompatActivity implements RequestAPIResultReceiver.Receiver, ProductTransferInterface {

    private String url, token, shoppingListId, shoppingListName;
    private RequestAPIResultReceiver mReceiver;
    private Activity act = this;
    private Button addProduct;
    private TextView shoppingListNameTextView;
    private String requestType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list_detail);

        Bundle b = getIntent().getExtras();
        shoppingListId = b.getString("idSL");
        shoppingListName = b.getString("nameSL");
        shoppingListNameTextView = (TextView) findViewById(R.id.shoppingsListTitle);

        shoppingListNameTextView.setText(shoppingListName);
        try {
            JSONObject userInfos = getUserInfos();
            token = userInfos.getString("token");

            getProductList();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        requestApi(url);

        addProduct = (Button) findViewById(R.id.addProduct);
        addProduct.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addProductDialog();
            }
        });

    }

    JSONObject getUserInfos(){
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

    void requestApi(String requestUrl){
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
                            if (Objects.equals(requestType, "list")){
                                ArrayList<Product> list = new ArrayList<>();
                                double totalPrice = 0;
                                TextView total = (TextView) findViewById(R.id.totalPrice);

                                if(jObject.getJSONArray("result") != null && jObject.getJSONArray("result").length() > 0){
                                    JSONArray resultArray = jObject.getJSONArray("result");

                                    for (int i = 0; i < resultArray.length() ; i++){
                                        JSONObject product = resultArray.getJSONObject(i);
                                        Product p = new Product(product.getString("id"), product.getString("name"), product.getDouble("price"), product.getInt("quantity"));
                                        totalPrice += product.getDouble("price") * product.getDouble("quantity");
                                        list.add(p);
                                    }

                                    //instantiate custom adapter
                                    ProductCustomAdapter adapter = new ProductCustomAdapter(list, this, this);

                                    //handle listview and assign adapter
                                    ListView lView = (ListView)findViewById(R.id.productLists);
                                    lView.setAdapter(adapter);
                                    total.setText("Total : " + totalPrice + " €");
                                }else{
                                    total.setText("");
                                }

                            }else if (Objects.equals(requestType, "add")){
                                getProductList();
                            }else if (Objects.equals(requestType, "remove")){
                                getProductList();
                            }else if (Objects.equals(requestType, "edit")){
                                getProductList();
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

    void getProductList(){
        url = "http://appspaces.fr/esgi/shopping_list/product/list.php?token="+token+"&shopping_list_id="+shoppingListId;
        requestType = "list";

        requestApi(url);
    }


    private void addProductDialog()
    {
        final Dialog myDialog;
        final EditText productName, productQuantity, productPrice;

        myDialog = new Dialog(this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.add_product);
        myDialog.setCancelable(true);
        Button create = (Button) myDialog.findViewById(R.id.createProductButton);

        productName = (EditText) myDialog.findViewById(R.id.productName);
        productQuantity = (EditText) myDialog.findViewById(R.id.productQuantity);
        productPrice = (EditText) myDialog.findViewById(R.id.productPrice);
        myDialog.show();

        create.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                requestType = "add";
                url = "http://appspaces.fr/esgi/shopping_list/product/create.php" +
                        "?token="+token+
                        "&shopping_list_id="+shoppingListId+
                        "&name="+productName.getText().toString()+
                        "&quantity="+productQuantity.getText().toString()+
                        "&price="+productPrice.getText().toString();
                requestApi(url);
                myDialog.cancel();
            }
        });

    }

    @Override
    public void setValues(Product p, String action) {
        switch (action){
            case "remove":
                url = "http://appspaces.fr/esgi/shopping_list/product/remove.php?token="+token+"&id="+p.getId();
                requestType = "remove";

                requestApi(url);
                break;

            case "edit":
                url = "http://appspaces.fr/esgi/shopping_list/product/edit.php" +
                        "?token="+token+
                        "&id="+p.getId()+
                        "&name="+p.getName()+
                        "&quantity="+p.getQuantity()+
                        "&price="+p.getPrice();
                requestType = "edit";

                requestApi(url);

                break;
        }


    }
}
