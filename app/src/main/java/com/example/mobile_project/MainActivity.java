package com.example.mobile_project;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecyclerViewInterface {
    RecyclerView recyclerView;
    OutlineRecyclerViewAdapter adapter;
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
        recyclerView = findViewById(R.id.recyclerViewMainContacts);
        adapter = new OutlineRecyclerViewAdapter(this, contacts, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //receive SMS permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECEIVE_SMS
        ) != PackageManager.PERMISSION_GRANTED) {

            smsPermissionLauncher.launch(Manifest.permission.RECEIVE_SMS);
        }

        //send SMS permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
        ) != PackageManager.PERMISSION_GRANTED) {

            sendSmsPermissionLauncher.launch(Manifest.permission.SEND_SMS);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupContacts();
        adapter.notifyDataSetChanged();
    }

    private void setupContacts(){
        contacts.clear();
        try(DatabaseHelper helper = new DatabaseHelper(this)) {
            ArrayList<Contact> temp = helper.getAllContacts();
            for (int i = 0; i < temp.size(); i++) {
                Log.e("ID", String.valueOf(i));
                Log.e("Firstname", String.valueOf(temp.get(i).getFirstName()));
                Log.e("Lastname", String.valueOf(temp.get(i).getLastName()));
                Log.e("Nickname", String.valueOf(temp.get(i).getNickName()));
                Log.e("Country Code", String.valueOf(temp.get(i).getCountryCode()));
                Log.e("Phone", String.valueOf(temp.get(i).getPhone()));
                Log.e("Email", String.valueOf(temp.get(i).getEmail()));
                Log.e("Avatar", String.valueOf(temp.get(i).getAvatar()));
                contacts.add(temp.get(i));
            }
        }
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(MainActivity.this, MessagePage.class);
        intent.putExtra("firstname", String.valueOf(contacts.get(position).getFirstName()));
        intent.putExtra("lastname", String.valueOf(contacts.get(position).getLastName()));
        intent.putExtra("nickname", String.valueOf(contacts.get(position).getNickName()));
        intent.putExtra("countryCode", String.valueOf(contacts.get(position).getCountryCode()));
        intent.putExtra("phone", String.valueOf(contacts.get(position).getPhone()));
        intent.putExtra("email", String.valueOf(contacts.get(position).getEmail()));
        intent.putExtra("avatar", String.valueOf(contacts.get(position).getAvatar()));
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(int position) {
        if (position < 0 || position >= contacts.size()) {
            Log.e("CONTACT", "Invalid position: " + position);
            return;
        }
        Contact contact = contacts.get(position);
        try (DatabaseHelper databaseHelper = new DatabaseHelper(this)) {
            long id = databaseHelper.getContactIdByNumber(
                    contact.getCountryCode(),
                    contact.getPhone()
            );
            if (id < 0) {
                Log.e("DATABASE", "Contact not found");
                return;
            }
            int result = databaseHelper.deleteContact(id);
            Log.d("DATABASE", "Deleted rows: " + result);
            if (result > 0) {
                refreshContacts(databaseHelper);
            }
        }
    }

    private final ActivityResultLauncher<String> smsPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            Log.d("SMS", "Permission granted");
                        } else {
                            Log.d("SMS", "Permission denied");
                        }
                    }
            );
    private final ActivityResultLauncher<String> sendSmsPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            Log.d("SMS", "SEND_SMS permission granted");
                        } else {
                            Log.d("SMS", "SEND_SMS permission denied");
                        }
                    }
            );

    private final BroadcastReceiver messageUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            if ("com.example.mobile_project.SMS_UPDATED".equals(intent.getAction())) {
                refreshContacts(databaseHelper);
            }
        }
    };
    private void refreshContacts(DatabaseHelper databaseHelper) {
        ArrayList<Contact> updatedContact = databaseHelper.getAllContacts();
        contacts.clear();
        contacts.addAll(updatedContact);
        adapter.notifyDataSetChanged();
        if (!contacts.isEmpty()) {
            recyclerView.scrollToPosition(contacts.size() - 1);
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
}