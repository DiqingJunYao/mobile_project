package com.example.mobile_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class CountrySelection extends Activity {

    ArrayList<CountryInfo> countryInfos = new ArrayList<>();
    CountryListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_country_selection);

        loadCountries();

        ListView listView = findViewById(R.id.countryListView);

        adapter = new CountryListAdapter(this, countryInfos);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent();

            intent.putExtra(
                    "dialCode",
                    countryInfos.get(position).getCountryNumber()
            );

            setResult(RESULT_OK, intent);
            finish();
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            countryInfos.remove(position);
            adapter.notifyDataSetChanged();
            return true;
        });
    }

    private void loadCountries() {
        String[] countryNames =
                getResources().getStringArray(R.array.country_names);

        String[] countryDialingCodes =
                getResources().getStringArray(R.array.country_dialing_codes);

        String[] countryNativeNames =
                getResources().getStringArray(R.array.country_native_names);

        String[] countryFlags =
                getResources().getStringArray(R.array.country_iso_codes);

        if (countryNames.length != countryDialingCodes.length
                || countryNames.length != countryNativeNames.length
                || countryNames.length != countryFlags.length) {

            throw new RuntimeException("Arrays must have the same length");
        }

        for (int i = 0; i < countryNames.length; i++) {

            int resourceId = getResources().getIdentifier(
                    "flag_" + countryFlags[i],
                    "drawable",
                    getPackageName()
            );

            if (resourceId == 0) {
                resourceId = R.drawable.flag_cn;
            }

            countryInfos.add(new CountryInfo(
                    resourceId,
                    countryNames[i],
                    countryNativeNames[i],
                    countryDialingCodes[i]
            ));
        }
    }
}