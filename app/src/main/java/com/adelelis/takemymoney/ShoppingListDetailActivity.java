package com.adelelis.takemymoney;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class ShoppingListDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list_detail);

        Bundle b = getIntent().getExtras();
        Log.d("ID Shopping List : ", b.toString());

    }
}
