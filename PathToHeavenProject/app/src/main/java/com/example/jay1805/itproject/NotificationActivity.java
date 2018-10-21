/**TEAM PATH TO HEAVEN
 * Authors:
 *  - Hasitha Dias:   789929
 *  - Jay Parikh:     864675
 *  - Anupama Sodhi:  791288
 *  - Kushagra Gupta: 804729
 *  - Manindra Arora: 827703
 * **/

package com.example.jay1805.itproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
        elderlyID = getIntent().getStringExtra("elderlyID");

        // if current user id's REQUEST_IN is set to TRUE and long+lat of elderly is displayed
        AlertDialog.Builder a_builder = new AlertDialog.Builder(NotificationActivity.this);
        a_builder.setMessage("A volunteer request has been made. Do you choose to accept the challenge?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(NotificationActivity.this, "You are being connected..", Toast.LENGTH_SHORT).show();
                        FirebaseDatabase.getInstance().getReference().child("user").child(elderlyID).child("accepted").setValue("true");
                        getUserDetails();
                        // display elderly user's lat long on map
                        // navigate to destination
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        FirebaseDatabase.getInstance().getReference().child("user").child(elderlyID).child("accepted").setValue("false");
                        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Requested").setValue("False");
                        // do nothing
                        startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                        finish();
                    }
                });
        AlertDialog alert = a_builder.create();
        alert.setTitle("Incoming Request");
        alert.show();
    }

    private void getUserDetails() {
        volunteerDB = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        volunteerDB.child("ElderlyIDRequested").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null) {
                    elderlyID = dataSnapshot.getValue().toString();







                    volunteerDB = FirebaseDatabase.getInstance().getReference().child("user").child(elderlyID);
                    volunteerDB.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null) {
                                elderlyName = dataSnapshot.getValue().toString();


                                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                                intent.putExtra("elderlyName",elderlyName);
                                intent.putExtra("elderlyID",elderlyID);
//                                Bundle bundle = new Bundle();
//                                bundle.putString("elderlyName", elderlyName);
//                                bundle.putString("elderlyID", elderlyID);
//                                intent.putExtras(bundle);
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


