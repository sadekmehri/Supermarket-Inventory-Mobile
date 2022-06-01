package com.example.caisse.utils;

import android.widget.TextView;

public class TextViewUtils {

    /* Get text from text view input */
    public static String getText(TextView tv) {
        return tv.getText().toString().trim();
    }

}
