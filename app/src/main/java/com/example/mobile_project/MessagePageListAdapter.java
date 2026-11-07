package com.example.mobile_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MessagePageListAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<Message> messages;

    private static final int TYPE_IN = 0;
    private static final int TYPE_OUT = 1;

    public MessagePageListAdapter(
            Context context,
            ArrayList<Message> messages
    ) {
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Message getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isSent()
                ? TYPE_OUT
                : TYPE_IN;
    }

    @Override
    public View getView(
            int position,
            View convertView,
            ViewGroup parent
    ) {

        int viewType = getItemViewType(position);

        ViewHolder holder;

        if (convertView == null) {

            LayoutInflater inflater =
                    LayoutInflater.from(context);

            if (viewType == TYPE_IN) {

                convertView = inflater.inflate(
                        R.layout.recycler_view_message_in,
                        parent,
                        false
                );

            } else {

                convertView = inflater.inflate(
                        R.layout.recycler_view_message_out,
                        parent,
                        false
                );
            }

            holder = new ViewHolder();

            if (viewType == TYPE_IN) {

                holder.content =
                        convertView.findViewById(R.id.textContentIn);

                holder.timeStamp =
                        convertView.findViewById(R.id.textTimeIn);

            } else {

                holder.content =
                        convertView.findViewById(R.id.textContentOut);

                holder.timeStamp =
                        convertView.findViewById(R.id.textTimeOut);

                holder.imageView =
                        convertView.findViewById(R.id.messageStatus);
            }

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        Message message = getItem(position);

        holder.content.setText(message.getContent());

        SimpleDateFormat formatter =
                new SimpleDateFormat("HH:mm", Locale.getDefault());

        String time = formatter.format(
                new Date(message.getTimestamp())
        );

        holder.timeStamp.setText(time);

        if (viewType == TYPE_OUT) {

            String status = message.getMessageStatus();

            if ("pending".equals(status)) {

                holder.imageView.setImageResource(
                        R.drawable.message_pending_clock_fixed
                );

            } else if ("sent".equals(status)) {

                holder.imageView.setImageResource(
                        R.drawable.message_sent_tick_fixed
                );

            } else {

                holder.imageView.setImageResource(
                        R.drawable.message_failed_exclamation
                );
            }
        }

        return convertView;
    }

    private static class ViewHolder {

        TextView content;
        TextView timeStamp;
        ImageView imageView;
    }
}