package com.maphomework.mypolyphonyapp.ui.library;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.maphomework.mypolyphonyapp.Adapter.MusicAdapter;
import com.maphomework.mypolyphonyapp.Model.Music;
import com.maphomework.mypolyphonyapp.R;

import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment {
    private RecyclerView recyclerView;
    private MusicAdapter musicAdapter;
    private List<Music> mMusics;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        readMusics();
    }

    public LibraryFragment(){ }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_library);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mMusics = new ArrayList<>();

        musicAdapter = new MusicAdapter(getContext(), mMusics);

        recyclerView.setAdapter(musicAdapter);

        return view;
    }

    public void readMusics(){
        StorageReference reference = FirebaseStorage.getInstance().getReference();

        reference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(@NonNull ListResult listResult) {
                ArrayList<StorageReference> items = (ArrayList<StorageReference>) listResult.getPrefixes();
                for (StorageReference item : items) {
                    Log.d("Item", item.getPath().toString());
                    showMusic(item.getPath().toString());
                }
            }
        });
    }

    public void showMusic(String path){
        StorageReference reference = FirebaseStorage.getInstance().getReference(path);
        Task<ListResult> resultTask = reference.listAll();
        resultTask.addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(@NonNull ListResult listResult) {
                ArrayList<StorageReference> items = (ArrayList<StorageReference>) listResult.getItems();
                Music music = new Music();
                StorageReference musicRef = items.get(1);
                StorageReference coverRef = items.get(0);

                musicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(@NonNull Uri uri) {
                        Log.d("Music URL:", uri.toString());
                        music.setPlayableurl(uri.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Music Linking Error", "Music cannot added", e);
                    }
                });
                coverRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(@NonNull Uri uri) {
                        Log.d("Cover URL:", uri.toString());
                        music.setImageurl(uri.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Album Cover Linking Error","Album cover cannot added", e );
                    }
                });

                String[] names = musicRef.getName().split("-");
                music.setMusicName(names[1]);
                Log.d("Music Name", music.getMusicName());
                music.setSingerName(names[0]);
                Log.d("Singer Name", music.getSingerName());

                music.setId(path.substring(6));
                mMusics.add(music);
                musicAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Show Music Failed", "onFailure: Show Music Failed", e);
            }
        });
    }
}