package com.example.caisse.requests;

import java.io.Serializable;

public class PaymentRequest implements Serializable {

    private int[] productsIds;
    private int[] productsQty;
    private int staffId;
    private int storeId;

    public PaymentRequest() {
        this.productsIds = null;
        this.productsQty = null;
        this.staffId = -1;
        this.storeId = -1;
    }

    public PaymentRequest(int[] productsIds, int[] productsQty, int staffId, int storeId) {
        this.productsIds = productsIds;
        this.productsQty = productsQty;
        this.staffId = staffId;
        this.storeId = storeId;
    }

    public int[] getProductsIds() {
        return productsIds;
    }

    public void setProductsIds(int[] productsIds) {
        this.productsIds = productsIds;
    }

    public int[] getProductsQty() {
        return productsQty;
    }

    public void setProductsQty(int[] productsQty) {
        this.productsQty = productsQty;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

}
