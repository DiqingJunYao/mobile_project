package com.example.mobile_project;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.Button;
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
        String name = intent.getStringExtra("name");
        String nickname = intent.getStringExtra("nickname");
        String phone = intent.getStringExtra("phone");
        String email = intent.getStringExtra("email");
        String avatar = intent.getStringExtra("avatar");
        Contact temp = new Contact(name, nickname, phone, email, avatar);
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
        nameTextView.setText(temp.getName());
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
        //
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        int id = databaseHelper.getContactIdByNumber(phone);
        ArrayList<Message> messages = databaseHelper.getMessagesByContactId(id);
        RecyclerView recyclerView = findViewById(R.id.messageRecyclerView);
        MessagePageRecyclerViewAdapter adapter = new MessagePageRecyclerViewAdapter((Context) this, messages, (RecyclerViewInterface) this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}