package com.example.jay1805.itproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.jay1805.itproject.Call.BaseActivity;
import com.example.jay1805.itproject.Call.CallScreenActivity;
import com.example.jay1805.itproject.Call.SinchService;
import com.example.jay1805.itproject.Chat.MediaAdapter;
import com.example.jay1805.itproject.Chat.MessageAdapter;
import com.example.jay1805.itproject.Chat.MessageObject;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sinch.android.rtc.calling.Call;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends BaseActivity {

    private RecyclerView ChatView, MediaView;
    private RecyclerView.Adapter ChatViewAdapter, MediaViewAdapter;
    private RecyclerView.LayoutManager ChatViewLayoutManager, MediaViewLayoutManager;
    private Button callButton;
    ArrayList<MessageObject> messageList;
    String chatID;
    DatabaseReference chatDB;
    DatabaseReference nameOfSenderDB;
    String nameOfSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        callButton = findViewById(R.id.callButton);
        chatID = getIntent().getExtras().getString("chatID");
        chatDB = FirebaseDatabase.getInstance().getReference().child("chat").child(chatID);
        nameOfSenderDB = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("name");

        Button mSend = findViewById(R.id.send);
        Button mAddMedia = findViewById(R.id.addMedia);
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        mAddMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        initializeMessage();
        initializeMedia();
        getChatMessages();
        callButton.setOnClickListener(buttonClickListener);
    }

    private void getChatMessages() {

        nameOfSenderDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nameOfSender = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        chatDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()) {
                    String text = "";
                    String creatorId = "";
                    String creator = "";
                    ArrayList<String> mediaUrlList = new ArrayList<>();
                    if (dataSnapshot.child("text").getValue() != null) {
                        text = dataSnapshot.child("text").getValue().toString();
                    }
                    if (dataSnapshot.child("creator").getValue() != null) {
                        creatorId = dataSnapshot.child("creator").getValue().toString();
                    }
                    if (dataSnapshot.child("creatorID").getValue() != null) {
                        creator = dataSnapshot.child("creatorID").getValue().toString();
                    }
                    if (dataSnapshot.child("media").getChildrenCount()>0) {
                        for (DataSnapshot mediaSnapshot : dataSnapshot.child("media").getChildren()) {
                            mediaUrlList.add(mediaSnapshot.getValue().toString());
                        }
                    }
                    MessageObject myMessage = new MessageObject(dataSnapshot.getKey(), creatorId, creator, text, mediaUrlList);
                    messageList.add(myMessage);
                    ChatViewLayoutManager.scrollToPosition(messageList.size()-1);
                    ChatViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    int totalMediaUploaded = 0;
    ArrayList<String> mediaIdList = new ArrayList<>();
    EditText mMessage;

    private void sendMessage() {
        mMessage = findViewById(R.id.messageText);
            String messageId = chatDB.push().getKey();
            final DatabaseReference newMessageDB = chatDB.child(messageId);

            final Map newMessageMap = new HashMap<>();

            newMessageMap.put("creatorID", FirebaseAuth.getInstance().getUid());

            newMessageMap.put("creator", nameOfSender);

            if(!mMessage.getText().toString().isEmpty())
                newMessageMap.put("text", mMessage.getText().toString());

            //newMessageDB.updateChildren(newMessageMap);

            if(!mediaUriList.isEmpty()) {
                for(String mediaUri : mediaUriList) {
                    String mediaId = newMessageDB.child("media").push().getKey();
                    mediaIdList.add(mediaId);
                    final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("chat").child(chatID).child(messageId).child(mediaId);
                    UploadTask uploadTask = filePath.putFile(Uri.parse(mediaUri));

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newMessageMap.put("/media/" + mediaIdList.get(totalMediaUploaded) + "/", uri.toString());
                                    totalMediaUploaded += 1;

                                    if(totalMediaUploaded == mediaUriList.size()) {
                                        updateDatabaseWithNewMessage(newMessageDB, newMessageMap);
                                    }
                                }
                            });
                        }
                    });
                }
            }
            else {
                if(!mMessage.getText().toString().isEmpty())
                    updateDatabaseWithNewMessage(newMessageDB, newMessageMap);
            }
    }
    private void callButtonClicked() {
        Call call = getSinchServiceInterface().callUser(chatID);
        String callId = call.getCallId();

        Intent callScreen = new Intent(this, CallScreenActivity.class);
        callScreen.putExtra(SinchService.CALL_ID, callId);
        startActivity(callScreen);

    }

    private void updateDatabaseWithNewMessage(DatabaseReference newMessageDB, Map newMessageMap) {
        newMessageDB.updateChildren(newMessageMap);
        mMessage.setText(null);
        mediaUriList.clear();
        mediaIdList.clear();
        MediaViewAdapter.notifyDataSetChanged();
    }

    int PICK_IMAGE_INTENT = 1;
    ArrayList<String> mediaUriList = new ArrayList<>();

    private void initializeMedia() {
        mediaUriList = new ArrayList<>();
        MediaView = findViewById(R.id.mediaList);
        MediaView.setNestedScrollingEnabled(false);
        MediaView.setHasFixedSize(false);
        MediaViewLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.HORIZONTAL, false);
        MediaView.setLayoutManager(MediaViewLayoutManager);
        MediaViewAdapter = new MediaAdapter(getApplicationContext(), mediaUriList);
        MediaView.setAdapter(MediaViewAdapter);
    }

    private void initializeMessage() {
        messageList = new ArrayList<>();
        ChatView = findViewById(R.id.recyclerView);
        ChatView.setNestedScrollingEnabled(false);
        ChatView.setHasFixedSize(false);
        ChatViewLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
        ChatView.setLayoutManager(ChatViewLayoutManager);
        ChatViewAdapter = new MessageAdapter(messageList);
        ChatView.setAdapter(ChatViewAdapter);
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image(s)"), PICK_IMAGE_INTENT);
    }
    private void stopButtonClicked() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }
        finish();
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.callButton:
                    callButtonClicked();
                    break;

                case R.id.stopButton:
                    stopButtonClicked();
                    break;

            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == PICK_IMAGE_INTENT) {
                if(data.getClipData() == null) {
                    mediaUriList.add(data.getData().toString());
                }
                else {
                    for(int i=0; i<data.getClipData().getItemCount(); i++) {
                        mediaUriList.add(data.getClipData().getItemAt(i).getUri().toString());
                    }
                }
                MediaViewAdapter.notifyDataSetChanged();
            }
        }
    }
}
