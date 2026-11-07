package com.example.mobile_project;


import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class AddContactActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent> countryLauncher;
    private DatabaseHelper dbHelper;

    private ImageView contactAvatar;
    private ActivityResultLauncher<PickVisualMediaRequest> photoPickerLauncher;
    private Uri selectedAvatarUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_contact);
        ScrollView scrollView = findViewById(R.id.addContactScrollView);
        ViewCompat.setOnApplyWindowInsetsListener(scrollView, (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            int bottomPadding = Math.max(systemBars.bottom, ime.bottom);
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding);
            return insets;
        });

        //background animation
        ConstraintLayout main = findViewById(R.id.main);
        AnimationDrawable animationDrawable = (AnimationDrawable) main.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        //avatar pic
        contactAvatar = findViewById(R.id.avatar);
        photoPickerLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(),
            uri -> {
                if (uri != null) {
                    selectedAvatarUri = uri;
                    contactAvatar.setImageURI(uri);
                }
            }
        );
        contactAvatar.setOnClickListener(v -> photoPickerLauncher.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build()));

        //back button
        Button backButton = findViewById(R.id.backButton);
        //select country code
        Button selectCountryNumber = findViewById(R.id.selectCountryNumber);
        selectCountryNumber.setOnClickListener(v -> {
            Intent intent = new Intent(AddContactActivity.this, CountrySelection.class);
            countryLauncher.launch(intent);
        });
        countryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                String dialCode = result.getData().getStringExtra("dialCode");
                if (dialCode != null) {
                    selectCountryNumber.setText(dialCode);
                }
            }
        });

        Button addContactButton = findViewById(R.id.addContactButton);
        EditText inputFirstName = findViewById(R.id.inputFirstName);
        EditText inputLastName = findViewById(R.id.inputLastName);
        EditText inputEmail = findViewById(R.id.inputEmail);
        EditText inputNickname = findViewById(R.id.inputNickname);
        EditText inputPhoneNumber = findViewById(R.id.inputPhoneNumber);
        //Intent coming in
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            String firstname = intent.getStringExtra("firstname");
            String lastname = intent.getStringExtra("lastname");
            String nicknameShow = intent.getStringExtra("nickname");
            String countryCodeShow = intent.getStringExtra("countryCode");
            String phoneShow = intent.getStringExtra("phone");
            String emailShow = intent.getStringExtra("email");
            String avatar = intent.getStringExtra("avatar");
            inputFirstName.setText(firstname);
            inputLastName.setText(lastname);
            selectCountryNumber.setText(countryCodeShow);
            inputPhoneNumber.setText(phoneShow);
            inputEmail.setText(emailShow);
            inputNickname.setText(nicknameShow);

            backButton.setOnClickListener(v -> {
                Intent tempIntent = new Intent(this, MessagePage.class);
                tempIntent.putExtra("firstname", firstname);
                tempIntent.putExtra("lastname", lastname);
                tempIntent.putExtra("nickname", nicknameShow);
                tempIntent.putExtra("countryCode", countryCodeShow);
                tempIntent.putExtra("phone", phoneShow);
                tempIntent.putExtra("email", emailShow);
                tempIntent.putExtra("avatar", avatar);
                startActivity(tempIntent);
                finish();
            });

            addContactButton.setOnClickListener(v -> {
                dbHelper = new DatabaseHelper(this);
                String firstName = inputFirstName.getText().toString();
                String lastName = inputLastName.getText().toString();
                String email = inputEmail.getText().toString();
                String nickname = inputNickname.getText().toString();
                String countryCode = selectCountryNumber.getText().toString();
                String phoneNumber = inputPhoneNumber.getText().toString();
                String phone = countryCode + phoneNumber;
                String avatarFilename;
                if (avatar != null) {
                    avatarFilename = avatar;
                } else {
                    avatarFilename = null;
                }
                if (selectedAvatarUri != null) {
                    avatarFilename = saveAvatar(selectedAvatarUri);
                    if (avatarFilename == null) {
                        return;
                    }
                }
                int id = dbHelper.getContactIdByNumber(countryCode, phoneNumber);
                Log.e("all info", phoneNumber);
                long result = dbHelper.updateContact(id, firstName, lastName, countryCode, phoneNumber, email, nickname, avatarFilename);
                if (result == -1) {
                    Log.e("AddContactActivity", "Contact not update: " + firstName + " " + lastName + " " + phone + " " + email + " " + nickname + " " + avatarFilename);
                    return;
                }
                Log.e("AddContactActivity", "Contact update: " + firstName + " " + lastName + " " + phone + " " + email + " " + nickname + " " + avatarFilename);
                finish();
            });

            if (avatar != null) {
                File temp = null;
                if (!avatar.isEmpty()) {
                    temp =  new File(new File(this.getFilesDir(), "avatars"), avatar);
                }
                if (temp != null) {
                    if (temp.isFile()){
                        contactAvatar.setImageURI(Uri.fromFile(temp));
                    } else {
                        contactAvatar.setImageResource(R.drawable.outline_account_circle_24);
                    }
                } else {
                    contactAvatar.setImageResource(R.drawable.outline_account_circle_24);
                }
            }
        } else {
            //back button
            backButton.setOnClickListener(v -> finish());
            //add Contact button
            addContactButton.setOnClickListener(v -> {
                dbHelper = new DatabaseHelper(this);
                String firstName = inputFirstName.getText().toString();
                String lastName = inputLastName.getText().toString();
                String email = inputEmail.getText().toString();
                String nickname = inputNickname.getText().toString();
                String countryCode = selectCountryNumber.getText().toString();
                String phoneNumber = inputPhoneNumber.getText().toString();

                String avatarFilename = null;

                if (selectedAvatarUri != null) {
                    avatarFilename = saveAvatar(selectedAvatarUri);
                    if (avatarFilename == null) {
                        return;
                    }
                }
                long result = dbHelper.insertContact(firstName, lastName, countryCode, phoneNumber, email, nickname, avatarFilename);
                if (result == -1) {
                    Log.e("AddContactActivity", "Contact not added: " + firstName + " " + lastName + " " + countryCode + " " + phoneNumber + " " + email + " " + nickname + " " + avatarFilename);
                    return;
                }
                Log.e("AddContactActivity", "Contact added: " + firstName + " " + lastName + " " + countryCode + " " + phoneNumber + " " + email + " " + nickname + " " + avatarFilename);
                finish();
            });
        }

    }

    private String saveAvatar(Uri imageUri) {
        File avatarDirectory = new File(getFilesDir(), "avatars");
        if (!avatarDirectory.exists() && !avatarDirectory.mkdirs()) {
            Log.e("AVATAR", "Failed to create avatar directory");
            return null;
        }
        String filename = UUID.randomUUID().toString() + ".jpg";
        File destination = new File(avatarDirectory, filename);
        try (
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                FileOutputStream outputStream = new FileOutputStream(destination)
        ) {
            if (inputStream == null) {
                return null;
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return filename;
        } catch (IOException e) {
            Log.e("AVATAR", "Failed to save avatar", e);
            destination.delete();
            return null;
        }
    }
}