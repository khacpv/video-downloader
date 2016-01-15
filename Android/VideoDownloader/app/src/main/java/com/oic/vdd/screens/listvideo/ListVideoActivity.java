package com.oic.vdd.screens.listvideo;

import android.app.AlertDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.facebook.login.LoginResult;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.oic.vdd.R;
import com.oic.vdd.common.event.DownloadEvent;
import com.oic.vdd.common.event.RefreshEvent;
import com.oic.vdd.common.imageloader.DefaultImageOption;
import com.oic.vdd.common.utils.ToastUtils;
import com.oic.vdd.manager.DownloadMng;
import com.oic.vdd.manager.FacebookMng;
import com.oic.vdd.manager.ParseMng;
import com.oic.vdd.manager.SharePrefMng;
import com.oic.vdd.models.Video;
import com.thin.downloadmanager.DownloadStatusListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import rx.Subscriber;

/**
 * Created by khacpham on 1/6/16.
 */
public class ListVideoActivity extends AppCompatActivity {

    public static final String EXTRA_PAGE_ID = "page_id";
    public static final String EXTRA_PAGE_NAME = "page_name";

    String pageId;

    List<Video> videos = new ArrayList<>();
    ArrayAdapter<Video> adapter;
    DownloadEvent downloadEvent;

