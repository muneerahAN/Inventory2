package com.exaple.android.inventory;


public class Product {

    private int mProductId;
    private String mProductName;
    private String mProductQuantity;
    private String mProductPrice;
    private byte[] mProductImage;
    private int mSupplierId;


    // constructor
    public Product(int productId, String productName, String productQuantity, String productPrice, byte[] productImage, int supplierId) {
        mProductId = productId;
        mProductName = productName;
        mProductQuantity = productQuantity;
        mProductPrice = productPrice;
        mProductImage = productImage;
        mSupplierId = supplierId;
    }//end constructor


    public int getProductId() {
        return mProductId;
    }

    public String getProductName() {
        return mProductName;
    }

    public String getProductQuantity() {
        return mProductQuantity;
    }

    public String getProductPrice() {
        return mProductPrice;
    }

    public byte[] getProductImage() {
        return mProductImage;
    }

    public int getSupplierId() {
        return mSupplierId;
    }

}//end class
