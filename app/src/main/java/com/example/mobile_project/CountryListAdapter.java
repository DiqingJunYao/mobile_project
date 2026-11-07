package com.example.mobile_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CountryListAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<CountryInfo> countryInfos;

    public CountryListAdapter(
            Context context,
            ArrayList<CountryInfo> countryInfos
    ) {
        this.context = context;
        this.countryInfos = countryInfos;
    }

    @Override
    public int getCount() {
        return countryInfos.size();
    }

    @Override
    public CountryInfo getItem(int position) {
        return countryInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(
            int position,
            View convertView,
            ViewGroup parent
    ) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.recycler_view_country_select,
                    parent,
                    false
            );
        }

        CountryInfo country = getItem(position);

        ImageView imageView =
                convertView.findViewById(R.id.countryFlags);

        TextView countryName =
                convertView.findViewById(R.id.countryName);

        TextView countryDialingCode =
                convertView.findViewById(R.id.countryDialingCode);

        TextView countryNativeName =
                convertView.findViewById(R.id.countryNativeName);

        imageView.setImageResource(country.getImage());

        countryName.setText(country.getCountryName());

        countryDialingCode.setText(country.getCountryNumber());

        countryNativeName.setText(
                country.getCountryNameOwnLanguage()
        );

        return convertView;
    }
}