package com.oic.vdd.manager;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.oic.vdd.common.event.DownloadEvent;
import com.oic.vdd.models.Video;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.ThinDownloadManager;

import java.io.File;

/**
 * Created by khacpham on 1/7/16.
 */
public class DownloadMng {

    private static DownloadMng _instance;

    private ThinDownloadManager downloadManager;

    private Context context;

    private DownloadMng(Context context) {
        this.context = context;
        this.downloadManager = new ThinDownloadManager();
    }

    public static DownloadMng getInstance(Context context) {
        if (_instance == null) {
            _instance = new DownloadMng(context);
        }
        return _instance;
    }

    public void cancelDownload(int id){
        downloadManager.cancel(id);
    }

    public void cancelAll(){
        downloadManager.cancelAll();
    }

    /**
     * @return STATUS_PENDING, STATUS_STARTED, STATUS_RUNNING
     * */
    public int getStatus(int downloadId){
        return downloadManager.query(downloadId);
    }

    public File getLocalFolder(){
        File path = new File(Environment.getExternalStorageDirectory()+"/Video-Downloader/");
        if(!path.exists()|| !path.isDirectory()){
            path.mkdirs();
        }
        return path;
    }

    public File getLocalPath(Video video){
        File path = getLocalFolder();
        String filePath = path.getAbsolutePath()+"/"+ video.id + ".mp4";
        return new File(filePath);
    }

    /**
     * @return download id
     * */
    public DownloadEvent downloadVideo(Video video,DownloadStatusListener listener){
        Log.e("TAG","start download: "+video.source);
        boolean createFolderResult = true;
        File path = getLocalFolder();
        String filePath = getLocalPath(video).getAbsolutePath();
        if(!path.exists() || !path.isDirectory()){
            createFolderResult = path.mkdirs();
        }
        if(!createFolderResult){
            DownloadEvent downloadEvent =  new DownloadEvent(-1,filePath,video);
            downloadEvent.message = "can not save to folder: "+path;
            return downloadEvent;
        }

        File fileDownload = new File(filePath);
        if(fileDownload.exists()){
            Log.e("TAG","file is exist");
            DownloadEvent downloadEvent =  new DownloadEvent(-1,filePath,video);
            downloadEvent.message = "saved to:\n"+fileDownload.getName();
            return downloadEvent;
        }
        Uri downloadUri = Uri.parse(video.source);
        Uri destinationUri = Uri.parse(filePath);
        DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
//                .addCustomHeader("Auth-Token", "YourTokenApiKey")
                .setRetryPolicy(new DefaultRetryPolicy())
                .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                .setDownloadListener(listener);
        int downloadId = downloadManager.add(downloadRequest);
        return new DownloadEvent(downloadId,filePath,video);
    }
}
