package com.oic.vdd;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.oic.vdd.common.event.DownloadEvent;
import com.oic.vdd.common.event.RefreshEvent;
import com.oic.vdd.common.event.SnackEvent;
import com.oic.vdd.common.utils.ConnectionUtils;
import com.oic.vdd.common.utils.PackageUtils;
import com.oic.vdd.common.utils.ToastUtils;
import com.oic.vdd.manager.DownloadMng;
import com.oic.vdd.manager.FacebookMng;
import com.oic.vdd.manager.ParseMng;
import com.oic.vdd.manager.SharePrefMng;
import com.oic.vdd.models.User;
import com.oic.vdd.screens.develop.DevelopFragment;
import com.oic.vdd.screens.downloaded.DownloadedFragment;
import com.oic.vdd.screens.gallery.GalleryFragment;
import com.oic.vdd.screens.setting.SettingFragment;
import com.oic.vdd.screens.social.SocialFragment;
import com.oic.vdd.screens.socialgroup.SocialGroupFragment;
import com.parse.ParseAnalytics;

import java.util.Set;

import de.greenrobot.event.EventBus;
import hotchemi.android.rate.AppRate;
import rx.Subscriber;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String SCREEN_NONE = "Video Downloader";
    public static final String SCREEN_SOCIAL = "Uploaded Videos";
    public static final String SCREEN_SOCIAL_GROUP = "Videos on Facebook";
    public static final String SCREEN_GALLERY = "Videos on Device";
    public static final String SCREEN_DOWNLOADED = "Downloaded Videos";
    public static final String SCREEN_SETTING = "Setting";
    public static final String SCREEN_DEVELOP = "Develop";

    public static final int RESULT_SETTINGS = 0;

    public static final String EVENT_REFRESH = "refresh";

    String currentScreen = SCREEN_SOCIAL_GROUP;

    CallbackManager callbackManager;

    AccessTokenTracker accessTokenTracker;
    ProfileTracker profileTracker;

    FrameLayout mainContent;
    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar;

    Menu menu;
    MenuItem searchItem;
    SearchView searchView;

    SocialFragment socialFragment;
    SocialGroupFragment socialGroupFragment;
    GalleryFragment galleryFragment;
    DownloadedFragment downloadedFragment;
    SettingFragment settingFragment;
    DevelopFragment developFragment;

    ImageView avatar;
    TextView username;

    boolean isBegin = true;

    private final SearchView.OnQueryTextListener mOnQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextChange(String newText) {
            if(TextUtils.isEmpty(newText)){
                socialGroupFragment.query("");
            }
            return false;
        }
        @Override
        public boolean onQueryTextSubmit(String query) {
            socialGroupFragment.query(query);
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        mainContent = (FrameLayout)findViewById(R.id.mainContent);

        socialFragment = new SocialFragment();
        socialGroupFragment = new SocialGroupFragment();
        galleryFragment = new GalleryFragment();
        downloadedFragment = new DownloadedFragment();
        settingFragment = new SettingFragment();
        developFragment = new DevelopFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.mainContent, socialFragment).commitAllowingStateLoss();
        getSupportFragmentManager().beginTransaction().add(R.id.mainContent,socialGroupFragment).commitAllowingStateLoss();
        getSupportFragmentManager().beginTransaction().add(R.id.mainContent, galleryFragment).commitAllowingStateLoss();
        getSupportFragmentManager().beginTransaction().add(R.id.mainContent, downloadedFragment).commitAllowingStateLoss();
        getSupportFragmentManager().beginTransaction().add(R.id.mainContent, settingFragment).commitAllowingStateLoss();
        getSupportFragmentManager().beginTransaction().add(R.id.mainContent, developFragment).commitAllowingStateLoss();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.getHeaderView(0);
        LoginButton authButton = (LoginButton)headerLayout.findViewById(R.id.login_button);
        authButton.setReadPermissions(FacebookMng.PERMISSION);
        avatar = (ImageView)headerLayout.findViewById(R.id.avatar);
        username = (TextView)headerLayout.findViewById(R.id.username);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        String accessToken = loginResult.getAccessToken().getToken();
                        Set<String> perm = loginResult.getAccessToken().getPermissions();
                        SharePrefMng.getInstance(MainActivity.this).setValue(SharePrefMng.KEY_ACCESSTOKEN, accessToken);
                        SharePrefMng.getInstance(MainActivity.this).setValue(SharePrefMng.KEY_PERMISSION, perm);

                        Log.e("TAG", "accessToken:" + AccessToken.getCurrentAccessToken().getToken());
                        Log.e("TAG", "permission:" + AccessToken.getCurrentAccessToken().getPermissions().toString());
                        Log.e("TAG", "user_id:" + FacebookMng.getInstance(MainActivity.this).getUserId());

                        EventBus.getDefault().post(loginResult);

                        refresh();

                        drawer.closeDrawer(GravityCompat.START);
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }

                });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                Log.d("TAG", "onCurrentAccessTokenChanged()");
                if (oldAccessToken == null) {
                    // Log in Logic
                } else if (currentAccessToken == null) {
                    // Log out logic
                    EventBus.getDefault().post(new RefreshEvent());
                }
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                // App code
            }
        };

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        // show hash key
        PackageUtils.showHashKey(getApplicationContext(), null);

        checkNetwork();
    }

    private void checkNetwork(){
        if(!ConnectionUtils.isOnline(this)){
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();

            alertDialog.setTitle("Info");
            alertDialog.setMessage("Internet not available, Cross check your internet connectivity and try again");
            alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
            alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    changeScreen(SCREEN_GALLERY);
                }
            });

            alertDialog.show();
        }
    }

    private void refresh(){
        FacebookMng.getInstance(MainActivity.this).getUserName().subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String name) {
                username.setText(name);
                SharePrefMng.getInstance(MainActivity.this).setValue(SharePrefMng.KEY_USER_NAME, name + "");

                User user = new User();
                user.fbId = FacebookMng.getInstance(getApplicationContext()).getUserId();
                user.name = name;
                user.accessToken = FacebookMng.getInstance(getApplicationContext()).getAccessToken();
                user.email = "";
                user.password = "";
                user.permission = FacebookMng.getInstance(getApplicationContext()).getPermission().toString();

                ParseMng.getInstance(getApplicationContext()).postUser(user);
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bmp = FacebookMng.getInstance(MainActivity.this).getPhotoFacebook();
                avatar.post(new Runnable() {
                    @Override
                    public void run() {
                        avatar.setImageBitmap(bmp);
                    }
                });
            }
        }).start();
    }

    public void changeScreen(String type){
        currentScreen = type;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.hide(socialFragment);
        fragmentTransaction.hide(socialGroupFragment);
        fragmentTransaction.hide(galleryFragment);
        fragmentTransaction.hide(downloadedFragment);
        fragmentTransaction.hide(settingFragment);
        fragmentTransaction.hide(developFragment);

        switch (type){
            case SCREEN_SOCIAL:
                fragmentTransaction.show(socialFragment);
                navigationView.setCheckedItem(R.id.nav_social);
                setSearchMenuVisibility(false);
                break;
            case SCREEN_SOCIAL_GROUP:
                fragmentTransaction.show(socialGroupFragment);
                navigationView.setCheckedItem(R.id.nav_social_group);
                setSearchMenuVisibility(true);
                break;
            case SCREEN_GALLERY:
                fragmentTransaction.show(galleryFragment);
                navigationView.setCheckedItem(R.id.nav_gallery);
                setSearchMenuVisibility(false);
                break;
            case SCREEN_DOWNLOADED:
                fragmentTransaction.show(downloadedFragment);
                navigationView.setCheckedItem(R.id.nav_downloaded);
                setSearchMenuVisibility(false);
                break;
            case SCREEN_SETTING:
                fragmentTransaction.show(settingFragment);
                navigationView.setCheckedItem(R.id.nav_setting);
                setSearchMenuVisibility(false);
                break;
            case SCREEN_DEVELOP:
                fragmentTransaction.show(developFragment);
                navigationView.setCheckedItem(R.id.nav_develop);
                setSearchMenuVisibility(false);
                break;
            case SCREEN_NONE:
                break;
        }
        fragmentTransaction.commitAllowingStateLoss();

        toolbar.setTitle(type);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
        // check login
        // should not be called in onCreate because fragments is not attached
        if(FacebookMng.getInstance(this).isLoggedin()){
            refresh();
        }

        if(isBegin) {
            currentScreen = SharePrefMng.getInstance(this).getValue(SharePrefMng.KEY_LASTSCREEN,SCREEN_SOCIAL_GROUP);

            if(currentScreen.equalsIgnoreCase(SCREEN_SETTING)){
                currentScreen = SCREEN_SOCIAL_GROUP;
            }

            changeScreen(currentScreen);
            isBegin = false;
            EventBus.getDefault().post(new RefreshEvent());
        }

        if(SharePrefMng.getInstance(this).getValue(SharePrefMng.KEY_FIRST_TIME_USE,true) || !FacebookMng.getInstance(this).isLoggedin()){
            SharePrefMng.getInstance(this).setValue(SharePrefMng.KEY_FIRST_TIME_USE,false);
            drawer.openDrawer(GravityCompat.START);
        }

        if(Config.MODE == Config.BUILD_MODE.PRODUCT){
            navigationView.getMenu().removeGroup(R.id.nav_group_develop);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
        EventBus.getDefault().unregister(this);
        SharePrefMng.getInstance(this).setValue(SharePrefMng.KEY_LASTSCREEN, currentScreen);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        this.menu = menu;
        searchItem = menu.findItem(R.id.action_search);
        searchView = (android.support.v7.widget.SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(mOnQueryTextListener);
        setSearchMenuVisibility(false);
        return true;
    }

    private void setSearchMenuVisibility(boolean visible) {
        if(searchItem!=null) {
            searchItem.setVisible(visible);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_social) {
            changeScreen(SCREEN_SOCIAL);
        } else if (id == R.id.nav_gallery) {
            changeScreen(SCREEN_GALLERY);
        } else if (id == R.id.nav_social_group) {
            changeScreen(SCREEN_SOCIAL_GROUP);
        } else if (id == R.id.nav_downloaded) {
            changeScreen(SCREEN_DOWNLOADED);
        } else if (id == R.id.nav_setting) {
            changeScreen(SCREEN_SETTING);
        } else if (id == R.id.nav_develop) {
            changeScreen(SCREEN_DEVELOP);
        } else if (id == R.id.nav_share) {
            shareApp();
        } else if (id == R.id.nav_rate) {
            AppRate.with(this).showRateDialog(this);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void shareApp(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);

        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_link));
        startActivity(Intent.createChooser(intent, "Share"));
    }

    public void onEvent(SnackEvent event){
        ToastUtils.showSnack(event.text);
    }

    public void onEvent(DownloadEvent event){
        String message = event.message;
        int status = DownloadMng.getInstance(this).getStatus(event.downloadId);
        switch (status){
            case DownloadManager.STATUS_RUNNING:
                message = "downloading "+event.video.toString();
                break;
            case DownloadManager.STATUS_FAILED:
                message = "download "+event.video.toString()+" failed";
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                message = "download "+event.video.toString()+" successful";
                break;
        }
        if(TextUtils.isEmpty(message)){
            return;
        }
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }
}
