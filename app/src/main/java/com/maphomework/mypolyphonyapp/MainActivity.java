package com.maphomework.mypolyphonyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.style.TtsSpan;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.maphomework.mypolyphonyapp.Model.Music;
import com.maphomework.mypolyphonyapp.databinding.ActivityMainBinding;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static ActivityMainBinding mainActivityBinding;
    public static MediaPlayer mediaPlayer = new MediaPlayer();

    public static SeekBar seekBar;

    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mediaPlayer = new MediaPlayer();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences.Editor editor = this.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentUser", currentUser.getUid());
        editor.apply();

        seekBar = findViewById(R.id.seekBarMusicActivity);

        mainActivityBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainActivityBinding.getRoot());
        readLastPlayedMusic(currentUser.getUid());
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_music_search, R.id.navigation_rhythm,
                R.id.navigation_library, R.id.navigation_profile).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(mainActivityBinding.navView, navController);

        mainActivityBinding.musicControlBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                Log.d("snapshot", currentPosition+"");
                editor.putInt("currentMusicPosition", currentPosition);
                editor.apply();
                Intent intent = new Intent(MainActivity.this, MusicActivity.class);
                startActivity(intent);
            }
        });
    }

    public void readLastPlayedMusic(String userId){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("LastPlayed").child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Log.d("Snapshot", dataSnapshot.toString());
                    Music music = dataSnapshot.getValue(Music.class);

                    String name = music.getMusicName().substring(0, music.getMusicName().length()-4);
                    mainActivityBinding.txtMusicNameMusicBar.setText(name);

                    mainActivityBinding.txtSingerNameMusicBar.setText(music.getSingerName());


                    MusicCoverDownloadTask task = new MusicCoverDownloadTask(mainActivityBinding.albumCoverMusicBar);
                    task.execute(new String[]{music.getImageurl()});
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}