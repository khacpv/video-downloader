package com.oic.vdd.common.imageloader;

import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.oic.vdd.R;

/**
 * Created by khacpham on 1/7/16.
 */
public class DefaultImageOption {
    public static DisplayImageOptions defOptions = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.ic_thumb_loading) // resource or drawable
            .showImageForEmptyUri(R.drawable.ic_thumb_empty) // resource or drawable
            .showImageOnFail(R.drawable.ic_thumb_warning) // resource or drawable
            .resetViewBeforeLoading(false)  // default
            .delayBeforeLoading(1000)
            .cacheInMemory(false) // default
            .cacheOnDisk(false) // default
            .considerExifParams(false) // default
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
            .bitmapConfig(Bitmap.Config.ARGB_8888) // default
            .displayer(new SimpleBitmapDisplayer()) // default
            .handler(new Handler()) // default
            .build();

    public static void loadImage(String url, final ImageView imageView, final ProgressBar loading){
        ImageLoader.getInstance().displayImage(url, imageView, DefaultImageOption.defOptions, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                        loading.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        loading.setVisibility(View.GONE);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        loading.setVisibility(View.GONE);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                        loading.setVisibility(View.GONE);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                }
        });
    }
}
