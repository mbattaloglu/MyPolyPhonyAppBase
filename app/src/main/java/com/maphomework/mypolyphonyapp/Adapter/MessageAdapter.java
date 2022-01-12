package com.maphomework.mypolyphonyapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.maphomework.mypolyphonyapp.Model.Chat;
import com.maphomework.mypolyphonyapp.Model.User;
import com.maphomework.mypolyphonyapp.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> chats;
    private String imageurl;

    FirebaseUser firebaseUser;

    public MessageAdapter(Context mContext, List<Chat> chats, String imageurl) {
        this.mContext = mContext;
        this.chats = chats;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_sender, parent,false);

            return new MessageViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_receiver,parent,false);
            return new MessageViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Chat chat = chats.get(position);
        Log.d("Message", chat.getMessage());
        holder.txtShowMessage.setText(chat.getMessage());
        if (imageurl.equals("default")){
            holder.imgProfileImage.setImageResource(R.mipmap.ic_launcher);
        }
        else {
            Glide.with(mContext).load(imageurl).into(holder.imgProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView txtShowMessage;
        public ImageView imgProfileImage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            txtShowMessage = itemView.findViewById(R.id.txtMessageSenderItem);
            imgProfileImage = itemView.findViewById(R.id.imgProfileSender);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (chats.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        return MSG_TYPE_LEFT;
    }
}
