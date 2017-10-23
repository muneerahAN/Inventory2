package com.exaple.android.inventory;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Product> listOfProducts;
    private ProductAdapter listAdapter;
    private Cursor cursor;
    private ProductHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //call method to display database info into the list view
        displayDatabaseInfo();

        //fill DB by suppliers if count of rows = 0
        addSuppliers();

        displayProductToSupplierInfo();
        //open add product form
        Button addProduct = (Button) findViewById(R.id.add_product);
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent productDetailsActivity = new Intent(MainActivity.this, ProductDetails.class);
                Bundle bundle = new Bundle();

                bundle.putString("action", "add");
                bundle.putInt("productId", 0);
                bundle.putString("productName", "");
                bundle.putString("productQuantity", "1");
                bundle.putString("productPrice", "");
                bundle.putInt("supplierId", 0);

                Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.placeholder);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte imageInByte[] = stream.toByteArray();

                bundle.putByteArray("productImage", imageInByte);

                productDetailsActivity.putExtras(bundle);
                startActivity(productDetailsActivity);
            }
        });

    }//end onCreate

    private void displayProductToSupplierInfo() {

        SQLiteDatabase readDb = mDbHelper.getReadableDatabase();
        cursor = readDb.query(ProductContract.productToSupplierEntry.TABLE_NAME, null, null, null, null, null, null);

        if (cursor.getCount() > 0) {
            DatabaseUtils.dumpCursor(cursor);
            Log.i("count of pro2Sup rows:", cursor.getCount() + "");
            Toast.makeText(this, " suppliers found in the DB", Toast.LENGTH_LONG).show();
            return;
        } else {
            Log.i("count of pro2Sup rows:", cursor.getCount() + "");
            Toast.makeText(this, " suppliers not found in the DB", Toast.LENGTH_LONG).show();
        }

    }//end displayProductToSupplierInfo

    //fill DB by suppliers if count of rows = 0
    private void addSuppliers() {

        SQLiteDatabase readDb = mDbHelper.getReadableDatabase();
        cursor = readDb.query(ProductContract.supplierEntry.TABLE_NAME, null, null, null, null, null, null);

        if (cursor.getCount() > 0) {
            DatabaseUtils.dumpCursor(cursor);
            Log.i("count of rows:", cursor.getCount() + "");
            Toast.makeText(this, " suppliers found in the DB", Toast.LENGTH_LONG).show();
            return;
        }


        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues value1 = new ContentValues();
        value1.put(ProductContract.supplierEntry.COLUMN_SUPPLIER_FIRST_NAME, "Ahmad");
        value1.put(ProductContract.supplierEntry.COLUMN_SUPPLIER_LAST_NAME, "abdullah");
        value1.put(ProductContract.supplierEntry.COLUMN_SUPPLIER_EMAIL, "Ahmad@domain.com");
        value1.put(ProductContract.supplierEntry.COLUMN_SUPPLIER_PHONE, "0555555555");

        db.insert(ProductContract.supplierEntry.TABLE_NAME, null, value1);

        ContentValues value2 = new ContentValues();
        value2.put(ProductContract.supplierEntry.COLUMN_SUPPLIER_FIRST_NAME, "Waleed");
        value2.put(ProductContract.supplierEntry.COLUMN_SUPPLIER_LAST_NAME, "khalid");
        value2.put(ProductContract.supplierEntry.COLUMN_SUPPLIER_EMAIL, "Waleed@domain.com");
        value2.put(ProductContract.supplierEntry.COLUMN_SUPPLIER_PHONE, "0588888888");

        db.insert(ProductContract.supplierEntry.TABLE_NAME, null, value2);

        //display suppliers info into log to check successful of insert query
        DatabaseUtils.dumpCursor(cursor);
        Log.i("count of rows:", cursor.getCount() + "");
        Toast.makeText(this, "new suppliers added", Toast.LENGTH_LONG).show();

    }//end addSuppliers


    public void displayDatabaseInfo() {

        listOfProducts = new ArrayList<>();

        mDbHelper = new ProductHelper(this);

        String[] projection = {
                ProductContract.productEntry.COLUMN_PRODUCT_ID,
                ProductContract.productEntry.COLUMN_PRODUCT_NAME,
                ProductContract.productEntry.COLUMN_PRODUCT_QUANTITY,
                ProductContract.productEntry.COLUMN_PRODUCT_PRICE,
                ProductContract.productEntry.COLUMN_PRODUCT_IMAGE
        };

        cursor = getContentResolver().query(ProductContract.productEntry.CONTENT_URI, projection, null, null, null);

        TextView empty_db_msg = (TextView) findViewById(R.id.empty_db_msg);
        if (cursor.getCount() == 0) {
            empty_db_msg.setVisibility(View.VISIBLE);
        } else {
            empty_db_msg.setVisibility(View.GONE);
        }

        String rows = "";

        try {
            while (cursor.moveToNext()) {

                int cursorProductId = cursor.getInt(cursor.getColumnIndexOrThrow(ProductContract.productEntry.COLUMN_PRODUCT_ID));
                String cursorProductName = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.productEntry.COLUMN_PRODUCT_NAME));
                String cursorProductQuantity = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.productEntry.COLUMN_PRODUCT_QUANTITY));
                String cursorProductPrice = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.productEntry.COLUMN_PRODUCT_PRICE));
                byte[] cursorProductImage = cursor.getBlob(cursor.getColumnIndexOrThrow(ProductContract.productEntry.COLUMN_PRODUCT_IMAGE));

                int cursorSupplierId = getSupplierId(cursorProductId);

                Product pro = new Product(cursorProductId, cursorProductName, cursorProductQuantity, cursorProductPrice, cursorProductImage, cursorSupplierId);
                listOfProducts.add(pro);
                rows += "\n" + "product id: " + cursorProductId + ", name: " + cursorProductName + ", quantity: " + cursorProductQuantity + ", price: " + cursorProductPrice + ", image: " + cursorProductImage + ", supplier_id: " + cursorSupplierId;

            }//end while

            listAdapter = new ProductAdapter(this, listOfProducts);
            ListView listView = (ListView) findViewById(R.id.list);
            listView.setAdapter(listAdapter);

            Log.i("count of rows:", cursor.getCount() + "");
            Log.i("db contents: ", rows);

        } finally {
            cursor.close();
        }

    }//end displayDatabaseInfo

    protected int getSupplierId(int productId) {
        mDbHelper = new ProductHelper(this);

        String[] projection = {
                ProductContract.productToSupplierEntry.COLUMN_SUPPLIER_ID,
                ProductContract.productToSupplierEntry.COLUMN_PRODUCT_ID
        };

        Cursor cursor2 = getContentResolver().query(ProductContract.productToSupplierEntry.CONTENT_URI, projection, ProductContract.productToSupplierEntry.COLUMN_PRODUCT_ID + "=" + productId, null, null);
        cursor2.moveToFirst();
        int cursorSupplierId = cursor2.getInt(cursor2.getColumnIndexOrThrow(ProductContract.productToSupplierEntry.COLUMN_SUPPLIER_ID));

        return cursorSupplierId;
    }//end getSupplierId

    @Override
    protected void onStart() {
        super.onStart();

        if (!listAdapter.isEmpty()) {
            listAdapter.clear();
        }

        displayDatabaseInfo();
    }//end onStart

}//end class

