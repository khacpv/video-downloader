package com.oic.vdd.models.social;

import com.google.gson.annotations.SerializedName;

/**
 * Created by khacpham on 1/7/16.
 */
public class Cover {

    public String id;

    @SerializedName("cover_id")
    public String corverId;

    @SerializedName("offset_x")
    public int offsetX;

    @SerializedName("offset_y")
    public int offsetY;

    @SerializedName("source")
    public String source;


}
