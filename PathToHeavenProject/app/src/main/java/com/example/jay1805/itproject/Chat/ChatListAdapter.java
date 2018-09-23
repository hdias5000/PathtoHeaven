package com.example.jay1805.itproject.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jay1805.itproject.ChatActivity;
import com.example.jay1805.itproject.R;

import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>{

    ArrayList<ChatObject> ChatList;

    public ChatListAdapter(ArrayList<ChatObject> ChatList) {
        this.ChatList = ChatList;
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        ChatListViewHolder rcv = new ChatListViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatListViewHolder holder, final int position) {
        holder.mTitle.setText(ChatList.get(position).getChatId());

        holder.contactLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("chatID", ChatList.get(holder.getAdapterPosition()).getChatId());
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (ChatList == null) {
            return 0;
        }
        return ChatList.size();
    }

    public class ChatListViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public LinearLayout contactLayout;
        public ChatListViewHolder(View view) {
            super(view);
            mTitle = view.findViewById(R.id.title);
            contactLayout = view.findViewById(R.id.contact_layout);
        }
    }

}
