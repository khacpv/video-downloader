package com.oic.vdd.models;

import com.google.gson.annotations.SerializedName;
import com.oic.vdd.models.social.Cover;
import com.oic.vdd.models.social.Picture;

/**
 * Created by khacpham on 1/7/16.
 */
public class Page {
    public String id;

    public String name;

    public Cover cover;

    public String category;

    public Picture picture;

    @SerializedName("created_time")
    public String createdTime;

    @Override
    public String toString() {
        return name+" ("+category+")";
    }
}
