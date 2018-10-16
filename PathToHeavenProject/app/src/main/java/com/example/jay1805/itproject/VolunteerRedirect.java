package com.example.jay1805.itproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class VolunteerRedirect extends AppCompatActivity {

    String elderlyName;
    String elderlyID;
    TextView elderlyNameTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_redirect);
        elderlyName = getIntent().getExtras().getString("elderlyName");
        elderlyID = getIntent().getExtras().getString("elderlyID");
        elderlyNameTV = (TextView) findViewById(R.id.textViewElderName);
        elderlyNameTV.setText(elderlyName);

    }
}
