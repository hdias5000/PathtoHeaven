/**TEAM PATH TO HEAVEN
 * Authors:
 *  - Hasitha Dias:   789929
 *  - Jay Parikh:     864675
 *  - Anupama Sodhi:  791288
 *  - Kushagra Gupta: 804729
 *  - Manindra Arora: 827703
 * **/

package com.example.jay1805.itproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
public class TimerActivity extends AppCompatActivity{

    private static final long START_TIME_IN_MILLIS = 46000;

    private TextView mTextViewCountDown;

    private CountDownTimer mCountDownTimer;

    private Button mButtonCancel;

    private boolean flag;
    private boolean mTimerRunning;

    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        flag = false;
        mTextViewCountDown = findViewById(R.id.text_view_countdown);
        mButtonCancel = findViewById(R.id.cancel_button);

        startTimer();
        updateCountDownText();


        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
                FirebaseDatabase.getInstance().getReference().child("user").
                        child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("accepted").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue().equals("true")){
                            cancel();
                            Toast.makeText(TimerActivity.this, "The volunteer has connected with you!!!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent("Start Listener for Cancel Connection");
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                            finish();
                        }
                        else if (dataSnapshot.getValue().equals("false")){
                            Toast.makeText(TimerActivity.this, "Volunteer cancelled request. Please try again. ", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent("Make Null");
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                            cancel();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                }
            @Override
            public void onFinish() {
                mTimerRunning = false;
                finish();
            }
        }.start();

        mTimerRunning = true;
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        mTextViewCountDown.setText(timeLeftFormatted);
    }
}