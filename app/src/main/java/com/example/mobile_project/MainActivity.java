package com.example.mobile_project;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.annotation.SuppressLint;
import android.widget.PopupMenu;

import java.util.ArrayList;

public class MainActivity extends Activity implements RecyclerViewInterface {
    private static final int SMS_PERMISSION_REQUEST = 100;
    private ListView listView;
    private OutlineListAdapter adapter;
    private final ArrayList<Contact> contacts = new ArrayList<>();
    private boolean receiverRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Background animation.
        LinearLayout mainLayout = findViewById(R.id.mainLayout);
        if (mainLayout.getBackground() instanceof AnimationDrawable) {
            AnimationDrawable animationDrawable = (AnimationDrawable) mainLayout.getBackground();
            animationDrawable.setEnterFadeDuration(2500);
            animationDrawable.setExitFadeDuration(5000);
            animationDrawable.start();
        }

        // Add Contact button.
        Button addContactButton = findViewById(R.id.addContact);
        addContactButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
            startActivity(intent);
        });

        // Contact list.
        listView = findViewById(R.id.listViewMainContacts);
        adapter = new OutlineListAdapter(this, contacts, this);
        listView.setAdapter(adapter);

        // SMS permissions.
        checkSmsPermissions();

        //header menu
        Button menuButton = findViewById(R.id.headerMenu);
        View header = findViewById(R.id.header);
        HeaderColorHelper.applyColor(this, header);
        menuButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, menuButton);
            popupMenu.getMenuInflater().inflate(
                    R.menu.header_color_menu,
                    popupMenu.getMenu()
            );

            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                String selectedColor;
                if (itemId == R.id.color_default) {
                    selectedColor = "default";
                } else if (itemId == R.id.color_blue) {
                    selectedColor = "blue";
                } else if (itemId == R.id.color_green) {
                    selectedColor = "green";
                } else if (itemId == R.id.color_red) {
                    selectedColor = "red";
                } else if (itemId == R.id.color_purple) {
                    selectedColor = "purple";
                } else {
                    return false;
                }
                HeaderColorHelper.saveColor(
                        MainActivity.this,
                        selectedColor
                );
                HeaderColorHelper.applyColor(
                        MainActivity.this,
                        header
                );
                return true;
            });
            popupMenu.show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupContacts();
        adapter.notifyDataSetChanged();
    }

    private void setupContacts() {
        contacts.clear();
        try (DatabaseHelper helper = new DatabaseHelper(this)) {
            contacts.addAll(helper.getAllContacts());
        }
    }

    private void openMessagePage(int position) {
        if (position < 0 || position >= contacts.size()) {
            return;
        }
        Contact contact = contacts.get(position);
        Intent intent = new Intent(
                MainActivity.this,
                MessagePage.class
        );

        intent.putExtra("firstname", contact.getFirstName());
        intent.putExtra("lastname", contact.getLastName());
        intent.putExtra("nickname", contact.getNickName());
        intent.putExtra("countryCode", contact.getCountryCode());
        intent.putExtra("phone", contact.getPhone());
        intent.putExtra("email", contact.getEmail());
        intent.putExtra("avatar", contact.getAvatar());

        startActivity(intent);
    }

    private void deleteContact(int position) {
        if (position < 0 || position >= contacts.size()) {
            Log.e("CONTACT", "Invalid position: " + position);
            return;
        }
        Contact contact = contacts.get(position);
        boolean deleted = false;
        try (DatabaseHelper databaseHelper = new DatabaseHelper(this)) {
            long id = databaseHelper.getContactIdByNumber(contact.getCountryCode(), contact.getPhone());
            if (id < 0) {
                Log.e("DATABASE", "Contact not found");
                return;
            }
            int result = databaseHelper.deleteContact(id);
            Log.d("DATABASE", "Deleted rows: " + result);
            deleted = result > 0;
        }
        if (deleted) {
            refreshContacts();
        }
    }

    private void refreshContacts() {
        try (DatabaseHelper databaseHelper = new DatabaseHelper(this)) {
            ArrayList<Contact> updatedContacts = databaseHelper.getAllContacts();
            contacts.clear();
            contacts.addAll(updatedContacts);
            adapter.notifyDataSetChanged();
            //jump to the new contact
            int lastVisiblePosition = listView.getLastVisiblePosition();
            boolean wasNearBottom = lastVisiblePosition >= adapter.getCount() - 2;
            if (wasNearBottom && !contacts.isEmpty()) {
                listView.setSelection(contacts.size() - 1);
            }
        }
    }

    private void checkSmsPermissions() {
        ArrayList<String> missingPermissions = new ArrayList<>();
        if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            missingPermissions.add(Manifest.permission.RECEIVE_SMS);
        }

        if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            missingPermissions.add(Manifest.permission.SEND_SMS);
        }
        if (!missingPermissions.isEmpty()) {
            requestPermissions(missingPermissions.toArray(new String[0]), SMS_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {
        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );
        if (requestCode != SMS_PERMISSION_REQUEST) {
            return;
        }
        for (int i = 0; i < permissions.length; i++) {
            boolean granted = i < grantResults.length && grantResults[i] == PackageManager.PERMISSION_GRANTED;
            Log.d("SMS", permissions[i] + ": " + (granted ? "granted" : "denied"));
        }
    }

    private final BroadcastReceiver messageUpdateReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
        if ("com.example.mobile_project.SMS_UPDATED".equals(intent.getAction())) refreshContacts();
        }
    };

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(
                "com.example.mobile_project.SMS_UPDATED"
        );
        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(
                    messageUpdateReceiver,
                    filter,
                    Context.RECEIVER_NOT_EXPORTED
            );
        } else {
            registerReceiver(
                    messageUpdateReceiver,
                    filter
            );
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
    public void onItemClick(int position) {
        openMessagePage(position);
    }

    @Override
    public void onItemLongClick(int position) {
        deleteContact(position);
    }
}