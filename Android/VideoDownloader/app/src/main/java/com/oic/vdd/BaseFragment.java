package com.oic.vdd;

import android.support.v4.app.Fragment;

import de.greenrobot.event.EventBus;

/**
 * Created by khacpham on 1/6/16.
 */
public abstract class BaseFragment extends Fragment{

    public BaseFragment(){
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
