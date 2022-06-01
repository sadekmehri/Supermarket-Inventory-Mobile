package com.example.caisse.singletons;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.caisse.models.Cart;

public class CartSingleton {

    @SuppressLint("StaticFieldLeak")
    private static CartSingleton instance;

    @SuppressLint("StaticFieldLeak")
    private static Context ctx;

    private final Cart cart = new Cart();

    private CartSingleton(Context context) {
        ctx = context;
    }

    public static synchronized CartSingleton getInstance(Context context) {
        if (instance == null) instance = new CartSingleton(context);
        return instance;
    }

    /* Add product item to shopping cart */
    public Cart getCart() {
        return cart;
    }

    public void clear() {
        cart.clearItems();
    }

}
