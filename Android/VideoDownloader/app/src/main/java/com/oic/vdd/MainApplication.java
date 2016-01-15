package com.oic.vdd;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.oic.vdd.common.imageloader.SmartUriDecoder;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;

/**
 * Created by khacpham on 12/22/15.
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        MultiDex.install(this);
        super.onCreate();

        initImageLoader();
        initRate();
        initParse();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void initParse(){
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
        Parse.initialize(this);

        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        // defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }

    private void initImageLoader() {
        int size = 1080;
        int width = size;
        ImageLoaderConfiguration config =
                new ImageLoaderConfiguration.Builder(getApplicationContext()).threadPriority(
                        Thread.NORM_PRIORITY-1)
                        .threadPoolSize(3)
                        .denyCacheImageMultipleSizesInMemory()
                        .memoryCacheSizePercentage(50)
                        .imageDecoder(new SmartUriDecoder(getContentResolver(), new BaseImageDecoder(false)))
                        .diskCacheSize(10 * 50 * 1024 * 1024)
                        .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                        .diskCacheExtraOptions(width, size, null)
                        .tasksProcessingOrder(QueueProcessingType.LIFO)
                        .build();
        ImageLoader.getInstance().init(config);
    }

    private void initRate(){
        AppRate.with(this)
                .setInstallDays(0) // default 10, 0 means install day.
                .setLaunchTimes(3) // default 10
                .setRemindInterval(2) // default 1
                .setShowLaterButton(true) // default true
                .setDebug(false) // default false
                .setOnClickButtonListener(new OnClickButtonListener() { // callback listener.
                    @Override
                    public void onClickButton(int which) {
                        Log.d(MainActivity.class.getName(), Integer.toString(which));
                    }
                })
                .monitor();
    }
}
