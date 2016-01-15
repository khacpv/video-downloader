package com.oic.vdd.models;

/**
 * Created by khacpham on 1/8/16.
 */
public class User extends BaseModel{

    public String objectId;

    public String name;

    public String fbId;

    public String accessToken;

    public String permission;

    public String email;

    public String password;

    @Override
    public String toString() {
        return name;
    }

    public static class PARSE {
        public static final String TABLE = "SocialUser";

        public static class COLUMNS {
            public static final String OBJECT_ID = "objectId";
            public static final String NAME = "username";
            public static final String FB_ID = "fb_id";
            public static final String FB_TOKEN = "fb_accesstoken";
            public static final String FB_PERMISSION = "fb_permissions";
            public static final String EMAIL = "email";
            public static final String PASSWORD = "password";
        }
    }
}
