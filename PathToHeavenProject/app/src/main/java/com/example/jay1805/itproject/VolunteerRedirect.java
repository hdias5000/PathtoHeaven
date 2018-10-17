package com.example.jay1805.itproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.calling.Call;

import java.util.ArrayList;

public class VolunteerRedirect extends BaseActivity {

    String elderlyName;
    String elderlyID;
    TextView elderlyNameTV;
    Button callVolunteer;
    Button chatVolunteer;
    Button mapRoute;


    ArrayList<String> CurrentUserChatIDs = new ArrayList<String>();
    ArrayList<String> ToUserChatIDs = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_redirect);
        elderlyName = getIntent().getExtras().getString("elderlyName");
        elderlyID = getIntent().getExtras().getString("elderlyID");

        elderlyNameTV = findViewById(R.id.textViewElderlyName);
        callVolunteer = findViewById(R.id.call_volunteer);
        chatVolunteer = findViewById(R.id.chat_volunteer);
        mapRoute = findViewById(R.id.map_volunteer);



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

        callVolunteer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call call = getSinchServiceInterface().callUser(elderlyID);
                String callId = call.getCallId();

                Intent callScreen = new Intent(VolunteerRedirect.this, CallScreenActivity.class);
                callScreen.putExtra(SinchService.CALL_ID, callId);
                startActivity(callScreen);
            }
        });

        chatVolunteer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("user").child(elderlyID).child("chat").addListenerForSingleValueEvent(new ValueEventListener() {
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
                            FirebaseDatabase.getInstance().getReference().child("user").child(elderlyID).child("chat").child(key).setValue(true);
                        }

                        else {
                            Toast.makeText(getApplicationContext(), "Chat Already Exists", Toast.LENGTH_LONG).show();
                        }

                        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("chatID", chatIDKey);
                        intent.putExtras(bundle);
                        getApplicationContext().startActivity(intent);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        mapRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        elderlyNameTV.setText(elderlyName);


    }
}
