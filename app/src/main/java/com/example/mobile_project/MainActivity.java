package com.example.mobile_project;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecyclerViewInterface {
    ArrayList<Contact> contacts = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //background animation
        LinearLayout LinearLayout = findViewById(R.id.mainLayout);
        Button addContactButton = findViewById(R.id.addContact);
        AnimationDrawable animationDrawable = (AnimationDrawable) LinearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        //add Contact button
        addContactButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
            startActivity(intent);
        });

        //main Contacts recycler view
        setupContacts();
        RecyclerView recyclerView = findViewById(R.id.recyclerViewMainContacts);
        OutlineRecyclerViewAdapter adapter = new OutlineRecyclerViewAdapter(this, contacts, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onResume() {
        super.onResume();

        //main Contacts recycler view
        setupContacts();
        RecyclerView recyclerView = findViewById(R.id.recyclerViewMainContacts);
        OutlineRecyclerViewAdapter adapter = new OutlineRecyclerViewAdapter(this, contacts, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupContacts(){
        contacts.clear();
        DatabaseHelper helper = new DatabaseHelper(this);
        ArrayList<Contact> temp = helper.getAllContacts();
        for (int i = 0; i < temp.size(); i++) {
            Log.e("ID", String.valueOf(i));
            Log.e("Name", temp.get(i).getName());
            Log.e("Nickname", String.valueOf(temp.get(i).getNickName()));
            Log.e("Phone", String.valueOf(temp.get(i).getPhone()));
            Log.e("Email", String.valueOf(temp.get(i).getEmail()));
            Log.e("Avatar", String.valueOf(temp.get(i).getAvatar()));
            contacts.add(temp.get(i));
        }
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(MainActivity.this, MessagePage.class);
        intent.putExtra("name", String.valueOf(contacts.get(position).getName()));
        intent.putExtra("nickname", String.valueOf(contacts.get(position).getNickName()));
        intent.putExtra("phone", String.valueOf(contacts.get(position).getPhone()));
        intent.putExtra("email", String.valueOf(contacts.get(position).getEmail()));
        intent.putExtra("avatar", String.valueOf(contacts.get(position).getAvatar()));
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(int position) {

    }
}