package com.example.mobile_project;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

public class SmsSentReceiver extends BroadcastReceiver {

//    @Override
//    public void onReceive(Context context, Intent intent) {
//        long messageId = intent.getLongExtra("message_id", -1);
//        if (messageId == -1) {
//            Log.e("SMS_SENT", "Message ID not found!");
//            return;
//        }
//        int result = getResultCode();
//        String status;
//        switch (result) {
//            case Activity.RESULT_OK:
//                status = "SENT";
//                break;
//            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
//            case SmsManager.RESULT_ERROR_NO_SERVICE:
//            case SmsManager.RESULT_ERROR_NULL_PDU:
//            case SmsManager.RESULT_ERROR_RADIO_OFF:
//                status = "FAILED";
//                break;
//            default:
//                status = "FAILED";
//                Log.e("SMS_SENT", "Error code: " + result);
//                break;
//        }
//        DatabaseHelper dbHelper = new DatabaseHelper(context);
//        dbHelper.updateMessageStatus(messageId, status);
//        Log.d("SMS_SENT", "Message " + messageId + ": " + status);
//    }

    @Override
    public void onReceive(Context context, Intent intent) {

        long messageId = intent.getLongExtra("message_id", -1);

        if (messageId == -1) {
            Log.e("SMS_SENT", "Message ID not found!");
            return;
        }

        int result = getResultCode();

        Log.e("SMS_SENT", "Callback received!");
        Log.e("SMS_SENT", "Message ID: " + messageId);
        Log.e("SMS_SENT", "Result code: " + result);

        switch (result) {

            case Activity.RESULT_OK:
                Log.e("SMS_SENT", "SMS sent successfully!");
                break;

            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                Log.e("SMS_SENT", "Generic failure");
                break;

            case SmsManager.RESULT_ERROR_NO_SERVICE:
                Log.e("SMS_SENT", "No mobile network service");
                break;

            case SmsManager.RESULT_ERROR_NULL_PDU:
                Log.e("SMS_SENT", "Invalid SMS PDU");
                break;

            case SmsManager.RESULT_ERROR_RADIO_OFF:
                Log.e("SMS_SENT", "Mobile radio is off");
                break;

            default:
                Log.e("SMS_SENT", "SMS sending failed: " + result);
                break;
        }
    }
}