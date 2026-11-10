package com.example.mobile_project;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class AddContactActivity extends Activity {

    private static final int REQUEST_COUNTRY = 100;
    private static final int REQUEST_AVATAR = 101;

    private DatabaseHelper dbHelper;
    private ImageView contactAvatar;
    private Button selectCountryNumber;
    private Uri selectedAvatarUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_contact);

        dbHelper = new DatabaseHelper(this);

        // Handle system bars and keyboard using framework APIs.
        setupWindowInsets();

        // Background animation.
        View main = findViewById(R.id.main);
        if (main.getBackground() instanceof AnimationDrawable) {
            AnimationDrawable animationDrawable =
                    (AnimationDrawable) main.getBackground();
            animationDrawable.setEnterFadeDuration(2500);
            animationDrawable.setExitFadeDuration(5000);
            animationDrawable.start();
        }

        // Avatar.
        contactAvatar = findViewById(R.id.avatar);
        contactAvatar.setOnClickListener(v -> openAvatarPicker());

        // Buttons.
        Button backButton = findViewById(R.id.backButton);
        selectCountryNumber = findViewById(R.id.selectCountryNumber);
        Button addContactButton = findViewById(R.id.addContactButton);

        // Input fields.
        EditText inputFirstName = findViewById(R.id.inputFirstName);
        EditText inputLastName = findViewById(R.id.inputLastName);
        EditText inputEmail = findViewById(R.id.inputEmail);
        EditText inputNickname = findViewById(R.id.inputNickname);
        EditText inputPhoneNumber = findViewById(R.id.inputPhoneNumber);

        // Country selection.
        selectCountryNumber.setOnClickListener(v -> {
            Intent intent = new Intent(AddContactActivity.this, CountrySelection.class);
            startActivityForResult(intent, REQUEST_COUNTRY);
        });

        // Existing contact information.
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

            // Back button: preserve your original behavior.
            backButton.setOnClickListener(v -> {
                Intent tempIntent = new Intent(
                        this,
                        MessagePage.class
                );
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

            // Update existing contact.
            addContactButton.setOnClickListener(v -> {
                String firstName = inputFirstName.getText().toString();
                String lastName = inputLastName.getText().toString();
                String email = inputEmail.getText().toString();
                String nickname = inputNickname.getText().toString();
                String countryCode = selectCountryNumber.getText().toString();
                String phoneNumber = inputPhoneNumber.getText().toString();

                if (countryCode.isEmpty() || phoneNumber.isEmpty()) {
                    Log.e("AddContactActivity", "Missing country code or phone number");
                    return;
                }

                String avatarFilename = avatar;
                if (selectedAvatarUri != null) {
                    avatarFilename = saveAvatar(selectedAvatarUri);
                    if (avatarFilename == null) {
                        return;
                    }
                }

                // Look up the original contact, not the edited number.
                int id = dbHelper.getContactIdByNumber(
                        countryCodeShow,
                        phoneShow
                );

                if (id < 0) {
                    Log.e(
                            "AddContactActivity",
                            "Original contact not found"
                    );
                    return;
                }

                long result = dbHelper.updateContact(
                        id,
                        firstName,
                        lastName,
                        countryCode,
                        phoneNumber,
                        email,
                        nickname,
                        avatarFilename
                );
                if (result <= 0) {
                    Log.e("AddContactActivity", "Contact update failed");
                    return;
                }
                Log.d("AddContactActivity", "Contact updated successfully");
                finish();
            });

            // Display existing avatar.
            if (avatar != null && !avatar.isEmpty()) {
                File avatarFile = new File(
                        new File(getFilesDir(), "avatars"),
                        avatar
                );
                if (avatarFile.isFile()) {
                    contactAvatar.setImageURI(Uri.fromFile(avatarFile));
                } else {
                    contactAvatar.setImageResource(R.drawable.outline_account_circle_24);
                }
            }

        } else {

            // New contact.
            backButton.setOnClickListener(v -> finish());

            addContactButton.setOnClickListener(v -> {
                String firstName = inputFirstName.getText().toString();
                String lastName = inputLastName.getText().toString();
                String email = inputEmail.getText().toString();
                String nickname = inputNickname.getText().toString();
                String countryCode = selectCountryNumber.getText().toString();
                String phoneNumber = inputPhoneNumber.getText().toString();

                if (countryCode.isEmpty() || phoneNumber.isEmpty()) {
                    Log.e("AddContactActivity", "Missing country code or phone number");
                    return;
                }

                String avatarFilename = null;
                if (selectedAvatarUri != null) {
                    avatarFilename = saveAvatar(selectedAvatarUri);
                    if (avatarFilename == null) {
                        return;
                    }
                }
                long result = dbHelper.insertContact(
                        firstName,
                        lastName,
                        countryCode,
                        phoneNumber,
                        email,
                        nickname,
                        avatarFilename
                );

                if (result <= 0) {
                    Log.e("AddContactActivity", "Contact insertion failed");
                    return;
                }
                Log.d("AddContactActivity", "Contact added successfully");
                finish();
            });
        }
    }

    private void openAvatarPicker() {
        Intent intent;
        if (Build.VERSION.SDK_INT >= 33) {
            // Native Android Photo Picker.
            intent = new Intent("android.provider.action.PICK_IMAGES");
            intent.setType("image/*");
        } else {
            // Framework fallback for older Android versions.
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        try {
            startActivityForResult(intent, REQUEST_AVATAR);
        } catch (android.content.ActivityNotFoundException e) {
            Log.e(
                    "AddContactActivity",
                    "No image picker available",
                    e
            );
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        if (requestCode == REQUEST_COUNTRY) {
            String dialCode = data.getStringExtra("dialCode");
            if (dialCode != null) {
                selectCountryNumber.setText(dialCode);
            }
        } else if (requestCode == REQUEST_AVATAR) {
            Uri uri = data.getData();
            if (uri != null) {
                selectedAvatarUri = uri;
                contactAvatar.setImageURI(uri);
            }
        }
    }

    private void setupWindowInsets() {
        ScrollView scrollView = findViewById(R.id.addContactScrollView);
        if (Build.VERSION.SDK_INT >= 30) {
            scrollView.setOnApplyWindowInsetsListener((view, insets) -> {
                android.graphics.Insets systemBars =
                        insets.getInsets(
                                WindowInsets.Type.systemBars()
                        );
                android.graphics.Insets ime =
                        insets.getInsets(
                                WindowInsets.Type.ime()
                        );
                int bottomPadding = Math.max(
                        systemBars.bottom,
                        ime.bottom
                );
                view.setPadding(
                        systemBars.left,
                        systemBars.top,
                        systemBars.right,
                        bottomPadding
                );
                return insets;
            });
            scrollView.requestApplyInsets();
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
                InputStream inputStream =
                        getContentResolver().openInputStream(imageUri);
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