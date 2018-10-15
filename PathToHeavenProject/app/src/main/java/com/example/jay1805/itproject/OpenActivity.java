package com.example.jay1805.itproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.SinchError;

public class OpenActivity extends BaseActivity implements SinchService.StartFailedListener{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);

        startActivity(new Intent(getApplicationContext(), LaunchGPSActivity.class));

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            final DatabaseReference userUid = FirebaseDatabase.getInstance().getReference().child("user").child(user.getUid());
            userUid.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userIsLoggedIn();
                    while(getSinchServiceInterface()==null) {

                    }
                    if(getSinchServiceInterface()!=null) {
                        System.out.println("THE SINCH SERVICE IS NOT NULL");
                    }
                    else {
                        System.out.println("THE SINCH SERVICE IS NULL");
                    }
//                    startActivity(new Intent(getApplicationContext(),MapsActivity.class));
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
        }
    }



    private void userIsLoggedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // to double check user has logged in
        if(user != null){
            SinchService.SinchServiceInterface in = getSinchServiceInterface();
            if (!in.isStarted()) {
                getSinchServiceInterface().startClient(user.getUid());
            }
        }
    }

    @Override
    protected void onServiceConnected() {

        getSinchServiceInterface().setStartListener(this);
    }

    @Override
    public void onStartFailed(SinchError error) {

    }

    @Override
    public void onStarted() {
        Log.d("Sinch","STARTED please work");
        startActivity(new Intent(getApplicationContext(),MapsActivity.class));
    }
}
