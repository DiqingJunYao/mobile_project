package com.example.mobile_project;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class MessagePage extends AppCompatActivity {
    public static final String PENDING = "pending";
    public static final String SENT = "sent";
    public static final String FAILED = "failed";
    public static final String DELIVERED = "delivered";
    public static final String UNKNOWN = "unknown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        backButton.setOnClickListener(v -> {
            finish();
        });
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
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        int id = databaseHelper.getContactIdByNumber(countryCode, phone);
        ArrayList<Message> messages = databaseHelper.getMessagesByContactId(id);
        RecyclerView recyclerView = findViewById(R.id.messageRecyclerView);
        MessagePageRecyclerViewAdapter adapter = new MessagePageRecyclerViewAdapter((Context) this, messages);
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
            databaseHelper.insertMessage(id, content, timestamp, true, name, PENDING);
            int messageId = databaseHelper.getMessageIdByContactIdTimestamp(id, timestamp);
            boolean accepted = SmsHelper.sendSms(
                    this,
                    phoneNumber,
                    content,
                    messageId
            );
            if (accepted) {
//                databaseHelper.insertMessage(id, content, System.currentTimeMillis(), true, name);
                editTextForInputMessage.setText("");
            }
        });
    }
}