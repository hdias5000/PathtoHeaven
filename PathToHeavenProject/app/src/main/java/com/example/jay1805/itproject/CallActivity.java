package com.example.jay1805.itproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;

import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;

public class CallActivity extends AppCompatActivity {

    SinchClient sinchClient = Sinch.getSinchClientBuilder()
            .context(this)
            .userId("current-user-id")
            .applicationKey("b1632d4b-e8ff-4ca2-acda-a48e4149479e")
            .applicationSecret("phtfIVDR5kCNxi2n7uxMew==")
            .environmentHost("clientapi.sinch.com")
            .build();

    private Call call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);


        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sinchClient.setSupportCalling(true);
                sinchClient.start();
                // make a call!
                sinchClient.getCallClient().callUser("call-recipient-id");


            }

        });
    }

}
