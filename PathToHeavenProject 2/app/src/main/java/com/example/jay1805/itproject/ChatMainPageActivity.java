package com.example.jay1805.itproject;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.jay1805.itproject.Chat.ChatListAdapter;
import com.example.jay1805.itproject.Chat.ChatObject;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatMainPageActivity extends AppCompatActivity {

    private RecyclerView chatListView;
    private RecyclerView.Adapter chatListViewAdapter;
    private RecyclerView.LayoutManager chatListViewLayoutManager;

    ArrayList<ChatObject> chatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main_page);

        Fresco.initialize(this);

        chatList = new ArrayList<>();

        getPermissions();
        initializeRecyclerView();
        getUserChatList();
    }

    private void getUserChatList() {
        DatabaseReference userChatDB = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat");
        userChatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        ChatObject mChats = new ChatObject(childSnapshot.getKey());
                        boolean exists = false;
                        for (ChatObject mChatIterator : chatList) {
                            if(mChatIterator.getChatId().equals(mChats.getChatId())) {
                                exists = true;
                            }
                        }
                        if(exists) {
                            continue;
                        }
                        chatList.add(mChats);
                        chatListViewAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initializeRecyclerView() {
        chatListView = findViewById(R.id.chatList);
        chatListView.setNestedScrollingEnabled(false);
        chatListView.setHasFixedSize(false);
        chatListViewLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
        chatListView.setLayoutManager(chatListViewLayoutManager);
        chatListViewAdapter = new ChatListAdapter(chatList);
        chatListView.setAdapter(chatListViewAdapter);
    }

    private void getPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS}, 1);
        }
    }
}
