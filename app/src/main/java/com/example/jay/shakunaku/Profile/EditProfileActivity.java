package com.example.jay.shakunaku.Profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.hardware.input.InputManager;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jay.shakunaku.Dialogs.ConfirmPassword;
import com.example.jay.shakunaku.Models.User;
import com.example.jay.shakunaku.Models.UserSettings;
import com.example.jay.shakunaku.R;
import com.example.jay.shakunaku.Timeline_Posting.GalleryActivity;
import com.example.jay.shakunaku.Utils.FirebaseMethods;
import com.example.jay.shakunaku.Utils.UniversalImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 *
 *There is a hacking needs to be done in this activity about the keyboard and inputs.. Ally Told me
 */

public class EditProfileActivity extends AppCompatActivity implements ConfirmPassword.PasswordCommunicator{
    private static final String TAG = "EditProfileActivity";

    private static final int SELECT_PICTURE = 0;

    private Context mContext = EditProfileActivity.this;

    //EditProfileActivity Widgets
    private Toolbar toolbar;
    private ImageView profileImage, returnBack;
    private ImageButton photoFab;
    private EditText mFullName, mUsername, mWebsite, mBio, mEmail, mPhone;
    private RelativeLayout editFullName, editUsername, editWebsite;

    private ProgressBar progressBar;
    private ScrollView scrollView;
    private ImageView checkMark;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseMethods firebaseMethods;
    private String userID;

    //vars
    private UserSettings mUserSettings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        setContentView(R.layout.activity_edit_profile);

        firebaseMethods = new FirebaseMethods(mContext);

        if(savedInstanceState != null){
            setupFirebaseAuth();
        }else{
            setupFirebaseAuth();
        }

        toolbar = findViewById(R.id.toolbar);
        toolbar.setPadding(0, 0, 0, 0);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        scrollView = findViewById(R.id.scroll_view);
        scrollView.setVisibility(View.GONE);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        checkMark = findViewById(R.id.saveChanges);
        checkMark.setVisibility(View.GONE);

        profileImage = findViewById(R.id.profile_image);
        photoFab = findViewById(R.id.iv_camera);

        editFullName = findViewById(R.id.edit_full_name);
        mFullName = findViewById(R.id.full_name);

        editUsername = findViewById(R.id.edit_username);
        mUsername = findViewById(R.id.username);

        editWebsite = findViewById(R.id.edit_website);
        mWebsite = findViewById(R.id.web);

        mBio = findViewById(R.id.edit_bio);
        mEmail = findViewById(R.id.edit_email);
        mPhone = findViewById(R.id.edit_phone);

