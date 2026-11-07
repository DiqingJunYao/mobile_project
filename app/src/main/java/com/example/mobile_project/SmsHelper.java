package com.example.mobile_project;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class SmsHelper {

    public static boolean sendSms(Context context, String phoneNumber, String message, long messageId) {
        if (phoneNumber == null || phoneNumber.isEmpty() || message == null || message.isEmpty()) {
            return false;
        }
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.SEND_SMS
        ) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        SmsManager smsManager;
        if (android.os.Build.VERSION.SDK_INT >= 31) {
            smsManager = context.getSystemService(SmsManager.class);
        } else {
            smsManager = SmsManager.getDefault();
        }
        if (smsManager == null) {
            return false;
        }
        try {
            ArrayList<String> parts = smsManager.divideMessage(message);
            if (parts.size() == 1) {
                sendInIntent(context, smsManager, phoneNumber, message, messageId);
            } else {
                sendInIntentMultiparts(context, smsManager, phoneNumber, parts, messageId);
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    protected static void sendInIntent(Context context, SmsManager smsManager, String phoneNumber, String message, long messageId) {
        Intent intent = new Intent(context, SmsSentReceiver.class);
        intent.putExtra("message_id", messageId);
        intent.setData(
                Uri.parse("sms-status://message/" + messageId)
        );
        PendingIntent sentIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );
        smsManager.sendTextMessage(
                phoneNumber,
                null,
                message,
                sentIntent,
                null
        );
    }

    protected static void sendInIntentMultiparts(
            Context context,
            SmsManager smsManager,
            String phoneNumber,
            ArrayList<String> parts,
            long messageId
    ) {
        ArrayList<PendingIntent> sentIntents = new ArrayList<>();
        try (DatabaseHelper databaseHelper = new DatabaseHelper(context)) {
            for (int i = 0; i < parts.size(); i++) {
                Intent intent = new Intent(context, SmsSentReceiver.class);

                intent.putExtra("message_id", messageId);
                intent.putExtra("part_index", i);
                intent.putExtra("total_parts", parts.size());

                intent.setData(
                        Uri.parse("sms-status://message/" + messageId + "/part/" + i)
                );
                long result = databaseHelper.insertMultipartMessage(messageId, i, "pending");
                if (result == -1) {
                    Log.e("DATABASE", "Failed to insert multipart message status!");
                    return;
                }
                PendingIntent sentIntent = PendingIntent.getBroadcast(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                );

                sentIntents.add(sentIntent);
            }
            if (databaseHelper.checkMultipartInserted(messageId, parts.size())) {
                smsManager.sendMultipartTextMessage(
                        phoneNumber,
                        null,
                        parts,
                        sentIntents,
                        null
                );
            }
        } catch (Exception e) {
            Log.e("DATABASE", "Failed to insert multipart message status!", e);
        }
    }
}