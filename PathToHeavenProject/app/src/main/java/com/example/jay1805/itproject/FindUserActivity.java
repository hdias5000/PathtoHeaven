package com.example.jay1805.itproject;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.widget.LinearLayout;

import com.example.jay1805.itproject.User.UserListAdapter;
import com.example.jay1805.itproject.User.UserObject;
import com.example.jay1805.itproject.Utilities.CountryToPhonePrefix;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FindUserActivity extends AppCompatActivity {


    private RecyclerView userListView;
    private RecyclerView.Adapter userListViewAdapter;
    private RecyclerView.LayoutManager userListViewLayoutManager;
    public ArrayList<UserObject> contactList;

    ArrayList<UserObject> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);
        contactList = new ArrayList<>();
        userList = new ArrayList<>();
        initializeRecyclerView();
        getContactList();
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
            UserObject mContacts = new UserObject(name, phone, "");
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
                    String phone = "", name = "";
                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        if(childSnapshot.child("phone").getValue() != null) {
                            phone = childSnapshot.child("phone").getValue().toString();
                        }
                        if(childSnapshot.child("name").getValue() != null) {
                            name = childSnapshot.child("name").getValue().toString();
                        }

                        UserObject mUser = new UserObject(name, phone, childSnapshot.getKey());
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

    private void initializeRecyclerView() {
        userListView = findViewById(R.id.userList);
        userListView.setNestedScrollingEnabled(false);
        userListView.setHasFixedSize(false);
        userListViewLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
        userListView.setLayoutManager(userListViewLayoutManager);
        userListViewAdapter = new UserListAdapter(userList);
        userListView.setAdapter(userListViewAdapter);
    }
}
