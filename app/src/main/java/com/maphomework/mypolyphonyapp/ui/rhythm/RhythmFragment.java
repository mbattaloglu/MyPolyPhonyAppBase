package com.maphomework.mypolyphonyapp.ui.rhythm;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maphomework.mypolyphonyapp.Adapter.PostAdapter;
import com.maphomework.mypolyphonyapp.Model.Post;
import com.maphomework.mypolyphonyapp.NotificationsFragment;
import com.maphomework.mypolyphonyapp.PreviousChatsFragment;
import com.maphomework.mypolyphonyapp.R;
import com.maphomework.mypolyphonyapp.ui.searchuser.SearchUserFragment;

import java.util.ArrayList;
import java.util.List;


public class RhythmFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> posts;

    private List<String> followingList;

    ImageView btnGoChat;
    ImageView btnGoNotifications;
    ImageView btnGoSearch;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rhythm, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewRhythm);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        posts = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), posts);
        recyclerView.setAdapter(postAdapter);

        checkFollowing();

        btnGoChat = view.findViewById(R.id.btnGoChatRhythm);
        btnGoNotifications = view.findViewById(R.id.btnGoNotificationsRhythm);
        btnGoSearch = view.findViewById(R.id.btnGoSearchRhythm);

        btnGoChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main, new ProfileFragment()).commit();
                //((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main, new PreviousChatsFragment()).commit();
                FragmentManager fm = getParentFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                PreviousChatsFragment pcf = new PreviousChatsFragment();
                ft.replace(R.id.nav_host_fragment_activity_main, pcf);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        btnGoNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getParentFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                NotificationsFragment nf = new NotificationsFragment();
                ft.replace(R.id.nav_host_fragment_activity_main, nf);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        btnGoSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getParentFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                SearchUserFragment sf = new SearchUserFragment();
                ft.replace(R.id.nav_host_fragment_activity_main, sf);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        return view;
    }

    private void checkFollowing(){
        followingList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    followingList.add(dataSnapshot.getKey());
                }
                readPost();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readPost(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    for (String id : followingList){
                        if(post.getPublisher().equals(id)){
                            posts.add(post);
                        }
                    }
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}