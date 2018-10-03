package com.example.jay1805.itproject.Chat;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jay1805.itproject.MapsActivity;
import com.example.jay1805.itproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_OTHER = 2;
    ViewGroup par;

    ArrayList<MessageObject> messageList;

    public MessageAdapter(ArrayList<MessageObject> Message) {
        this.messageList = Message;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        par = parent;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPE_ME:
                View viewChatMine = layoutInflater.inflate(R.layout.item_message_mine, null, false);
                viewHolder = new MyChatViewHolder(viewChatMine);
                break;
            case VIEW_TYPE_OTHER:
                View viewChatOther = layoutInflater.inflate(R.layout.item_message_other, null, false);
                viewHolder = new OtherChatViewHolder(viewChatOther);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (TextUtils.equals(messageList.get(position).getSenderUid(), FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            configureMyChatViewHolder((MyChatViewHolder) holder, position);
        } else {
            configureOtherChatViewHolder((OtherChatViewHolder) holder, position);
        }
    }

    private void configureMyChatViewHolder(final MyChatViewHolder myChatViewHolder, int position) {
        MessageObject message = messageList.get(position);

        String alphabet = message.getSenderId().substring(0, 1);

        myChatViewHolder.message.setText(message.getMessage());
        myChatViewHolder.sender.setText(alphabet);

        if(messageList.get(myChatViewHolder.getAdapterPosition()).getMediaUrlList().isEmpty()) {
            myChatViewHolder.mViewMedia.setVisibility(View.GONE);
        }

        if(messageList.get(myChatViewHolder.getAdapterPosition()).getGPSShared().equals(false)) {
            myChatViewHolder.helpMessageMine.setVisibility(View.GONE);
        }

        if(messageList.get(myChatViewHolder.getAdapterPosition()).getGPSShared().equals(true)) {
            myChatViewHolder.message.setText("Click to Disable");
        }

        myChatViewHolder.helpMessageMine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessagetoStopTracking();
            }
        });

        myChatViewHolder.mViewMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImageViewer.Builder(v.getContext(), messageList.get(myChatViewHolder.getAdapterPosition()).getMediaUrlList())
                        .setStartPosition(0)
                        .show();
            }
        });
    }

    private void sendMessagetoStopTracking(){
        Intent intent = new Intent("STOP NUDES");
        LocalBroadcastManager.getInstance(par.getContext()).sendBroadcast(intent);
    }

    private void configureOtherChatViewHolder(final OtherChatViewHolder otherChatViewHolder, int position) {
        MessageObject message = messageList.get(position);

        String alphabet = message.getSenderId().substring(0, 1);

        otherChatViewHolder.message.setText(message.getMessage());
        otherChatViewHolder.sender.setText(alphabet);

        if(messageList.get(otherChatViewHolder.getAdapterPosition()).getMediaUrlList().isEmpty()) {
            otherChatViewHolder.mViewMedia.setVisibility(View.GONE);
        }

        if(messageList.get(otherChatViewHolder.getAdapterPosition()).getGPSShared().equals(false)) {
            otherChatViewHolder.helpMessageOther.setVisibility(View.GONE);
        }

        if(messageList.get(otherChatViewHolder.getAdapterPosition()).getGPSShared().equals(true)) {
            otherChatViewHolder.message.setText("Click to view GPS location");
        }

        otherChatViewHolder.helpMessageOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(par.getContext(), MapsActivity.class);
                intent.putExtra("Share ID", shareID);
                par.getContext().startActivity(intent);
            }
        });

        otherChatViewHolder.mViewMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImageViewer.Builder(v.getContext(), messageList.get(otherChatViewHolder.getAdapterPosition()).getMediaUrlList())
                        .setStartPosition(0)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (messageList == null) {
            return 0;
        }
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.equals(messageList.get(position).getSenderUid(), FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            return VIEW_TYPE_ME;
        } else {
            return VIEW_TYPE_OTHER;
        }
    }

    private static class MyChatViewHolder extends RecyclerView.ViewHolder {

        TextView message, sender;
        Button mViewMedia;
        public RelativeLayout theRelativeLayout;
        Button helpMessageMine;

        public MyChatViewHolder(View view) {
            super(view);
            theRelativeLayout = view.findViewById(R.id.the_relative_layout);
            message = view.findViewById(R.id.message);
            sender = view.findViewById(R.id.sender);
            mViewMedia = view.findViewById(R.id.viewMedia);
            helpMessageMine = view.findViewById(R.id.helpMessage_mine);
        }
    }

    private static class OtherChatViewHolder extends RecyclerView.ViewHolder {

        TextView message, sender;
        Button mViewMedia;
        public RelativeLayout theRelativeLayout;
        Button helpMessageOther;

        public OtherChatViewHolder(View view) {
            super(view);
            theRelativeLayout = view.findViewById(R.id.the_relative_layout);
            message = view.findViewById(R.id.message);
            sender = view.findViewById(R.id.sender);
            mViewMedia = view.findViewById(R.id.viewMedia);
            helpMessageOther = view.findViewById(R.id.B_helpMessage_other);
        }
    }
}