package com.example.mobile_project;

import android.content.Context;
import android.view.View;

public class HeaderColorHelper {
    private static final String PREF_NAME = "app_settings";
    private static final String KEY_COLOR = "header_color";

    public static void saveColor(Context context, String colorName) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_COLOR, colorName)
                .apply();
    }

    public static int getColor(Context context) {
        String colorName = context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
        ).getString(KEY_COLOR, "default");
        int colorResource;
        switch (colorName) {

            case "blue":
                colorResource = R.color.header_blue;
                break;

            case "green":
                colorResource = R.color.header_green;
                break;

            case "red":
                colorResource = R.color.header_red;
                break;

            case "purple":
                colorResource = R.color.header_purple;
                break;

            default:
                colorResource = R.color.header_default;
                break;
        }
        return context.getColor(colorResource);
    }

    public static void applyColor(Context context, View header) {
        header.setBackgroundColor(getColor(context));
    }
}