package com.example.jay1805.itproject;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VolunteerActivity extends AppCompatActivity {

    private String lat = "";
    private String longi = "";

    private String currentlat = "";
    private String currentlongi = "";

    private String currentUserId;
    private DatabaseReference userRef;
    private ListView mListView;
    private String currentVolunteerName = "";


    double distFromCurrentUser=0;
    private ArrayList<String> listViewValues = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer);

        FirebaseDatabase.getInstance().getReference().child("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childsnapshot : dataSnapshot.getChildren()) {
                    for(DataSnapshot volunteersnapshot : childsnapshot.getChildren())
                    {
                        // getting current user's coordinates
                        if(childsnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            for(DataSnapshot volunteersnapshot2 : childsnapshot.getChildren())
                            {
                                if (volunteersnapshot2.getKey().equals("latitude")) {
                                    currentlat = (volunteersnapshot2.getValue().toString());
                                }
                                if (volunteersnapshot2.getKey().equals("longitude")) {
                                    currentlongi = (volunteersnapshot2.getValue().toString());
                                }
                                System.out.println("Current User is: " + childsnapshot.getKey() + " " + "Position: " + currentlat + " " + currentlongi);

                            }
                        }

                        // getting volunteers' coordinates
                        if (volunteersnapshot.getKey().equals("User Type") && volunteersnapshot.getValue().toString().equals("Helper")) {
                            for(DataSnapshot volunteersnapshot2 : childsnapshot.getChildren())
                            {
                                if (volunteersnapshot2.getKey().equals("latitude")) {
                                    lat = (volunteersnapshot2.getValue().toString());
                                }
                                if (volunteersnapshot2.getKey().equals("longitude")) {
                                    longi = (volunteersnapshot2.getValue().toString());

                                }
                                if (volunteersnapshot2.getKey().equals("name")) {
                                    currentVolunteerName = volunteersnapshot2.getValue().toString();

                                }


                                if(lat!="" && longi!="" && currentVolunteerName!="")
                                {
                                    distFromCurrentUser = distance();

                                    String data = "\nVolunteer name: " + currentVolunteerName + "\nPosition: " + lat + ", " + longi +
                                            "\nDistance from you: "+ Double.toString(distFromCurrentUser)+ "km\n";

                                    listViewValues.add(data);

                                    currentUserId = childsnapshot.getKey();
                                    userRef = FirebaseDatabase.getInstance().getReference().child("user").child(currentUserId);
                                    Map map = new HashMap<>();
                                    map.put("distance", distFromCurrentUser);
                                    userRef.updateChildren(map);
                                    mListView = (ListView) findViewById(R.id.simpleListView);
                                    ListAdapter adapter = new ArrayAdapter<String>(VolunteerActivity.this, R.layout.activity_list_view,listViewValues);
                                    mListView.setAdapter(adapter);

                                    // printed the values in list view, empty variables
                                    lat = "";
                                    longi = "";
                                    currentVolunteerName = "";
                                }
                            }
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public double distance(){
        final int R = 6371; // Radius of the earth
        System.out.println("inside distance  "+ currentlat+"  "+ lat);
        double latDistance = Math.toRadians(Double.parseDouble(currentlat) - Double.parseDouble(lat));
        double lonDistance = Math.toRadians(Double.parseDouble(currentlongi) - Double.parseDouble(longi));
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(Double.parseDouble(currentlat))) * Math.cos(Math.toRadians(Double.parseDouble(lat)))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // convert to km


        distance = Math.pow(distance, 2);

        //String dist =Double.toString(Math.sqrt(distance));


        DecimalFormat df=new DecimalFormat("#.##");
        return Double.parseDouble(df.format(Math.sqrt(distance)));


    }

}
