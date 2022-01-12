package com.maphomework.mypolyphonyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maphomework.mypolyphonyapp.Adapter.MessageAdapter;
import com.maphomework.mypolyphonyapp.Model.Chat;
import com.maphomework.mypolyphonyapp.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    TextView txtUsername;
    CircleImageView imgProfile;
    ImageView btnSendMessage;
    EditText txtMessage;

    String username;
    String imageurl;
    String fullname;
    String receiverUserId;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    MessageAdapter adapter;
    RecyclerView chatView;
    List<Chat> chats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        chatView = findViewById(R.id.chatViewChat);
        chatView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        chatView.setLayoutManager(linearLayoutManager);

        txtUsername = findViewById(R.id.txtUsernameChat);
        imgProfile = findViewById(R.id.imgProfileChat);
        btnSendMessage = findViewById(R.id.btnSendMessageChat);
        txtMessage = findViewById(R.id.txtChatBoxChat);

        SharedPreferences preferences = getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        username = preferences.getString("username", "error");
        imageurl = preferences.getString("profileimg", "error");
        fullname = preferences.getString("fullname", "error");
        receiverUserId = preferences.getString("userId", "error");

        txtUsername.setText(username);
        Glide.with(this).load(imageurl).into(imgProfile);

        readMessage(firebaseUser.getUid(), receiverUserId, imageurl);

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = txtMessage.getText().toString();

                if (!msg.equals("")){
                    sendMessage(firebaseUser.getUid(), receiverUserId, msg);
                    txtMessage.setText("");
                }
            }
        });

    }

    private void sendMessage(String sender, String receiver, String message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        reference.child("Chats").push().setValue(hashMap);
    }

    private void readMessage(String myId, String userId, String imageurl){
        chats = new ArrayList<>();

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Chats");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chats.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myId) && chat.getSender().equals(userId) || chat.getReceiver().equals(userId) && chat.getSender().equals(myId)){
                        chats.add(chat);
                    }
                    adapter = new MessageAdapter(ChatActivity.this, chats, imageurl);
                    chatView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}