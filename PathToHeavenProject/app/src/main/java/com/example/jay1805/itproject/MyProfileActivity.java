package com.example.jay1805.itproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class MyProfileActivity extends AppCompatActivity {

    ImageView ProfilePic;
    TextView Name;
    TextView DoB;
    TextView HomeAddress;
    TextView UserType;

    String currentUserUid;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        ProfilePic = findViewById(R.id.profilePic);
        Name = findViewById(R.id.theActualName);
        DoB = findViewById(R.id.theRealDOB);
        HomeAddress = findViewById(R.id.theRealHomeAddress);
        UserType = findViewById(R.id.theRealUserType);

        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("user").child(currentUserUid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapShot: dataSnapshot.getChildren()) {
//                    if(childSnapShot.getKey().equals("Profile Picture")) {
//                            String url = childSnapShot.getValue().toString();
//                        InputStream in = null;
//                        try {
//                            in = new URL(url).openStream();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        Bitmap bmp = BitmapFactory.decodeStream(in);
//                            ProfilePic.setImageBitmap(bmp);
//                    }
                    if(childSnapShot.getKey().equals("name")) {
                        Name.setText(childSnapShot.getValue().toString());
                    }
                    if(childSnapShot.getKey().equals("Date of Birth")) {
                        DoB.setText(childSnapShot.getValue().toString());
                    }
                    if(childSnapShot.getKey().equals("Home Address")) {
                        HomeAddress.setText(childSnapShot.getValue().toString());
                    }
                    if(childSnapShot.getKey().equals("User Type")) {
                        UserType.setText(childSnapShot.getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
