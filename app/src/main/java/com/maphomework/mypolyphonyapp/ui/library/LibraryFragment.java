package com.maphomework.mypolyphonyapp.ui.library;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.maphomework.mypolyphonyapp.Adapter.MusicAdapter;
import com.maphomework.mypolyphonyapp.LoginActivity;
import com.maphomework.mypolyphonyapp.MainActivity;
import com.maphomework.mypolyphonyapp.Model.Music;
import com.maphomework.mypolyphonyapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LibraryFragment extends Fragment {
    private RecyclerView recyclerView;
    private static MusicAdapter musicAdapter;
    private static List<Music> mMusics;

    public static ArrayList<StorageReference> musicDirectories;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
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

        readMusics();

        return view;
    }

    public void readMusics(){

        StorageReference reference = FirebaseStorage.getInstance().getReference("musics");

        reference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(@NonNull ListResult listResult) {
                ArrayList<StorageReference> musicDirectories = (ArrayList<StorageReference>) listResult.getPrefixes();
                for(StorageReference ref : musicDirectories){
                    Log.d("Show", ref.getPath().toString());
                    showMusic(ref.getPath().toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("READ FAILED", "Failed to read musics", e);
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
                Log.d("Show", musicRef.getPath());
                StorageReference coverRef = items.get(0);
                Log.d("Show", coverRef.getPath());

                musicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(@NonNull Uri uri) {
                        Log.d("Show", uri.toString());
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
                        Log.d("Show", uri.toString());
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
                music.setSingerName(names[0]);

                music.setId(path.substring(path.length()-2, path.length()));
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