package com.example.jay1805.itproject.User;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jay1805.itproject.ChatActivity;
import com.example.jay1805.itproject.FindUserActivity;
import com.example.jay1805.itproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder>{

    ArrayList<UserObject> userList;
    ArrayList<String> CurrentUserChatIDs = new ArrayList<String>();
    ArrayList<String> ToUserChatIDs = new ArrayList<String>();

    public UserListAdapter(ArrayList<UserObject> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("chat").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot childSnapShot : dataSnapshot.getChildren()) {
                    CurrentUserChatIDs.add(childSnapShot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        UserListViewHolder rcv = new UserListViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final UserListViewHolder holder, final int position) {
        holder.mName.setText(userList.get(position).getName());
        holder.mPhone.setText(userList.get(position).getPhone());

        holder.contactLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                FirebaseDatabase.getInstance().getReference().child("user").child(userList.get(position).getUid()).child("chat").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

//                        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("chat").addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                for(DataSnapshot childSnapShot : dataSnapshot.getChildren()) {
//                                    CurrentUserChatIDs.add(childSnapShot.getKey());
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });

                        String chatIDKey = null;

                        for (DataSnapshot childSnapShot : dataSnapshot.getChildren()) {
                            ToUserChatIDs.add(childSnapShot.getKey());
                        }

                        Boolean chatExists = false;
                        for(String MyChatIDs : CurrentUserChatIDs) {
                            for(String ToChatIDs : ToUserChatIDs) {
                                if(MyChatIDs.equals(ToChatIDs)) {
                                    chatIDKey = MyChatIDs;
                                    chatExists = true;
                                }
                            }
                        }

                        if(chatExists.equals(false)) {
                            String key = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();
                            CurrentUserChatIDs.add(key);
                            ToUserChatIDs.add(key);
                            chatIDKey = key;
                            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).setValue(true);
                            FirebaseDatabase.getInstance().getReference().child("user").child(userList.get(position).getUid()).child("chat").child(key).setValue(true);
                        }

                        else {
                            Toast.makeText(v.getContext(), "Chat Already Exists", Toast.LENGTH_LONG).show();
                        }

                        Intent intent = new Intent(v.getContext(), ChatActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("chatID", chatIDKey);
                        intent.putExtras(bundle);
                        v.getContext().startActivity(intent);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserListViewHolder extends RecyclerView.ViewHolder {
        public TextView mName, mPhone;
        public LinearLayout contactLayout;
        public UserListViewHolder(View view) {
            super(view);
            mName = view.findViewById(R.id.name);
            mPhone = view.findViewById(R.id.phone);
            contactLayout = view.findViewById(R.id.contact_layout);
        }
    }

}
