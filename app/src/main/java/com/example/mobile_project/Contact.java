package com.example.mobile_project;

import android.content.Context;

import java.io.File;

public class Contact {
    String firstName;
    String lastName;
    String nickName;
    String countryCode;
    String phone;
    String email;
    String avatar;


    public Contact(String firstName, String lastName,  String nickName, String countryCode, String phone, String email, String avatar) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickName = nickName;
        this.countryCode = countryCode;
        this.phone = phone;
        this.email = email;
        this.avatar = avatar;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName(){ return lastName; }

    public String getNickName() {
        return nickName;
    }

    public String getCountryCode() { return countryCode; }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatar() {
        return avatar;
    }

    public File getAvatarFile(Context context) {
        if (avatar != null && !avatar.isEmpty()) {
            return new File(new File(context.getFilesDir(), "avatars"), avatar);
        } else {
            return null;
        }
    }
}
