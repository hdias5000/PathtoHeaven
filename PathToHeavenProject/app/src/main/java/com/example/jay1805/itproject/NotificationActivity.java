package com.example.jay1805.itproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaDrm;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jay1805.itproject.User.UserObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class NotificationActivity extends AppCompatActivity {

    private DatabaseReference volunteerDB;
    private TextView userName;
    private String elderlyName;
    private String elderlyID;
    private TextView elderlyNameTextView;
    private DatabaseReference elderlyDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);


        // if current user id's REQUEST_IN is set to TRUE and long+lat of elderly is displayed
        AlertDialog.Builder a_builder = new AlertDialog.Builder(NotificationActivity.this);
        a_builder.setMessage("A volunteer request has been made. Do you choose to accept the challenge?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(NotificationActivity.this, "You are being connected..", Toast.LENGTH_SHORT).show();
                        getUserDetails();
                        // display elderly user's lat long on map
                        // navigate to destination
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        // do nothing
                        startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                    }
                });
        AlertDialog alert = a_builder.create();
        alert.setTitle("Incoming Request");
        alert.show();
    }

    private void getUserDetails() {
        System.out.println("User id is + " + FirebaseAuth.getInstance().getCurrentUser().getUid());
        volunteerDB = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        volunteerDB.child("ElderlyIDRequested").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null) {
                    System.out.println("\nHELLO ITS ME: "+dataSnapshot.getValue().toString()+"\n");
                    elderlyID = dataSnapshot.getValue().toString();







                    volunteerDB = FirebaseDatabase.getInstance().getReference().child("user").child(elderlyID);
                    volunteerDB.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null) {
                                System.out.println("\nHELLO ITS ME AGAIN: " + dataSnapshot.getValue().toString() + "\n");
                                elderlyName = dataSnapshot.getValue().toString();


                                Intent intent = new Intent(getApplicationContext(), VolunteerRedirect.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("elderlyName", elderlyName);
                                bundle.putString("elderlyID", elderlyID);
                                intent.putExtras(bundle);
                                getApplicationContext().startActivity(intent);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }});

        }
}

