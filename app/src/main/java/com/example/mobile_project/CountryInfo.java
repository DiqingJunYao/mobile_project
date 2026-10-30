package com.example.mobile_project;

public class CountryInfo {
    int image;
    String countryName;
    String countryNameOwnLanguage;
    String countryNumber;


    public CountryInfo(int image, String countryName, String countryNameOwnLanguage, String countryNumber) {
        this.image = image;
        this.countryName = countryName;
        this.countryNameOwnLanguage = countryNameOwnLanguage;
        this.countryNumber = countryNumber;
    }

    public int getImage() {
        return image;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getCountryNameOwnLanguage() {
        return countryNameOwnLanguage;
    }

    public String getCountryNumber() {
        return countryNumber;
    }
}
