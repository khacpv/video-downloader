package com.oic.vdd.manager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oic.vdd.models.Page;
import com.oic.vdd.models.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by khacpham on 12/23/15.
 */
public class FacebookMng {
    public static String[] PERMISSION = {
            "user_videos",
            "user_likes",
//            "user_actions.video",
//            ,"user_actions.news",
//            "user_location",
//            "user_about_me",
//            "user_actions.books",
//            "user_friends",
//            "user_posts",
//            "user_events",
//            "user_photos",
//            "user_relationship_details",
//            "user_website",
//            "user_religion_politics",
//            "user_work_history",
//            "user_hometown",
//            "user_tagged_places",
//            "user_status",
//            "user_managed_groups",
//            "email",
//            "public_profile",
//            "user_birthday",
//            "user_actions.music",
//            "user_relationships",
//            "user_actions.video"
//
//            // advanced
//            //,"ads_management"
//            //,"publish_actions"
//            ,"read_insights"
//            ,"email"
//            ,"pages_manage_leads"
//            //,"read_custom_friendlists"
//            ,"read_page_mailboxes"
//            //,"rsvp_event"
//            //,"publish_pages"
//            ,"ads_read"
            };

    private static FacebookMng _instance;

    private Context context;

    private FacebookMng(Context context) {
        this.context = context;

    }

    public static FacebookMng getInstance(Context context) {
        if (_instance == null) {
            _instance = new FacebookMng(context);
        }
        return _instance;
    }

    public void login(Activity context){
        LoginManager.getInstance().logInWithReadPermissions(context, Arrays.asList(PERMISSION));
    }

    public String getUserId() {
        return AccessToken.getCurrentAccessToken().getUserId();
    }

