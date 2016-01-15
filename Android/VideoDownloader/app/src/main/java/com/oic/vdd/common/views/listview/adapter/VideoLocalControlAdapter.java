package com.oic.vdd.common.views.listview.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.oic.vdd.R;
import com.oic.vdd.common.event.DownloadEvent;
import com.oic.vdd.common.imageloader.DefaultImageOption;
import com.oic.vdd.models.VideoLocal;
import com.sprylab.android.widget.TextureVideoView;

import java.io.File;
import java.util.List;

/**
 * Created by khacpham on 1/7/16.
 */
public class VideoLocalControlAdapter extends ArrayAdapter<VideoLocal> {

    DownloadEvent downloadEvent;

    public VideoLocalControlAdapter(Context context, int resource,List<VideoLocal> videos) {
        super(context, resource,videos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.downloaded_item,parent,false);
        }
        final ImageView thumbImageView = (ImageView)convertView.findViewById(R.id.thumb);
        TextView title = (TextView)convertView.findViewById(R.id.title);
        ProgressBar loading = (ProgressBar)convertView.findViewById(R.id.loading);

        VideoLocal item = getItem(position);
        convertView.setTag(item);

        title.setText(item.toString());
        thumbImageView.setImageResource(R.drawable.ic_menu_gallery);
        if(!TextUtils.isEmpty(item.path)) {
            DefaultImageOption.loadImage(item.path, thumbImageView, loading);
        }

        // buttons click
        ImageButton btnInfo = (ImageButton)convertView.findViewById(R.id.btnInfo);
        ImageButton btnPlay = (ImageButton)convertView.findViewById(R.id.btnPlay);
        ImageButton btnDownload = (ImageButton)convertView.findViewById(R.id.btnDownload);

        btnDownload.setVisibility(View.GONE);

        final TextureVideoView videoView = (TextureVideoView)convertView.findViewById(R.id.videoView);
        final FrameLayout videoViewWrapper = (FrameLayout)convertView.findViewById(R.id.videoViewWrapper);
        final ProgressBar progressDownload = (ProgressBar)convertView.findViewById(R.id.progressDownload);
        progressDownload.setProgressDrawable(getContext().getResources().getDrawable(R.drawable.xml_progressbar));

        videoViewWrapper.setVisibility(View.GONE);
        File localFile = new File(item.path);
        if(localFile.exists()){
            videoView.setVideoURI(Uri.parse(localFile.getAbsolutePath()));
        }else {
            videoView.setVideoURI(Uri.parse(item.path));
        }
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoViewWrapper.setVisibility(View.GONE);
                thumbImageView.setVisibility(View.VISIBLE);
            }
        });

        btnInfo.setTag(item);
        btnPlay.setTag(item);
        btnDownload.setTag(item);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoLocal video = (VideoLocal) v.getTag();
                switch (v.getId()){
                    case R.id.btnInfo:
                        View dialogContent = LayoutInflater.from(getContext()).inflate(R.layout.video_info_dialog,null,false);
                        ImageView thumb = (ImageView)dialogContent.findViewById(R.id.thumb);
                        TextView title = (TextView)dialogContent.findViewById(R.id.title);
                        TextView pathLocal = (TextView)dialogContent.findViewById(R.id.pathLocal);
                        TextView path = (TextView)dialogContent.findViewById(R.id.path);
                        ProgressBar loading = (ProgressBar)dialogContent.findViewById(R.id.loading);

                        title.setText(video.toString());
                        File localFile = new File(video.path);
                        if(localFile.exists()) {
                            pathLocal.setText(localFile.getAbsolutePath());
                        }
                        path.setText(video.path);
                        DefaultImageOption.loadImage(video.path, thumb, loading);

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setView(dialogContent);
                        builder.setTitle("Video Info");
                        builder.setIcon(R.drawable.ic_menu_gallery);

                        AlertDialog dialog = builder.create();
                        dialog.show();
                        break;
                    case R.id.btnPlay:
                        if(videoView.isPlaying()){
                            videoView.pause();
                            thumbImageView.setVisibility(View.VISIBLE);
                            videoViewWrapper.setVisibility(View.GONE);
                            return;
                        }
                        thumbImageView.setVisibility(View.INVISIBLE);
                        videoViewWrapper.setVisibility(View.VISIBLE);
                        videoView.seekTo(0);
                        videoView.start();
                        break;
                }
            }
        };

        btnInfo.setOnClickListener(listener);
        btnPlay.setOnClickListener(listener);
        btnDownload.setOnClickListener(listener);

        return convertView;
    }
}
