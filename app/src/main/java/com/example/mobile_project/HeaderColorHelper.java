package com.example.mobile_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

public class HeaderColorHelper {

    private static final String PREF_NAME = "app_settings";
    private static final String KEY_COLOR = "header_color_value";

    // Read the saved color.
    public static int getColor(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(KEY_COLOR, context.getColor(R.color.header_default));
    }

    // Save a color.
    private static void saveColor(Context context, int color) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_COLOR, color)
                .apply();
    }

    // Apply the saved color to a header.
    public static void applyColor(Context context, View header) {
        header.setBackgroundColor(getColor(context));
    }

    // Display the color picker.
    public static void showColorPicker(Activity activity, View header) {
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_header_color, null);
        SeekBar colorSeekBar = dialogView.findViewById(R.id.colorSeekBar);
        View colorPreview = dialogView.findViewById(R.id.colorPreview);

        // Get the previously selected color.
        int originalColor = getColor(activity);

        // Convert RGB to HSV to restore the slider position.
        float[] hsv = new float[3];
        Color.colorToHSV(originalColor, hsv);

        // Preserve the original saturation and brightness.
        final float saturation = hsv[1];
        final float brightness = hsv[2];

        // Store the currently previewed color.
        final int[] selectedColor = {originalColor};

        // Initialize the slider before registering the listener.
        colorSeekBar.setProgress(Math.round(hsv[0]));

        // Initialize the preview.
        colorPreview.setBackgroundColor(originalColor);

        colorSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        float[] newHsv = {progress, saturation, brightness};
                        int color = Color.HSVToColor(newHsv);
                        selectedColor[0] = color;

                        // Update immediately.
                        colorPreview.setBackgroundColor(color);
                        header.setBackgroundColor(color);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                }
        );

        new AlertDialog.Builder(activity)
                .setTitle("Header Color")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    saveColor(activity, selectedColor[0]);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    header.setBackgroundColor(originalColor);
                })
                .show();
    }
}