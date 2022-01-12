package com.maphomework.mypolyphonyapp.Model;

import java.io.Serializable;

public class Music implements Serializable{

    private String id;
    private String musicName;
    private String singerName;
    private String imageurl;
    private String playableurl;
    private String length;
    private boolean isLiked;

    public Music(){
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getPlayableurl() {
        return playableurl;
    }

    public void setPlayableurl(String playableurl) {
        this.playableurl = playableurl;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setIsLiked(boolean liked) {
        isLiked = liked;
    }

    @Override
    public String toString() {
        return "Music{" +
                "id='" + id + '\'' +
                ", musicName='" + musicName + '\'' +
                ", singerName='" + singerName + '\'' +
                ", imageurl='" + imageurl + '\'' +
                ", playableurl='" + playableurl + '\'' +
                ", length='" + length + '\'' +
                ", isLiked=" + isLiked +
                '}';
    }
}
