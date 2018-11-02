package com.example.jay.shakunaku.Timeline_Homepage;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jay.shakunaku.Models.User;
import com.example.jay.shakunaku.Models.UserAccountSettings;
import com.example.jay.shakunaku.Models.UserSettings;
import com.example.jay.shakunaku.Profile.EditProfileActivity;
import com.example.jay.shakunaku.Profile.ProfileFavoritesFragment;
import com.example.jay.shakunaku.Profile.ProfileFragmentTabsAdapter;
import com.example.jay.shakunaku.Profile.ProfileMediaFragment;
import com.example.jay.shakunaku.Profile.ProfileTimelineFragment;
import com.example.jay.shakunaku.Profile.SettingsActivity;
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
public class ViewProfileFragment extends Fragment {

    private static final String TAG = "ViewProfileFragment";
    //private Context context = getContext();

    private ViewPager viewPager;
    private TabLayout tabLayout;

    public static Toolbar viewProfileToolbar;
    private ImageView profileImage, backArrow;
    private TextView toolbarTitle, followers, following, posts, fullname, username;
    private TextView follow, unfollow, editProfile;
    private ProgressBar progressBar;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mRef;

    //vars
    private User mUser;
    private int mFollowersCount = 0;
    private int mFollowingCount = 0;
    private int mPostsCount = 0;

