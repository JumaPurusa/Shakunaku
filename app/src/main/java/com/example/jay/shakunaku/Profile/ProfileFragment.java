package com.example.jay.shakunaku.Profile;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jay.shakunaku.Activities.LoginActivity;
import com.example.jay.shakunaku.Models.User;
import com.example.jay.shakunaku.Models.UserAccountSettings;
import com.example.jay.shakunaku.Models.UserSettings;
import com.example.jay.shakunaku.R;
import com.example.jay.shakunaku.Utils.FirebaseMethods;
import com.example.jay.shakunaku.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    public static final String key = "key";

    private ViewPager viewPager;
    private ProfileFragmentTabsAdapter profileFragmentTabsAdapter;
    private TabLayout tabLayout;

    String imgURL = "";

    public static Toolbar profileToolbar;
    private ImageView profileImage, contextMenu;
    private TextView toolbarTitle, followers, following, posts, fullname, username;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseMethods firebaseMethods;

    //vars
    private int mFollowersCount = 0;
    private int mFollowingCount = 0;
    private int mPostsCount = 0;

    private User mUser;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        if(savedInstanceState != null){
            setupFirebaseAuth();
        }else{
            setupFirebaseAuth();
        }

        profileToolbar = view.findViewById(R.id.toolbar);
        profileToolbar.setPadding(0,0,0,0);
        ((AppCompatActivity)getActivity()).setSupportActionBar(profileToolbar);


        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);


        profileImage = view.findViewById(R.id.profile_image);
        toolbarTitle = view.findViewById(R.id.title);
        followers = view.findViewById(R.id.followers_number);
        following = view.findViewById(R.id.following_number);
        posts = view.findViewById(R.id.posts_number);
        fullname = view.findViewById(R.id.full_name);
        username = view.findViewById(R.id.username);
        contextMenu = view.findViewById(R.id.context_menu);

        firebaseMethods = new FirebaseMethods(getContext());

        contextMenu.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent settingsIntent = new Intent(getContext(), SettingsActivity.class);
                        settingsIntent.putExtra(key, imgURL);
                        startActivity(settingsIntent);
                        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    }
                }
        );

        getFollowingCount();
        getFollowersCount();
        getPostsCount();

        return view;
    }

    private void getFollowersCount(){

        try{

            mFollowersCount = 0;

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child(getContext().getString(R.string.dbname_followers))
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            query.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                                mFollowersCount++;
                            }

                            followers.setText(String.valueOf(mFollowersCount));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }
            );

        }catch(NullPointerException e){
            Log.d(TAG, "getFollowersCount: NullPointerException: " + e.getMessage());
        }

    }

    private void getFollowingCount(){

        try{

            mFollowingCount = 0;

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child(getContext().getString(R.string.dbname_following))
                    .child(mAuth.getCurrentUser().getUid());

            query.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                                mFollowingCount++;
                            }

                            following.setText(String.valueOf(mFollowingCount));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }
            );

        }catch(NullPointerException e){
            Log.d(TAG, "getFollowingCount: NullPointerException");
        }

    }

    private void getPostsCount(){

        try{

            mPostsCount = 0;

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child(getContext().getString(R.string.dbname_user_photos))
                    .child(mAuth.getCurrentUser().getUid());

            query.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                                mPostsCount++;
                            }

                            posts.setText(String.valueOf(mPostsCount));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }
            );

        }catch(NullPointerException e){
            Log.d(TAG, "getPostsCount: NullPointerException: " + e.getMessage());
        }

    }


    private void setProfileWidgets(UserSettings userSettings){
        Log.d(TAG, "setProfileWidgets: retrieving user settings data from the database");
        View view = getView();
        UserAccountSettings settings = userSettings.getUserAccountSettings();

        mUser = userSettings.getUser();

        toolbarTitle.setText(settings.getUsername());
        UniversalImageLoader.setImage(settings.getProfile_photo(), profileImage, null, "");

        fullname.setText(settings.getDisplay_name());
        username.setText(settings.getUsername());

        profileFragmentTabsAdapter = new ProfileFragmentTabsAdapter(getFragmentManager());
        viewPager = view.findViewById(R.id.viewpager);
        setUpViewPager(viewPager);
        viewPager.setCurrentItem(0);
        tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setUpTabs();

        LinearLayout linearLayout = (LinearLayout) tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.GRAY);
        drawable.setSize(1,1);
        linearLayout.setDividerPadding(10);
        linearLayout.setDividerDrawable(drawable);

    }

    private void setUpViewPager(ViewPager viewPager){
        ProfileFragmentTabsAdapter profileFragmentTabsAdapter = new ProfileFragmentTabsAdapter(getFragmentManager());
        profileFragmentTabsAdapter.addFragment(new ProfileTimelineFragment(), "Timeline");
        profileFragmentTabsAdapter.addFragment(new ProfileMediaFragment(), "Media");
        profileFragmentTabsAdapter.addFragment(new ProfileFavoritesFragment(), "Favorites");
        viewPager.setAdapter(profileFragmentTabsAdapter);
    }

    private void setUpTabs(){
        tabLayout.getTabAt(0).setText(ProfileFragmentTabsAdapter.fragmentTitles.get(0));
        tabLayout.getTabAt(1).setText(ProfileFragmentTabsAdapter.fragmentTitles.get(1));
        tabLayout.getTabAt(2).setText(ProfileFragmentTabsAdapter.fragmentTitles.get(2));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.new_profile_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_settings:
                Intent settingsIntent = new Intent(getContext(), SettingsActivity.class);
                settingsIntent.putExtra(key, imgURL);
                startActivity(settingsIntent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        if(mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
    }

    private void setupFirebaseAuth(){

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mRef = firebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                if(currentUser != null){
                    // user is signed in
                    Log.d(TAG, "onAuthStateChanged: signed_in: " + currentUser.getUid());

                    mRef.addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    //retrieve user information from the database
                                    setProfileWidgets(firebaseMethods.getUserSettings(dataSnapshot));

                                    //retrieve images for the user in question

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {


                                }
                            }
                    );

                }else{
                    // user is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };



    }
}
