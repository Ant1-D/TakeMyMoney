package com.adelelis.takemymoney;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class ShoppingListCustomAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<ShoppingList> list = new ArrayList<>();
    private Context context;
    private int pos;
    ShoppingListTransferInterface sLInterface;



    public ShoppingListCustomAdapter(ArrayList<ShoppingList> list, Context context, ShoppingListTransferInterface sLInterface) {
        this.list = list;
        this.context = context;
        this.sLInterface = sLInterface;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        pos = position;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.edit_list_item, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.listItemString);
        listItemText.setText(list.get(position).getName());

        //Handle buttons and add onClickListeners
        Button deleteBtn = (Button)view.findViewById(R.id.delete_btn);
        Button editBtn = (Button)view.findViewById(R.id.edit_btn);

        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Open the dialog to confirm deletion
                deleteShoppingListDialog();
            }
        });
        editBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(context, ShoppingListDetailActivity.class);
                sendIntent.putExtra("idSL", list.get(position).getId());
                sendIntent.putExtra("nameSL", list.get(position).getName());
                context.startActivity(sendIntent);
            }
        });

        return view;
    }

    private void deleteShoppingListDialog()
    {
        final Dialog myDialog;

        myDialog = new Dialog(context);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.delete_list_item);
        myDialog.setCancelable(true);
        Button delete = (Button) myDialog.findViewById(R.id.confirmDelete);
        Button cancel = (Button) myDialog.findViewById(R.id.cancelDelete);

        myDialog.show();

        delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                list.remove(pos);
                notifyDataSetChanged();
                myDialog.cancel();

                //TODO: ENVOYER LA SUPPRESSION A L'API

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.cancel();
            }
        });


    }
}
