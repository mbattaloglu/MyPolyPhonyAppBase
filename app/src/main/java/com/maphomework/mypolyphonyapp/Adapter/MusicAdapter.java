package com.maphomework.mypolyphonyapp.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.maphomework.mypolyphonyapp.MainActivity;
import com.maphomework.mypolyphonyapp.Model.Music;
import com.maphomework.mypolyphonyapp.MusicActivity;
import com.maphomework.mypolyphonyapp.R;

import java.io.IOException;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    private Context mContext;
    private List<Music> musics;

    private MediaPlayer mediaPlayer;
    private FirebaseStorage storage;

    private FirebaseUser firebaseUser;

    final long ONE_MEGABYTE = 1024 * 1024;
    final String ALBUM_COVER = "ALBUM_COVER.JPG";

    public MusicAdapter(Context mContext, List<Music> musics) {
        this.mContext = mContext;
        this.musics = musics;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_item, parent, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mediaPlayer = MainActivity.mediaPlayer;
        return new MusicAdapter.MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {

        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();

        storage = FirebaseStorage.getInstance();

        final Music music = musics.get(position);

        holder.txtMusicName.setText(music.getMusicName().substring(0,music.getMusicName().length()-4));
        holder.txtSingerName.setText(music.getSingerName());

        StorageReference albumCoverRef = storage.getReference().child("musics").child(music.getId()+"/"+ALBUM_COVER);
        //TODO : CHECK -> Glide.with(mContext).load(music.getImageurl()).into(holder.albumCover);

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

        holder.btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!music.isLiked()){
                    music.setIsLiked(true);
                    holder.btnFav.setImageResource(R.drawable.ic_star_filled);
                    addFavourites(firebaseUser.getUid(), music);
                }
                else if(music.isLiked()){
                    music.setIsLiked(false);
                    holder.btnFav.setImageResource(R.drawable.ic_star_border);
                    deleteFavourites(firebaseUser.getUid(), music);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StorageReference musicDirRef = storage.getReference().child("musics").child(music.getId());
                StorageReference musicRef = musicDirRef.child(music.getSingerName() + "-" + music.getMusicName());
                Log.d("Music Clicked", music.getSingerName() + "-" + music.getMusicName());
                musicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(@NonNull Uri uri) {
                        String musicUrl = uri.toString();
                        try {
                            if (mediaPlayer.isPlaying()){
                                Log.d("isPlaying", mediaPlayer.isPlaying()+"");
                                mediaPlayer.reset();
                            }
                            mediaPlayer.setDataSource(musicUrl);
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mediaPlayer) {
                                    mediaPlayer.start();
                                }
                            });
                            mediaPlayer.prepare();
                            Log.d("Success", "Playing Music: "+musicUrl);
                            //Music bar attribute changes
                            MainActivity.mainActivityBinding.txtMusicNameMusicBar.setText(music.getMusicName().substring(0,music.getMusicName().length()-4));
                            MainActivity.mainActivityBinding.txtSingerNameMusicBar.setText(music.getSingerName());
                            StorageReference albumCoverRef = musicDirRef.child(ALBUM_COVER);
                                    albumCoverRef.getBytes(ONE_MEGABYTE * 8).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(@NonNull byte[] bytes) {
                                    Bitmap b = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                                    MainActivity.mainActivityBinding.albumCoverMusicBar.setImageBitmap(b);
                                }
                            });

                            //Music bar button adjustments
                            MainActivity.mainActivityBinding.imgPlayBtnMusicBar.setImageResource(R.drawable.ic_pause);

                            MainActivity.mainActivityBinding.imgPlayBtnMusicBar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (mediaPlayer.isPlaying()){
                                        mediaPlayer.pause();
                                        MainActivity.mainActivityBinding.imgPlayBtnMusicBar.setImageResource(R.drawable.ic_play_arrow);
                                    }
                                    else {
                                        int length = mediaPlayer.getCurrentPosition();
                                        mediaPlayer.seekTo(length);
                                        mediaPlayer.start();
                                        MainActivity.mainActivityBinding.imgPlayBtnMusicBar.setImageResource(R.drawable.ic_pause);
                                    }
                                }
                            });

                            MainActivity.mainActivityBinding.imgFavBtnMusicBar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (music.isLiked()){
                                        MainActivity.mainActivityBinding.imgFavBtnMusicBar.setImageResource(R.drawable.ic_star_filled);
                                    }
                                    else if(!music.isLiked()){
                                        MainActivity.mainActivityBinding.imgFavBtnMusicBar.setImageResource(R.drawable.ic_star_border);
                                    }
                                }
                            });
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

                editor.putString("playingMusicName",music.getMusicName());
                editor.putString("playingMusicSingerName",music.getSingerName());
                editor.putString("playingMusicAlbumCover", music.getImageurl());

                editor.apply();

                recordLastPlayed(firebaseUser.getUid(), music);
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
        public final ImageView btnFav;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);

            txtMusicName = itemView.findViewById(R.id.txtMusicNameMusicItem);
            txtSingerName = itemView.findViewById(R.id.txtSingerNameMusicItem);
            albumCover = itemView.findViewById(R.id.imgAlbumeCoverMusicItem);
            btnFav = itemView.findViewById(R.id.imgFavBtnMusicItem);

        }
    }

    public void addFavourites(String userId, Music music){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Favourites").child(userId);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", music.getId());
        hashMap.put("musicName", music.getMusicName());
        hashMap.put("singerName", music.getSingerName());
        hashMap.put("playableurl", music.getPlayableurl());
        hashMap.put("imageurl", music.getImageurl());
        hashMap.put("isLiked", true);
        hashMap.put("length", null);


        reference.push().setValue(hashMap);
    }

    public void deleteFavourites(String userId, Music music){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Favourites");
        DatabaseReference leaf = reference.child(userId);
        leaf.removeValue();
    }

    public void recordLastPlayed(String userId, Music music){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("LastPlayed").child(userId);
        reference.removeValue();

        DatabaseReference nowPlaying = FirebaseDatabase.getInstance().getReference().child("LastPlayed").child(userId);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", music.getId());
        hashMap.put("musicName", music.getMusicName());
        hashMap.put("singerName", music.getSingerName());
        hashMap.put("playableurl", music.getPlayableurl());
        hashMap.put("imageurl", music.getImageurl());
        hashMap.put("isLiked", music.isLiked());
        hashMap.put("length", null);

        nowPlaying.push().setValue(hashMap);

    }
}
