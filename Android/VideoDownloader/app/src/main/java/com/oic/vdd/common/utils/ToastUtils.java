package com.oic.vdd.common.utils;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

/**
 * Created by khacpham on 1/7/16.
 */
public class ToastUtils {

    static Toast toast;
    static Snackbar snackbar;

    public static void showToast(Context context,String text){
        if(toast == null){
            toast = Toast.makeText(context,text,Toast.LENGTH_SHORT);
        }
        toast.cancel();
        toast.setText(text);
        toast.show();
    }

    public static void setSnackbar(Snackbar snackbar){
        snackbar = snackbar;
    }

    public static void showSnack(String text){
        if(snackbar == null){
            return;
        }
        snackbar.setText(text);
        snackbar.setAction("Close", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }
}
