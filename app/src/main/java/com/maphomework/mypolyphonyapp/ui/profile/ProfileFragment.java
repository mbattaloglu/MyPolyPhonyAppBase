package com.maphomework.mypolyphonyapp.ui.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.maphomework.mypolyphonyapp.Adapter.MyMusicAdapter;
import com.maphomework.mypolyphonyapp.Model.Post;
import com.maphomework.mypolyphonyapp.Model.User;
import com.maphomework.mypolyphonyapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ProfileFragment extends Fragment {

    ImageView imgProfile, imgOptions;

    TextView txtPost, txtFollowers, txtFollowing, txtFullname, txtBio, txtUsername;
    Button btnEditProfile;
    FirebaseUser firebaseUser;

    String profileId;

    ImageButton btnMyPhotos, btnSavedPhotos;

    List<Post> posts;
    MyMusicAdapter myMusicAdapter;
    RecyclerView postView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);

        profileId = firebaseUser.getUid();

        imgProfile = view.findViewById(R.id.imgProfileProfile);
        imgOptions = view.findViewById(R.id.btnOptionsProfile);
        txtPost = view.findViewById(R.id.txtPostProfile);
        txtFollowers = view.findViewById(R.id.txtFollowersProfile);
        txtFollowing = view.findViewById(R.id.txtFollowingProfile);
        txtFullname = view.findViewById(R.id.txtFullnameProfile);
        txtBio = view.findViewById(R.id.txtBioProfile);
        txtUsername = view.findViewById(R.id.txtUsernameProfile);
        btnMyPhotos = view.findViewById(R.id.btnMyPosts);
        btnSavedPhotos = view.findViewById(R.id.btnMySavings);
        btnEditProfile = view.findViewById(R.id.btnEditProfileProfile);

        postView = view.findViewById(R.id.postsProfile);
        postView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 3);
        postView.setLayoutManager(linearLayoutManager);
        posts = new ArrayList<>();


        myMusicAdapter = new MyMusicAdapter(getContext(), posts);
        postView.setAdapter(myMusicAdapter);

        userInfo();
        getFollowers();
        getPostCount();
        getMyPosts();

        if (profileId.equals(firebaseUser.getUid())){
            btnEditProfile.setText("Edit Profile");
        }
        else {
            checkFollow();
            btnSavedPhotos.setVisibility(View.GONE);
        }

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = btnEditProfile.getText().toString();

                if (btn.equals("Edit Profile")){

                }
                else if(btn.equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").child(profileId).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("followers").child(firebaseUser.getUid()).setValue(true);
                    addNotification();

                }
                else if(btn.equals("following")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").child(profileId).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });


        return view;
    }

    private void addNotification(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileId);
        HashMap<String, Object> hashMap= new HashMap<>();

        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "started following you!");

        reference.push().setValue(hashMap);
    }

    private void userInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null){
                    return;
                }

                Log.d("Snapshot", snapshot.toString());
                User user = snapshot.getValue(User.class);

                Glide.with(getContext()).load(user.getImageurl()).into(imgProfile);
                txtUsername.setText(user.getUsername());
                txtFullname.setText(user.getFullname());
                txtBio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFollow(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(profileId).exists()){
                    btnEditProfile.setText("following");
                }
                else {
                    btnEditProfile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txtFollowers.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("following");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txtFollowing.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPostCount(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;

                for (DataSnapshot s : snapshot.getChildren()){
                    Post post = s.getValue(Post.class);
                    if (post.getPublisher().equals(profileId)){
                        i++;
                    }
                }
                txtPost.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMyPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);

                    if (post.getPostImage().equals(profileId))
                        posts.add(post);
                }
                Collections.reverse(posts);
                myMusicAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}