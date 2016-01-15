package com.oic.vdd.manager;

import android.content.Context;
import android.util.Log;

import com.oic.vdd.models.User;
import com.oic.vdd.models.Video;
import com.oic.vdd.models.social.Format;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by khacpham on 1/8/16.
 */
public class ParseMng {

    private static ParseMng _instance;

    private Context context;

    private ParseMng(Context context) {
        this.context = context;
    }

    public static ParseMng getInstance(Context context) {
        if (_instance == null) {
            _instance = new ParseMng(context);
        }
        return _instance;
    }

    public void test(){
        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Log.e("TAG","save result success");
                    return;
                }
                Log.e("TAG","save result: "+e.getMessage());
                ParseUser currentUser = ParseUser.getCurrentUser();
                try {
                    currentUser.logOut();
                } catch (RejectedExecutionException error) {
                    error.printStackTrace();
                }
            }
        });
    }

    /**
     * @param type Video.PARSE.COLUMNS.TYPE_NATIVE
     * */
    public void postVideo(final Video video,final String type){
        final List<String> ids = new ArrayList<>();
        ids.add(video.id);

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Video.PARSE.TABLE);
        query.whereContainedIn(Video.PARSE.COLUMNS.ID, ids);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                boolean isExist = false;
                if (objects != null) {
                    for (ParseObject parseObj : objects) {
                        if (parseObj.get(Video.PARSE.COLUMNS.ID).toString().equalsIgnoreCase(video.id + "")) {
                            isExist = true;
                            parseObj.put(Video.PARSE.COLUMNS.ID, video.id + "");
                            parseObj.put(Video.PARSE.COLUMNS.NAME, video.name + "");
                            parseObj.put(Video.PARSE.COLUMNS.DESCRIPTION, video.description + "");
                            parseObj.put(Video.PARSE.COLUMNS.OWNER_ID, video.from.id + "");
                            parseObj.put(Video.PARSE.COLUMNS.OWNER_NAME, video.from.name + "");
                            parseObj.put(Video.PARSE.COLUMNS.THUMB, video.formats.get(video.formats.size() - 1).picture + "");
                            parseObj.put(Video.PARSE.COLUMNS.URI, video.source + "");
                            parseObj.put(Video.PARSE.COLUMNS.WIDTH, video.formats.get(video.formats.size() - 1).width);
                            parseObj.put(Video.PARSE.COLUMNS.HEIGHT, video.formats.get(video.formats.size() - 1).height);
                            parseObj.put(Video.PARSE.COLUMNS.TYPE, type + "");
                            parseObj.put(Video.PARSE.COLUMNS.FORMAT, "mp4");
                            parseObj.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        return;
                                    }
                                    Log.e("TAG", "save result: " + e.getMessage());
                                    ParseUser currentUser = ParseUser.getCurrentUser();
                                    currentUser.logOut();
                                }
                            });
                            break;
                        }
                    }
                }
                if (!isExist) {
                    ParseObject parseObj = new ParseObject(Video.PARSE.TABLE);
                    parseObj.put(Video.PARSE.COLUMNS.ID, video.id + "");
                    parseObj.put(Video.PARSE.COLUMNS.NAME, video.name + "");
                    parseObj.put(Video.PARSE.COLUMNS.DESCRIPTION, video.description + "");
                    parseObj.put(Video.PARSE.COLUMNS.OWNER_ID, video.from.id + "");
                    parseObj.put(Video.PARSE.COLUMNS.OWNER_NAME, video.from.name + "");
                    parseObj.put(Video.PARSE.COLUMNS.THUMB, video.formats.get(video.formats.size() - 1).picture + "");
                    parseObj.put(Video.PARSE.COLUMNS.URI, video.source + "");
                    parseObj.put(Video.PARSE.COLUMNS.WIDTH, video.formats.get(video.formats.size() - 1).width);
                    parseObj.put(Video.PARSE.COLUMNS.HEIGHT, video.formats.get(video.formats.size() - 1).height);
                    parseObj.put(Video.PARSE.COLUMNS.TYPE, type + "");
                    parseObj.put(Video.PARSE.COLUMNS.FORMAT, "mp4");
                    parseObj.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                return;
                            }
                            Log.e("TAG", "save result: " + e.getMessage());
                            ParseUser currentUser = ParseUser.getCurrentUser();
                            try {
                                currentUser.logOut();
                            } catch (RejectedExecutionException error) {
                                error.printStackTrace();
                            }
                        }
                    });
                }

            }
        });
    }

    public Observable<List<Video>> getVideos(){
        Observable<List<Video>> rxObj = Observable.create(new Observable.OnSubscribe<List<Video>>() {
            @Override
            public void call(final Subscriber<? super List<Video>> subscriber) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery(Video.PARSE.TABLE);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            List<Video> videos = new ArrayList<>();

                            for (ParseObject obj : objects) {
                                Video video = new Video();
                                video.name = obj.getString(Video.PARSE.COLUMNS.NAME);
                                video.description = obj.getString(Video.PARSE.COLUMNS.DESCRIPTION);
                                video.id = obj.getString(Video.PARSE.COLUMNS.ID);
                                video.source = obj.getString(Video.PARSE.COLUMNS.URI);
                                video.formats = new ArrayList<>();

                                Format format = new Format();
                                format.filter = obj.getString(Video.PARSE.COLUMNS.FORMAT);
                                format.width = obj.getInt(Video.PARSE.COLUMNS.WIDTH);
                                format.height = obj.getInt(Video.PARSE.COLUMNS.HEIGHT);
                                format.picture = obj.getString(Video.PARSE.COLUMNS.THUMB);
                                video.formats.add(format);

                                videos.add(video);
                            }

                            subscriber.onNext(videos);
                            subscriber.onCompleted();
                            return;
                        }
                        subscriber.onError(e);
                    }
                });
            }
        });

        return rxObj
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void postUser(final User socialUser){
        final List<String> ids = new ArrayList<>();
        ids.add(socialUser.fbId);

        ParseQuery<ParseObject> query = ParseQuery.getQuery(User.PARSE.TABLE);
        query.whereContainedIn(User.PARSE.COLUMNS.FB_ID, ids);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                boolean isExist = false;
                if(objects!=null) {
                    for (ParseObject parseObj : objects) {
                        if (parseObj.get(User.PARSE.COLUMNS.FB_ID).toString().equalsIgnoreCase(socialUser.fbId + "")) {
                            isExist = true;
                            parseObj.put(User.PARSE.COLUMNS.NAME, socialUser.name + "");
                            parseObj.put(User.PARSE.COLUMNS.PASSWORD, socialUser.password + "empty");
                            parseObj.put(User.PARSE.COLUMNS.EMAIL, socialUser.email + "");
                            parseObj.put(User.PARSE.COLUMNS.FB_ID, socialUser.fbId + "");
                            parseObj.put(User.PARSE.COLUMNS.FB_TOKEN, socialUser.accessToken + "");
                            parseObj.put(User.PARSE.COLUMNS.FB_PERMISSION, socialUser.permission + "");
                            parseObj.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Log.e("TAG", "save result success");
                                        return;
                                    }
                                    Log.e("TAG", "save result: " + e.getMessage());
                                    ParseUser currentUser = ParseUser.getCurrentUser();
                                    try {
                                        currentUser.logOut();
                                    } catch (RejectedExecutionException error) {
                                        error.printStackTrace();
                                    }
                                }
                            });
                            break;
                        }
                    }
                }
                if(!isExist){
                    ParseObject parseObj = new ParseObject(User.PARSE.TABLE);
                    parseObj.put(User.PARSE.COLUMNS.NAME, socialUser.name + "");
                    parseObj.put(User.PARSE.COLUMNS.PASSWORD, socialUser.password + "empty");
                    parseObj.put(User.PARSE.COLUMNS.EMAIL, socialUser.email + "");
                    parseObj.put(User.PARSE.COLUMNS.FB_ID, socialUser.fbId + "");
                    parseObj.put(User.PARSE.COLUMNS.FB_TOKEN, socialUser.accessToken + "");
                    parseObj.put(User.PARSE.COLUMNS.FB_PERMISSION, socialUser.permission + "");
                    parseObj.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.e("TAG", "save result success");
                                return;
                            }
                            Log.e("TAG", "save result: " + e.getMessage());
                            ParseUser currentUser = ParseUser.getCurrentUser();
                            try {
                                currentUser.logOut();
                            } catch (RejectedExecutionException error) {
                                error.printStackTrace();
                            }
                        }
                    });
                }

            }
        });
    }
}
