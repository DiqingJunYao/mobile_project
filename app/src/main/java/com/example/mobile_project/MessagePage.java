package com.example.mobile_project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class MessagePage extends AppCompatActivity {
    private RecyclerView recyclerView;
    public MessagePageRecyclerViewAdapter adapter;
    private ArrayList<Message> messages;
    private long contactId;
    private DatabaseHelper debugDbHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        debugDbHelper = new DatabaseHelper(this);
        debugDbHelper.getReadableDatabase();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_message_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.messageLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        String firstname = intent.getStringExtra("firstname");
        String lastname = intent.getStringExtra("lastname");
        String name = firstname + " " + lastname;
        String nickname = intent.getStringExtra("nickname");
        String countryCode = intent.getStringExtra("countryCode");
        String phone = intent.getStringExtra("phone");
        String email = intent.getStringExtra("email");
        String avatar = intent.getStringExtra("avatar");
        Contact temp = new Contact(firstname, lastname, nickname, countryCode, phone, email, avatar);
        //background animation
        ConstraintLayout constraintLayout = findViewById(R.id.messageLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();
        //set back button
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
        //set name and nickname and avatar
        TextView nameTextView = findViewById(R.id.name);
        TextView nicknameTextView = findViewById(R.id.nickName);
        ImageView avatarImageView = findViewById(R.id.avatarForMessage);
        nameTextView.setText(name);
        nicknameTextView.setText(temp.getNickName());
        File avatarFile = temp.getAvatarFile(this);
        if (avatarFile != null) {
            if (avatarFile.isFile()) {
                avatarImageView.setImageURI(android.net.Uri.fromFile(avatarFile));
            } else {
                avatarImageView.setImageResource(R.drawable.outline_account_circle_24);
            }
        } else {
            avatarImageView.setImageResource(R.drawable.outline_account_circle_24);
        }
        //recycler view
        try (DatabaseHelper databaseHelper = new DatabaseHelper(this)) {
            int id = databaseHelper.getContactIdByNumber(countryCode, phone);
            contactId = id;
            messages = databaseHelper.getMessagesByContactId(contactId);
            recyclerView = findViewById(R.id.messageRecyclerView);
            adapter = new MessagePageRecyclerViewAdapter(this, messages);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            //personal info page
            avatarImageView.setOnClickListener(v -> {
                Intent intent1 = new Intent(this, AddContactActivity.class);
                intent1.putExtra("firstname", firstname);
                intent1.putExtra("lastname", lastname);
                intent1.putExtra("nickname", nickname);
                intent1.putExtra("countryCode", countryCode);
                intent1.putExtra("phone", phone);
                intent1.putExtra("email", email);
                intent1.putExtra("avatar", avatar);
                startActivity(intent1);
                finish();
            });
            nameTextView.setOnClickListener(v -> {
                Intent intent1 = new Intent(this, AddContactActivity.class);
                intent1.putExtra("firstname", firstname);
                intent1.putExtra("lastname", lastname);
                intent1.putExtra("nickname", nickname);
                intent1.putExtra("countryCode", countryCode);
                intent1.putExtra("phone", phone);
                intent1.putExtra("email", email);
                intent1.putExtra("avatar", avatar);
                startActivity(intent1);
                finish();
            });
            nicknameTextView.setOnClickListener(v -> {
                Intent intent1 = new Intent(this, AddContactActivity.class);
                intent1.putExtra("firstname", firstname);
                intent1.putExtra("lastname", lastname);
                intent1.putExtra("nickname", nickname);
                intent1.putExtra("countryCode", countryCode);
                intent1.putExtra("phone", phone);
                intent1.putExtra("email", email);
                intent1.putExtra("avatar", avatar);
                startActivity(intent1);
                finish();
            });
            //send SMS
            Button sendButton = findViewById(R.id.sendMessageButton);
            EditText editTextForInputMessage = findViewById(R.id.editTextForInputMessage);
            sendButton.setOnClickListener(v -> {
                String content = editTextForInputMessage.getText().toString();
                if (content.trim().isEmpty()) {
                    return;
                }
                String phoneNumber = temp.getCountryCode() + temp.getPhone();
                long timestamp = System.currentTimeMillis();
                long messageId;
                try (DatabaseHelper dbHelper = new DatabaseHelper(this)) {
                    messageId = dbHelper.insertMessage(contactId, content, timestamp, true, name, "pending");
                    refreshMessages();
                }
                boolean accepted = SmsHelper.sendSms(
                        this,
                        phoneNumber,
                        content,
                        messageId
                );
                if (!accepted) {
                    Log.e("SMS", "Failed to send SMS!");
                }
//                databaseHelper.insertMessage(id, content, System.currentTimeMillis(), true, name);
                editTextForInputMessage.setText("");
            });
        } catch (Exception e) {
            Log.e("DATABASE", "Failed to get messages!", e);
        }
    }

    private final BroadcastReceiver messageUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            if ("com.example.mobile_project.SMS_UPDATED".equals(intent.getAction())) {
                refreshMessages();
            }
        }
    };
    private void refreshMessages() {
        try (DatabaseHelper dbHelper = new DatabaseHelper(this)) {
            ArrayList<Message> updatedMessages = dbHelper.getMessagesByContactId(contactId);
            messages.clear();
            messages.addAll(updatedMessages);
            adapter.notifyDataSetChanged();
            if (!messages.isEmpty()) {
                recyclerView.scrollToPosition(messages.size() - 1);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(
                "com.example.mobile_project.SMS_UPDATED"
        );
        ContextCompat.registerReceiver(
                this,
                messageUpdateReceiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(messageUpdateReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null && messages != null) {
            refreshMessages();
        }
    }
}