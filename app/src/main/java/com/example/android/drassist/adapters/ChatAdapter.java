package com.example.android.drassist.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.drassist.R;
import com.example.android.drassist.home.Chat;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Objects;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<Chat> chatList;
    String userType;


    public static final int MESSAGE_TYPE_IN = 1;
    public static final int MESSAGE_TYPE_OUT = 2;

    public ChatAdapter(Context context, ArrayList<Chat> chatList, String userType) {
        this.context = context;
        this.chatList = chatList;
        this.userType = userType;
    }

    private class MessageInViewHolder extends RecyclerView.ViewHolder {

        TextView messageTV;

        MessageInViewHolder(final View itemView) {
            super(itemView);
            messageTV = itemView.findViewById(R.id.textMessage);
        }

        void bindi(int position) {
            Chat messageModel = chatList.get(position);
            messageTV.setText(messageModel.getMessage());
        }
    }

    private class MessageOutViewHolder extends RecyclerView.ViewHolder {

        TextView messageTV;

        MessageOutViewHolder(final View itemView) {
            super(itemView);
            messageTV = itemView.findViewById(R.id.textMessage);
        }

        void bind(int position) {
            Chat messageModel = chatList.get(position);
            messageTV.setText(messageModel.getMessage());
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        System.out.println(viewType+" viewType");
        if (viewType == MESSAGE_TYPE_IN) {
            return new MessageInViewHolder(LayoutInflater.from(context).inflate(R.layout.item_container_recieved_message, parent, false));
        }
        return new MessageOutViewHolder(LayoutInflater.from(context).inflate(R.layout.item_container_sent_message, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == MESSAGE_TYPE_IN) {
//            System.out.println(" in--------------");
            ((MessageInViewHolder) holder).bindi(position);

        } else {
//            System.out.println(" out--------------");
            ((MessageOutViewHolder) holder).bind(position);

        }
    }


    @Override
    public int getItemViewType(int position) {
        if (userType.equals(chatList.get(position).getSentBy())) {
            return MESSAGE_TYPE_OUT;

        } else {
            return MESSAGE_TYPE_IN;
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

}
