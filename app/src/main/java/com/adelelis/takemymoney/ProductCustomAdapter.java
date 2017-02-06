package com.adelelis.takemymoney;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;


class ProductCustomAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<Product> list = new ArrayList<>();
    private Context context;
    private Product editProduct, deletedProduct;
    private ProductTransferInterface dtInterface;

    ProductCustomAdapter(ArrayList<Product> list, Context context, ProductTransferInterface dtInterface) {
        this.list = list;
        this.context = context;
        this.dtInterface = dtInterface;
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
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.edit_list_item, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.listItemString);
        TextView listItemSubtext = (TextView)view.findViewById(R.id.listItemSubstring);
        listItemText.setText(list.get(position).getName());
        listItemSubtext.setText(list.get(position).getQuantity() + " à " + list.get(position).getPrice() + " €");

        //Handle buttons and add onClickListeners
        Button deleteBtn = (Button)view.findViewById(R.id.deleteBtn);
        Button editBtn = (Button)view.findViewById(R.id.editBtn);

        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Open the dialog to confirm deletion
                deletedProduct = list.get(position);
                deleteProductDialog();
            }
        });
        editBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                editProduct = list.get(position);
                editProductDialog();
            }
        });

        return view;
    }

    private void editProductDialog()
    {
        final Dialog myDialog;

        Button edit;
        final EditText productName, productQuantity, productPrice;

        myDialog = new Dialog(context);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.add_product);
        myDialog.setCancelable(true);
        edit = (Button) myDialog.findViewById(R.id.createProductButton);
        productName = (EditText) myDialog.findViewById(R.id.productName);
        productQuantity = (EditText) myDialog.findViewById(R.id.productQuantity);
        productPrice = (EditText) myDialog.findViewById(R.id.productPrice);

        edit.setText(R.string.edit);
        productName.setText(editProduct.getName());
        productQuantity.setText(String.valueOf(editProduct.getQuantity()));
        productPrice.setText(String.valueOf(editProduct.getPrice()));

        myDialog.show();

        edit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editProduct.setName(productName.getText().toString());
                editProduct.setQuantity(Integer.parseInt(productQuantity.getText().toString()));
                editProduct.setPrice(Double.parseDouble(productPrice.getText().toString()));

                dtInterface.setValues(editProduct, "edit");
                notifyDataSetChanged();
                myDialog.cancel();
            }
        });

    }

    private void deleteProductDialog()
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
                list.remove(deletedProduct);
                dtInterface.setValues(deletedProduct, "remove");
                notifyDataSetChanged();
                myDialog.cancel();
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
