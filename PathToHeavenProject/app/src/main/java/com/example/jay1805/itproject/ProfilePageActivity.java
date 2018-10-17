package com.example.jay1805.itproject;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProfilePageActivity extends AppCompatActivity {

    private EditText HomeAddress;
    private TextView nameOfUser;
    private Button saveDetails;
    private Button calenderBtn;
    private EditText dateEditText;
    private RadioGroup rg;
    private RadioButton rb;
    private Calendar myCalendar;
    private String name;
    private ImageButton UploadImage;
    private ImageView ImageViewer;

    private DatabaseReference userDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        UploadImage = (ImageButton) findViewById(R.id.image_button);
        HomeAddress = (EditText) findViewById(R.id.home_address_editText);
        nameOfUser = (TextView) findViewById(R.id.theActualName);
        saveDetails = (Button) findViewById(R.id.save_button);
        calenderBtn = (Button) findViewById(R.id.calender_button);
        dateEditText  = (EditText) findViewById(R.id.date_editText);
        rg = (RadioGroup) findViewById(R.id.user_type_rg);
        myCalendar =  Calendar.getInstance();
        ImageViewer = findViewById(R.id.imageViewer);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };


        UploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Image"), 1);
            }
        });

        calenderBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(ProfilePageActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        userDB = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userDB.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null) {
                    name = dataSnapshot.getValue().toString();
                    nameOfUser.setText(name);
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
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                finish();
            }
        });
    }

    String selectedImageUri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == 1) {
                if(data != null) {
                    selectedImageUri = data.getData().toString();
                    InputStream inputStream;
                    try {
                        inputStream = getContentResolver().openInputStream(data.getData());
                        Bitmap image = BitmapFactory.decodeStream(inputStream);
                        ImageViewer.setImageBitmap(image);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    System.out.println(selectedImageUri);
                }
                else {
                    System.out.println("NO IMAGE CHOSEN");
                }
            }
        }
    }

    private void InputingToDatabase() {
        final Map userMap= new HashMap<>();
        if(!HomeAddress.getText().toString().isEmpty()) {
            userMap.put("Home Address", HomeAddress.getText().toString());
        }
        if(!dateEditText.getText().toString().isEmpty()) {
            userMap.put("Date of Birth", dateEditText.getText().toString());
        }
        if(!rb.getText().toString().isEmpty()) {
            userMap.put("User Type", rb.getText().toString());
            System.out.println( "User type:"+rb.getText().toString());

            if (rb.getText().toString().equals("Helper")) {
                System.out.println( "HELLLLLLLLLOOO$$$$$$$$$$$$$$$$$$$$$");
                userMap.put("Requested", "False");
                userMap.put("ElderlyIDRequested","");
            }
        }
        if(!selectedImageUri.isEmpty()) {
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Profile Pictures");
            UploadTask uploadTask = filePath.putFile(Uri.parse(selectedImageUri));

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            userMap.put("Profile Picture", uri.toString());
                            userDB.updateChildren(userMap);
                        }
                    });
                }
            });
        }

        userDB.updateChildren(userMap);
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);

        dateEditText.setText(sdf.format(myCalendar.getTime()));
    }

    public void CheckedRadioButton(View v) {
        int radioID = rg.getCheckedRadioButtonId();
        rb = (RadioButton) findViewById(radioID);
    }
}
