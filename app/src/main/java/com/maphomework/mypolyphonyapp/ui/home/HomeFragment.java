package com.maphomework.mypolyphonyapp.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.maphomework.mypolyphonyapp.PostActivity;
import com.maphomework.mypolyphonyapp.R;
import com.maphomework.mypolyphonyapp.StartActivity;

public class HomeFragment extends Fragment {

    Button btnPost;

    public HomeFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        btnPost = view.findViewById(R.id.btnGoPostAct);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeFragment.this.getContext(), PostActivity.class));
            }
        });

        return view;
    }
}