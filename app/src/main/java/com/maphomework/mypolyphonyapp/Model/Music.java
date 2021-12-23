package com.maphomework.mypolyphonyapp.Model;

public class Music {

    private String id;
    private String musicName;
    private String singerName;
    private String imageurl;
    private String playableurl;
    private String length;

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
}
