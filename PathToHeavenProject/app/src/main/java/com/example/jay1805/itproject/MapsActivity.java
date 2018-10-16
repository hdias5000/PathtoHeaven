package com.example.jay1805.itproject;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
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
import com.example.jay1805.itproject.Map.DirectionsViewAdapter;
import com.example.jay1805.itproject.Map.GetDirectionsData;
import com.example.jay1805.itproject.Map.Map;
import com.example.jay1805.itproject.Map.RouteData;
import com.example.jay1805.itproject.Map.URLCreator;
import com.example.jay1805.itproject.User.UserListAdapter;
import com.example.jay1805.itproject.User.UserObject;
import com.example.jay1805.itproject.Utilities.CountryToPhonePrefix;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MapsActivity extends BaseActivity implements OnMapReadyCallback,NavigationView.OnNavigationItemSelectedListener {

    static final String TAG = MapsActivity.class.getSimpleName();

    private CurrentLocation currentLocation;
    private Map map;
    private Location lastKnownLoc;
    private DrawerLayout myDrawerLayout;
    private ActionBarDrawerToggle myToggle;
    private NavigationView myView;

    private AudioPlayer mAudioPlayer;
    private String mCallId;
    private FloatingActionButton endCallButton;

    private RecyclerView DirectionsView;
    private RecyclerView.Adapter DirectionsViewAdapter;
    private RecyclerView.LayoutManager DirectionsViewLayoutManager;

    private PlaceAutocompleteFragment placeAutocompleteFragment;

    private RecyclerView userListView;
    private RecyclerView.Adapter userListViewAdapter;
    private RecyclerView.LayoutManager userListViewLayoutManager;

    public ArrayList<UserObject> contactList;

    ArrayList<UserObject> userList;

    private URLCreator urlCreator;

    ////////////////////////////////////////
    private SlidingUpPanelLayout slidingLayout;
    private FloatingActionButton SosButton;
    private Button VolunteersButton;
    ///////////////////////////////////////

    private Marker destinationMarker;
    private MarkerOptions destMarkerOptions;
    private List<Polyline> route;

    private RouteData currentRouteData;

    private String modeOfTransport;

    private boolean helpMode;

    LatLng currentDestination;
    Marker marker;

    Marker markerOfElderly;
    LatLng locationOfElderly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        helpMode = false;
        //set layout slide listener
        slidingLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);

        endCallButton = findViewById(R.id.endCallButton);
        mAudioPlayer = new AudioPlayer(this);
        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
        if(mCallId!=null) {
            endCallButton.setVisibility(View.VISIBLE);
        }
        else {
            endCallButton.setVisibility(View.GONE);
        }
        endCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endCall();
            }
        });
