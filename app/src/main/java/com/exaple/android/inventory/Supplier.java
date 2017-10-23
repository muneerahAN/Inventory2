package com.exaple.android.inventory;


public class Supplier {

    private int mSupplierId;
    private String mSupplierFirstName;
    private String mSupplierLastName;
    private String mSupplierEmail;
    private String mSupplierPhone;

    // constructor
    public Supplier(int supplierId, String supplierFirstName, String supplierLastName, String supplierEmail, String supplierPhone) {
        mSupplierId = supplierId;
        mSupplierFirstName = supplierFirstName;
        mSupplierLastName = supplierLastName;
        mSupplierEmail = supplierEmail;
        mSupplierPhone = supplierPhone;
    }//end constructor

    public int getSupplierId() {
        return mSupplierId;
    }

    public String getSupplierFirstName() {
        return mSupplierFirstName;
    }

    public String getSupplierLastName() {
        return mSupplierLastName;
    }

    public String getSupplierEmail() {
        return mSupplierEmail;
    }

    public String getSupplierPhone() {
        return mSupplierPhone;
    }

    @Override
    public String toString() {
        return mSupplierFirstName + " " + mSupplierLastName;
    }
}
