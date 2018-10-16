package com.example.jay1805.itproject;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jay1805.itproject.Map.CurrentLocation;
import com.example.jay1805.itproject.Map.GetDirectionsData;
import com.example.jay1805.itproject.Map.Map;
import com.example.jay1805.itproject.Map.URLCreator;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,NavigationView.OnNavigationItemSelectedListener{
    private CurrentLocation currentLocation;
    private Map map;
    private Location lastKnownLoc;
    private DrawerLayout myDrawerLayout;
    private ActionBarDrawerToggle myToggle;
    private NavigationView myView;

    private PlaceAutocompleteFragment placeAutocompleteFragment;

    private URLCreator urlCreator;

    private double volLat=0;
    private double volLongi=0;
    private String currentVolunteerName;


    private HashMap<Marker, String> markers;

    LatLng currentDestination;
    Marker marker;
    Marker markerOfElderly;

    ////////////////////////////////////////
    private SlidingUpPanelLayout slidingLayout;
    private FloatingActionButton SosButton;
    private Button VolunteersButton;
    ///////////////////////////////////////

    private Marker destinationMarker;
    private MarkerOptions destMarkerOptions;
    private List<Polyline> route;

    private String modeOfTransport;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setButtonListeners();
        markers = new HashMap<Marker, String>();

        /////////////////////////////////////////////////////
//        btnShow = (Button)findViewById(R.id.btn_show);
//        btnHide = (Button)findViewById(R.id.btn_hide);
//        textView = (TextView)findViewById(R.id.text);

        //set layout slide listener
        slidingLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        SosButton = findViewById(R.id.floatingButton);
        VolunteersButton = findViewById(R.id.volunteersButton);


        createVolunteerChildrenInDB();


        SosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout routeSlider = findViewById(R.id.route);
                routeSlider.setVisibility(View.GONE);
                LinearLayout helpSlider = findViewById(R.id.sosSlider);
                helpSlider.setVisibility(View.VISIBLE);
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        VolunteersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                //startActivity(new Intent(getApplicationContext(),MapsActivity.class));
                PlaceVolunteerMarkerOnMap();

            }
        });
