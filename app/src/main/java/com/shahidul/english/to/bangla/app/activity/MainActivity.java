package com.shahidul.english.to.bangla.app.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.view.menu.ActionMenuItem;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.shahidul.english.to.bangla.app.R;
import com.shahidul.english.to.bangla.app.config.Configuration;
import com.shahidul.english.to.bangla.app.constant.Constant;
import com.shahidul.english.to.bangla.app.constant.Key;
import com.shahidul.english.to.bangla.app.fragment.EditWordFragment;
import com.shahidul.english.to.bangla.app.fragment.FavoriteFragment;
import com.shahidul.english.to.bangla.app.fragment.HomeFragment;
import com.shahidul.english.to.bangla.app.fragment.WordDetailFragment;
import com.shahidul.english.to.bangla.app.listener.WordClickListener;
import com.shahidul.english.to.bangla.app.util.Util;

import java.util.Locale;
import java.util.Stack;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, WordClickListener, TextToSpeech.OnInitListener {

    public static final int SWIPE_MIN_DISTANCE = 120;
    public static final int SWIPE_MAX_OFF_PATH = 250;
    public static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private static String TAG = MainActivity.class.getSimpleName();
    public GestureDetector gesture;
    private TextToSpeech textToSpeech;
    private Toolbar mToolbar;
    private TextView fromView;
    private TextView toView;
    private ImageView arrowView;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    AdView adView;
    private Fragment homeFragment = new HomeFragment();
    private Fragment favoriteFragment = new FavoriteFragment();
    private WordDetailFragment wordDetailFragment = new WordDetailFragment();
    private EditWordFragment editWordFragment = new EditWordFragment();
    private Stack<Fragment> fragmentStack = new Stack<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, new Exception().getStackTrace()[0].getMethodName());
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        fromView = (TextView) findViewById(R.id.from_language);
        toView = (TextView) findViewById(R.id.to_language);
        arrowView = (ImageView) findViewById(R.id.arrow);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        adView = (AdView)findViewById(R.id.ad_view);
        if (Util.isNetworkAvailable(getApplicationContext())) {
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }
        else {
            adView.setVisibility(View.GONE);
        }
        String searchText = null;
        if (intent.getExtras() != null) {
            searchText = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (searchText != null) {
                searchText = searchText.trim();
            }
        }
        if ((intent.getExtras() != null && intent.getIntExtra(Key.LANGUAGE_KEY, Constant.ENGLISH_LANGUAGE) == Constant.OTHER_LANGUAGE)) {
            Configuration.isPrimaryLanguageEnglish = false;
            fromView.setText(R.string.second_language);
            toView.setText(R.string.first_language);
        } else {
            Configuration.isPrimaryLanguageEnglish = true;
            fromView.setText(R.string.first_language);
            toView.setText(R.string.second_language);
        }
        arrowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getString(R.string.first_language).equals(fromView.getText().toString())) {
                    Configuration.isPrimaryLanguageEnglish = false;
                    fromView.setText(R.string.second_language);
                    toView.setText(R.string.first_language);
                } else {
                    Configuration.isPrimaryLanguageEnglish = true;
                    fromView.setText(R.string.first_language);
                    toView.setText(R.string.second_language);
                }
                loadNewConfiguration();
            }
        });
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.empty, R.string.empty) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);


        gesture = new GestureDetector(this, gestureListener);


        actionBarDrawerToggle.syncState();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container_body, homeFragment);
        fragmentTransaction.commit();
        fragmentStack.clear();
        if (searchText != null) {
            Bundle bundle = new Bundle();
            bundle.putString(Intent.EXTRA_TEXT, searchText);
            homeFragment.setArguments(bundle);
        }
        fragmentStack.push(homeFragment);

        textToSpeech = new TextToSpeech(this, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent("android.intent.action.VIEW");
        switch (item.getItemId()) {
            case R.id.action_rate_me:
                intent.setData(Uri.parse(Constant.MARKET_DETAILS + getPackageName()));
                startActivity(intent);
                return true;
            case R.id.more_apps:
                intent.setData(Uri.parse(Constant.MORE_APPS));
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        drawerLayout.closeDrawers();
        menuItem.setChecked(true);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (menuItem.getItemId()) {
            case R.id.home:
                fragmentTransaction.replace(R.id.container_body, homeFragment);
                fragmentTransaction.commit();
                fragmentStack.clear();
                fragmentStack.push(homeFragment);
                return true;
            case R.id.favorite:
                fragmentTransaction.replace(R.id.container_body, favoriteFragment);
                fragmentTransaction.commit();
                fragmentStack.clear();
                fragmentStack.push(favoriteFragment);
                return true;
            case R.id.add_new_word:
                editWordFragment = new EditWordFragment();
                Bundle arguments = editWordFragment.getArguments();
                if (arguments == null){
                    arguments = new Bundle();
                }
                arguments.putInt(Key.RECORD_ID, Constant.INVALID_ROW_ID);
                editWordFragment.setArguments(arguments);
                fragmentTransaction.replace(R.id.container_body, editWordFragment);
                fragmentTransaction.commit();
                fragmentStack.clear();
                fragmentStack.push(editWordFragment);
                return true;
            case R.id.exit:
                finish();
                return true;
            default:
                return false;
        }
    }


    @Override
    public void onClickWord(int wordId) {
        fragmentStack.push(wordDetailFragment);
        Bundle arguments = new Bundle();
        arguments.putInt(Key.RECORD_ID, wordId);
        wordDetailFragment.setArguments(arguments);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container_body, wordDetailFragment);
        fragmentTransaction.commit();
    }

    public void editWord(int wordId) {
        fragmentStack.push(editWordFragment);
        Bundle arguments = editWordFragment.getArguments();
        if (arguments == null){
            arguments = new Bundle();
        }
        arguments.putInt(Key.RECORD_ID, wordId);
        editWordFragment.setArguments(arguments);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container_body, editWordFragment);
        fragmentTransaction.commit();

    }

    public void displayHomeView() {
        MenuItem menuItem = new ActionMenuItem(this, 0, R.id.home, 0, 0, "");
        onNavigationItemSelected(menuItem);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, new Exception().getStackTrace()[0].getMethodName());
        Fragment fragment = fragmentStack.peek();
        if (fragment instanceof HomeFragment && ((HomeFragment) fragment).canConsumeBackButtonPressEvent()) {
            return;
        }
        if (fragmentStack.size() > 1) {
            fragmentStack.pop();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragmentStack.peek());
            fragmentTransaction.commit();
        } else {
            super.onBackPressed();
        }
    }


    private void loadNewConfiguration(){
        mToolbar.setVisibility(View.VISIBLE);
        homeFragment = new HomeFragment();
        favoriteFragment = new FavoriteFragment();
        wordDetailFragment = new WordDetailFragment();
        editWordFragment = new EditWordFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container_body, homeFragment);
        fragmentTransaction.commit();
        fragmentStack.clear();
        fragmentStack.push(homeFragment);
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    public void hideSoftKeyboard() {
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }

    public void speak(String word) {
        textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.setLanguage(Locale.US);
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, new Exception().getStackTrace()[0].getMethodName());
        textToSpeech.stop();
        textToSpeech.shutdown();
        super.onDestroy();
    }

    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    Log.i("tag", "Right to Left");
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    Log.i("tag", "Left to Right");
                    if (fragmentStack.size() > 1) {
                        onBackPressed();
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    };
}
