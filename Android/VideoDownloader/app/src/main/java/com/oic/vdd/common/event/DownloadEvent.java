package com.oic.vdd.common.event;

import com.oic.vdd.models.Video;

/**
 * Created by khacpham on 1/7/16.
 */
public class DownloadEvent {
    public String message;
    public int downloadId=-1;
    public String path;
    public Video video;

    public DownloadEvent(){

    }

    public DownloadEvent(int downloadId,String path, Video video) {
        this.downloadId = downloadId;
        this.path = path;
        this.video = video;
    }

    public DownloadEvent setMessage(String message){
        this.message = message;
        return this;
    }

    public boolean valid(){
        return downloadId>=0;
    }
}
