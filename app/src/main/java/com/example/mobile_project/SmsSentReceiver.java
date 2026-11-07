package com.example.mobile_project;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

public class SmsSentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        long messageId = intent.getLongExtra("message_id", -1);
        int partIndex = intent.getIntExtra("part_index", 0);
        int totalParts = intent.getIntExtra("total_parts", 1);
        if (messageId == -1) {
            Log.e("SMS_SENT", "Message ID not found!");
            return;
        }
        int result = getResultCode();
        String status;
        switch (result) {
            case Activity.RESULT_OK:
                status = "sent";
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
            case SmsManager.RESULT_ERROR_NO_SERVICE:
            case SmsManager.RESULT_ERROR_NULL_PDU:
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                status = "failed";
                break;
            default:
                status = "failed";
                Log.e("SMS_SENT", "Error code: " + result);
                break;
        }
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        if (totalParts > 1) {
            long updateResult = dbHelper.updateMultipartMessageStatus(messageId, partIndex, status);
            if (updateResult == -1) {
                Log.e("DATABASE", "Failed to update multipart message status!");
                return;
            }
            dbHelper.updateOverallMessages(messageId);
        } else {
            dbHelper.updateMessageStatus(messageId, status);
        }
        Intent updateIntent = new Intent("com.example.mobile_project.SMS_UPDATED");
        updateIntent.setPackage(context.getPackageName());
        context.sendBroadcast(updateIntent);
        Log.d("SMS_SENT", "Message " + messageId + ": " + status);
    }

//    @Override
//    public void onReceive(Context context, Intent intent) {
//
//        long messageId = intent.getLongExtra("message_id", -1);
//
//        if (messageId == -1) {
//            Log.e("SMS_SENT", "Message ID not found!");
//            return;
//        }
//
//        int partIndex = intent.getIntExtra("part_index", 0);
//        int totalParts = intent.getIntExtra("total_parts", 1);
//
//        int result = getResultCode();
//        long updateResult = -1;
//
//        Log.e("SMS_SENT", "Callback received!");
//        Log.e("SMS_SENT", "Message ID: " + messageId);
//        Log.e("SMS_SENT", "Part: " + (partIndex + 1) + "/" + totalParts);
//        Log.e("SMS_SENT", "Result code: " + result);
//        DatabaseHelper dbHelper = new DatabaseHelper(context);
//        if (result == Activity.RESULT_OK) {
//            if (totalParts > 1) {
//                updateResult = dbHelper.updateMultipartMessageStatus(messageId, partIndex, "sent");
//                if (updateResult == -1) {
//                    Log.e("DATABASE", "Failed to update multipart message status!");
//                    return;
//                }
//            }
//            Log.e(
//                    "SMS_SENT",
//                    "Message " + messageId
//                            + ", part " + (partIndex + 1)
//                            + " sent successfully!"
//            );
//
//        } else {
//            if (totalParts > 1) {
//                updateResult = dbHelper.updateMultipartMessageStatus(messageId, partIndex, "failed");
//                if (updateResult == -1) {
//                    Log.e("DATABASE", "Failed to update multipart message status!");
//                    return;
//                }
//            }
//            Log.e(
//                    "SMS_SENT",
//                    "Message " + messageId
//                            + ", part " + (partIndex + 1)
//                            + " failed! Error code: " + result
//            );
//        }
//    }
}