package com.example.mobile_project;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CountrySelection extends AppCompatActivity implements RecyclerViewInterface {

    ArrayList<CountryInfo> countryInfos = new ArrayList<>();

    CountryRecyclerViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_country_selection);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loadCountries();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CountryRecyclerViewAdapter(this, countryInfos, this);

        recyclerView.setAdapter(adapter);
    }

    private void loadCountries() {
        String[] countryNames = getResources().getStringArray(R.array.country_names);
        String[] countryDialingCodes = getResources().getStringArray(R.array.country_dialing_codes);
        String[] countryNativeNames = getResources().getStringArray(R.array.country_native_names);
        String[] countryFlags = getResources().getStringArray(R.array.country_iso_codes);
        if (countryNames.length != countryDialingCodes.length || countryNames.length != countryNativeNames.length || countryNames.length != countryFlags.length) {
            throw new RuntimeException("Arrays must have the same length");
        }
        for (int i = 0; i < countryNames.length; i++) {
            int resourceId = getResources().getIdentifier("flag_" + countryFlags[i], "drawable", getPackageName());
            if (resourceId == 0) {
                resourceId = R.drawable.flag_cn;
            }
            countryInfos.add(new CountryInfo(resourceId, countryNames[i], countryNativeNames[i], countryDialingCodes[i]));
        }
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent();
        intent.putExtra("dialCode", countryInfos.get(position).getCountryNumber());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onItemLongClick(int position) {
        countryInfos.remove(position);
        adapter.notifyItemRemoved(position);
    }
}