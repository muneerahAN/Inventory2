package com.exaple.android.inventory;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class ProductProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = ProductProvider.class.getSimpleName();
    private static final int PRODUCT = 100;
    private static final int PRODUCT_ID = 101;
    private static final int SUPPLIER = 200;
    private static final int SUPPLIER_ID = 201;
    private static final int PRODUCT_TO_SUPPLIER = 300;
    private static final int PRODUCT_TO_SUPPLIER_ID = 301;


    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCT, PRODUCT);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCT + "/#", PRODUCT_ID);

        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_SUPPLIER, SUPPLIER);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_SUPPLIER + "/#", SUPPLIER_ID);

        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCT_TO_SUPPLIER, PRODUCT_TO_SUPPLIER);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCT_TO_SUPPLIER + "/#", PRODUCT_TO_SUPPLIER_ID);
    }

    private final ThreadLocal<Boolean> mIsInBatchMode = new ThreadLocal<Boolean>();
    private ProductHelper mDbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new ProductHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match) {
            case PRODUCT:
                cursor = db.query(ProductContract.productEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductContract.productEntry.COLUMN_PRODUCT_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ProductContract.productEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case SUPPLIER:
                cursor = db.query(ProductContract.supplierEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SUPPLIER_ID:
                selection = ProductContract.supplierEntry.COLUMN_SUPPLIER_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ProductContract.supplierEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case PRODUCT_TO_SUPPLIER:
                cursor = db.query(ProductContract.productToSupplierEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_TO_SUPPLIER_ID:
                selection = ProductContract.productToSupplierEntry.COLUMN_PRODUCT_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ProductContract.productToSupplierEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("cannot query unknown URI " + uri);
        }

        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        int count = 0;
        switch (match) {
            case PRODUCT:
                count = db.update(ProductContract.productEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                if (count > 0 && !isInBatchMode()) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return count;
            case PRODUCT_ID:
                selection = ProductContract.productEntry.COLUMN_PRODUCT_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                count = db.update(ProductContract.productEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                if (count > 0 && !isInBatchMode()) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return count;
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }

    private boolean isInBatchMode() {
        return mIsInBatchMode.get() != null && mIsInBatchMode.get();
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }
}
