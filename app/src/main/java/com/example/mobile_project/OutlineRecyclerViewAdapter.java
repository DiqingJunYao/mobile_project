package com.example.mobile_project;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class OutlineRecyclerViewAdapter extends RecyclerView.Adapter<OutlineRecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<Contact> contacts;

    private final RecyclerViewInterface recyclerViewInterface;

    public OutlineRecyclerViewAdapter(Context context, ArrayList<Contact> contacts, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.contacts = contacts;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public OutlineRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.recycler_view_row, parent, false);
        return new MyViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull OutlineRecyclerViewAdapter.MyViewHolder holder, int position) {
        String name = contacts.get(position).getFirstName() + " " + contacts.get(position).getLastName();
        holder.title.setText(name);
        holder.detail.setText(contacts.get(position).getNickName());
        // Set the image using the avatar filename
        Contact contact = contacts.get(position);
        File temp = contact.getAvatarFile(context);
        if (temp != null) {
            if (temp.isFile()){
            holder.imageView.setImageURI(Uri.fromFile(temp));
            } else {
                holder.imageView.setImageResource(R.drawable.outline_account_circle_24);
            }
        } else {
            holder.imageView.setImageResource(R.drawable.outline_account_circle_24);
        }
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title;
        TextView detail;


        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            title = itemView.findViewById(R.id.title);
            detail = itemView.findViewById(R.id.detail);

            itemView.setOnClickListener(v -> {
                if (recyclerViewInterface != null) {
                    int pos = getAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onItemClick(pos);
                    }
                }
            });

//            itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    if (recyclerViewInterface != null) {
//                        int pos = getAdapterPosition();
//                        if (pos != RecyclerView.NO_POSITION) {
//                            recyclerViewInterface.onItemLongClick(pos);
//                        }
//                    }
//                    return true;
//                }
//            });
            itemView.setOnLongClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);

                popupMenu.getMenu().add("Delete");
                popupMenu.setOnMenuItemClickListener(menuItem -> {

                    if (recyclerViewInterface != null) {
                        int pos = getBindingAdapterPosition();
                        String title = Objects.requireNonNull(menuItem.getTitle()).toString();

                        if (title.equals("Delete")) {
                            recyclerViewInterface.onItemLongClick(pos);
                            return true;
                        }
                    }
                    return false;

                });

                popupMenu.show();
                return true;
            });

        }
    }
}
