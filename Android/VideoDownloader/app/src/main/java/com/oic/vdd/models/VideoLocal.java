package com.oic.vdd.models;

import android.text.TextUtils;

/**
 * Created by khacpham on 1/7/16.
 */
public class VideoLocal extends BaseModel {

    public int id;

    public String title = "";

    public String size;

    public String thumb;

    public String path;

    public int width;

    public int height;

    @Override
    public String toString() {
        return (TextUtils.isEmpty(title))?path:title;
    }
}
