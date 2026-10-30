package com.example.mobile_project;

import android.content.Context;

import java.io.File;

public class Contact {
    String name;
    String nickName;
    String phone;
    String email;
    String avatar;


    public Contact(String name, String nickName, String phone, String email, String avatar) {
        this.name = name;
        this.nickName = nickName;
        this.phone = phone;
        this.email = email;
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public String getNickName() {
        return nickName;
    }

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
