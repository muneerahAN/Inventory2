package com.exaple.android.inventory;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.exaple.android.inventory.ProductContract.SQL_CREATE_PRODUCT_TABLE;
import static com.exaple.android.inventory.ProductContract.SQL_CREATE_PRODUCT_TO_SUPPLIER_TABLE;
import static com.exaple.android.inventory.ProductContract.SQL_CREATE_SUPPLIER_TABLE;
import static com.exaple.android.inventory.ProductContract.SQL_DELETE_PRODUCT_TABLE;
import static com.exaple.android.inventory.ProductContract.SQL_DELETE_PRODUCT_TO_SUPPLIER_TABLE;
import static com.exaple.android.inventory.ProductContract.SQL_DELETE_SUPPLIER_TABLE;

public class ProductHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "inventory.db";


    public ProductHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQL_CREATE_PRODUCT_TABLE);
        db.execSQL(SQL_CREATE_SUPPLIER_TABLE);
        db.execSQL(SQL_CREATE_PRODUCT_TO_SUPPLIER_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drop table and recreate it
        db.execSQL(SQL_DELETE_PRODUCT_TABLE);
        db.execSQL(SQL_DELETE_SUPPLIER_TABLE);
        db.execSQL(SQL_DELETE_PRODUCT_TO_SUPPLIER_TABLE);

        onCreate(db);
    }
}