//        hideSliders();

        OneSignal.startInit(this).setNotificationOpenedHandler(new NotificationIsOpened(getApplicationContext())).init();
        OneSignal.setSubscription(true);
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {

                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificationKey").setValue(userId);
            }
        });
        OneSignal.setInFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification);

        setButtonListeners();
        gettingPermissions();
        createAutoCompleteSearch();
        creatingMenu();
        loadMapFragment();
        gpsSharing();
        setUpBroadcastReceivers();

        urlCreator = new URLCreator();
        lastKnownLoc = null;


        VolunteersButton = findViewById(R.id.volunteersButton);

        contactList = new ArrayList<>();
        userList = new ArrayList<>();
        //initializeRecyclerView();
        userListView = findViewById(R.id.userList);
        userListView.setNestedScrollingEnabled(false);
        userListView.setHasFixedSize(false);
        userListViewLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
        userListView.setLayoutManager(userListViewLayoutManager);
        FirebaseDatabase.getInstance().getReference().child("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(getSinchServiceInterface()!=null) {
                    Log.d("Sinch","NIT NULL");
                    System.out.println("SinchService is not null");
                }
                else {
                    Log.d("Sinch","YEEt NULL");
                    System.out.println("SinchService is null");
                }
                userListViewAdapter = new UserListAdapter(userList, getSinchServiceInterface(), slidingLayout);
                userListView.setAdapter(userListViewAdapter);
                getContactList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        VolunteersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            }
        });


    }

    @Override
    public void onServiceConnected() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.addCallListener(new SinchCallListener());
        } else {
            Log.e(TAG, "Started with invalid callId, aborting.");
//            finish();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void endCall() {
        mAudioPlayer.stopProgressTone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
        finish();
    }

    private class SinchCallListener implements CallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended. Reason: " + cause.toString());
            mAudioPlayer.stopProgressTone();
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            String endMsg = "Call ended: " + call.getDetails().toString();
            Toast.makeText(MapsActivity.this, endMsg, Toast.LENGTH_LONG).show();
            endCall();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
            mAudioPlayer.stopProgressTone();
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
            mAudioPlayer.playProgressTone();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }
    }

    private void hideSliders(){
        LinearLayout route = findViewById(R.id.route);
        LinearLayout sos = findViewById(R.id.sosSlider);
        LinearLayout help = findViewById(R.id.help);
        LinearLayout helpRoute = findViewById(R.id.helpRoute);
        route.setVisibility(View.GONE);
        sos.setVisibility(View.GONE);
        help.setVisibility(View.GONE);
        helpRoute.setVisibility(View.GONE);
        slidingLayout.setPanelHeight(120);
    }

    private void loadMapFragment() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void setUpBroadcastReceivers() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("GPSLocationUpdates"));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                onRouteSuccess, new IntentFilter("RouteSuccess"));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        final Button stopButton = findViewById(R.id.B_StopGPSShare);
                        stopButton.setVisibility(View.VISIBLE);
                        stopButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent("STOP GPS");
                                LocalBroadcastManager.getInstance(view.getContext()).sendBroadcast(intent);
                                stopButton.setVisibility(View.GONE);
                            }
                        });
                    }
                }, new IntentFilter("GPS ID"));
    }

    private void gettingPermissions() {
        if (ContextCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.READ_PHONE_STATE,android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS},
                    1);
        }
    }

    private void setDestinationMarker(){
        destMarkerOptions = new MarkerOptions();
        destMarkerOptions.position(currentDestination);
        destMarkerOptions.title("Your search results");
        destMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        destinationMarker = map.addMarker(destMarkerOptions,currentDestination);
    }

    private void createAutoCompleteSearch() {
        placeAutocompleteFragment = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        placeAutocompleteFragment.setFilter(new AutocompleteFilter.Builder().setCountry("AU").build());

        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                currentLocation.hideCurrentLocation();
                final LatLng latLngLoc = place.getLatLng();
                if(marker!=null){
                    marker.remove();
                }
                map.clearMap();
                currentDestination = latLngLoc;

                setDestinationMarker();

                modeOfTransport = "walking";

                hideSliders();

                if (helpMode){
                    slidingLayout.setPanelHeight(400);
                    LinearLayout helpRouteSlider = findViewById(R.id.helpRoute);
                    helpRouteSlider.setVisibility(View.VISIBLE);
                }else {
                    LinearLayout routeSlider = findViewById(R.id.route);
                    routeSlider.setVisibility(View.VISIBLE);
                }


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
    }

    private void creatingMenu() {
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
                if(dataSnapshot.getValue() != null) {
                    new DownloadImageTask(myProfileImage)
                            .execute(dataSnapshot.getValue().toString());
                }
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
    }

    private void gpsSharing() {
        final String shareID;
        String userID;
        Intent intent = getIntent();
        if (intent.hasExtra("Share ID") && intent.getExtras().containsKey("Share ID") && intent.getExtras().containsKey("userID")) {

            shareID = intent.getExtras().getString("Share ID");
            userID = intent.getExtras().getString("userID");
            System.out.println(userID);
            System.out.println("Share ID is: " + shareID);
            Log.d("SHAREID", shareID);

            FirebaseDatabase.getInstance().getReference().child("gps-sharing").child(shareID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        setGPSSharing(shareID);
                    }else{
                        /////////send name of elderly person
                        makeToast("Elderly Person has Disabled GPS Sharing");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    makeToast("Tracking has Stopped");
                    helpMode = false;
                    startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                }
            });

        }
    }

    private void setGPSSharing(String shareID){

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

                helpMode = true;

                if (markerOfElderly!=null){
                    markerOfElderly.remove();
                }

                Log.d("Coord", "lat is: " +newLatitude);
                Log.d("Coord", "long is: " +newLongitude);
                locationOfElderly= new LatLng(newLatitude,newLongitude);

                setMarkerForElderlyPerson();

                hideSliders();
                slidingLayout.setPanelHeight(240);
                LinearLayout route = findViewById(R.id.help);
                route.setVisibility(View.VISIBLE);
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                makeToast("Tracking has Stopped");
                helpMode = false;
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            }
        });
