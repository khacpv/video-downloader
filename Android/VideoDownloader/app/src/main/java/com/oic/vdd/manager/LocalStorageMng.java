package com.oic.vdd.manager;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.oic.vdd.models.VideoLocal;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by khacpham on 1/7/16.
 */
public class LocalStorageMng {

    private static LocalStorageMng _instance;

    private Context context;

    private LocalStorageMng(Context context) {
        this.context = context;

    }

    public static LocalStorageMng getInstance(Context context) {
        if (_instance == null) {
            _instance = new LocalStorageMng(context);
        }
        return _instance;
    }

    public List<VideoLocal> getDownloadedVideo(Context context){
        File folder = DownloadMng.getInstance(context).getLocalFolder();
        File[] files = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (filename.toLowerCase().endsWith(".mp4")) {
                    return true;
                }
                return false;
            }
        });
        List<VideoLocal> result = new ArrayList<>();
        for(File file: files){
            VideoLocal video=new VideoLocal();
            video.title = file.getName();
            video.path = file.getAbsolutePath();
            video.thumb = "";
            video.id = -1;
            result.add(video);
        }
        return result;
    }

    public List<VideoLocal> getVideos(){
        List<VideoLocal> result = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Video.VideoColumns.DATA };
        Cursor c = context.getContentResolver().query(uri, projection, null, null, null);

        if (c != null) {
            int vidsCount = c.getCount();
            Log.e("TAG", "num video:" + vidsCount);

            while (c.moveToNext()) {
                VideoLocal video = new VideoLocal();
                int colIndex = -1;

                colIndex = c.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME);
                if(colIndex>-1)video.title = c.getString(colIndex);

                colIndex = c.getColumnIndex(MediaStore.Video.Media.SIZE);
                if(colIndex>-1)video.size = c.getString(colIndex);

                colIndex = c.getColumnIndex(MediaStore.Video.Media._ID);
                if(colIndex>-1)video.id = c.getInt(colIndex);

                colIndex = c.getColumnIndex(MediaStore.Video.Media.WIDTH);
                if(colIndex>-1)video.width = c.getInt(colIndex);

                colIndex = c.getColumnIndex(MediaStore.Video.Media.HEIGHT);
                if(colIndex>-1)video.height = c.getInt(colIndex);

                colIndex = c.getColumnIndex(MediaStore.Video.Media.DATA);
                if(colIndex>-1)video.path = c.getString(colIndex);

                Cursor thumCursor = ((Activity)context).managedQuery(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Video.Thumbnails.DATA, MediaStore.Video.Thumbnails.VIDEO_ID},
                        MediaStore.Video.Thumbnails.VIDEO_ID+"="+video.id,null,null);
                if(thumCursor.moveToFirst()){
                    colIndex = thumCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA);
                    if(colIndex>-1)video.thumb = thumCursor.getString(colIndex);
                }
                result.add(video);
            }
            c.close();
        }
        return result;
    }
}
