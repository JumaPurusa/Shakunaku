package com.example.jay.shakunaku;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.jay.shakunaku.Activities.LoginActivity;
import com.example.jay.shakunaku.DirectMessage.DMFragment;
import com.example.jay.shakunaku.Notifications.NotificationsFragment;
import com.example.jay.shakunaku.Profile.ProfileFragment;
import com.example.jay.shakunaku.Timeline_Homepage.HomeFragment;
import com.example.jay.shakunaku.Timeline_Posting.PostingMediaFragment;
import com.example.jay.shakunaku.Utils.BaseFragment;
import com.example.jay.shakunaku.Utils.BottomNavigationViewHelper;
import com.example.jay.shakunaku.Utils.FragmentHistory;
import com.example.jay.shakunaku.Utils.UniversalImageLoader;
import com.example.jay.shakunaku.Utils.Utils;
import com.example.jay.shakunaku.Views.FragNavController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ContainerActivity extends AppCompatActivity{

    private static final String TAG = "ContainerActivity";
    //private static final int ACTIVITY_NUM = 1;

    //FrameLayout contentFrame;

    public static Toolbar toolbar;

    /*
    private int[] mTabIconsSelected = {
            R.drawable.home,
            R.drawable.tongue,
            R.drawable.photo_home,
            R.drawable.envelope,
            R.drawable.avatar
    };
    */

    //String[] TABS;

    //TabLayout bottomTabLayout;

    //private FragNavController mNavController;

    //private FragmentHistory fragmentHistory;


    private Context mContext = ContainerActivity.this;

    // firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    final Fragment fragment1 = new HomeFragment();
    final Fragment fragment2 = new NotificationsFragment();
    final Fragment fragment3 = new PostingMediaFragment();
    final Fragment fragment4 = new DMFragment();
    final Fragment fragment5 = new ProfileFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;


    private BottomNavigationViewEx.OnNavigationItemSelectedListener bottomNavigationItemListener = new BottomNavigationViewEx.OnNavigationItemSelectedListener() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()){

                case R.id.ic_home:
                    fm.beginTransaction().hide(active).show(fragment1).commit();

                    Window window4 = getWindow();

                    // clear FLAG_TRANSLUCENT_STATUS flag
                    window4.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                    // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                    window4.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                    // finally change the color
                    window4.setStatusBarColor(Color.parseColor("#E6392F"));

                    active = fragment1;
                    return true;

                case R.id.social:
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    Window window3= getWindow();

                    // clear FLAG_TRANSLUCENT_STATUS flag
                    window3.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                    // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                    window3.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                    // finally change the color
                    window3.setStatusBarColor(Color.parseColor("#E6392F"));
                    active = fragment2;
                    return true;

                case R.id.ic_media:
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    Window window = getWindow();

                    // clear FLAG_TRANSLUCENT_STATUS flag
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                    // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                    // finally change the color
                    window.setStatusBarColor(Color.BLACK);
                    active = fragment3;
                    return true;

                case R.id.ic_message:
                    fm.beginTransaction().hide(active).show(fragment4).commit();
                    Window window1 = getWindow();

                    // clear FLAG_TRANSLUCENT_STATUS flag
                    window1.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                    // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                    window1.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                    // finally change the color
                    window1.setStatusBarColor(Color.parseColor("#E6392F"));
                    active = fragment4;
                    return true;

                case R.id.ic_profile:
                    fm.beginTransaction().hide(active).show(fragment5).commit();

                    Window window2 = getWindow();

                    // clear FLAG_TRANSLUCENT_STATUS flag
                    window2.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                    // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                    window2.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                    // finally change the color
                    window2.setStatusBarColor(Color.parseColor("#E6392F"));

                    active = fragment5;
                    return true;

            }

            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        setContentView(R.layout.activity_container);
        Log.d(TAG, "onCreate: starting");

        /*
        if(savedInstanceState == null){
            setupFirebaseAuth();
        }else{
            setupFirebaseAuth();
        }
        */

        toolbar = findViewById(R.id.toolbar);
        toolbar.setPadding(0, 0, 0, 0);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        initImageLoader();
        setUpBottomNavigationView();

        fm.beginTransaction().add(R.id.content_frame, fragment5, "5").hide(fragment5).commit();
        fm.beginTransaction().add(R.id.content_frame, fragment4, "4").hide(fragment4).commit();
        fm.beginTransaction().add(R.id.content_frame, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.content_frame, fragment2, "2").hide(fragment2).commit();

        if(savedInstanceState != null){
           fm.beginTransaction().add(R.id.content_frame, active).commit();
        }else{
           fm.beginTransaction().add(R.id.content_frame, fragment1, "1").commit();
        }

        setupFirebaseAuth();
        // on backstack item needs to be check

        //contentFrame = findViewById(R.id.content_frame);
        //bottomTabLayout = findViewById(R.id.bottom_tab_layout);
        //TABS = getResources().getStringArray(R.array.tab_name);

        //initTab();

        //fragmentHistory = new FragmentHistory();

        //mNavController = FragNavController.newBuilder(savedInstanceState, getSupportFragmentManager(), R.id.content_frame)
              //  .transactionListener(this)
               // .rootFragmentListener(this, TABS.length)
                //.build();

        //switchTab(0);

        /*
        bottomTabLayout.addOnTabSelectedListener(
                new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        fragmentHistory.push(tab.getPosition());

                        switchTab(tab.getPosition());

                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                        mNavController.clearStack();
                        switchTab(tab.getPosition());
                    }
                }
        );
        */

    }

    /*
    private void initTab(){
        if(bottomTabLayout != null){
            for(int i=0; i<TABS.length; i++){
                bottomTabLayout.addTab(bottomTabLayout.newTab());
                TabLayout.Tab tab = bottomTabLayout.getTabAt(i);
                if(tab != null)
                    tab.setCustomView(getTabView(i));
            }
        }
    }*/



    /*
    private View getTabView(int position) {
        View view = LayoutInflater.from(ContainerActivity.this).inflate(R.layout.tab_item_bottom, null);
        ImageView icon = view.findViewById(R.id.tab_icon);
        icon.setImageDrawable(Utils.setDrawableSelector(ContainerActivity.this, mTabIconsSelected[position], mTabIconsSelected[position]));
        //icon.setBackground(Utils.selectorBackgroundColor(ContainerActivity.this, Color.WHITE, R.color.colorPrimary));
        return view;
    }
    */

    /**
     * setup a bottom navigation View
     */


    private void setUpBottomNavigationView(){
        //Log.d(TAG, "setUpBottomNavigationView: setting up BottomNavigationView");

        /*
        BottomNavigationView bnav = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.disableShiftMode(bnav);
        bnav.setOnNavigationItemSelectedListener(bottomNavigationItemListener);
        */

       BottomNavigationViewEx bnav = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setUpBottomNavigationView(bnav);
        bnav.setOnNavigationItemSelectedListener(bottomNavigationItemListener);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    // init ImageLoaderConfiguration is global, set only once
    private void initImageLoader(){

        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getApplicationContext());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());

    }


    /**
     *  ----------------------------------- Firebase ----------------------------------------
     */

    /**
     * checks to see if the user is logged in
     */

    private void checkCurrentUser(FirebaseUser currentUser){
        Log.d(TAG, "checkCurrentUser: checking if user is logged in");

        if(currentUser == null){

            Intent loginIntent = new Intent(mContext, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
        }
    }

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                //checkCurrentUser(currentUser);
                if(currentUser == null){
                    Intent loginIntent = new Intent(mContext, LoginActivity.class);
                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                    finish();
                }else{

                }
            }
        };
    }


    @Override
    protected void onStart() {
        super.onStart();

        //mAuth.addAuthStateListener(mAuthListener);
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);


    }


    @Override
    protected void onStop() {
        super.onStop();

        if(mAuthListener != null){
           FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {


            case android.R.id.home:


                onBackPressed();
                return true;
        }


        return super.onOptionsItemSelected(item);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    @Override
    public void onBackPressed() {
            super.onBackPressed();
        /*
        if (!mNavController.isRootFragment()) {
            mNavController.popFragment();
        } else {

            if (fragmentHistory.isEmpty()) {
                super.onBackPressed();
            } else {


                if (fragmentHistory.getStackSize() > 1) {

                    int position = fragmentHistory.popPrevious();

                    switchTab(position);

                    updateTabSelection(position);

                } else {

                    switchTab(0);

                    updateTabSelection(0);

                    fragmentHistory.emptyStack();
                }
            }

        }
        */
    }


    /*
    private void updateTabSelection(int currentTab){

        for (int i = 0; i <  TABS.length; i++) {
            TabLayout.Tab selectedTab = bottomTabLayout.getTabAt(i);
            if(currentTab != i) {
                selectedTab.getCustomView().setSelected(false);
            }else{
                selectedTab.getCustomView().setSelected(true);
            }
        }
    }
    */


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*
        if (mNavController != null) {
            mNavController.onSaveInstanceState(outState);
        }
        */
    }

    /*
    private void switchTab(int position) {
        mNavController.switchTab(position);
    }*/
}
