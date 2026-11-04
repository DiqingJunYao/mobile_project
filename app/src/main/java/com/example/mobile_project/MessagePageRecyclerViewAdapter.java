package com.example.mobile_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MessagePageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    ArrayList<Message> messages;
    private static final int TYPE_IN = 0;
    private static final int TYPE_OUT = 1;

    public MessagePageRecyclerViewAdapter(Context context, ArrayList<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).isSent()) {
            return TYPE_OUT;
        } else {
            return TYPE_IN;
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        if (viewType == TYPE_IN) {
            View view = layoutInflater.inflate(R.layout.recycler_view_message_in, parent, false);
            return new MyViewHolderIn(view);
        } else {
            View view = layoutInflater.inflate(R.layout.recycler_view_message_out, parent, false);
            return new MyViewHolderOut(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        long timestamp = messages.get(position).getTimestamp();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String time = formatter.format(new Date(timestamp));
        if (holder instanceof MyViewHolderIn) {
            ((MyViewHolderIn) holder).content.setText(messages.get(position).getContent());
            ((MyViewHolderIn) holder).timeStamp.setText(time);
        } else if (holder instanceof MyViewHolderOut) {
            ((MyViewHolderOut) holder).content.setText(messages.get(position).getContent());
            ((MyViewHolderOut) holder).timeStamp.setText(time);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MyViewHolderIn extends RecyclerView.ViewHolder {
        TextView content;
        TextView timeStamp;

        public MyViewHolderIn(@NonNull View itemViewIn) {
            super(itemViewIn);
            content = itemViewIn.findViewById(R.id.textContentIn);
            timeStamp = itemViewIn.findViewById(R.id.textTimeIn);
        }
    }

    public static class MyViewHolderOut extends RecyclerView.ViewHolder {
        TextView content;
        TextView timeStamp;

        public MyViewHolderOut(@NonNull View itemViewOut) {
            super(itemViewOut);
            content = itemViewOut.findViewById(R.id.textContentOut);
            timeStamp = itemViewOut.findViewById(R.id.textTimeOut);
        }
    }
}