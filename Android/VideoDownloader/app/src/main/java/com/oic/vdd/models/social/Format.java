package com.oic.vdd.models.social;

import com.google.gson.annotations.SerializedName;

/**
 * Created by khacpham on 1/7/16.
 */
public class Format {

    @SerializedName("embed_html")
    public String embedHtml;

    public String filter;

    public int height;

    public int width;

    public String picture;
}
