package com.oic.vdd.screens.socialgroup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.oic.vdd.common.views.listview.adapter.FilterAdapter;
import com.oic.vdd.manager.FacebookMng;
import com.oic.vdd.models.Page;
import com.oic.vdd.screens.listvideo.ListVideoActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;

/**
 * Created by khacpham on 1/6/16.
 */
public class SocialGroupFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    List<Page> pages = new ArrayList<>();
    FilterAdapter adapter;
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
        View view = inflater.inflate(R.layout.group_social_fragment, container, false);
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
        adapter = new FilterAdapter(getContext(),pages);
        listVideo.setAdapter(adapter);

        listVideo.setOnItemClickListener(this);
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
        FacebookMng.getInstance(getContext()).getLikePage().subscribe(new Subscriber<List<Page>>() {
            @Override
            public void onCompleted() {
                adapter.notifyDataSetChanged();
                if(adapter.getCount()==0){
                    textEmpty.setText("Pages empty");
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
                textEmpty.setText("You're not login FB or\nhas no network");
                textEmpty.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNext(List<Page> pages) {
                adapter.clear();
                adapter.addAll(pages);

            }
        });
    }

    public void query(String search){
        adapter.getFilter().filter(search);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Page page = adapter.getItem(position);
        String pageId = page.id;

        Intent listGroupVideoIntent = new Intent(this.getContext(), ListVideoActivity.class);
        listGroupVideoIntent.putExtra(ListVideoActivity.EXTRA_PAGE_ID,pageId);
        startActivity(listGroupVideoIntent);
    }
}
