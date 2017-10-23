package com.exaple.android.inventory;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SupplierAdapter extends ArrayAdapter<Supplier> {

    Supplier currentObjItem;
    LayoutInflater flater;


    public SupplierAdapter(Activity context, int resouceId, int textviewId, ArrayList<Supplier> list) {

        super(context, resouceId, textviewId, list);
        flater = context.getLayoutInflater();
    }

    @NonNull
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_supplier_item, parent, false);
        }

        currentObjItem = getItem(position);

        TextView supplier_name = listItemView.findViewById(R.id.supplier_name);
        String supplierFirstName = currentObjItem.getSupplierFirstName();
        String supplierLastName = currentObjItem.getSupplierLastName();
        supplier_name.setText(supplierFirstName + " " + supplierLastName);

        return listItemView;
    }//end get view
}//end class
