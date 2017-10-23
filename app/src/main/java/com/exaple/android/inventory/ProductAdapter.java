package com.exaple.android.inventory;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;


public class ProductAdapter extends ArrayAdapter<Product> {

    TextView quantityField;
    String productQuantitiy;
    private Context mcontext;
    private Product currentObjItem;
    private ProductHelper mDbHelper;


    public ProductAdapter(Context context, ArrayList<Product> ObjItems) {
        super(context, 0, ObjItems);
        mcontext = context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_product_item, parent, false);
        }

        currentObjItem = getItem(position);

        TextView name = listItemView.findViewById(R.id.product_name);
        final String productName = currentObjItem.getProductName();
        name.setText("Product name: " + productName);

        quantityField = listItemView.findViewById(R.id.product_quantity);
        productQuantitiy = currentObjItem.getProductQuantity();
        quantityField.setText("Product quantity: " + productQuantitiy);

        TextView price = listItemView.findViewById(R.id.product_price);
        final String productPrice = currentObjItem.getProductPrice();
        price.setText("Product price: " + productPrice);

        ImageView image = listItemView.findViewById(R.id.product_image);
        final byte[] productImage = currentObjItem.getProductImage();

        ByteArrayInputStream imageStream = new ByteArrayInputStream(productImage);

        Bitmap theImage = BitmapFactory.decodeStream(imageStream);

        image.setImageBitmap(theImage);

        final int productId = currentObjItem.getProductId();
        final int supplierId = currentObjItem.getSupplierId();

        //Open product details activity
        Button details = listItemView.findViewById(R.id.details_button);
        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent productDetailsActivity = new Intent(mcontext, ProductDetails.class);
                Bundle bundle = new Bundle();

                bundle.putString("action", "edit");
                bundle.putInt("productId", productId);
                bundle.putString("productName", productName);
                bundle.putString("productQuantity", productQuantitiy);
                bundle.putString("productPrice", productPrice);
                bundle.putByteArray("productImage", productImage);
                bundle.putInt("supplierId", supplierId);


                productDetailsActivity.putExtras(bundle);
                mcontext.startActivity(productDetailsActivity);
            }

        });

        //reduce product quantity by one then refresh Products list
        Button sale = listItemView.findViewById(R.id.sale_button);
        sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(productQuantitiy);
                saleProduct(productId, quantity);
            }

        });

        return listItemView;
    }//end get view

    private void saleProduct(int product_id, int quantity) {

        if (quantity <= 0) {
            Toast.makeText(mcontext, "can not reduce quantity, since it has 0 value", Toast.LENGTH_LONG).show();
            return;
        }

        int newQuantity = quantity - 1;

        mDbHelper = new ProductHelper(mcontext);

        ContentValues value = new ContentValues();

        value.put(ProductContract.productEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);

        int numOfRowsAffected = mcontext.getContentResolver().update(ProductContract.productEntry.CONTENT_URI, value, ProductContract.productEntry.COLUMN_PRODUCT_ID + "=?", new String[]{String.valueOf(product_id)}); //id is the id of the row you wan to update

        if (numOfRowsAffected == 0) {
            Toast.makeText(mcontext, "Error in updating Product quantity", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mcontext, "Product quantity updated successfully, num of updated rows: " + numOfRowsAffected, Toast.LENGTH_LONG).show();
            ((MainActivity) mcontext).displayDatabaseInfo();
        }
    }//end saleProduct
}//end class
