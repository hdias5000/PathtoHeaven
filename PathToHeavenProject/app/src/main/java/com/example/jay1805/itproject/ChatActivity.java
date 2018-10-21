package com.example.jay1805.itproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.jay1805.itproject.Chat.MediaAdapter;
import com.example.jay1805.itproject.Chat.MessageAdapter;
import com.example.jay1805.itproject.Chat.MessageObject;
import com.example.jay1805.itproject.Utilities.SendNotifications;
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This activity is the base for any chat. When someone clicks on chat from menu, this activity is
 * opened up. The chats are displayed using a recyclerview. All the data from chats is stored in
 * Firebase Real-Time Database under the chat branch and the chat ID's are also stored under the user
 */
public class ChatActivity extends Activity {

    private RecyclerView ChatView, MediaView;
    private RecyclerView.Adapter ChatViewAdapter, MediaViewAdapter;
    private RecyclerView.LayoutManager ChatViewLayoutManager, MediaViewLayoutManager;
    ArrayList<MessageObject> messageList;
    String chatID;
    DatabaseReference chatDB;
    DatabaseReference nameOfSenderDB;
    String nameOfSender;
    String notificationKeyOfReciever;
    DatabaseReference myRef;
    String currentShareID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Passing the ChatID from FireBaseDatabase into the ChatActivity
        chatID = getIntent().getExtras().getString("chatID");
        chatDB = FirebaseDatabase.getInstance().getReference().child("chat").child(chatID);
        nameOfSenderDB = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("name");
        myRef = FirebaseDatabase.getInstance().getReference("user");
        currentShareID = "";

        ImageButton mSend = findViewById(R.id.send);

        mSend.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        initializeMessage();
        initializeMedia();
        getChatMessages();
    }

    //Class for printing an image from a given URL
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

    //Chat messages and other relevant information are retrieved here from the firebase database
    private void getChatMessages() {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childsnapshot : dataSnapshot.getChildren()) {

                    if(!childsnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                        for (DataSnapshot chatsnapshot: childsnapshot.child("chat").getChildren()) {

                            if(chatsnapshot.getKey().equals(chatID)) {

                                notificationKeyOfReciever = childsnapshot.child("notificationKey").getValue().toString();

                            }
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

                    MessageObject myMessage = new MessageObject(dataSnapshot.getKey(), creatorId, creator, text, mediaUrlList, false);
                    if (dataSnapshot.child("isGpsShared").getValue() != null){
                        if(dataSnapshot.child("isGpsShared").getValue().equals("false")) {
                            myMessage = new MessageObject(dataSnapshot.getKey(), creatorId, creator, text, mediaUrlList, false);
                        }
                        else {
                            myMessage = new MessageObject(dataSnapshot.getKey(), creatorId, creator, "Click here", mediaUrlList, true);
                        }
                    }
                    //messageList.add(new MessageObject(messageId, nameOfSender, FirebaseAuth.getInstance().getCurrentUser().getUid(), "Click here", mediaUriList, true));
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

    //This inputs all the data of messages into the Firebase Database
    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void sendMessage() {
        mMessage = findViewById(R.id.messageText);
        String messageId = chatDB.push().getKey();
        final DatabaseReference newMessageDB = chatDB.child(messageId);

        final Map newMessageMap = new HashMap<>();

        newMessageMap.put("creatorID", FirebaseAuth.getInstance().getUid());

        newMessageMap.put("creator", nameOfSender);

        newMessageMap.put("isGpsShared", "false");

        if(!mMessage.getText().toString().isEmpty()) {
            HashMap<String,String> notification = new HashMap();
            notification.put("type","message");
            notification.put("message",mMessage.getText().toString());
            notification.put("heading",nameOfSender);
            notification.put("notificationKey",notificationKeyOfReciever);
            notification.put("chatID",chatID);
            new SendNotifications(notification);
            newMessageMap.put("text", mMessage.getText().toString());
        }

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

    //Updating the database with messages
    private void updateDatabaseWithNewMessage(DatabaseReference newMessageDB, Map newMessageMap) {
        newMessageDB.updateChildren(newMessageMap);
        mMessage.setText(null);
        mediaUriList.clear();
        mediaIdList.clear();
        MediaViewAdapter.notifyDataSetChanged();
    }

    int PICK_IMAGE_INTENT = 1;
    ArrayList<String> mediaUriList = new ArrayList<>();

    //Initializing the media recycler view
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

    //Initializing the message recycler view
    private void initializeMessage() {
        messageList = new ArrayList<>();
        ChatView = findViewById(R.id.recyclerView);
        ChatView.setNestedScrollingEnabled(false);
        ChatView.setHasFixedSize(false);
        ChatViewLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
        ChatView.setLayoutManager(ChatViewLayoutManager);
        ChatViewAdapter = new MessageAdapter(messageList, chatID);
        ChatView.setAdapter(ChatViewAdapter);
    }

    //Open gallery when media is to be sent
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image(s)"), PICK_IMAGE_INTENT);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
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