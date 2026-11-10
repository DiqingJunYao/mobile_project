package com.example.mobile_project;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class OutlineListAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<Contact> contacts;
    private final RecyclerViewInterface recyclerViewInterface;

    public OutlineListAdapter(
            Context context,
            ArrayList<Contact> contacts,
            RecyclerViewInterface recyclerViewInterface
    ) {
        this.context = context;
        this.contacts = contacts;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Contact getItem(int position) {
        return contacts.get(position);
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
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.recycler_view_row,
                    parent,
                    false
            );
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.imageView);
            holder.title = convertView.findViewById(R.id.title);
            holder.detail = convertView.findViewById(R.id.detail);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Contact contact = getItem(position);

        // Full name.
        String name = contact.getFirstName() + " " + contact.getLastName();
        if (name.trim().isEmpty()) {
            String phoneNumber = contact.getCountryCode() + contact.getPhone();
            holder.title.setText(phoneNumber);
        } else {
            holder.title.setText(name.trim());
        }

        // Nickname.
        holder.detail.setText(contact.getNickName());

        // Avatar.
        File avatarFile = contact.getAvatarFile(context);
        if (avatarFile != null && avatarFile.isFile()) {
            holder.imageView.setImageURI(Uri.fromFile(avatarFile));
        } else {
            holder.imageView.setImageResource(
                    R.drawable.outline_account_circle_24
            );
        }

        // Click: open conversation.
        convertView.setOnClickListener(v -> {
            if (recyclerViewInterface != null) {
                recyclerViewInterface.onItemClick(position);
            }
        });

        // Long click: show Delete menu.
        convertView.setOnLongClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.getMenu().add("Delete");
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if ("Delete".contentEquals(menuItem.getTitle())) {
                    if (recyclerViewInterface != null) {
                        recyclerViewInterface.onItemLongClick(position);
                    }
                    return true;
                }
                return false;
            });
            popupMenu.show();
            return true;
        });
        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView title;
        TextView detail;
    }
}