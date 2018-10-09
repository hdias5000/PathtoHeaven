package com.example.jay1805.itproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Button mapButton;
    private Button chatsButton;
    private Button profileButton;
    private Button findUserButton;
    private Button logoutButton;
    private DrawerLayout myDrawerLayout;
    private ActionBarDrawerToggle myToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startActivity(new Intent(getApplicationContext(), LaunchGPSActivity.class));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapButton = findViewById(R.id.B_map);
        chatsButton = findViewById(R.id.B_chat);
        profileButton = findViewById(R.id.B_profile);
        findUserButton = findViewById(R.id.findUser);
        logoutButton = findViewById(R.id.logout_btn);

        myDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        myToggle = new ActionBarDrawerToggle(MainActivity.this, myDrawerLayout, R.string.open, R.string.close);
        myDrawerLayout.addDrawerListener(myToggle);
        myToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_viewID);
        navigationView.setNavigationItemSelectedListener(this);

        findUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), FindUserActivity.class), 1);
            }
        });


        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            }
        });

        chatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ChatMainPageActivity.class));
            }
        });
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MyProfileActivity.class));
            }
        });


        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                // make sure the user is who he says he is
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return;
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
}


