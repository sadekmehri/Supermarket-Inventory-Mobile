package com.example.caisse.utils;

import android.os.Environment;

public class ConstantsUtils {

    public static final String KEY_URI = "https://caisse-spring-boot.azurewebsites.net/api/v1/";
    public static final String DOWNLOAD_FOLDER = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .getAbsolutePath();

    public static final String INVOICE_FOLDER = DOWNLOAD_FOLDER + "/Invoices/";
    public static final float TVA = 0.07f;
}
