package com.example.caisse.utils;

import android.widget.EditText;

public class EditTextUtils {

    public static void clearText(EditText editText) {
        editText.setText("");
    }

    public static void setBgResource(EditText editText, int resource) {
        editText.setBackgroundResource(resource);
    }

}