        mFullName.setCursorVisible(false);
        editFullName.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mFullName.setCursorVisible(true);
                    }
                }
        );

        editUsername.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                    }
                }
        );

        editWebsite.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                    }
                }
        );


        checkMark.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: attempting to save changes");
                        saveProfileSettings();
                    }
                }
        );

        profileImage.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }
        );

    }

    private void setIntentExtra(){

        //Intent intent = getIntent();

        //if(intent.hasExtra(getString(R.string.shared_image))){

            //String profile_photo_extra = intent.getStringExtra(getString(R.string.shared_image));
            //firebaseMethods = new FirebaseMethods(mContext);
            //firebaseMethods.uploadNewPhoto(getString(R.string.profile_photo), null, 0,
                   // profile_photo_extra, null, null);
        //}

    }
    private void onRequiredEditTextClicked(EditText editText, RelativeLayout layout){
        //InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        //editText.requestFocus();
        //imm.showSoftInput(editText, 1);
        editText.setCursorVisible(true);
        layout.setBackgroundResource(R.drawable.underline_edit_changed);
    }
    private void freezeOtherEdit(RelativeLayout layout){
        //editText.setCursorVisible(false);
        layout.setBackgroundResource(R.drawable.underline_edit);
    }

    private void setupProfileWidgets(UserSettings userSettings){

        mUserSettings = userSettings;

        UniversalImageLoader.setImage(userSettings.getUserAccountSettings().getProfile_photo()
                               , profileImage, null, "");

        mFullName.setText(userSettings.getUserAccountSettings().getDisplay_name());
        mUsername.setText(userSettings.getUserAccountSettings().getUsername());
        mWebsite.setText(userSettings.getUserAccountSettings().getWebsite());
        mBio.setText(userSettings.getUserAccountSettings().getBio());
        mEmail.setText(userSettings.getUser().getEmail());
        mPhone.setText(String.valueOf(userSettings.getUser().getPhone_number()));

        photoFab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SelectProfilePhotoDialog dialog = new SelectProfilePhotoDialog();
                        dialog.show(getSupportFragmentManager(), "dialog");
                    }
                }
        );
    }

    /**
     * Retrieving the data contained in the widgets and submits it to the database
     * Before doing so it checks to make sure the username chosen is unqiue
     */
    private void saveProfileSettings(){

        final String fullName = mFullName.getText().toString();
        final String username = mUsername.getText().toString();
        final String website = mWebsite.getText().toString();
        final String bio = mBio.getText().toString();
        final String email = mEmail.getText().toString();
        final long phone = Long.parseLong(mPhone.getText().toString());

        //case1: if the user made a change to their username
        if(!mUserSettings.getUser().getUsername().equals(username)){
            checkIfUsernameExists(username);
        }

        //case2: if the user made a change to their email
        if(!mUserSettings.getUser().getEmail().equals(email)){

            // Re-authenticate - confirm the password and email
            ConfirmPassword confirmPassword = new ConfirmPassword();
            confirmPassword.show(getSupportFragmentManager(), "ConfirmPasswordDialog");

        }

        /**
         *  change the rest of the settings that do not require uniqueness
         */

        if(!mUserSettings.getUserAccountSettings().getDisplay_name().equals(fullName)){
            //update display name
            firebaseMethods.updateUserSettings(fullName, null, null, 0);
        }

        if(!mUserSettings.getUserAccountSettings().getWebsite().equals(website)){
            //update website
            firebaseMethods.updateUserSettings(null, website, null, 0);
        }

        if(!mUserSettings.getUserAccountSettings().getBio().equals(bio)){
            //update bio
            firebaseMethods.updateUserSettings(null, null, bio, 0);
        }

        if(mUserSettings.getUser().getPhone_number() != phone){
            //update phoneNumber
            firebaseMethods.updateUserSettings(null, null, null, phone);
        }


    }

    /**
     * check if @param username already exists in the database
     * @param username
     */
    private void checkIfUsernameExists(final String username) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.username))
                .equalTo(username);

        query.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(!dataSnapshot.exists()){
                            //add the username
                            firebaseMethods.updateUsername(username);
                            Toast.makeText(mContext, "saved username", Toast.LENGTH_SHORT).show();
                        }

                        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                            if(singleSnapshot.exists()){
                                Log.d(TAG, "onDataChange: checkIfUsernameExists: FOUND_A_MATCH : " + singleSnapshot.getValue(User.class).getUsername());
                                Toast.makeText(mContext, "that username already exists", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
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


  /*  private void setProfileImage(){
        Log.d(TAG, "setProfileImage: setting profile image.");
       // String imgURL = "answersafrica.com/wp-content/uploads/2013/10/1522660141-3179-Vanessa-Mdee.jpg";
        UniversalImageLoader.setImage("", profileImage, null, "");
    }*/

    private void setupFirebaseAuth(){

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mRef = firebaseDatabase.getReference();
        userID = mAuth.getCurrentUser().getUid();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                if (currentUser != null){
                    // user is signed in

                    mRef.addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    setupProfileWidgets(firebaseMethods.getUserSettings(dataSnapshot));
                                    checkMark.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                    scrollView.setVisibility(View.VISIBLE);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            }
                    );

                }else{
                    // user is signed out
                }
            }
        };

        setIntentExtra();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    public void passConfirmPassword(String password) {
        // Get auth credentials from the user for re-authentication

        AuthCredential credential = EmailAuthProvider
                .getCredential(mAuth.getCurrentUser().getEmail(), password);

        // Prompt the user to re-provide their sign-in credentials
        mAuth.getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                
                                if(task.isSuccessful()) {
                                    Log.d(TAG, "onComplete: User re-authenticated");


                                    // check to see if the email is not already present in the database
                                    mAuth.fetchProvidersForEmail(mEmail.getText().toString()).addOnCompleteListener(
                                            new OnCompleteListener<ProviderQueryResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                                                    if (task.isSuccessful()) {

                                                        try {
                                                            if (task.getResult().getProviders().size() == 1) {
                                                                Log.d(TAG, "onComplete: that email is already in use");
                                                                Toast.makeText(mContext, "That email is already in use", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Log.d(TAG, "onComplete: That email is available");

                                                                // the email is available so update it
                                                                mAuth.getCurrentUser().updateEmail(mEmail.getText().toString())
                                                                        .addOnCompleteListener(
                                                                                new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            Log.d(TAG, "onComplete: user email address updated");
                                                                                            Toast.makeText(mContext, "email updated", Toast.LENGTH_SHORT).show();
                                                                                            firebaseMethods.updateEmail(mEmail.getText().toString());
                                                                                        }
                                                                                    }
                                                                                }
                                                                        );
                                                            }

                                                        } catch (NullPointerException e) {
                                                            Log.e(TAG, "onComplete: NullPointerException: " + e.getMessage());
                                                        }
                                                    }
                                                }
                                            }
                                    );
                                }else{
                                    Log.d(TAG, "onComplete: re-authenticated failed");
                                }
                            }
                        }
                );

    }
}
