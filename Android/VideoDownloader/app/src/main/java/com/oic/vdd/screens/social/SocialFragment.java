package com.oic.vdd.screens.social;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.login.LoginResult;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.oic.vdd.BaseFragment;
import com.oic.vdd.R;
import com.oic.vdd.common.event.DownloadEvent;
import com.oic.vdd.common.event.RefreshEvent;
import com.oic.vdd.common.views.listview.adapter.VideoControlAdapter;
import com.oic.vdd.manager.FacebookMng;
import com.oic.vdd.manager.ParseMng;
import com.oic.vdd.models.Video;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;

/**
 * Created by khacpham on 1/6/16.
 */
public class SocialFragment extends BaseFragment {

    List<Video> videos = new ArrayList<>();
    ArrayAdapter<Video> adapter;
    DownloadEvent downloadEvent;

    @Bind(R.id.listVideo)
    ListView listVideo;

    @Bind(R.id.textEmpty)
    TextView textEmpty;

    @Bind(R.id.adView)
    AdView mAdView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.social_fragment, container, false);
        ButterKnife.bind(this, view);

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

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init(){
        adapter = new VideoControlAdapter(getContext(),android.R.layout.simple_list_item_1,videos);
        listVideo.setAdapter(adapter);
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
        textEmpty.setVisibility(View.GONE);

        Log.e("TAG", FacebookMng.getInstance(getContext()).getPermission().toString());
        FacebookMng.getInstance(getContext()).getUploadedVideos().subscribe(new Subscriber<List<Video>>() {
            @Override
            public void onCompleted() {
                adapter.notifyDataSetChanged();
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
                textEmpty.setText("Sorry, can not get video list.\nTry again later.");
                textEmpty.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNext(List<Video> videos) {
                int numVideo = 0;
                for(Video video: videos){
                    adapter.add(video);
                    numVideo++;
                    if(numVideo<10) {
                        ParseMng.getInstance(getContext()).postVideo(video, Video.PARSE.COLUMNS.TYPE_FB);
                    }
                }

            }
        });
    }
}
