package com.example.jay1805.itproject;

import android.support.v7.app.AppCompatActivity;

public class FindUserActivity extends AppCompatActivity {


//    private RecyclerView userListView;
//    private RecyclerView.Adapter userListViewAdapter;
//    private RecyclerView.LayoutManager userListViewLayoutManager;
//    private DrawerLayout myDrawerLayout;
//    private ActionBarDrawerToggle myToggle;
//
//    public ArrayList<UserObject> contactList;
//
//    ArrayList<UserObject> userList;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_find_user);
//
//        myDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
//        myToggle = new ActionBarDrawerToggle(FindUserActivity.this, myDrawerLayout, R.string.open, R.string.close);
//        myDrawerLayout.addDrawerListener(myToggle);
//        myToggle.syncState();
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_viewID);
//        navigationView.setNavigationItemSelectedListener(this);
//        View headerView = navigationView.getHeaderView(0);
//        final ImageView myProfileImage = headerView.findViewById(R.id.headerImage);
//        final TextView myHeaderName = headerView.findViewById(R.id.headerTextView);
//        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Profile Picture").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                new DownloadImageTask(myProfileImage)
//                        .execute(dataSnapshot.getValue().toString());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                myHeaderName.setText(dataSnapshot.getValue().toString());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//        contactList = new ArrayList<>();
//        userList = new ArrayList<>();
//        initializeRecyclerView();
//        getContactList();
//    }
//
//    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
//        ImageView bmImage;
//
//        public DownloadImageTask(ImageView bmImage) {
//            this.bmImage = bmImage;
//        }
//
//        protected Bitmap doInBackground(String... urls) {
//            String urldisplay = urls[0];
//            Bitmap mIcon11 = null;
//            try {
//                InputStream in = new java.net.URL(urldisplay).openStream();
//                mIcon11 = BitmapFactory.decodeStream(in);
//            } catch (Exception e) {
//                Log.e("Error", e.getMessage());
//                e.printStackTrace();
//            }
//            return mIcon11;
//        }
//
//        protected void onPostExecute(Bitmap result) {
//            bmImage.setImageBitmap(result);
//        }
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if(myToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//
//        int id = item.getItemId();
//
//        if (id == R.id.Chats) {
//            startActivity(new Intent(getApplicationContext(), ChatMainPageActivity.class));
//        }
//
//        if (id == R.id.Profile) {
//            startActivity(new Intent(getApplicationContext(), MyProfileActivity.class));
//        }
//
//        if (id == R.id.Maps) {
//            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
//        }
//
//        if (id == R.id.FindUser) {
//            startActivityForResult(new Intent(getApplicationContext(), FindUserActivity.class), 1);
//        }
//
//        if (id == R.id.Logout) {
//            OneSignal.setSubscription(false);
//            FirebaseAuth.getInstance().signOut();
//            // make sure the user is who he says he is
//            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
//            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//            finish();
//        }
//
//        return false;
//    }
//
//    private void getContactList() {
//        String isoPrefix = getCountryIso();
//        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
//        while (phones.moveToNext()) {
//            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//            String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//
//            phone = phone.replace(" ", "");
//            phone = phone.replace("-", "");
//            phone = phone.replace("(", "");
//            phone = phone.replace(")", "");
//
//            if(!String.valueOf(phone.charAt(0)).equals("+")) {
//                phone = isoPrefix + phone;
//            }
//            UserObject mContacts = new UserObject(name, phone, "", myNotificationKey);
//            contactList.add(mContacts);
//            getUserDetails(mContacts);
//        }
//    }
//
//    private void getUserDetails(UserObject mContacts) {
//        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("user");
//        Query query = userDB.orderByChild("phone").equalTo(mContacts.getPhone());
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists()) {
//                    String phone = "", name = "";
//                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
//                        if(childSnapshot.child("phone").getValue() != null) {
//                            phone = childSnapshot.child("phone").getValue().toString();
//                        }
//                        if(childSnapshot.child("name").getValue() != null) {
//                            name = childSnapshot.child("name").getValue().toString();
//                        }
//
//                        UserObject mUser = new UserObject(name, phone, childSnapshot.getKey(), myNotificationKey);
//                        for(UserObject mContactIterator : contactList) {
//                            if(mContactIterator.getPhone().equals(phone)) {
//                                mUser.setName(mContactIterator.getName());
//
//                            }
//                        }
//
//                        userList.add(mUser);
//                        userListViewAdapter.notifyDataSetChanged();
//                        return;
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    private String getCountryIso() {
//        String ISO = null;
//
//        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
//
//        if(telephonyManager.getNetworkCountryIso() != null) {
//            if(telephonyManager.getNetworkCountryIso().toString().equals("")) {
//                ISO = telephonyManager.getNetworkCountryIso().toString();
//            }
//        }
//        if (ISO == null) {
//            return "";
//        }
//        return CountryToPhonePrefix.getPhone(ISO);
//
//    }
//
//    private void initializeRecyclerView() {
////        userListView = findViewById(R.id.userList);
////        userListView.setNestedScrollingEnabled(false);
////        userListView.setHasFixedSize(false);
////        userListViewLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
////        userListView.setLayoutManager(userListViewLayoutManager);
////        userListViewAdapter = new UserListAdapter(userList,);
////        userListView.setAdapter(userListViewAdapter);
//    }
}
