package com.oic.vdd.screens.develop;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.oic.vdd.BaseFragment;
import com.oic.vdd.R;
import com.oic.vdd.common.event.RefreshEvent;
import com.oic.vdd.manager.ParseMng;
import com.oic.vdd.models.User;
import com.oic.vdd.models.Video;
import com.oic.vdd.models.social.Format;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by khacpham on 1/6/16.
 */
public class DevelopFragment extends BaseFragment {

    @Bind(R.id.btnDevTestParse)
    Button btnDevTestParse;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.develop_fragment, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init(){

    }

    @OnClick({R.id.btnDevTestParse,R.id.btnDevPostVideo,R.id.btnDevPostUser})
    public void btnClicked(View view){
        switch (view.getId()){
            case R.id.btnDevTestParse:
                ParseMng.getInstance(getContext()).test();
                break;
            case R.id.btnDevPostVideo:
                Video video = new Video();
                video.formats = new ArrayList<>();
                Format format = new Format();
                video.formats.add(format);
                ParseMng.getInstance(getContext()).postVideo(video,Video.PARSE.COLUMNS.TYPE_NATIVE);
                break;
            case R.id.btnDevPostUser:
                User user = new User();
                user.objectId = "-1";
                user.fbId = "-1";
                user.name = "test";
                user.password = "";
                user.email = "";
                user.accessToken = "token";
                user.permission = "public";
                ParseMng.getInstance(getContext()).postUser(user);
                break;
        }
    }

    public void onEvent(RefreshEvent event){
        refresh();
    }

    public void refresh(){

    }
}
