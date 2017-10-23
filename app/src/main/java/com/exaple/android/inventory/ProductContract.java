package com.exaple.android.inventory;


import android.net.Uri;
import android.provider.BaseColumns;

public class ProductContract {

    public static final String CONTENT_AUTHORITY = "com.exaple.android.inventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCT = "product";

    public static final String PATH_SUPPLIER = "supplier";
    public static final String PATH_PRODUCT_TO_SUPPLIER = "product_to_supplier";


    public static final String SQL_CREATE_PRODUCT_TABLE = "CREATE TABLE " + ProductContract.productEntry.TABLE_NAME + "(" +
            ProductContract.productEntry.COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            ProductContract.productEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " + ProductContract.productEntry.COLUMN_PRODUCT_QUANTITY + " TEXT NOT NULL, "
            + ProductContract.productEntry.COLUMN_PRODUCT_PRICE + " TEXT NOT NULL, " + productEntry.COLUMN_PRODUCT_IMAGE + " blob NULL );";

    public static final String SQL_CREATE_SUPPLIER_TABLE = "CREATE TABLE " + ProductContract.supplierEntry.TABLE_NAME + "(" +
            ProductContract.supplierEntry.COLUMN_SUPPLIER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            ProductContract.supplierEntry.COLUMN_SUPPLIER_FIRST_NAME + " TEXT NOT NULL, " + ProductContract.supplierEntry.COLUMN_SUPPLIER_LAST_NAME + " TEXT NOT NULL, "
            + ProductContract.supplierEntry.COLUMN_SUPPLIER_EMAIL + " TEXT NOT NULL, " + supplierEntry.COLUMN_SUPPLIER_PHONE + " TEXT NOT NULL );";

    public static final String SQL_CREATE_PRODUCT_TO_SUPPLIER_TABLE = "CREATE TABLE " + ProductContract.productToSupplierEntry.TABLE_NAME + "(" +
            ProductContract.productToSupplierEntry.COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY NOT NULL, " +
            ProductContract.productToSupplierEntry.COLUMN_SUPPLIER_ID + " INTEGER NOT NULL );";


    public static final String SQL_DELETE_PRODUCT_TABLE = "DROP table IF EXIST " + ProductContract.productEntry.TABLE_NAME + ");";
    public static final String SQL_DELETE_SUPPLIER_TABLE = "DROP table IF EXIST " + ProductContract.supplierEntry.TABLE_NAME + ");";
    public static final String SQL_DELETE_PRODUCT_TO_SUPPLIER_TABLE = "DROP table IF EXIST " + ProductContract.productToSupplierEntry.TABLE_NAME + ");";


    private ProductContract() {
    }

    public static final class productEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCT);

        public static final String TABLE_NAME = "product";
        public static final String COLUMN_PRODUCT_ID = "product_id";
        public static final String COLUMN_PRODUCT_NAME = "product_name";
        public static final String COLUMN_PRODUCT_QUANTITY = "product_quantity";
        public static final String COLUMN_PRODUCT_PRICE = "product_price";
        public static final String COLUMN_PRODUCT_IMAGE = "product_image";

    }//end trackingEntry

    public static final class supplierEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SUPPLIER);

        public static final String TABLE_NAME = "supplier";
        public static final String COLUMN_SUPPLIER_ID = "supplier_id";
        public static final String COLUMN_SUPPLIER_FIRST_NAME = "first_name";
        public static final String COLUMN_SUPPLIER_LAST_NAME = "last_name";
        public static final String COLUMN_SUPPLIER_EMAIL = "email";
        public static final String COLUMN_SUPPLIER_PHONE = "phone";

    }//end supplierEntry

    public static final class productToSupplierEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCT_TO_SUPPLIER);

        public static final String TABLE_NAME = "product_to_supplier";
        public static final String COLUMN_PRODUCT_ID = "product_id";
        public static final String COLUMN_SUPPLIER_ID = "supplier_id";

    }//end supplierEntry

}//END CLASS
