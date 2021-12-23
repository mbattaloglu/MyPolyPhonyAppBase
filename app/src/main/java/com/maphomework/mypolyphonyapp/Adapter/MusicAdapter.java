package com.maphomework.mypolyphonyapp.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.maphomework.mypolyphonyapp.Model.Music;
import com.maphomework.mypolyphonyapp.R;

import java.io.IOException;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    private Context mContext;
    private List<Music> musics;

    private FirebaseStorage storage;

    public MusicAdapter(Context mContext, List<Music> musics) {
        this.mContext = mContext;
        this.musics = musics;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_item, parent, false);
        return new MusicAdapter.MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        final long ONE_MEGABYTE = 1024 * 1024;
        storage = FirebaseStorage.getInstance();

        final Music music = musics.get(position);

        holder.txtMusicName.setText(music.getMusicName().substring(0,music.getMusicName().length()-4));
        holder.txtSingerName.setText(music.getSingerName());

        StorageReference albumCoverRef = storage.getReference().child("music"+music.getId()+"/ALBUM_COVER.JPG");
        albumCoverRef.getBytes(ONE_MEGABYTE * 8).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(@NonNull byte[] bytes) {
                Bitmap b = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.albumCover.setImageBitmap(b);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Fail Image Loading","Failed Loading Album Cover ",e);
            }
        });



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Click", "I got clicked");
                StorageReference musicDirRef = storage.getReference().child("music"+music.getId());
                StorageReference musicRef = musicDirRef.child(music.getSingerName() + "-" + music.getMusicName());
                Log.d("Music Clicked", music.getSingerName() + "-" + music.getMusicName());
                musicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(@NonNull Uri uri) {
                        String musicUrl = uri.toString();
                        MediaPlayer mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(musicUrl);
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mediaPlayer) {
                                    mediaPlayer.start();
                                }
                            });
                            mediaPlayer.prepare();
                            Log.d("Success", "Playing Music: "+musicUrl);
                        }
                        catch (IOException e){
                            Log.e("Fail", "Not Playing File:"+musicUrl, e);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Fail", "File Error", e);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return musics.size();
    }

    public class MusicViewHolder extends RecyclerView.ViewHolder{

        public final TextView txtMusicName;
        public final TextView txtSingerName;
        public final ImageView albumCover;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);

            txtMusicName = itemView.findViewById(R.id.txtMusicNameMusicItem);
            txtSingerName = itemView.findViewById(R.id.txtSingerNameMusicItem);
            albumCover = itemView.findViewById(R.id.imgAlbumeCoverMusicItem);
        }
    }
}
