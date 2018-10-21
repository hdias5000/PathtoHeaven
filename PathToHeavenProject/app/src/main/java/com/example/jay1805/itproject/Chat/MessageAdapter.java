package com.example.jay1805.itproject.Chat;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.jay1805.itproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;

/**
 * This class is the adapter for the messages recycler view which appears in the chats in
 * ChatActivity. This adapter lets you see all teh messages you sent to someone in the Chat
 * activity.
 */
public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_OTHER = 2;
    ViewGroup par;

    String chatID;
    ArrayList<MessageObject> messageList;

    public MessageAdapter(ArrayList<MessageObject> Message, String chatID) {
        this.messageList = Message;
        this.chatID = chatID;
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

        if(messageList.get(position).getMediaUrlList().size() != 0) {
            myChatViewHolder.mViewMedia.setVisibility(View.VISIBLE);
        }
        else {
            myChatViewHolder.mViewMedia.setVisibility(View.GONE);
        }

        myChatViewHolder.mViewMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImageViewer.Builder(v.getContext(), messageList.get(myChatViewHolder.getAdapterPosition()).getMediaUrlList())
                        .setStartPosition(0)
                        .show();
            }
        });
    }

    private void configureOtherChatViewHolder(final OtherChatViewHolder otherChatViewHolder, int position) {

        MessageObject message = messageList.get(position);

        String alphabet = message.getSenderId().substring(0, 1);

        otherChatViewHolder.message.setText(message.getMessage());
        otherChatViewHolder.sender.setText(alphabet);

        if(messageList.get(position).getMediaUrlList().size() != 0) {
            otherChatViewHolder.mViewMedia.setVisibility(View.VISIBLE);
        }
        else {
            otherChatViewHolder.mViewMedia.setVisibility(View.GONE);
        }

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

        public MyChatViewHolder(View view) {
            super(view);
            message = view.findViewById(R.id.message);
            sender = view.findViewById(R.id.sender);
            mViewMedia = view.findViewById(R.id.viewMedia);
        }
    }

    private static class OtherChatViewHolder extends RecyclerView.ViewHolder {

        TextView message, sender;
        Button mViewMedia;

        public OtherChatViewHolder(View view) {
            super(view);
            message = view.findViewById(R.id.message);
            sender = view.findViewById(R.id.sender);
            mViewMedia = view.findViewById(R.id.viewMedia);
        }
    }
}