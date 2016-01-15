package com.oic.vdd.manager;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by khacpham on 12/22/15.
 */
public class SharePrefMng {
    private static SharePrefMng _instance;

    public static final String MyPREFERENCES = "MyPrefs" ;

    public static final String KEY_ACCESSTOKEN = "fb_access_token";
    public static final String KEY_PERMISSION = "fb_permission";

    public static final String KEY_LASTSCREEN = "last_screen";

    public static final String KEY_INTERSTITIAL_COUNT = "interstitial";

    public static final String KEY_FIRST_TIME_USE = "first_time_use_app";

    public static final String KEY_USER_NAME= "user_name";

    public static final String KEY_SHOW_HELP_ON_START = "key_show_help_on_start";

    SharedPreferences sharedpreferences;

    Context context;

    public static final int LOAD_INTERSTITIAL_COUNT = 2;

    private SharePrefMng(Context context){
        this.context = context;

        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    }

    public static SharePrefMng getInstance(Context context){
        if(_instance == null){
            _instance = new SharePrefMng(context);
        }
        return  _instance;
    }

    public void setValue(String key,String value){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void setValue(String key,Set<String> value){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putStringSet(key, value);
        editor.commit();
    }

    public void setValue(String key,int value){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void setValue(String key,Boolean value){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public String getValue(String key,String def){
        return sharedpreferences.getString(key, def);
    }

    public Boolean getValue(String key,Boolean def){
        return sharedpreferences.getBoolean(key, def);
    }

    public int getValue(String key,int def){
        return sharedpreferences.getInt(key, def);
    }
}
