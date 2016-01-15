package com.oic.vdd.screens.downloaded;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.decode.ImageDecoder;
import com.oic.vdd.BaseFragment;
import com.oic.vdd.R;
import com.oic.vdd.common.event.RefreshEvent;
import com.oic.vdd.common.imageloader.SmartUriDecoder;
import com.oic.vdd.common.views.listview.adapter.VideoLocalControlAdapter;
import com.oic.vdd.manager.LocalStorageMng;
import com.oic.vdd.models.VideoLocal;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by khacpham on 1/7/16.
 */
public class DownloadedFragment extends BaseFragment {

    List<VideoLocal> videos = new ArrayList<>();
    ArrayAdapter<VideoLocal> adapter;

    @Bind(R.id.listVideo)
    ListView listVideo;

    @Bind(R.id.textEmpty)
    TextView textEmpty;

    ImageDecoder smartUriDecoder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.downloaded_fragment,container,false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        smartUriDecoder = new SmartUriDecoder(getActivity().getContentResolver(), new BaseImageDecoder(false));

        init();
    }

    private void init(){
        adapter = new VideoLocalControlAdapter(getContext(),android.R.layout.simple_list_item_1,videos);
        listVideo.setAdapter(adapter);
    }

    public void refresh(){
        adapter.clear();
        adapter.notifyDataSetChanged();

        List<VideoLocal> result = LocalStorageMng.getInstance(getContext()).getDownloadedVideo(getContext());
        for(VideoLocal video: result){
            adapter.add(video);
        }
        adapter.notifyDataSetChanged();

        if(adapter.getCount()==0){
            textEmpty.setVisibility(View.VISIBLE);
        }else{
            textEmpty.setVisibility(View.GONE);
        }
    }

    public void onEvent(RefreshEvent event){
        refresh();
    }
}