    public Observable<String> getUserName() {
        final Observable<String> rxObj = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                Bundle bundle = new Bundle();
                bundle.putString("limit","999");

                /* make the API call */
                GraphRequest request = new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/me",
                        bundle,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                /* handle the result */
                                String json=response.getRawResponse();
                                Log.e("TAG",json+"");
                                try {
                                    String name = response.getJSONObject().get("name")+"";
                                    subscriber.onNext(name);
                                    subscriber.onCompleted();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    subscriber.onError(e);
                                } catch (Exception e){
                                    e.printStackTrace();
                                    subscriber.onError(e);
                                }
                            }
                        }
                        ,"v2.5");
                request.executeAsync();
            }
        });
        return rxObj
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public String getAccessToken(){
        return AccessToken.getCurrentAccessToken().getToken();
    }

    public Set<String> getPermission(){
        try {
            return AccessToken.getCurrentAccessToken().getPermissions();
        }catch (NullPointerException e){
            return new HashSet<>();
        }
    }

    public boolean isLoggedin(){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken == null || TextUtils.isEmpty(accessToken.getToken()) || accessToken.isExpired()){
            return false;
        }
        return true;
    }

    public Bitmap getPhotoFacebook() {
        Bitmap bitmap=null;
        final String nomimg = "https://graph.facebook.com/"+getUserId()+"/picture?type=large";
        URL imageURL = null;

        try {
            imageURL = new URL(nomimg);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            HttpURLConnection connection = (HttpURLConnection) imageURL.openConnection();
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects( true );
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            //img_value.openConnection().setInstanceFollowRedirects(true).getInputStream()
            bitmap = BitmapFactory.decodeStream(inputStream);

        } catch (IOException e) {

            e.printStackTrace();
        }
        return bitmap;

    }

    public Observable<List<Video>> getVideoUser(){
        final Observable<List<Video>> rxObj = Observable.create(new Observable.OnSubscribe<List<Video>>() {
            @Override
            public void call(final Subscriber<? super List<Video>> subscriber) {
                Bundle bundle = new Bundle();
                bundle.putString("fields","id,description,source,format,from");
                bundle.putString("limit","999");

                /* make the API call */
                GraphRequest request = new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "v2.2/me/videos",
                        bundle,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                /* handle the result */
                                String json=response.getRawResponse();
                                Log.e("TAG",json);
                                try {
                                    JSONArray data = response.getJSONObject().getJSONArray("data");
                                    String dataStr = data.toString();
                                    List<Video> videos = new Gson().fromJson(dataStr,new TypeToken<List<Video>>(){}.getType());
                                    subscriber.onNext(videos);
                                    subscriber.onCompleted();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    subscriber.onError(e);
                                } catch (Exception e){
                                    e.printStackTrace();
                                    subscriber.onError(e);
                                }
                            }
                        }
                ,"v2.2");
                request.setVersion("v2.2");
                request.executeAsync();
            }
        });
        return rxObj
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Video>> getVideoPage(final String pageId){
        final Observable<List<Video>> rxObj = Observable.create(new Observable.OnSubscribe<List<Video>>() {
            @Override
            public void call(final Subscriber<? super List<Video>> subscriber) {
                Bundle bundle = new Bundle();
                bundle.putString("fields","id,description,source,format,from");
                bundle.putString("limit","999");

                /* make the API call */
                GraphRequest request = new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        String.format("v2.2/%s/videos", pageId),
                        bundle,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                /* handle the result */
                                String json=response.getRawResponse();
                                Log.e("TAG",json);
                                try {
                                    JSONArray data = response.getJSONObject().getJSONArray("data");
                                    String dataStr = data.toString();
                                    List<Video> videos = new Gson().fromJson(dataStr,new TypeToken<List<Video>>(){}.getType());
                                    subscriber.onNext(videos);
                                    subscriber.onCompleted();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    subscriber.onError(e);
                                } catch (Exception e){
                                    e.printStackTrace();
                                    subscriber.onError(e);
                                }
                            }
                        }
                        ,"v2.2");
                request.setVersion("v2.2");
                request.executeAsync();
            }
        });
        return rxObj
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Video>> getUploadedVideos(){
        Observable<List<Video>> rxObj = Observable.create(new Observable.OnSubscribe<List<Video>>() {
            @Override
            public void call(final Subscriber<? super List<Video>> subscriber) {
                Bundle bundle = new Bundle();
                bundle.putString("fields","id,description,source,format,from");
                bundle.putString("limit","999");
                /* make the API call */
                GraphRequest request = new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "v2.2/me/videos/uploaded",
                        bundle,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                /* handle the result */
                                String json = response.getRawResponse();
                                Log.e("TAG","getUploadedVideos:"+json);
                                try {
                                    JSONArray data = response.getJSONObject().getJSONArray("data");
                                    JSONObject paging = response.getJSONObject().getJSONObject("paging");
                                    String dataStr = data.toString();
                                    List<Video> videos = new Gson().fromJson(dataStr,new TypeToken<List<Video>>(){}.getType());
                                    subscriber.onNext(videos);
                                    subscriber.onCompleted();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    subscriber.onError(e);
                                } catch (Exception e){
                                    e.printStackTrace();
                                    subscriber.onError(e);
                                }
                            }
                        }
                        ,"v2.2");
                request.setVersion("v2.2");
                request.executeAsync();
            }
        });
        return rxObj.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Page>> getLikePage(){
        Observable<List<Page>> rxObj = Observable.create(new Observable.OnSubscribe<List<Page>>() {
            @Override
            public void call(final Subscriber<? super List<Page>> subscriber) {
                Bundle bundle = new Bundle();
                bundle.putString("fields","id,name,cover,category,created_time,picture.type(large)");
                bundle.putString("limit","999");
                /* make the API call */
                GraphRequest request = new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "v2.2/me/likes",
                        bundle,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                /* handle the result */
                                String json = response.getRawResponse();
                                Log.e("TAG","getLikePage:"+json);
                                try {
                                    JSONArray data = response.getJSONObject().getJSONArray("data");
                                    JSONObject paging = response.getJSONObject().getJSONObject("paging");
                                    String dataStr = data.toString();
                                    List<Page> pages = new Gson().fromJson(dataStr,new TypeToken<List<Page>>(){}.getType());
                                    subscriber.onNext(pages);
                                    subscriber.onCompleted();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    subscriber.onError(e);
                                } catch (Exception e){
                                    e.printStackTrace();
                                    subscriber.onError(e);
                                }
                            }
                        }
                        ,"v2.2");
                request.setVersion("v2.2");
                request.executeAsync();
            }
        });
        return rxObj.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
