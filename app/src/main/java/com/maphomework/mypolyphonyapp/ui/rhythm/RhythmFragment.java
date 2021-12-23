package com.maphomework.mypolyphonyapp.ui.rhythm;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maphomework.mypolyphonyapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RhythmFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RhythmFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rhythm, container, false);

        return view;
    }
}