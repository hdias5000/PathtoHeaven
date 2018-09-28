package com.example.jay1805.itproject;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProfilePageActivity extends AppCompatActivity {

    private EditText HomeAddress;
    private TextView nameOfUser;
    private Button saveDetails;

    private String name;

    private DatabaseReference userDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        HomeAddress = (EditText) findViewById(R.id.home_address_editText);
        nameOfUser = (TextView) findViewById(R.id.theActualName);
        saveDetails = (Button) findViewById(R.id.save_button);

        userDB = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userDB.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null) {
                    name = dataSnapshot.getValue().toString();
                    nameOfUser.setText(name);
                    Context context = getApplicationContext();
                    CharSequence text = name;
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        saveDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputingToDatabase();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    private void InputingToDatabase() {

        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot != null) {
                    Map<String,Object> userMap= new HashMap<>();
                    userMap.put("Home Address", HomeAddress.getText().toString());
                    userDB.updateChildren(userMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
