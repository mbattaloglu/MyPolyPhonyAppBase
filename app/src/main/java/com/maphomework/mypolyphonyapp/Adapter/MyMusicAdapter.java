package com.maphomework.mypolyphonyapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.maphomework.mypolyphonyapp.GlideApp;
import com.maphomework.mypolyphonyapp.Model.Post;
import com.maphomework.mypolyphonyapp.R;

import java.util.List;

public class MyMusicAdapter extends RecyclerView.Adapter<MyMusicAdapter.MyMusicViewHolder> {

    private Context mContext;
    private List<Post> mPosts;

    public MyMusicAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public MyMusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.photo_item, parent, false);
        return new MyMusicAdapter.MyMusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyMusicViewHolder holder, int position) {
        Post post = mPosts.get(position);
        Log.d("posturl", post.getPostImage());
        Glide.with(mContext).load(post.getPostImage()).into(holder.postImage);
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class MyMusicViewHolder extends RecyclerView.ViewHolder{

        public ImageView postImage;

        public MyMusicViewHolder(@NonNull View itemView) {
            super(itemView);

            postImage = itemView.findViewById(R.id.imgPostImagePhotoItem);
        }
    }
}
