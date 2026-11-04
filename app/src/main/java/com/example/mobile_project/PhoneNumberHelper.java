package com.example.mobile_project;


import android.content.Context;

import java.util.ArrayList;

public class PhoneNumberHelper {
    public static ArrayList<String> getCountryCode (Context context, String phoneNumber) {
        String countryCode = "";
        String[] countryCodes = context.getResources().getStringArray(R.array.country_dialing_codes);
        for (String code : countryCodes) {
            if (phoneNumber.startsWith(String.valueOf(code)) && code.length() > countryCode.length()) {
                countryCode = code;
            }
        }
        String phoneNumberWithoutCountryCode = phoneNumber.substring(countryCode.length());
        ArrayList<String> numbers = new ArrayList<>();
        numbers.add(countryCode);
        numbers.add(phoneNumberWithoutCountryCode);
        return numbers;
    }
}
