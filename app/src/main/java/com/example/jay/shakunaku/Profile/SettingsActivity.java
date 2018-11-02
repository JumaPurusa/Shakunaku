package com.example.jay.shakunaku.Profile;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jay.shakunaku.Dialogs.LoadingDialog;
import com.example.jay.shakunaku.Dialogs.LogoutDialog;
import com.example.jay.shakunaku.Models.UserSettings;
import com.example.jay.shakunaku.R;
import com.example.jay.shakunaku.Utils.FirebaseMethods;
import com.example.jay.shakunaku.Utils.ProgressDialogUtil;
import com.example.jay.shakunaku.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private Context mContext = SettingsActivity.this;

    //private CardView cardView;
    private RelativeLayout cardLayout;
    private CircleImageView profileImage;
    private Toolbar toolbar;
    private RelativeLayout logoutLayout;

    //widgets
    private TextView fullName;
    private TextView username;

    String imgURL = null;

    // firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private FirebaseMethods firebaseMethods;

    //private UserSettings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        setContentView(R.layout.activity_settings);

        if(savedInstanceState != null){
            setupFirebaseAuth();
        }else{
            setupFirebaseAuth();
        }

        toolbar = findViewById(R.id.toolbar);
        toolbar.setPadding(0, 0, 0, 0);
        setSupportActionBar(toolbar);

        imgURL = getIntent().getStringExtra(ProfileFragment.key).toString();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        profileImage = findViewById(R.id.profile_image);
        fullName = findViewById(R.id.full_name);
        username = findViewById(R.id.username);

        firebaseMethods = new FirebaseMethods(mContext);

        //setProfileImage();

        cardViewClick();

        logoutClick();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void cardViewClick(){

        //cardView = findViewById(R.id.profile_image_card);
        cardLayout = findViewById(R.id.card_layout);
        cardLayout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent editProfileIntent = new Intent(SettingsActivity.this, EditProfileActivity.class);
                        //Pair<View, String> p0 = Pair.create((View)toolbar, "toolbar");
                        //Pair<View, String> p1 = Pair.create((View)cardView, "profileImageCard");
                        //Pair<View, String> p2 = Pair.create((View)cardLayout, "cardLayout");
                        //Pair<View, String> p3 = Pair.create((View)profileImage, "profileImage");
                        /*
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                SettingsActivity.this, p0
                        );
                        */
                        startActivity(editProfileIntent/*options.toBundle()*/);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }
        );

    }


    private void setImageFullUsername(UserSettings userSettings){

        UniversalImageLoader.setImage(userSettings.getUserAccountSettings().getProfile_photo(), profileImage, null, "");
        fullName.setText(userSettings.getUserAccountSettings().getDisplay_name());
        username.setText(userSettings.getUserAccountSettings().getUsername());

    }

    private void logoutClick(){
        //final ProgressDialogUtil progressDialogUtil = new ProgressDialogUtil(mContext, "Logging out ...");

        logoutLayout = findViewById(R.id.logout_layout);
        logoutLayout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //I will build a custom dialog

                        /*
                        final AlertDialog.Builder logoutBuilder = new AlertDialog.Builder(mContext);
                        logoutBuilder.setMessage("Log out of Shakunaku?");

                        logoutBuilder.setPositiveButton("Log out",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        new LoadingDialog("Logging out").show(getFragmentManager(), "Dialog");
                                        Thread thread = new Thread(){

                                            @Override
                                            public void run() {

                                                try {
                                                    sleep(5000);
                                                    mAuth.signOut();
                                                    finish();

                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        };

                                        thread.start();

                                    }
                                });

                        logoutBuilder.setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {


                                    }
                                });

                        Dialog dialog = logoutBuilder.create();
                        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlide2;
                        dialog.show();
                        */

                        LogoutDialog logoutDialog = new LogoutDialog(mContext);
                        logoutDialog.show(getSupportFragmentManager(), "Logout Dialog");
                    }
                }
        );
    }

    /*
    private void setProfileImage(){

        Log.d(TAG, "setProfileImage: setting up profile image");
        profileImage = findViewById(R.id.profile_image);
        UniversalImageLoader.setImage(imgURL, profileImage, null, "https://");
    }
    */

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser currentUser = mAuth.getCurrentUser();

                if(currentUser != null){
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:sign_in " + currentUser.getUid());

                    reference.addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    setImageFullUsername(firebaseMethods.getUserSettings(dataSnapshot));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            }
                    );

                }else{

                    Log.d(TAG, "onAuthStateChanged: sign_out");

                }

            }
        };


    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
    }


}
