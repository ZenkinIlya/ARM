package com.qs5501.demo.utils;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

//https://overcoder.net/q/4038/%D0%B7%D0%B0%D0%BA%D1%80%D1%8B%D1%82%D1%8C-%D1%81%D0%BA%D1%80%D1%8B%D1%82%D1%8C-%D0%BF%D1%80%D0%BE%D0%B3%D1%80%D0%B0%D0%BC%D0%BC%D0%BD%D1%83%D1%8E-%D0%BA%D0%BB%D0%B0%D0%B2%D0%B8%D0%B0%D1%82%D1%83%D1%80%D1%83-android
public class KeyboardHider {
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
