package com.example.jay1805.itproject.User;

<<<<<<< HEAD
=======
import android.content.BroadcastReceiver;
import android.content.Context;
>>>>>>> master
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jay1805.itproject.CallScreenActivity;
import com.example.jay1805.itproject.ChatActivity;
import com.example.jay1805.itproject.R;
import com.example.jay1805.itproject.SinchService;
import com.example.jay1805.itproject.Utilities.SendNotifications;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.calling.Call;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.HashMap;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder>{

    SinchService.SinchServiceInterface sinchServiceInterface;
    SlidingUpPanelLayout slidingLayout;
    ArrayList<UserObject> userList;
    ArrayList<String> CurrentUserChatIDs = new ArrayList<String>();
    ArrayList<String> ToUserChatIDs = new ArrayList<String>();
    private String currentShareID = "";

    public UserListAdapter(ArrayList<UserObject> userList, SinchService.SinchServiceInterface sinchServiceInterface, SlidingUpPanelLayout slidingLayout) {
        this.userList = userList;
        this.slidingLayout = slidingLayout;
        this.sinchServiceInterface = sinchServiceInterface;
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

        if(position%2 == 0) {
            holder.contactLayout.setBackgroundResource(R.color.green_200);
        }
        else {
            holder.contactLayout.setBackgroundResource(R.color.yellow_200);
        }
        holder.mName.setText(userList.get(position).getName());
        holder.mPhone.setText(userList.get(position).getPhone());

        holder.chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                FirebaseDatabase.getInstance().getReference().child("user").child(userList.get(position).getUid()).child("chat").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

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

        holder.callsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call call = sinchServiceInterface.callUser(userList.get(position).getUid());
                String callId = call.getCallId();

                Intent callScreen = new Intent(v.getContext(), CallScreenActivity.class);
                callScreen.putExtra(SinchService.CALL_ID, callId);
                v.getContext().startActivity(callScreen);
            }
        });

        holder.helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context v = view.getContext();
                LocalBroadcastManager.getInstance(v).registerReceiver(
                        new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                String shareID = intent.getStringExtra("ID");
                                if (!currentShareID.equals(shareID)){
                                    HashMap<String,String> notification = new HashMap<>();
                                    notification.put("type","help");
                                    notification.put("name",userList.get(position).getName());
                                    notification.put("message","Click Here For Nudes");
                                    notification.put("shareID",shareID);
                                    notification.put("notificationKey",userList.get(position).getNotificationKey());
                                    new SendNotifications(notification);

                                }

                            }
                        }, new IntentFilter("GPS ID"));
                Intent intent = new Intent("UPLOAD GPS");
                LocalBroadcastManager.getInstance(v).sendBroadcast(intent);
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
        public ImageButton chatButton, callsButton, helpButton;
        public UserListViewHolder(View view) {
            super(view);
            mName = view.findViewById(R.id.name);
            mPhone = view.findViewById(R.id.phone);
            contactLayout = view.findViewById(R.id.contact_layout);
            chatButton = view.findViewById(R.id.chatButton);
            callsButton = view.findViewById(R.id.callButton);
            helpButton = view.findViewById(R.id.helpButton);
        }
    }

}
