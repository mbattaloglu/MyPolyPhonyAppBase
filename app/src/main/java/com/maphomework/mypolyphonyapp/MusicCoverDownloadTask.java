package com.maphomework.mypolyphonyapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.maphomework.mypolyphonyapp.Model.Music;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class MusicCoverDownloadTask extends AsyncTask<String, Integer, Bitmap>{

    public ImageView view;

    public MusicCoverDownloadTask(ImageView view) {
        this.view = view;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        return downloadAlbumCover(strings[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        view.setImageBitmap(bitmap);
    }

    private Bitmap downloadAlbumCover(String strUrl){
        Bitmap bitmap = null;
        try {
            URL url = new URL(strUrl);
            URLConnection connection = url.openConnection();
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

            bitmap = BitmapFactory.decodeStream(bufferedInputStream);

            bufferedInputStream.close();
            inputStream.close();

        } catch (Exception e) {
            Log.e("downloadAlbumCover", "Cannot Download"+strUrl, e);
        }
        return bitmap;
    }
}
