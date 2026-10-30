package com.example.mobile_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CountryRecyclerViewAdapter extends RecyclerView.Adapter<CountryRecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<CountryInfo> countryInfos;
    private final RecyclerViewInterface recyclerViewInterface;


    public CountryRecyclerViewAdapter(Context context, ArrayList<CountryInfo> countryInfos, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.countryInfos = countryInfos;
        this.recyclerViewInterface = recyclerViewInterface;
    }


    @NonNull
    @Override
    public CountryRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.recycler_view_country_select, parent, false);
        return new MyViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull CountryRecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.imageView.setImageResource(countryInfos.get(position).getImage());
        holder.countryName.setText(countryInfos.get(position).getCountryName());
        holder.countryDialingCode.setText(countryInfos.get(position).getCountryNumber());
        holder.countryNativeName.setText(countryInfos.get(position).getCountryNameOwnLanguage());
    }

    @Override
    public int getItemCount() {
        return countryInfos.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView countryName;
        TextView countryDialingCode;
        TextView countryNativeName;
        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            imageView = itemView.findViewById(R.id.countryFlags);
            countryName = itemView.findViewById(R.id.countryName);
            countryDialingCode = itemView.findViewById(R.id.countryDialingCode);
            countryNativeName = itemView.findViewById(R.id.countryNativeName);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}