//        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        //some "demo" event
//        slidingLayout.setPanelSlideListener(onSlideListener());
//        btnHide.setOnClickListener(onHideListener());
//        btnShow.setOnClickListener(onShowListener());
        ///////////////////////////////////////////////////

        if (ContextCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.READ_PHONE_STATE,android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS},
                    1);
        }

        placeAutocompleteFragment = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        placeAutocompleteFragment.setFilter(new AutocompleteFilter.Builder().setCountry("AU").build());

        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                currentLocation.hideCurrentLocation();
                final LatLng latLngLoc = place.getLatLng();
                destMarkerOptions = new MarkerOptions();
                if(marker!=null){
                    marker.remove();
                }
                map.clearMap();
                currentDestination = latLngLoc;

                destMarkerOptions.position(currentDestination);
                destMarkerOptions.title("Your search results");
                destMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                destinationMarker = map.addMarker(destMarkerOptions,currentDestination);
                modeOfTransport = "driving";
                LinearLayout routeSlider = findViewById(R.id.route);
                routeSlider.setVisibility(View.VISIBLE);
                LinearLayout helpSlider = findViewById(R.id.sosSlider);
                helpSlider.setVisibility(View.GONE);
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

            }

            @Override
            public void onError(Status status) {
                Toast.makeText(MapsActivity.this, ""+status.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        placeAutocompleteFragment.getView().findViewById(R.id.place_autocomplete_clear_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // example : way to access view from PlaceAutoCompleteFragment
                        // ((EditText) autocompleteFragment.getView()
                        // .findViewById(R.id.place_autocomplete_search_input)).setText("");
                        placeAutocompleteFragment.setText("");
                        view.setVisibility(View.GONE);
                        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                        map.clearMap();
                        currentLocation.showCurrentLocation();
                        modeOfTransport = "driving";
                    }
                });

        myDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        myToggle = new ActionBarDrawerToggle(MapsActivity.this, myDrawerLayout, R.string.open, R.string.close);
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




        //testing alert!!!!
        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Requested").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    System.out.println("###############DATASNAPSHOT: " + dataSnapshot.getValue().toString());
                    if (dataSnapshot.getValue().toString().equals("True")) {
                        startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
                        java.util.Map map = new HashMap<>();
                        final DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                        // map.put("Requested", "False");
                       // map.put("ElderlyIDRequested","");
                        userDB.updateChildren(map);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        urlCreator = new URLCreator();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("GPSLocationUpdates"));
        lastKnownLoc = null;

        String shareID;
        Intent intent = getIntent();

        if (intent.hasExtra("Share ID") && intent.getExtras().containsKey("Share ID")) {

            shareID = intent.getExtras().getString("Share ID");
            System.out.println("Share ID is: " + shareID);
            Log.d("SHAREID", shareID);
            // Get a reference to our posts
//            if (FirebaseDatabase.getInstance().getReference().child("gps-sharing").child(shareID) != null) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("gps-sharing").child(shareID);
//                if ((ref.child("latitude") != null) && (ref.child("longitude") != null)){
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            double newLatitude = 0;
                            double newLongitude = 0;
                            for (DataSnapshot childSnapshot: dataSnapshot.getChildren()){
                                if (childSnapshot.getKey().toString().equals("latitude")){
                                    newLatitude = Double.parseDouble(childSnapshot.getValue().toString());
                                }
                                if (childSnapshot.getKey().toString().equals("longitude")){
                                    newLongitude = Double.parseDouble(childSnapshot.getValue().toString());
                                }
                            }

                            if (markerOfElderly!=null){
                                markerOfElderly.remove();
                            }

                            Log.d("Coord", "lat is: " +newLatitude);
                            Log.d("Coord", "long is: " +newLongitude);
                            LatLng latLng = new LatLng(newLatitude,newLongitude);
                            MarkerOptions mo = new MarkerOptions();
                            mo.position(latLng);
                            mo.title("Location of Elderly");
                            mo.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            markerOfElderly = map.addMarker(mo,latLng);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            makeToast("Tracking has Stopped");
                        }
                    });
