package com.maphomework.mypolyphonyapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maphomework.mypolyphonyapp.Adapter.ChatUserAdapter;
import com.maphomework.mypolyphonyapp.Model.User;
import com.maphomework.mypolyphonyapp.ui.rhythm.RhythmFragment;

import java.util.ArrayList;
import java.util.List;


public class PreviousChatsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatUserAdapter chatUserAdapter;
    private List<User> mUsers;

    FirebaseUser currentUser;

    public PreviousChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_previous_chats, container, false);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = view.findViewById(R.id.chatView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers = new ArrayList<>();

        chatUserAdapter = new ChatUserAdapter(getContext(), mUsers);

        recyclerView.setAdapter(chatUserAdapter);

        readUsers();

        /*
        this.getParentFragment().getView().setFocusableInTouchMode(true);
        this.getParentFragment().getView().requestFocus();
        PreviousChatsFragment fragment = this;
        FragmentManager fm = getParentFragmentManager();
        this.getParentFragment().getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK){
                    RhythmFragment f = new RhythmFragment();
                    fm.beginTransaction().remove(fragment).commit();
                    return true;
                }
                return false;
            }
        });*/
        return view;
    }

    private void readUsers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    if (!user.getId().equals(currentUser.getUid())){
                        mUsers.add(user);
                    }
                }
                chatUserAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}