//                } else{
//                    makeToast("missing coordinates");
//                }
//            } else{
//                makeToast("String don't exist.");
//            }

    }

    private void setMarkerForElderlyPerson(){
        MarkerOptions mo = new MarkerOptions();
        mo.position(locationOfElderly);
        mo.title("Location of Elderly");
        mo.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        markerOfElderly = map.addMarker(mo,locationOfElderly);
    }

    private void getContactList() {
        String isoPrefix = getCountryIso();
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            phone = phone.replace(" ", "");
            phone = phone.replace("-", "");
            phone = phone.replace("(", "");
            phone = phone.replace(")", "");

            if(!String.valueOf(phone.charAt(0)).equals("+")) {
                phone = isoPrefix + phone;
            }
            UserObject mContacts = new UserObject(name, phone, "", "");
            contactList.add(mContacts);
            getUserDetails(mContacts);
        }
    }

    private void getUserDetails(UserObject mContacts) {
        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("user");
        Query query = userDB.orderByChild("phone").equalTo(mContacts.getPhone());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String phone = "", name = "", myNotificationKey="";
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        if(childSnapshot.child("phone").getValue() != null) {
                            phone = childSnapshot.child("phone").getValue().toString();
                        }
                        if(childSnapshot.child("name").getValue() != null) {
                            name = childSnapshot.child("name").getValue().toString();
                        }
                        if(childSnapshot.child("notificationKey").getValue() != null) {
                            myNotificationKey = childSnapshot.child("notificationKey").getValue().toString();
                        }

                        UserObject mUser = new UserObject(name, phone, childSnapshot.getKey(), myNotificationKey);
                        for(UserObject mContactIterator : contactList) {
                            if(mContactIterator.getPhone().equals(phone)) {
                                mUser.setName(mContactIterator.getName());

                            }
                        }

                        userList.add(mUser);
                        userListViewAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getCountryIso() {
        String ISO = null;

        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);

        if(telephonyManager.getNetworkCountryIso() != null) {
            if(telephonyManager.getNetworkCountryIso().toString().equals("")) {
                ISO = telephonyManager.getNetworkCountryIso().toString();
            }
        }
        if (ISO == null) {
            return "";
        }
        return CountryToPhonePrefix.getPhone(ISO);

    }

//    private void initializeRecyclerView() {
//
//        userListView = findViewById(R.id.userList);
//        userListView.setNestedScrollingEnabled(false);
//        userListView.setHasFixedSize(false);
//        userListViewLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
//        userListView.setLayoutManager(userListViewLayoutManager);
//        if(getSinchServiceInterface()!=null) {
//            Log.d("Sinch","NIT NULL");
//            System.out.println("SinchService is not null");
//        }
//        else {
//            Log.d("Sinch","YEEt NULL");
//            System.out.println("SinchService is null");
//        }
//        userListViewAdapter = new UserListAdapter(userList);
//        userListView.setAdapter(userListViewAdapter);
//    }

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
            OneSignal.setSubscription(false);
            FirebaseAuth.getInstance().signOut();
            // make sure the user is who he says he is
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        return false;
    }



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
                LatLng location = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                findRoute(location,currentDestination);
