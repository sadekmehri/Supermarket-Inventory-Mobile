package com.example.caisse.singletons;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.caisse.MainActivity;

public class SharedInvoiceSingleton {

    private static final String SHARED_PREF_NAME = "volley_invoice";
    private static final String KEY_INVOICE = "key_invoice";

    @SuppressLint("StaticFieldLeak")
    private static SharedInvoiceSingleton mInstance;

    @SuppressLint("StaticFieldLeak")
    private static Context ctx;

    private SharedInvoiceSingleton(Context context) {
        ctx = context;
    }

    public static synchronized SharedInvoiceSingleton getInstance(Context context) {
        if (mInstance == null) mInstance = new SharedInvoiceSingleton(context);
        return mInstance;
    }

    /* Store the last invoice number in shared preferences */
    public void save(String invoiceNumber) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_INVOICE, invoiceNumber);
        editor.apply();
    }

    /* Give the current invoice number details */
    public String getInvoiceNumber() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_INVOICE, null);
    }

    /* Clear */
    public void clear() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        ctx.startActivity(new Intent(ctx, MainActivity.class));
    }


}
