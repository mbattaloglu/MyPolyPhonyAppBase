package com.maphomework.mypolyphonyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MusicActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    Handler handler;

    ImageView imgAlbumCover;
    TextView txtMusicName;
    TextView txtSingerName;
    TextView txtCurrentTime;
    TextView txtLength;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        mediaPlayer = MainActivity.mediaPlayer;
        handler = new Handler();

        imgAlbumCover = findViewById(R.id.imgAlbumMusicActivity);
        txtMusicName = findViewById(R.id.txtMusicNameMusicActivity);
        txtSingerName = findViewById(R.id.txtSingerNameMusicActivity);
        txtCurrentTime = findViewById(R.id.txtInitialTimeMusicActivity);
        txtLength = findViewById(R.id.txtTimeMusicActivity);
        seekBar = findViewById(R.id.seekBarMusicActivity);

        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);

        String musicName = preferences.getString("playingMusicName","none");
        String singerName = preferences.getString("playingMusicSingerName", "none");
        String albumRef = preferences.getString("playingMusicAlbumCover", "none");
        int currentPosition = preferences.getInt("currentMusicPosition", -1);
        Uri uri = Uri.parse(albumRef);

        MusicCoverDownloadTask task = new MusicCoverDownloadTask(imgAlbumCover);
        String urls[] = {albumRef};
        task.execute(urls);

        txtSingerName.setText(singerName);
        txtMusicName.setText(musicName.substring(0, musicName.length()-4));

        seekBar.setMax(mediaPlayer.getDuration());

        seekBar.setProgress(currentPosition);

        if (mediaPlayer.isPlaying()){
            int length = mediaPlayer.getDuration();
            long minutes = (length / 1000) / 60;
            long seconds = (length / 1000) % 60;
            String lengthStr = minutes + ":" +seconds;
            txtLength.setText(lengthStr);
            SeekBarUpdater updater = new SeekBarUpdater();
            handler.post(updater);
        }

        while (mediaPlayer.isPlaying()){
            txtCurrentTime.setText(calculateInitialTime());
        }
    }


    private String calculateInitialTime(){
        long time = mediaPlayer.getCurrentPosition();
        long minutes = (time / 1000) / 60;
        long seconds = (time / 1000) % 60;

        String timeFormat = minutes +":"+seconds;
        return timeFormat;
    }


    public class SeekBarUpdater implements Runnable{

        @Override
        public void run() {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            handler.postDelayed(this, 100);
        }
    }


}