    public ViewProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_profile, container, false);

        viewProfileToolbar = view.findViewById(R.id.view_profile_toolbar);
        viewProfileToolbar.setPadding(0,0,0,0);
        ((AppCompatActivity)getActivity()).setSupportActionBar(viewProfileToolbar);


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
        backArrow = view.findViewById(R.id.back_arrow);
        progressBar = view.findViewById(R.id.progressBar);
        follow = view.findViewById(R.id.follow);
        unfollow = view.findViewById(R.id.unfollow);
        editProfile = view.findViewById(R.id.edit);

        progressBar.setVisibility(View.GONE);


        try{
            Log.d(TAG, "onCreateView: user : " + getUserFromBundle());
            mUser = getUserFromBundle();
            init();
        }catch (NullPointerException e){
            Log.d(TAG, "onCreateView: NullPointerException : " + e.getMessage());
            Toast.makeText(getContext(), "something went wrong", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }


        setupFirebaseAuth();
        isFollowing();
        getFollowersCount();
        getFollowingCount();
        getPostsCount();

        /*
        viewPager = view.findViewById(R.id.viewpager);
        setUpViewPager(viewPager);
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
        */

        return view;

    }

    private User getUserFromBundle(){
        Log.d(TAG, "getUserFromBundle: arguments : " + getArguments());

        Bundle bundle = this.getArguments();
        if(bundle != null){
            return bundle.getParcelable(getString(R.string.parcelable_user));
        }else{
            return null;
        }
    }

    private void init(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getContext().getString(R.string.dbname_user_account_settings))
                .orderByChild(getContext().getString(R.string.user_id))
                .equalTo(mUser.getUser_id());

        query.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                            UserSettings userSettings = new UserSettings();
                            userSettings.setUser(mUser);
                            userSettings.setUserAccountSettings(singleSnapshot.getValue(UserAccountSettings.class));

                            setProfileWidgets(userSettings);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

    private void isFollowing(){

        try{

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child(getContext().getString(R.string.dbname_following))
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .orderByChild(getContext().getString(R.string.user_id))
                    .equalTo(mUser.getUser_id());

            query.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d(TAG, "onDataChange: following found : " + dataSnapshot.getChildren());

                            for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                                setFollow();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }
            );

        }catch (NullPointerException e){
            Log.d(TAG, "isFollowing: NullPointerException: " + e.getMessage());
        }

    }

    private void getFollowersCount(){

        try{

            mFollowersCount = 0;

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child(getContext().getString(R.string.dbname_followers))
                    .child(mUser.getUser_id());

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
                    .child(mUser.getUser_id());

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
            Log.d(TAG, "getFollowingCount: NullPointerException: " + e.getMessage());
        }

    }

    private void getPostsCount(){

        try{

            mPostsCount = 0;

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child(getContext().getString(R.string.dbname_user_photos))
                    .child(mUser.getUser_id());

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

        }catch (NullPointerException e){
            Log.d(TAG, "getPostsCount: NullPointerException: " + e.getMessage());
        }

    }

    private void setProfileWidgets(UserSettings userSettings){

        View view = getView();

        Log.d(TAG, "setProfileWidgets: retrieving user settings data from the database");

        final UserAccountSettings settings = userSettings.getUserAccountSettings();

        progressBar.setVisibility(View.GONE);

        backArrow.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().finish();
                        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }
        );

        follow.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        FirebaseDatabase.getInstance().getReference()
                                .child(getContext().getString(R.string.dbname_following))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(mUser.getUser_id())
                                .child(getContext().getString(R.string.user_id))
                                .setValue(mUser.getUser_id());

                        FirebaseDatabase.getInstance().getReference()
                                .child(getContext().getString(R.string.dbname_followers))
                                .child(mUser.getUser_id())
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(getContext().getString(R.string.user_id))
                                .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

                        setFollow();
                    }
                }
        );

        unfollow.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        FirebaseDatabase.getInstance().getReference()
                                .child(getContext().getString(R.string.dbname_following))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(mUser.getUser_id())
                                .removeValue();

                        FirebaseDatabase.getInstance().getReference()
                                .child(getContext().getString(R.string.dbname_followers))
                                .child(mUser.getUser_id())
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .removeValue();

                        setUnFollow();

                    }
                }
        );

        editProfile.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                         Intent editProfileIntent = new Intent(getContext(), EditProfileActivity.class);
                         getActivity().startActivity(editProfileIntent);
                         getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }
        );

        toolbarTitle.setText(settings.getUsername());
        UniversalImageLoader.setImage(settings.getProfile_photo(), profileImage, null, "");

        fullname.setText(settings.getDisplay_name());
        username.setText(settings.getUsername());

        /*
        if(settings.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            profileToolbar.getMenu().clear();
        }
        */

        viewPager = view.findViewById(R.id.viewpager);
        setUpViewPager(viewPager);
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

    private void setFollow(){
        Log.d(TAG, "setFollow: updating UI to follow this user");
        follow.setVisibility(View.GONE);
        unfollow.setVisibility(View.VISIBLE);
        editProfile.setVisibility(View.GONE);
    }

    private void setUnFollow(){
        Log.d(TAG, "setUnFollow: updating UI to unfollow this user");
        follow.setVisibility(View.VISIBLE);
        unfollow.setVisibility(View.GONE);
        editProfile.setVisibility(View.GONE);
    }

    private void setCurrentUserProfile(){
        Log.d(TAG, "setCurrentUserProfile: updating UI for showing the current user their profile");
        follow.setVisibility(View.GONE);
        unfollow.setVisibility(View.GONE);
        editProfile.setVisibility(View.VISIBLE);
    }

    private void setUpViewPager(ViewPager viewPager){
        ProfileFragmentTabsAdapter profileFragmentTabsAdapter = new ProfileFragmentTabsAdapter(getFragmentManager());
        profileFragmentTabsAdapter.addFragment(new ViewProfileTimelineFragment(mUser), "Timeline");
        profileFragmentTabsAdapter.addFragment(new ViewProfileMediaFragment(mUser), "Media");
        profileFragmentTabsAdapter.addFragment(new ViewProfileFavoritesFragment(mUser), "Favorites");
        viewPager.setAdapter(profileFragmentTabsAdapter);
    }

    private void setUpTabs(){
        tabLayout.getTabAt(0).setText(ProfileFragmentTabsAdapter.fragmentTitles.get(0));
        tabLayout.getTabAt(1).setText(ProfileFragmentTabsAdapter.fragmentTitles.get(1));
        tabLayout.getTabAt(2).setText(ProfileFragmentTabsAdapter.fragmentTitles.get(2));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
         inflater.inflate(R.menu.view_profile_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.ic_home:
                getActivity().finish();
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



                }else{
                    // user is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(isAdded()){
            init();
        }
    }
}
