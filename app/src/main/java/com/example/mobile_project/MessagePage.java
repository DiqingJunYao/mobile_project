package com.example.mobile_project;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MessagePage extends Activity {
    private static final String SMS_UPDATED = "com.example.mobile_project.SMS_UPDATED";
    private ListView listView;
    private MessagePageListAdapter adapter;
    private ArrayList<Message> messages = new ArrayList<>();
    private long contactId = -1;
    private boolean receiverRegistered = false;
    private static final int CALL_PERMISSION_REQUEST = 101;
    private String pendingCallNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_page);
        setupWindowInsets();
        // Contact information.
        Intent intent = getIntent();

        String firstname = intent.getStringExtra("firstname");
        String lastname = intent.getStringExtra("lastname");
        String nickname = intent.getStringExtra("nickname");
        String countryCode = intent.getStringExtra("countryCode");
        String phone = intent.getStringExtra("phone");
        String email = intent.getStringExtra("email");
        String avatar = intent.getStringExtra("avatar");

        String name = (firstname == null ? "" : firstname) + " " + (lastname == null ? "" : lastname);

        Contact contact = new Contact(firstname, lastname, nickname, countryCode, phone, email, avatar);

        // Background animation.
        View messageLayout = findViewById(R.id.messageLayout);
        if (messageLayout.getBackground() instanceof AnimationDrawable) {
            AnimationDrawable animationDrawable = (AnimationDrawable) messageLayout.getBackground();
            animationDrawable.setEnterFadeDuration(2500);
            animationDrawable.setExitFadeDuration(5000);
            animationDrawable.start();
        }

        // Back button.
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Contact header.
        TextView nameTextView = findViewById(R.id.name);
        TextView nicknameTextView = findViewById(R.id.nickName);
        ImageView avatarImageView = findViewById(R.id.avatarForMessage);
        nameTextView.setText(name.trim());
        nicknameTextView.setText(contact.getNickName());
        File avatarFile = contact.getAvatarFile(this);
        if (avatarFile != null && avatarFile.isFile()) {
            avatarImageView.setImageURI(Uri.fromFile(avatarFile));
        } else {
            avatarImageView.setImageResource(R.drawable.outline_account_circle_24);
        }

        // Open contact information.
        View.OnClickListener openContactInfo = v -> {
            Intent editIntent = new Intent(MessagePage.this, AddContactActivity.class);
            editIntent.putExtra("firstname", firstname);
            editIntent.putExtra("lastname", lastname);
            editIntent.putExtra("nickname", nickname);
            editIntent.putExtra("countryCode", countryCode);
            editIntent.putExtra("phone", phone);
            editIntent.putExtra("email", email);
            editIntent.putExtra("avatar", avatar);
            startActivity(editIntent);
            finish();
        };
        avatarImageView.setOnClickListener(openContactInfo);
        nameTextView.setOnClickListener(openContactInfo);
        nicknameTextView.setOnClickListener(openContactInfo);

        // Get contact ID.
        try (DatabaseHelper databaseHelper = new DatabaseHelper(this)) {
            contactId = databaseHelper.getContactIdByNumber(countryCode, phone);
        } catch (Exception e) {
            Log.e("DATABASE", "Failed to find contact", e);
        }
        if (contactId < 0) {
            Log.e("DATABASE", "Contact not found");
        }

        // Message list.
        listView = findViewById(R.id.messageListView);
        adapter = new MessagePageListAdapter(this, messages);
        listView.setAdapter(adapter);
        if (contactId >= 0) {
            refreshMessages();
        }

        // Send SMS.
        Button sendButton = findViewById(R.id.sendMessageButton);
        EditText editTextForInputMessage = findViewById(R.id.editTextForInputMessage);
        sendButton.setOnClickListener(v -> {
            String content = editTextForInputMessage.getText().toString();
            if (content.trim().isEmpty()) {
                return;
            }
            if (contactId < 0) {
                Log.e("SMS", "Cannot send: contact not found");
                return;
            }

            String phoneNumber = contact.getCountryCode() + contact.getPhone();
            long timestamp = System.currentTimeMillis();
            long messageId;
            try (DatabaseHelper dbHelper = new DatabaseHelper(this)) {
                messageId = dbHelper.insertMessage(contactId, content, timestamp, true, name, "pending");
            } catch (Exception e) {
                Log.e("DATABASE", "Failed to save message", e);
                return;
            }
            if (messageId < 0) {
                Log.e("DATABASE", "Message insertion failed");
                return;
            }
            refreshMessages();
            boolean accepted = SmsHelper.sendSms(this, phoneNumber, content, messageId);
            if (!accepted) {
                Log.e("SMS", "Failed to send SMS");
                try (DatabaseHelper dbHelper = new DatabaseHelper(this)) {
                    dbHelper.updateMessageStatus(messageId, "failed");
                }
                refreshMessages();
            }
            editTextForInputMessage.setText("");
        });

        //call button
        ImageButton callButton = findViewById(R.id.callButton);
        callButton.setOnClickListener(v -> {
            String phoneNumber = countryCode + phone;
            callContact(phoneNumber);
        });

    }

    private void refreshMessages() {
        if (contactId < 0) {
            return;
        }
        try (DatabaseHelper dbHelper = new DatabaseHelper(this)) {
            ArrayList<Message> updatedMessages = dbHelper.getMessagesByContactId(contactId);
            messages.clear();
            messages.addAll(updatedMessages);
            adapter.notifyDataSetChanged();
            //jump to the new message
            int lastVisiblePosition = listView.getLastVisiblePosition();
            boolean wasNearBottom = lastVisiblePosition >= adapter.getCount() - 2;
            if (wasNearBottom && !messages.isEmpty()) {
                listView.setSelection(messages.size() - 1);
            }
        } catch (Exception e) {
            Log.e("DATABASE", "Failed to refresh messages", e);
        }
    }

    private final BroadcastReceiver messageUpdateReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
        if (SMS_UPDATED.equals(intent.getAction())) {
            refreshMessages();
        }
        }
    };

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(SMS_UPDATED);
        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(messageUpdateReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(messageUpdateReceiver, filter);
        }
        receiverRegistered = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (receiverRegistered) {
            unregisterReceiver(messageUpdateReceiver);
            receiverRegistered = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null && contactId >= 0) {
            refreshMessages();
        }
    }

    private void setupWindowInsets() {
        if (Build.VERSION.SDK_INT >= 30) {
            View root = findViewById(R.id.messageLayout);
            root.setOnApplyWindowInsetsListener((view, insets) -> {
                android.graphics.Insets systemBars = insets.getInsets(WindowInsets.Type.systemBars());
                android.graphics.Insets ime = insets.getInsets(WindowInsets.Type.ime());
                view.setPadding(systemBars.left, systemBars.top, systemBars.right, Math.max(systemBars.bottom, ime.bottom));
                return insets;
            });
            root.requestApplyInsets();
        }
    }

    private void callContact(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return;
        }
        if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            pendingCallNumber = phoneNumber;
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_REQUEST);
            return;
        }
        Intent intent = new Intent(
                Intent.ACTION_CALL,
                Uri.parse("tel:" + phoneNumber)
        );

        startActivity(intent);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CALL_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (pendingCallNumber != null) {
                    String number = pendingCallNumber;
                    pendingCallNumber = null;
                    callContact(number);
                }
            } else {
                pendingCallNumber = null;
                Toast.makeText(
                        this,
                        "Call permission denied",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }
}