    @Bind(R.id.listVideo)
    ListView listVideo;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.textEmpty)
    TextView textEmpty;

    @Bind(R.id.adView)
    AdView mAdView;

    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listvideo_activity);
        ButterKnife.bind(this);

        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }

        if(!getIntent().hasExtra(EXTRA_PAGE_ID)){
            finish();
        }

        pageId = getIntent().getExtras().getString(EXTRA_PAGE_ID);
        String pageName = getIntent().getExtras().getString(EXTRA_PAGE_NAME);
        setTitle(pageName);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(getString(R.string.test_device_id_mi3))
                .build();

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                System.out.println("ad banner finished loading!");
                mAdView.setVisibility(View.GONE);
                mAdView.setVisibility(View.VISIBLE);
            }
        });

        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.ads_interstitial_list_video));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {

            }
        });


        requestNewInterstitial();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mInterstitialAd.isLoaded()) {
                    if(SharePrefMng.getInstance(ListVideoActivity.this).getValue(SharePrefMng.KEY_INTERSTITIAL_COUNT,0)%SharePrefMng.LOAD_INTERSTITIAL_COUNT == 0){
                        mInterstitialAd.show();
                    }
                }
            }
        },5000);
    }

    private void requestNewInterstitial() {
        int interstitial = SharePrefMng.getInstance(this).getValue(SharePrefMng.KEY_INTERSTITIAL_COUNT,0)+1;
        if(interstitial>1000){
            interstitial = 0;
        }
        SharePrefMng.getInstance(this).setValue(SharePrefMng.KEY_INTERSTITIAL_COUNT,interstitial);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(getString(R.string.test_device_id_mi3))
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void init(){
        adapter = new ArrayAdapter<Video>(this,android.R.layout.simple_list_item_1,videos){
            @Override
            public View getView(int position, View convertView, final ViewGroup parent) {
                if(convertView == null){
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.listvideo_item,parent,false);
                }
                final ImageView thumbImageView = (ImageView)convertView.findViewById(R.id.thumb);
                TextView title = (TextView)convertView.findViewById(R.id.title);
                ProgressBar loading = (ProgressBar)convertView.findViewById(R.id.loading);

                Video item = getItem(position);
                convertView.setTag(item);

                title.setText(item.toString());
                thumbImageView.setImageResource(R.drawable.ic_menu_gallery);
                if(item.formats!=null && !item.formats.isEmpty()) {
                    DefaultImageOption.loadImage(item.formats.get(item.formats.size() - 1).picture, thumbImageView, loading);
                }

                // buttons click
                ImageButton btnInfo = (ImageButton)convertView.findViewById(R.id.btnInfo);
                ImageButton btnPlay = (ImageButton)convertView.findViewById(R.id.btnPlay);
                ImageButton btnDownload = (ImageButton)convertView.findViewById(R.id.btnDownload);
                final VideoView videoView = (VideoView)convertView.findViewById(R.id.videoView);
                final FrameLayout videoViewWrapper = (FrameLayout)convertView.findViewById(R.id.videoViewWrapper);
                final ProgressBar progressDownload = (ProgressBar)convertView.findViewById(R.id.progressDownload);
                progressDownload.setProgressDrawable(getResources().getDrawable(R.drawable.xml_progressbar));

                videoViewWrapper.setVisibility(View.GONE);

                File localFile = DownloadMng.getInstance(getContext()).getLocalPath(item);
                if(localFile.exists()){
                    videoView.setVideoURI(Uri.parse(localFile.getAbsolutePath()));
                }else {
                    videoView.setVideoURI(Uri.parse(item.source));
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
                        Video video = (Video) v.getTag();
                        switch (v.getId()){
                            case R.id.btnInfo:
                                View dialogContent = LayoutInflater.from(getContext()).inflate(R.layout.video_info_dialog,null,false);
                                ImageView thumb = (ImageView)dialogContent.findViewById(R.id.thumb);
                                TextView title = (TextView)dialogContent.findViewById(R.id.title);
                                TextView pathLocal = (TextView)dialogContent.findViewById(R.id.pathLocal);
                                TextView path = (TextView)dialogContent.findViewById(R.id.path);
                                ProgressBar loading = (ProgressBar)dialogContent.findViewById(R.id.loading);

                                title.setText(video.toString());
                                File localFile = DownloadMng.getInstance(getContext()).getLocalPath(video);
                                if(localFile.exists()) {
                                    pathLocal.setText(localFile.getAbsolutePath());
                                }
                                path.setText(video.source);
                                DefaultImageOption.loadImage(video.formats.get(video.formats.size() - 1).picture, thumb, loading);

                                AlertDialog.Builder builder = new AlertDialog.Builder(ListVideoActivity.this);
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
                            case R.id.btnDownload:
                                progressDownload.setProgress(2);
                                progressDownload.animate().alpha(1).start();
                                downloadEvent = DownloadMng.getInstance(getContext()).downloadVideo(video, new DownloadStatusListener() {
                                    @Override
                                    public void onDownloadComplete(int id) {
                                        Log.e("TAG","download complete:"+id);
                                        ToastUtils.showToast(getContext(),"download complete");
                                        progressDownload.setProgress(0);
                                        progressDownload.animate().alpha(0).start();
                                        if(downloadEvent.valid()) {
                                            EventBus.getDefault().post(downloadEvent);
                                        }
                                    }

                                    @Override
                                    public void onDownloadFailed(int id, int errorCode, String errorMessage) {
                                        Log.e("TAG","download error:"+errorMessage);
                                        ToastUtils.showToast(getContext(), "download failed:"+errorMessage);
                                        progressDownload.setProgress(0);
                                        progressDownload.animate().alpha(0).start();
                                    }

                                    @Override
                                    public void onProgress(int id, long totalBytes, long downloadedBytes, int progress) {
                                        progressDownload.setProgress(progress);
                                    }
                                });
                                if(!downloadEvent.valid()){
                                    progressDownload.setProgress(0);
                                    progressDownload.animate().alpha(0).start();
                                    EventBus.getDefault().post(downloadEvent);
                                }
                                break;
                        }
                    }
                };

                btnInfo.setOnClickListener(listener);
                btnPlay.setOnClickListener(listener);
                btnDownload.setOnClickListener(listener);

                return convertView;
            }


        };
        listVideo.setAdapter(adapter);
        refresh();
    }

    public void onEvent(RefreshEvent event){
        refresh();
    }

    public void onEvent(LoginResult loginResult){
        refresh();
    }

    public void refresh(){
        adapter.clear();
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.VISIBLE);
        textEmpty.setVisibility(View.GONE);

        Log.e("TAG", FacebookMng.getInstance(this).getPermission().toString());
        FacebookMng.getInstance(this).getVideoPage(pageId).subscribe(new Subscriber<List<Video>>() {
            @Override
            public void onCompleted() {
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                if(adapter.getCount()==0){
                    textEmpty.setText("This page has no video");
                    textEmpty.setVisibility(View.VISIBLE);
                }else{
                    textEmpty.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Throwable e) {
                if(adapter.getCount()==0){
                    textEmpty.setVisibility(View.VISIBLE);
                }else{
                    textEmpty.setVisibility(View.GONE);
                }

                progressBar.setVisibility(View.GONE);
                textEmpty.setText("Sorry, can not get video. Try again later");
                textEmpty.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNext(List<Video> videos) {
                int numVideo = 0;
                for (Video video : videos) {
                    adapter.add(video);
                    numVideo++;
                    if(numVideo<10) {
                        ParseMng.getInstance(ListVideoActivity.this).postVideo(video, Video.PARSE.COLUMNS.TYPE_FB);
                    }
                }
            }
        });
    }
}
