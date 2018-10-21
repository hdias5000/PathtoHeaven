/**TEAM PATH TO HEAVEN
 * Authors:
 *  - Hasitha Dias:   789929
 *  - Jay Parikh:     864675
 *  - Anupama Sodhi:  791288
 *  - Kushagra Gupta: 804729
 *  - Manindra Arora: 827703
 * **/

package com.example.jay1805.itproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * This class allows the user to view their profile and also allows them to view their profile picture.
 * Further, it also allows them to change the state of their volunteer mode on and off at anytime.
 */
public class MyProfileActivity extends AppCompatActivity {

    private ImageView ProfilePic;
    private TextView Name;
    private TextView DoB;
    private TextView HomeAddress;
    private ProgressBar progressBar;
    private TextView UserType;
    private TextView PhoneNumber;
    private Switch isVolunteer;
    private Button submit;

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
        PhoneNumber = findViewById(R.id.theRealPhoneNumber);
        isVolunteer= (Switch) findViewById(R.id.setSwitch);
        submit = (Button) findViewById(R.id.submitMyProfile);
        progressBar = findViewById(R.id.progressBar);

        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("user").child(currentUserUid);

        final Map userMap= new HashMap<>();

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot childSnapShot: dataSnapshot.getChildren()) {
                    if(childSnapShot.getKey().equals("Profile Picture")) {
                        new DownloadImageTask(ProfilePic)
                                .execute(childSnapShot.getValue().toString());
                    }

                    if(childSnapShot.getKey().equals("Volunteer")) {
                        if(childSnapShot.getValue().equals("Yes")){
                            isVolunteer.setChecked(true);

                        }
                        else if(childSnapShot.getValue().equals("No"))
                        {
                            isVolunteer.setChecked(false);


                        }
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

        submit = (Button) findViewById(R.id.submitMyProfile);
        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(isVolunteer.isChecked())
                {
                    userMap.put("Volunteer","Yes");
                    userMap.put("Requested", "False");
                    userMap.put("ElderlyIDRequested","");
                }
                else
                {
                    userMap.put("Volunteer","No");
                    userMap.put("accepted","null");

                }
                userRef.updateChildren(userMap);
            }
        });


    }

    //Class for printing an image from a given URL
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
            progressBar.setVisibility(View.GONE);
        }
    }
}
