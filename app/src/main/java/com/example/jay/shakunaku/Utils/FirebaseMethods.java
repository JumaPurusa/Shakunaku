package com.example.jay.shakunaku.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jay.shakunaku.Activities.InitialAccountSettings;
import com.example.jay.shakunaku.ContainerActivity;

import com.example.jay.shakunaku.Models.Photos;
import com.example.jay.shakunaku.Models.PostView;
import com.example.jay.shakunaku.Models.User;
import com.example.jay.shakunaku.Models.UserAccountSettings;
import com.example.jay.shakunaku.Models.UserSettings;
import com.example.jay.shakunaku.Profile.EditProfileActivity;
import com.example.jay.shakunaku.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static java.security.AccessController.getContext;

public class FirebaseMethods {
    private static final String TAG = "FirebaseMethods";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String userID;

    private Context mContext;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private StorageReference storageReference;

    //vars
    private double progress = 0.0;
    private double mProgressUpload = 0.0;
    private Uri downloadUri;

    public FirebaseMethods(Context context){

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        mContext = context;

        if(mAuth.getCurrentUser() != null){

            userID = mAuth.getCurrentUser().getUid();

        }
    }

    public void uploadNewPhoto(String photoType, final String photo_caption,
                               int imageCount, final String imgURL, Bitmap bitmap, final ProgressDialog progressDialog){
        Log.d(TAG, "uploadNewPhoto: attempting to upload photo ");

        FilePaths filePaths = new FilePaths();

        //case1: uploading new photo
        if(mContext.getString(R.string.new_photo).equals(photoType)){
            Log.d(TAG, "uploadNewPhoto: adding new photo");

            final StorageReference mStorageRef = storageReference.child(filePaths.FIREBASE_IMAGE_STORAGE
                                            + "/" + userID + "/photo" + (imageCount + 1));
            // convert image url to bitmap
            if(bitmap == null){
                bitmap = ImageManipulation.getBitmap(imgURL);
            }

            //before sending to the storage should be changed to byte array
            //so as to get number of bytes transferred so as to choose or determine the quality of image
            byte[] bytes = ImageManipulation.getBytesFromBitmap(bitmap, 100);

            UploadTask uploadTask = mStorageRef.putBytes(bytes);


            final Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                     if(!task.isSuccessful()){
                         String errorMessage = task.getException().getMessage();
                         Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
                     }

                     return mStorageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if(task.isSuccessful()){

                        progressDialog.dismiss();
                    }else{
                        Toast.makeText(mContext, "upload failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnSuccessListener(
                    new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            downloadUri = uri;

                            addNewPhotoToDatabase(downloadUri, photo_caption);

                            //navigate to the main feed so as to see the photo
                            Intent mainFeedIntent = new Intent(mContext, ContainerActivity.class);
                            mainFeedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            mContext.startActivity(mainFeedIntent);
                            ((AppCompatActivity)mContext).finish();
                        }
                    }
            );


            uploadTask.addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d(TAG, "onSuccess: uploading is successful");

                            /*
                            Intent mainFeedIntent = new Intent(mContext, ContainerActivity.class);
                            //mainFeedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            mContext.startActivity(mainFeedIntent);
                            ((AppCompatActivity)mContext).finish();

                            mStorageRef.getDownloadUrl().addOnSuccessListener(
                                    new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.d(TAG, "onSuccess: uri : " + uri);

                                            //add photo properties to the photos node and user_photos node
                                            addNewPhotoToDatabase(uri, caption);

                                            //navigate to the main feed so as to see the photo

                                        }
                                    }
                            );
                            */



                        }
                    }
            ).addOnFailureListener(
                    new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: uploading failed");
                            Toast.makeText(mContext, "uploading failed", Toast.LENGTH_SHORT).show();
                        }
                    }
            ).addOnProgressListener(
                    new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            progress = (100) * taskSnapshot.getTotalByteCount()/taskSnapshot.getTotalByteCount();

                            if(progress > mProgressUpload){
                                //String progressTextDisplay = String.format("%d", (int)progress) + "%";
                                mProgressUpload = progress;
                            }
                        }
                    }
            );
        }

        //case2: uploading profile photo
        if(mContext.getString(R.string.profile_photo).equals(photoType)){
            Log.d(TAG, "uploadNewPhoto: adding new profile photo");

            final StorageReference mStoregeRef = storageReference.child(filePaths.FIREBASE_IMAGE_STORAGE
                                           + "/" + userID + "/profilePhoto");

            if(bitmap == null){
                bitmap = ImageManipulation.getBitmap(imgURL);
            }

            byte[] bytes = ImageManipulation.getBytesFromBitmap(bitmap, 100);

            UploadTask uploadTask = mStoregeRef.putBytes(bytes);

            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if(!task.isSuccessful()){
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                    return mStoregeRef.getDownloadUrl();
                }
            }).addOnCompleteListener(
                    new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            if(task.isSuccessful()){
                                //downloadUri = task.getResult();

                                //addProfilePhotoToDatabase(downloadUri.toString());

                                //Intent editActivity = new Intent(mContext, EditProfileActivity.class);
                                //mContext.startActivity(editActivity);
                                //((AppCompatActivity)mContext).finish();
                            }
                        }
                    }
            ).addOnSuccessListener(
                    new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            downloadUri = uri;
                            addProfilePhotoToDatabase(downloadUri.toString());

                            Intent editIntent = new Intent(mContext, EditProfileActivity.class);
                            //editIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            mContext.startActivity(editIntent);
                            ((AppCompatActivity)mContext).finish();

                        }
                    }
            );

        }

    }

    /**
     *
     * @param uri
     */
    private void addProfilePhotoToDatabase(String uri) {
        Log.d(TAG, "addProfilePhotoToDatabase: adding profile photo using the uri string : " + uri);

        mRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .child(mContext.getString(R.string.profile_photo))
                .setValue(uri);
    }

    /**
     *
     * @return
     */
    private String getTimeStamp(long timeInMill){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date(timeInMill));
            return currentDateTime;
        }catch (Exception e){
            Log.d(TAG, "getTimeStamp: Exception : " + e.getMessage());
        }

        return null;
    }

    private void addNewPhotoToDatabase(Uri downloadUri, String photo_caption){
        Log.d(TAG, "addNewPhotoToDatabase: adding to the database");

        Log.d(TAG, "addNewPhotoToDatabase: download uri : " + downloadUri.toString());

        Photos photos = new Photos();

        photos.setPhoto_caption(photo_caption);
        photos.setDate_created(getTimeStamp(System.currentTimeMillis()));
        photos.setImage_path(downloadUri.toString());

        String photoID = mRef.child(mContext.getString(R.string.dbname_photos)).push().getKey();
        photos.setPhoto_id(photoID);

        photos.setUser_id(userID);
        //photos.setTags(StringManipulation.getTags(caption));

        //photo and its properties to the photos node
        mRef.child(mContext.getString(R.string.dbname_photos)).child(photoID).setValue(photos);

        //photo and its properties to the user_photos node
        mRef.child(mContext.getString(R.string.dbname_user_photos)).child(userID).child(photoID).setValue(photos);
    }

    /**
     * iterating in the user_photos in specific user_id to get the number of images such user has
     * @param dataSnapshot
     * @return
     */
    public int getImageCount(DataSnapshot dataSnapshot){
        Log.d(TAG, "getImageCount: counting number of image in the database for user_photo");
        int count = 0;
        for(DataSnapshot ds : dataSnapshot.child(mContext.getString(R.string.dbname_user_photos))
                .child(userID).getChildren()){
            count++;
        }

        return count;
    }
    /**
     *
     * @param username
     */
    public void updateUsername(String username){
        Log.d(TAG, "updateUsername: updating username to : " + username);

        mRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.username))
                .setValue(StringManipulation.condenseUsername(username));

        mRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .child(mContext.getString(R.string.username))
                .setValue(StringManipulation.condenseUsername(username));
    }

    public void updateEmail(String email){
        Log.d(TAG, "updateEmail: updating email to : " + email);
        mRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.email))
                .setValue(email);
    }

    /**
     * update 'user_account_settings' node for the current
     * @param fullname
     * @param website
     * @param bio
     * @param phone
     */
    public void updateUserSettings(String fullname, String website, String bio, long phone){
        Log.d(TAG, "updateUserAccountSettings: updating userSettings");
        
        if(fullname != null){
            mRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.display_name))
                    .setValue(fullname);
        }

        if(website != null){
            mRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.website))
                    .setValue(website);
        }

        if(bio != null){
            mRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.bio))
                    .setValue(bio);
        }

        if(phone != 0){
            mRef.child(mContext.getString(R.string.dbname_users))
                    .child(userID)
                    .child(mContext.getString(R.string.phone_number))
                    .setValue(phone);
        }
    }
    /*
    public boolean checkIfUsernameExists(String username, DataSnapshot dataSnapshot){
        Log.d(TAG, "checkIfEmailExists: checking if " + username + " already exits");

        User user = new User();

        for(DataSnapshot ds: dataSnapshot.child(userID).getChildren()){
            Log.d(TAG, "checkIfEmailExists: datasnapshot: " + ds);

            user.setUsername(ds.getValue(User.class).getUsername());
            Log.d(TAG, "checkIfEmailExists: email: " + user.getUsername());

            if (StringManipulation.expandUsername(user.getUsername()).equals(username)){
                Log.d(TAG, "checkIfEmailExists:  FOUND A MATCH " + user.getUsername());
                return true;
            }
        }
        return false;
    }*/
    /**
     *
     * @param email
     * @param password
     * @param progressDialog
     */
    public void registerNewWithEmailAndPassword(final String email, String password, final ProgressDialog progressDialog){

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            // send email verification
                            FirebaseUser user = mAuth.getCurrentUser();
                            sendEmailVerification(user);
                            userID = user.getUid();

                            /*
                            if(user.isEmailVerified()){
                                progressDialog.dismiss();
                                Intent initialSettings = new Intent(mContext, InitialAccountSettings.class);
                                mContext.startActivity(initialSettings);
                                ((AppCompatActivity)mContext).finish();
                            }
                            */
                            progressDialog.dismiss();

                        }else{

                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                    }
                }
        );
    }

    /**
     *
     * @param user
     */
    public void sendEmailVerification(FirebaseUser user){

        //FirebaseUser currentUser = mAuth.getCurrentUser();

        if(user != null){

            user.sendEmailVerification().addOnCompleteListener(
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                            }else{
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
            );
        }
    }
    /**
     * Add information to the user node
     * Add information to the user_account_settings node
     * @param email
     * @param username
     * @param bio
     * @param website
     * @param profile_photo
     */
    public void addNewUser(String email, String fullname, String username, String bio, String website, String profile_photo){

        User user = new User(userID, 1, email, username);

        mRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .setValue(user);

        UserAccountSettings settings = new UserAccountSettings(
                bio,
                fullname,
                profile_photo,
                username,
                website,
                userID
        );

        mRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .setValue(settings);
        
    }

    public UserSettings getUserSettings(DataSnapshot dataSnapshot){
        Log.d(TAG, "getUserAccountSettings: retrieving user account settings from the firebase");

        UserAccountSettings settings = new UserAccountSettings();
        User user = new User();

        for(DataSnapshot ds : dataSnapshot.getChildren()){
            Log.d(TAG, "getUserAccountSettings: " + ds);

            if(ds.getKey().equals(mContext.getString(R.string.dbname_user_account_settings))){
                Log.d(TAG, "getUserSettings: datasnapshot: " + ds);

                try{

                    settings.setBio(
                            ds.child(userID)
                            .getValue(UserAccountSettings.class)
                            .getBio()
                    );

                    settings.setDisplay_name(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getDisplay_name()
                    );

                    settings.setProfile_photo(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getProfile_photo()
                    );

                    settings.setUsername(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getUsername()
                    );

                    settings.setWebsite(
                            ds.child(userID)
                            .getValue(UserAccountSettings.class)
                            .getWebsite()
                    );

                    Log.d(TAG, "getUserAccountSettings: retrieved user_account_settings_information: " + settings.toString() );

                }catch(NullPointerException e){
                    Log.d(TAG, "getUserAccountSettings: NullPointerException: " + e.getMessage());
                }

            }

            if(ds.getKey().equals(mContext.getString(R.string.dbname_users))){
                Log.d(TAG, "getUserAccountSettings: retrieving user node : " + ds);

                try{

                    user.setUser_id(
                            ds.child(userID)
                            .getValue(User.class)
                            .getUser_id()
                    );

                    user.setEmail(
                            ds.child(userID)
                                    .getValue(User.class)
                                    .getEmail()
                    );

                    user.setPhone_number(
                            ds.child(userID)
                                    .getValue(User.class)
                                    .getPhone_number()
                    );

                    user.setUsername(
                            ds.child(userID)
                                    .getValue(User.class)
                                    .getUsername()
                    );

                    Log.d(TAG, "getUserSettings: retrieved users information: " + user.toString());

                }catch(NullPointerException e){
                    Log.d(TAG, "getUserAccountSettings: " + e.getMessage());
                }
            }
        }

        return new UserSettings(settings, user);
    }


}
