package com.example.jay1805.itproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.SinchError;

/**
 * This is the launcher activity of our application. It is the source of starting all the required
 * services. Further, if the user has already logged in, it takes you directly to the MapsActivity.
 * Otherwise, it takes you to the LoginActivity.
 */
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

    //Starting sinch call listener when user is logged in
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

    //starting the MapsActivity when the Sinch listener is established
    @Override
    public void onStarted() {
        Log.d("Sinch","STARTED");
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("called", "no");
        startActivity(intent);
    }
}
