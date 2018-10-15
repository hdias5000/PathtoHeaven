package com.example.jay1805.itproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class NotificationActivity extends AppCompatActivity {

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




}
