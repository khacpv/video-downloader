package com.oic.vdd.screens.setting;

import android.os.Bundle;

import com.oic.vdd.R;
import com.oic.vdd.common.views.fragments.PreferenceFragment;

/**
 * Created by khacpham on 1/8/16.
 */
public class SettingFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
