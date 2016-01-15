package com.oic.vdd.models;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.oic.vdd.models.social.Format;
import com.oic.vdd.models.social.From;

import java.util.List;

/**
 * Created by khacpham on 1/6/16.
 */
public class Video extends BaseModel {


    public String id;

    public String name;

    @SerializedName("updated_time")
    public String updatedTime;

    public String description;

    public String source;

    @SerializedName("format")
    public List<Format> formats;

    public From from;

    @Override
    public String toString() {
        return TextUtils.isEmpty(description)?id:description;
    }

    public static class PARSE {
        public static final String TABLE = "Videos";

        public static class COLUMNS {
            public static final String ID = "id";
            public static final String NAME = "name";
            public static final String DESCRIPTION = "description";
            public static final String OWNER_ID = "ownerId";
            public static final String OWNER_NAME = "ownerName";
            public static final String THUMB = "thumb";
            public static final String URI = "uri";
            public static final String WIDTH = "width";
            public static final String HEIGHT = "height";
            public static final String TYPE = "type";
            public static final String FORMAT = "format";

            public static final String TYPE_NATIVE = "native";
            public static final String TYPE_FB = "facebook";
            public static final String TYPE_YOUTUBE = "youtube";
            public static final String TYPE_TUMBLR = "tumblr";
            public static final String TYPE_INSTAGRAM = "instagram";
        }
    }
}
