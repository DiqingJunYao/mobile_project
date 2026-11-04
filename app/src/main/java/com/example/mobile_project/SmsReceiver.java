package com.example.mobile_project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.ArrayList;

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            return;
        }
        StringBuilder messageContent = new StringBuilder();
        String sender = "";
        long timestamp = -1;
        SmsMessage[] messages =
                Telephony.Sms.Intents.getMessagesFromIntent(intent);
        if (messages == null) {
            return;
        }
        for (SmsMessage message : messages) {
            sender = message.getOriginatingAddress();
            String content = message.getMessageBody();
            timestamp = message.getTimestampMillis();
            Log.d("SMS_RECEIVER", "Sender: " + sender);
            Log.d("SMS_RECEIVER", "Content: " + content);
            Log.d("SMS_RECEIVER", "Timestamp: " + timestamp);
            messageContent.append(content);
        }
        Log.e("SMS_RECEIVER", messageContent.toString());
        DatabaseHelper db = new DatabaseHelper(context);
        ArrayList<String> phoneNumbers = PhoneNumberHelper.getCountryCode(context, sender);
        int resultID = db.getContactIdByNumber(phoneNumbers.get(0), phoneNumbers.get(1));
        Log.e("this is the resultID", String.valueOf(resultID));
        if (resultID == -1) {
            db.insertContact(sender, "", phoneNumbers.get(0), phoneNumbers.get(1), "", "", "");
            resultID = db.getContactIdByNumber(phoneNumbers.get(0), phoneNumbers.get(1));
            db.insertMessage(resultID, String.valueOf(messageContent), timestamp, false, String.valueOf(sender), "received message");
        } else {
            Contact temp = db.getContactById(resultID);
            db.insertMessage(resultID, String.valueOf(messageContent), timestamp, false, temp.getFirstName() + " " + temp.getLastName(), "received message");
        }

    }
}