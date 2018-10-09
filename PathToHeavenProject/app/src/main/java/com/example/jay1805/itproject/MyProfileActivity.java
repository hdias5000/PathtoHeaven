package com.example.jay1805.itproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

public class MyProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ImageView ProfilePic;
    private TextView Name;
    private TextView DoB;
    private TextView HomeAddress;
    private TextView UserType;
    private TextView PhoneNumber;
    private DrawerLayout myDrawerLayout;
    private ActionBarDrawerToggle myToggle;

    String currentUserUid;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        myDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        myToggle = new ActionBarDrawerToggle(MyProfileActivity.this, myDrawerLayout, R.string.open, R.string.close);
        myDrawerLayout.addDrawerListener(myToggle);
        myToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_viewID);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        final ImageView myProfileImage = headerView.findViewById(R.id.headerImage);
        final TextView myHeaderName = headerView.findViewById(R.id.headerTextView);
        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Profile Picture").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                new DownloadImageTask(myProfileImage)
                        .execute(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myHeaderName.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ProfilePic = findViewById(R.id.profilePic);
        Name = findViewById(R.id.theActualName);
        DoB = findViewById(R.id.theRealDOB);
        HomeAddress = findViewById(R.id.theRealHomeAddress);
        UserType = findViewById(R.id.theRealUserType);
        PhoneNumber = findViewById(R.id.theRealPhoneNumber);

        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("user").child(currentUserUid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapShot: dataSnapshot.getChildren()) {
                    if(childSnapShot.getKey().equals("Profile Picture")) {
                        new DownloadImageTask(ProfilePic)
                                .execute(childSnapShot.getValue().toString());
                    }
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
                    if(childSnapShot.getKey().equals("phone")) {
                        PhoneNumber.setText(childSnapShot.getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(myToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.Chats) {
            startActivity(new Intent(getApplicationContext(), ChatMainPageActivity.class));
        }

        if (id == R.id.Profile) {
            startActivity(new Intent(getApplicationContext(), MyProfileActivity.class));
        }

        if (id == R.id.Maps) {
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
        }

        if (id == R.id.FindUser) {
            startActivityForResult(new Intent(getApplicationContext(), FindUserActivity.class), 1);
        }

        if (id == R.id.Logout) {
            FirebaseAuth.getInstance().signOut();
            // make sure the user is who he says he is
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        return false;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
