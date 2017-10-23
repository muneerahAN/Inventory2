package com.exaple.android.inventory;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ProductDetails extends AppCompatActivity {

    private static int RESULT_LOAD_IMAGE = 1;
    private ProductHelper mDbHelper;
    private ImageView imageView;
    private ArrayList<Supplier> listOfSuppliers;
    private SupplierAdapter listAdapter;
    private Cursor cursor;
    private int supplierId = 0;
    private int indexOfSupplier = 0;
    private Context mContext;
    private PopupWindow mPopupWindow;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        listOfSuppliers = new ArrayList<>();

        Bundle extras = getIntent().getExtras();

        String action = extras.getString("action");
        final int productId = extras.getInt("productId");
        final String productName = extras.getString("productName");
        String productQuantity = extras.getString("productQuantity");
        String productPrice = extras.getString("productPrice");
        byte[] productImage = extras.getByteArray("productImage");
        supplierId = extras.getInt("supplierId");

        //get the spinner from the xml.
        Spinner dropdown = (Spinner) findViewById(R.id.suppliers_spinner);
        listOfSuppliers = getSuppliersList();
        listAdapter = new SupplierAdapter(this, R.layout.list_supplier_item, R.id.supplier_name, listOfSuppliers);
        dropdown.setAdapter(listAdapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                Supplier supplier = listOfSuppliers.get(position);
                supplierId = supplier.getSupplierId();
                Log.i("selected supplierId : ", supplierId + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });

        //set spinner selection for existing product update
        for (int i = 0; i < listOfSuppliers.size(); i++) {
            if (listOfSuppliers.get(i).getSupplierId() == supplierId) {
                dropdown.setSelection(i);
                indexOfSupplier = i;
            }
        }

        //browse for an image
        Button buttonLoadImage = (Button) findViewById(R.id.buttonLoadPicture);
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                verifyStoragePermissions(ProductDetails.this);

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });


        if (action.equals("add")) {
            Button orderFromSuplier = (Button) findViewById(R.id.order_from_suplier);
            orderFromSuplier.setVisibility(View.GONE);

            Button update = (Button) findViewById(R.id.update_product);
            update.setVisibility(View.GONE);

            Button delete = (Button) findViewById(R.id.delete_product);
            delete.setVisibility(View.GONE);

        } else {
            Button add = (Button) findViewById(R.id.add_product);
            add.setVisibility(View.GONE);
        }

        final EditText productNameField = (EditText) findViewById(R.id.product_name);
        productNameField.setText(productName);

        final TextView productQuantityField = (TextView) findViewById(R.id.product_quantity);
        productQuantityField.setText(productQuantity);

        final EditText productPriceField = (EditText) findViewById(R.id.product_price);
        productPriceField.setText(productPrice);

        imageView = (ImageView) findViewById(R.id.product_image);
        ByteArrayInputStream imageStream = new ByteArrayInputStream(productImage);
        Bitmap theImage = BitmapFactory.decodeStream(imageStream);
        imageView.setImageBitmap(theImage);


        //validate fields and add product to DB
        Button addProduct = (Button) findViewById(R.id.add_product);
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                image.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                final byte imageInByte[] = stream.toByteArray();

                if (!productNameField.getText().toString().isEmpty() && !productQuantityField.getText().toString().isEmpty() && !productPriceField.getText().toString().isEmpty()) {
                    AddProduct(productNameField.getText().toString().trim(), productQuantityField.getText().toString().trim(), productPriceField.getText().toString().trim(), imageInByte, supplierId);
                } else {
                    Toast.makeText(ProductDetails.this, "Please fill the required fields", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button updateProduct = (Button) findViewById(R.id.update_product);
        updateProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                image.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                final byte imageInByte[] = stream.toByteArray();

                if (!productNameField.getText().toString().isEmpty() && !productQuantityField.getText().toString().isEmpty() && !productPriceField.getText().toString().isEmpty()) {
                    updateProduct(productId, productNameField.getText().toString().trim(), productQuantityField.getText().toString().trim(), productPriceField.getText().toString().trim(), imageInByte, supplierId);
                } else {
                    Toast.makeText(ProductDetails.this, "Please fill the required fields", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button deleteProduct = (Button) findViewById(R.id.delete_product);
        deleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(ProductDetails.this);
                alert.setTitle("Alert!!");
                alert.setMessage("Are you sure to delete record");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do your work here
                        deleteProduct(productId);
                        dialog.dismiss();

                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        });

        Button orderFromSupplier = (Button) findViewById(R.id.order_from_suplier);
        orderFromSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get the application context
                mContext = getApplicationContext();

                // Get the widgets reference from XML layout
                LinearLayout mLinearLayout = (LinearLayout) findViewById(R.id.parent);

                // Initialize a new instance of LayoutInflater service
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

                // Inflate the custom layout/view
                View customView = inflater.inflate(R.layout.popup, null);

                // Initialize a new instance of popup window
                mPopupWindow = new PopupWindow(
                        customView,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT

                );

                // Set an elevation value for popup window
                // Call requires API level 21
                if (Build.VERSION.SDK_INT >= 21) {
                    mPopupWindow.setElevation(5.0f);
                }

                // Get a reference for the custom view close button
                ImageButton closeButton = customView.findViewById(R.id.ib_close);

                TextView product_name = customView.findViewById(R.id.product_name);
                product_name.setText("Product Name: " + productName);

                TextView supplier_name = customView.findViewById(R.id.supplier_name);
                supplier_name.setText("Supplier Name: " + listOfSuppliers.get(indexOfSupplier).getSupplierFirstName() + " " + listOfSuppliers.get(indexOfSupplier).getSupplierLastName());

                //open call intent for supplier call
                Button callSupplier = customView.findViewById(R.id.call_supplier);
                callSupplier.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String supplierPhoneNumber = listOfSuppliers.get(indexOfSupplier).getSupplierPhone();

                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + supplierPhoneNumber));
                        startActivity(intent);
                    }
                });

                //open email intent for supplier call
                Button emailSupplier = customView.findViewById(R.id.email_supplier);
                emailSupplier.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String supplierEmail = listOfSuppliers.get(indexOfSupplier).getSupplierEmail();

                        Intent intent = new Intent(Intent.ACTION_SENDTO);

                        intent.setData(Uri.parse("mailto:" + supplierEmail));// only email apps should handle this
                        intent.putExtra(Intent.EXTRA_SUBJECT, "New order for: " + productName);

                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                });

                // Set a click listener for the popup window close button
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Dismiss the popup window
                        mPopupWindow.dismiss();
                    }
                });

                // Finally, show the popup window at the center location of root relative layout
                mPopupWindow.showAtLocation(mLinearLayout, Gravity.CENTER, 0, 0);
            }
        });

        //increase quantity by 1 , and quantity can not be more than 10
        Button increaseQuantity = (Button) findViewById(R.id.increase_quantity);
        increaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(productQuantityField.getText().toString());
                if (quantity >= 10) {
                    Toast.makeText(ProductDetails.this, "Quantity can not be more than 10", Toast.LENGTH_LONG).show();
                } else {
                    quantity++;
                    productQuantityField.setText(quantity + "");
                }
            }
        });

        //decrease quantity by 1 , and quantity can not be less than 1
        Button decreaseQuantity = (Button) findViewById(R.id.decrease_quantity);
        decreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(productQuantityField.getText().toString());
                if (quantity <= 0) {
                    Toast.makeText(ProductDetails.this, "Quantity can not be less than 0", Toast.LENGTH_LONG).show();
                } else {
                    quantity--;
                    productQuantityField.setText(quantity + "");
                }
            }
        });

    }//end onCreate

    private ArrayList<Supplier> getSuppliersList() {
        listOfSuppliers = new ArrayList<>();

        mDbHelper = new ProductHelper(this);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                ProductContract.supplierEntry.COLUMN_SUPPLIER_ID,
                ProductContract.supplierEntry.COLUMN_SUPPLIER_FIRST_NAME,
                ProductContract.supplierEntry.COLUMN_SUPPLIER_LAST_NAME,
                ProductContract.supplierEntry.COLUMN_SUPPLIER_EMAIL,
                ProductContract.supplierEntry.COLUMN_SUPPLIER_PHONE
        };

        cursor = db.query(ProductContract.supplierEntry.TABLE_NAME, projection, null, null, null, null, null);

        String rows = "";

        try {
            while (cursor.moveToNext()) {

                int cursorSupplierId = cursor.getInt(cursor.getColumnIndexOrThrow(ProductContract.supplierEntry.COLUMN_SUPPLIER_ID));
                String cursorSupplierFirstName = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.supplierEntry.COLUMN_SUPPLIER_FIRST_NAME));
                String cursorSupplierLastName = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.supplierEntry.COLUMN_SUPPLIER_LAST_NAME));
                String cursorSupplierEmail = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.supplierEntry.COLUMN_SUPPLIER_EMAIL));
                String cursorSupplierPhone = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.supplierEntry.COLUMN_SUPPLIER_PHONE));


                Supplier supplier = new Supplier(cursorSupplierId, cursorSupplierFirstName, cursorSupplierLastName, cursorSupplierEmail, cursorSupplierPhone);
                listOfSuppliers.add(supplier);
                rows += "\n" + "supplier id: " + cursorSupplierId + ", first name: " + cursorSupplierFirstName + ", last name: " + cursorSupplierLastName + ", email: " + cursorSupplierEmail + ", phone: " + cursorSupplierPhone;
            }//end while

            Log.i("db contents: ", rows);

        } finally {
            cursor.close();
        }

        return listOfSuppliers;

    }//end getSuppliersList


    private void AddProduct(String productName, String productQuantity, String productPrice, byte[] productImage, int supplierId) {

        Log.i("supplierId in add : ", supplierId + "");

        mDbHelper = new ProductHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues productValue = new ContentValues();

        //insert product values into the DB, except the Id which is an autoincrement
        productValue.put(ProductContract.productEntry.COLUMN_PRODUCT_NAME, productName);
        productValue.put(ProductContract.productEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
        productValue.put(ProductContract.productEntry.COLUMN_PRODUCT_PRICE, productPrice);
        productValue.put(ProductContract.productEntry.COLUMN_PRODUCT_IMAGE, productImage);

        long newRowId = db.insert(ProductContract.productEntry.TABLE_NAME, null, productValue);
        int product_Id = (int) newRowId;

        if (newRowId == -1) {
            Toast.makeText(ProductDetails.this, "Error in adding Product", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(ProductDetails.this, "Product added successfully, new row id: " + newRowId, Toast.LENGTH_LONG).show();
        }

        AddProductToSupplier(product_Id, supplierId);
        finish();
    }//end AddProduct


    private void AddProductToSupplier(int productId, int supplier_id) {
        mDbHelper = new ProductHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues supplierToProductValue = new ContentValues();
        supplierToProductValue.put(ProductContract.productToSupplierEntry.COLUMN_PRODUCT_ID, productId);
        supplierToProductValue.put(ProductContract.productToSupplierEntry.COLUMN_SUPPLIER_ID, supplier_id);
        long newRowId2 = db.insert(ProductContract.productToSupplierEntry.TABLE_NAME, null, supplierToProductValue);

        if (newRowId2 == -1) {
            Toast.makeText(ProductDetails.this, "Error in adding Product to supplier", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(ProductDetails.this, "Product2Supplier added successfully, new row id: " + newRowId2, Toast.LENGTH_LONG).show();
        }

        finish();

    }//end AddProductToSupplier


    private void updateProduct(int productId, String productName, String productQuantity, String productPrice, byte[] productImage, int supplierId) {

        mDbHelper = new ProductHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues value = new ContentValues();

        value.put(ProductContract.productEntry.COLUMN_PRODUCT_NAME, productName);
        value.put(ProductContract.productEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
        value.put(ProductContract.productEntry.COLUMN_PRODUCT_PRICE, productPrice);
        value.put(ProductContract.productEntry.COLUMN_PRODUCT_IMAGE, productImage);

        int numOfRowsAffected = db.update(ProductContract.productEntry.TABLE_NAME, value, ProductContract.productEntry.COLUMN_PRODUCT_ID + "=" + productId, null);

        if (numOfRowsAffected == 0) {
            Toast.makeText(this, "Error in updating Product.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Product  updated successfully, num of updated rows: " + numOfRowsAffected, Toast.LENGTH_LONG).show();
        }

        updateProductToSupplier(productId, supplierId);
        finish();

    }//end updateProduct

    private void updateProductToSupplier(int productId, int supplierId) {
        mDbHelper = new ProductHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues productToSupplierValue = new ContentValues();
        productToSupplierValue.put(ProductContract.productToSupplierEntry.COLUMN_SUPPLIER_ID, supplierId);
        int numOfProToSupplierRowsAffected = db.update(ProductContract.productToSupplierEntry.TABLE_NAME, productToSupplierValue, ProductContract.productToSupplierEntry.COLUMN_PRODUCT_ID + "=" + productId, null);

        if (numOfProToSupplierRowsAffected == 0) {
            Toast.makeText(this, "Error in updating Product to supplier.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Product to supplier  updated successfully, num of updated rows: " + numOfProToSupplierRowsAffected, Toast.LENGTH_LONG).show();
        }

        finish();
    }//end updateProductToSupplier


    private void deleteProduct(int productId) {

        mDbHelper = new ProductHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int numOfRowsAffected = db.delete(ProductContract.productEntry.TABLE_NAME, ProductContract.productEntry.COLUMN_PRODUCT_ID + "=" + productId, null);

        if (numOfRowsAffected == 0) {
            Toast.makeText(this, "Error in deleting Product.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Product  deleted successfully, num of deleted rows: " + numOfRowsAffected, Toast.LENGTH_LONG).show();
        }

        finish();
    }//end deleteProduct


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }//end onActivityResult

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }



}//end Class
