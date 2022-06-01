package com.example.caisse.models;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Cart {

    private final ArrayList<Product> cart;

    public Cart() {
        this.cart = new ArrayList<>();
    }

    /* Add product item to shopping cart */
    public void addItem(Product product) {
        if (isItemExist(product)) return;

        cart.add(product);
    }

    /* Delete existing product from shopping cart */
    public void deleteItem(Product product) {
        if (!isItemExist(product)) return;

        cart.remove(product);
    }

    public float calculateTotal() {
        float total = 0f;
        for (Product product : cart) total += product.getTotal();
        return total;

    }

    /* Check if the product item exists in cart */
    public boolean isItemExist(Product product) {
        return cart.contains(product);
    }

    /* Get shopping cart size */
    public int getSize() {
        return cart.size();
    }

    /* Clear all items from cart */
    public void clearItems() {
        cart.clear();
    }

    public ArrayList<Product> getCart() {
        return cart;
    }

    /* Check if the cart is empty */
    public boolean isEmptyCart() {
        return cart.isEmpty();
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (Product product : cart) {
            stringBuilder.append(product.toString()).append(" \n");
        }

        return "Cart{" +
                "cart=" + cart +
                '}';
    }

}