//                while (!getDirectionsData.isSuccess()){
//
//                }
//                getDirectionsData

                break;
        }

    }

    private void findRoute(LatLng currentLocation, LatLng currentDestination){
        Object dataTransfer[] = new Object[3];
        String url = urlCreator.getDirectionsUrl(currentLocation.latitude, currentLocation.longitude, currentDestination.latitude, currentDestination.longitude, modeOfTransport);
        Log.d("LOL",url);
        currentRouteData = new RouteData();
        GetDirectionsData getDirectionsData = new GetDirectionsData(currentRouteData, new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                printRouteInfo(currentRouteData.getRouteInformation(),currentRouteData.getStepInformation());
            }

            @Override
            public void onFailure(Exception e) {
                makeToast("Route Not Found");
            }
        });
        dataTransfer[0] = map;
        dataTransfer[1] = url;
        dataTransfer[2] = currentDestination;
        getDirectionsData.execute(dataTransfer);
        sendMessageToActivity(url, currentDestination);
        route = getDirectionsData.getRoute();
    }

    private ArrayList change(ArrayList step){
        ArrayList <HashMap> stepUpdated = new ArrayList<>();

        for (int i=0;i<step.size();i++){
            HashMap info = (HashMap) step.get(i);
            String str = info.get("Maneuver").toString().replaceAll("-","_");
            String drawable = "direction_"+str;
            int resID =getResources().getIdentifier(drawable, "drawable", getPackageName());
            System.out.println("Jay is shit "+ drawable);
            info.put("manRes", Integer.toString(resID));
            stepUpdated.add(info);
        }
        return stepUpdated;
    }

    private void printRouteInfo(HashMap<String,String> routeInformation, ArrayList stepInformation) {
        ArrayList newSteps = change(stepInformation);
        DirectionsView = findViewById(R.id.List_Directions);
        DirectionsView.setNestedScrollingEnabled(false);
        DirectionsView.setHasFixedSize(false);
        DirectionsViewLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
        DirectionsView.setLayoutManager(DirectionsViewLayoutManager);
        DirectionsViewAdapter = new DirectionsViewAdapter(newSteps);
        DirectionsView.setAdapter(DirectionsViewAdapter);

//        System.out.println(routeInformation.get("Summary"));
//        System.out.println(routeInformation.get("Distance"));
//        System.out.println(routeInformation.get("Duration"));
//        for (int i =0;i<stepInformation.size();i++){
//            HashMap step = (HashMap) stepInformation.get(i);
//            System.out.println(step.get("Distance"));
//            System.out.println(step.get("Duration"));
//            System.out.println(step.get("Maneuver"));
//            System.out.println(step.get("Instructions"));
//        }
    }

    private BroadcastReceiver onRouteSuccess = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            printRouteInfo(currentRouteData.getRouteInformation(),currentRouteData.getStepInformation());
        }
    };

//    public void removeRoute(){
//        if (route!=null) {
//            map.removeRoute(route);
//        }
//    }

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
        sosButtonListener();
        modesOfTransportListeners();

    }

    private void sosButtonListener() {
        SosButton = findViewById(R.id.floatingButton);
        SosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSliders();
                LinearLayout helpSlider = findViewById(R.id.sosSlider);
                helpSlider.setVisibility(View.VISIBLE);
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });


        Button routeToElderlyButton = findViewById(R.id.B_RouteToElder);
        routeToElderlyButton.setOnClickListener(alrightalrightalright);
        Button routeToElderlyButton1 = findViewById(R.id.B_RouteToElder1);
        routeToElderlyButton1.setOnClickListener(alrightalrightalright);

        Button elderToDestination = findViewById(R.id.B_ElderToDestination);
        elderToDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.clearMap();
                setMarkerForElderlyPerson();
                setDestinationMarker();
                findRoute(locationOfElderly, currentDestination);
            }
        });
    }

    View.OnClickListener alrightalrightalright = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            map.clearMap();
            setMarkerForElderlyPerson();
            LatLng location = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
            findRoute(location,locationOfElderly);
        }
    };

    private void modesOfTransportListeners() {
        final ImageButton button_Walk = (ImageButton) findViewById(R.id.B_walk);
        button_Walk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setBackgroundForModesOfTransport(button_Walk);
                modeOfTransport = "walking";
            }
        });
        final ImageButton button_Drive = (ImageButton) findViewById(R.id.B_car);
        button_Drive.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                setBackgroundForModesOfTransport(button_Drive);
                modeOfTransport = "driving";
            }
        });
        final ImageButton button_Bike = (ImageButton) findViewById(R.id.B_bike);
        button_Bike.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                setBackgroundForModesOfTransport(button_Bike);
                modeOfTransport = "bicycling";
            }
        });
        final ImageButton button_Transit = (ImageButton) findViewById(R.id.B_transit);
        button_Transit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                setBackgroundForModesOfTransport(button_Transit);
                modeOfTransport = "transit";
            }
        });
    }

    private void setBackgroundForModesOfTransport(ImageButton selectedButton){
        final ImageButton button_Walk = (ImageButton) findViewById(R.id.B_walk);
        ImageButton button_Drive = (ImageButton) findViewById(R.id.B_car);
        ImageButton button_Transit = (ImageButton) findViewById(R.id.B_transit);
        ImageButton button_Bike = (ImageButton) findViewById(R.id.B_bike);
        button_Walk.setBackgroundResource(R.color.blue_A400);
        button_Drive.setBackgroundResource(R.color.blue_A400);
        button_Transit.setBackgroundResource(R.color.blue_A400);
        button_Bike.setBackgroundResource(R.color.blue_A400);
        selectedButton.setBackgroundResource(R.color.grey_700);

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