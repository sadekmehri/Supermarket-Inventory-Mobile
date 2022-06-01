package com.example.caisse.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;

public class Product implements Serializable {

    private int id;
    private String name;
    private String qrCode;
    private int quantity;
    private float price;
    private float total;


    public Product() {
        this.id = 0;
        this.name = "";
        this.qrCode = "";
        this.quantity = 0;
        this.price = 0f;
        this.total = 0f;
    }

    public Product(int id, String name, String qrCode, int quantity, float price) {
        this.id = id;
        this.name = name;
        this.qrCode = qrCode;
        this.quantity = quantity;
        this.price = price;
        this.total = roundFloat2Decimal(quantity * price);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof Product))
            return false;

        Product product = (Product) obj;
        return id == product.id
                && qrCode.equalsIgnoreCase(product.qrCode);
    }

    private float roundFloat2Decimal(float total) {
        return Math.round(total * 100f) / 100f;
    }

    @NonNull
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", qrCode='" + qrCode + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", total=" + total +
                '}';
    }

    public void calculateTotal() {
        this.total = roundFloat2Decimal(quantity * price);
    }

    public float getTotal() {
        return total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