//                } else{
//                    makeToast("missing coordinates");
//                }
//            } else{
//                makeToast("String don't exist.");
//            }

        }

    }

    private void createVolunteerChildrenInDB() {
        java.util.Map hmap = new HashMap<>();
        final DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        hmap.put("Requested", "False");
        hmap.put("ElderlyIDRequested","");
        userDB.updateChildren(hmap);
    }

    ///////////////////////////////////////////////////////

    private void PlaceVolunteerMarkerOnMap() {

        FirebaseDatabase.getInstance().getReference().child("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childsnapshot : dataSnapshot.getChildren()) {
                    for(DataSnapshot volunteersnapshot : childsnapshot.getChildren())
                    {

                        // getting volunteers' coordinates
                        if (volunteersnapshot.getKey().equals("User Type") && volunteersnapshot.getValue().toString().equals("Helper")) {
                            for(DataSnapshot volunteersnapshot2 : childsnapshot.getChildren())
                            {
                                if (volunteersnapshot2.getKey().equals("latitude")) {
                                    volLat = Double.parseDouble(volunteersnapshot2.getValue().toString());
                                }
                                if (volunteersnapshot2.getKey().equals("longitude")) {
                                    volLongi = Double.parseDouble(volunteersnapshot2.getValue().toString());

                                }
                                if (volunteersnapshot2.getKey().equals("name")) {
                                    currentVolunteerName = volunteersnapshot2.getValue().toString();
                                }


                                if(( volLat!=0 && volLongi!=0 && currentVolunteerName!=""))
                                {
                                    System.out.println("current vol: "+currentVolunteerName);
                                    MarkerOptions mo = new MarkerOptions();
                                    LatLng volLatLng = new LatLng(volLat,volLongi);

                                    mo.position(volLatLng);
                                    mo.title(currentVolunteerName);
                                    mo.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                                    //mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person));


                                    markers.put(map.addMarker(mo,volLatLng), childsnapshot.getKey());
                                    currentVolunteerName = "";
                                }
                            }
                        }
                    }


                }
                map.setListOfVolunteers(markers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    ////////////////////////////////////////////////////////
    private View.OnClickListener onHideListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hide sliding layout
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
//                btnShow.setVisibility(View.VISIBLE);
            }
        };
    }
    ///////////////////////////////////////////////////////

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

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("Status");
            Bundle b = intent.getBundleExtra("Location");
            lastKnownLoc = (Location) b.getParcelable("Location");
            if (lastKnownLoc != null) {
                Log.d("BS","I don't believe it"+lastKnownLoc.getLongitude());
                currentLocation.changeCurrentLocation(lastKnownLoc);
            }
        }
    };

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map = new Map(googleMap);
            currentLocation = new CurrentLocation(map);
            askForCurrentLocation();
        }


    }

    public void onClick(View v) {
        double latitude = currentLocation.getLatitude();
        double longitude = currentLocation.getLongitude();

        switch (v.getId()) {

//            case R.id.B_Hospital:
//                showNearbyPlaces("hospital",latitude,longitude);
//                break;
//
//            case R.id.B_Restaurant:
//                showNearbyPlaces("restaurant",latitude,longitude);
//                break;
//
//            case R.id.B_School:
//                showNearbyPlaces("school",latitude,longitude);
//                break;

            case R.id.B_to:
//                removeRoute();
                map.clearMap();
                destinationMarker = map.addMarker(destMarkerOptions,currentDestination);
                Object dataTransfer[] = new Object[3];
                String url = urlCreator.getDirectionsUrl(latitude, longitude, currentDestination.latitude, currentDestination.longitude, modeOfTransport);
                Log.d("LOL",url);
                GetDirectionsData getDirectionsData = new GetDirectionsData();
                dataTransfer[0] = map;
                dataTransfer[1] = url;
                dataTransfer[2] = currentDestination;
                getDirectionsData.execute(dataTransfer);
                sendMessageToActivity(url,currentDestination);
                route = getDirectionsData.getRoute();
                break;
        }

    }

    public void removeRoute(){
        if (route!=null) {
            map.removeRoute(route);
        }
    }

    private void sendMessageToActivity(String url, LatLng dest) {
        Intent intent = new Intent("New Route");
        // You can also include some extra data.
        intent.putExtra("url", url);
        Bundle b = new Bundle();
        b.putParcelable("dest", dest);
        intent.putExtra("dest", b);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

//    private void showNearbyPlaces(String tag, double latitude, double longitude){
//        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
//        Object dataTransfer[] = new Object[2];
//        map.clearMap();
//        String url = urlCreator.getUrl(latitude, longitude, tag);
//        dataTransfer[0] = map;
//        dataTransfer[1] = url;
//
//        getNearbyPlacesData.execute(dataTransfer);
//        makeToast("Showing Nearby "+tag);
//
//    }

    public void makeToast(String message){
        Toast.makeText(MapsActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void setButtonListeners(){
        final ImageButton button_Walk = (ImageButton) findViewById(R.id.B_walk);
        button_Walk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                button_Walk.setBackground();
                modeOfTransport = "walking";
            }
        });
        ImageButton button_Drive = (ImageButton) findViewById(R.id.B_car);
        button_Drive.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                modeOfTransport = "driving";
            }
        });
        ImageButton button_Bike = (ImageButton) findViewById(R.id.B_bike);
        button_Bike.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                modeOfTransport = "bicycling";
            }
        });
        ImageButton button_Transit = (ImageButton) findViewById(R.id.B_transit);
        button_Transit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                modeOfTransport = "transit";
            }
        });
    }


    private void askForCurrentLocation() {
        Intent intent = new Intent("SEND GPS");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
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