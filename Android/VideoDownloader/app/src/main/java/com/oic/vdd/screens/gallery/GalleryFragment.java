package com.oic.vdd.screens.gallery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.decode.ImageDecoder;
import com.oic.vdd.BaseFragment;
import com.oic.vdd.R;
import com.oic.vdd.common.event.RefreshEvent;
import com.oic.vdd.common.imageloader.DefaultImageOption;
import com.oic.vdd.common.imageloader.SmartUriDecoder;
import com.oic.vdd.manager.FacebookMng;
import com.oic.vdd.manager.LocalStorageMng;
import com.oic.vdd.models.VideoLocal;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by khacpham on 1/7/16.
 */
public class GalleryFragment extends BaseFragment {

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
        View view = inflater.inflate(R.layout.gallery_fragment,container,false);
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
        adapter = new ArrayAdapter<VideoLocal>(getContext(),android.R.layout.simple_list_item_1,videos){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.gallery_item,parent,false);
                }
                ImageView thumb = (ImageView)convertView.findViewById(R.id.thumb);
                TextView title = (TextView)convertView.findViewById(R.id.title);
                ProgressBar loading = (ProgressBar)convertView.findViewById(R.id.loading);

                VideoLocal item = getItem(position);

                title.setText(item.toString());
                thumb.setImageResource(R.drawable.ic_menu_gallery);
                DefaultImageOption.loadImage(item.path, thumb, loading);

                return convertView;
            }
        };
        listVideo.setAdapter(adapter);
    }

    public void refresh(){
        adapter.clear();
        adapter.notifyDataSetChanged();

        Log.e("TAG", FacebookMng.getInstance(getContext()).getPermission().toString());
        List<VideoLocal> result = LocalStorageMng.getInstance(getContext()).getVideos();
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
