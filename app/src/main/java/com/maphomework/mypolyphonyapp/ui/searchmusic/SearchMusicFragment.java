package com.maphomework.mypolyphonyapp.ui.searchmusic;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maphomework.mypolyphonyapp.R;

public class SearchMusicFragment extends Fragment {

    public SearchMusicFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_music, container, false);
        // Inflate the layout for this fragment
        return view;
    }
}