package com.example.mobile_project;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SpyingApp extends Application {

    private static final long BACKGROUND_DELAY_MS = 700;

    private int startedActivities = 0;

    private long lastBackgroundTime = 0;

    private long candidateBackgroundTime = 0;

    private boolean shouldShowToast = false;

    private Handler handler;

    private Runnable backgroundRunnable;

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences preferences =
                getSharedPreferences("app_state", MODE_PRIVATE);

        lastBackgroundTime = preferences.getLong(
                "last_background_time",
                0
        );

        handler = new Handler(Looper.getMainLooper());

        backgroundRunnable = () -> {

            if (startedActivities == 0) {

                lastBackgroundTime = candidateBackgroundTime;

                preferences.edit()
                        .putLong(
                                "last_background_time",
                                lastBackgroundTime
                        )
                        .apply();

                shouldShowToast = true;
            }
        };

        registerActivityLifecycleCallbacks(
                new ActivityLifecycleCallbacks() {

                    @Override
                    public void onActivityStarted(Activity activity) {
                        handler.removeCallbacks(backgroundRunnable);
                        if (startedActivities == 0 && shouldShowToast) {
                            showBackgroundToast();
                            shouldShowToast = false;
                        }
                        startedActivities++;
                    }

                    @Override
                    public void onActivityStopped(Activity activity) {
                        startedActivities--;
                        if (startedActivities == 0) {
                            candidateBackgroundTime = System.currentTimeMillis();
                            handler.postDelayed(backgroundRunnable, BACKGROUND_DELAY_MS);
                        }
                    }

                    @Override
                    public void onActivityCreated(
                            Activity activity,
                            Bundle savedInstanceState
                    ) {}
                    @Override
                    public void onActivityResumed(Activity activity) {}
                    @Override
                    public void onActivityPaused(Activity activity) {}
                    @Override
                    public void onActivitySaveInstanceState(
                            Activity activity,
                            Bundle outState
                    ) {}
                    @Override
                    public void onActivityDestroyed(Activity activity) {}
                }
        );
    }

    private void showBackgroundToast() {

        if (lastBackgroundTime == 0) {
            return;
        }
        SimpleDateFormat formatter =
                new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        Locale.getDefault()
                );
        String time = formatter.format(
                new Date(lastBackgroundTime)
        );
        Toast.makeText(
                this,
                "SpyingApp was last backgrounded at " + time,
                Toast.LENGTH_LONG
        ).show();
    }
}