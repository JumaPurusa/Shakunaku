package com.example.jay.shakunaku;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jay.shakunaku.Comments.CommentsActivity;
import com.example.jay.shakunaku.Models.Comment;
import com.example.jay.shakunaku.Models.Like;
import com.example.jay.shakunaku.Models.Photos;
import com.example.jay.shakunaku.Models.User;
import com.example.jay.shakunaku.Models.UserAccountSettings;
import com.example.jay.shakunaku.Models.UserSettings;
import com.example.jay.shakunaku.Utils.FirebaseMethods;
import com.example.jay.shakunaku.Utils.Heart;
import com.example.jay.shakunaku.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostAcvitity extends AppCompatActivity {

    private static final String TAG = "PostAcvitity";

    private Context mContext = PostAcvitity.this;
    private Toolbar postToolbar;
    private ImageView backArrow, userPostImage,postImage, whiteHeart, redHeart, commentImage, ellipses;
    private TextView postUsername, postCaption, timeDiff, likes, commentNoText;
    private ProgressBar progressBar;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseMethods firebaseMethods;

    //vars
    private GestureDetector mGestureDetector;
    private Heart heart;
    private Photos photo;
    int numberOflikes = 0;
    private StringBuilder mUsers;
    private boolean likedByCurrentUser;
    private UserAccountSettings userAccountSettings;
    private User mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        Log.d(TAG, "onCreate: photo details : " + photo);

        setContentView(R.layout.activity_post_acvitity);

        postToolbar = findViewById(R.id.toolbar);
        postToolbar.setPadding(0, 0, 0, 0);

        firebaseMethods = new FirebaseMethods(mContext);

        backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        supportFinishAfterTransition();
                    }
                }
        );

        postImage = findViewById(R.id.post_image);
        progressBar = findViewById(R.id.progressBar);
        userPostImage = findViewById(R.id.profile_image);
        postUsername = findViewById(R.id.post_username);
        postCaption = findViewById(R.id.post_caption);
        timeDiff = findViewById(R.id.time_ag0);
        mGestureDetector = new GestureDetector(mContext, new GestureListener());
        whiteHeart = findViewById(R.id.white_heart);
        redHeart = findViewById(R.id.red_heart);
        heart = new Heart(whiteHeart, redHeart);
        likes = findViewById(R.id.text_likes);
        commentImage = findViewById(R.id.post_comments);
        commentNoText = findViewById(R.id.text_comments);

        //redHeart.setVisibility(View.GONE);
        //whiteHeart.setVisibility(View.VISIBLE);

        try{


            UniversalImageLoader.setImage(getPhotoFromBundle().getImage_path(), postImage, progressBar, "");

            setupFirebaseAuth();


        }catch (NullPointerException e){
            Log.d(TAG, "onCreate: NullPointerException : " + e.getMessage());
        }


        //testToggle();

    }

    private void getPhotoPostInformation(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_user_account_settings))
                .orderByChild(getString(R.string.user_id))
                .equalTo(photo.getUser_id());

        query.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                            userAccountSettings = singleSnapshot.getValue(UserAccountSettings.class);
                        }

                        //setupWidgets();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

    private Photos getPhotoFromBundle(){

        Intent inComingIntent = getIntent();
        if(inComingIntent.hasExtra(getString(R.string.post_info))){
            return photo = inComingIntent.getParcelableExtra(getString(R.string.post_info));
        }else{
            return null;
        }

    }

    private void setupWidgets(){

        try {
            timeDiff.setText(timestampConversion(getTimestampDifference(photo)));

            Glide.with(mContext)
                    .load(userAccountSettings.getProfile_photo())
                    .into(userPostImage);

            postUsername.setText(userAccountSettings.getUsername());

            if(!TextUtils.isEmpty(photo.getPhoto_caption())){
                postCaption.setVisibility(View.VISIBLE);
                postCaption.setText(photo.getPhoto_caption());
            }else{
                postCaption.setVisibility(View.GONE);
            }


            if(photo.getComments().size() > 0){
                commentNoText.setText(String.valueOf(photo.getComments().size()));
            }else{
                commentNoText.setText("");
            }

            commentImage.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent commentsIntent = new Intent(mContext, CommentsActivity.class);
                            commentsIntent.putExtra(getString(R.string.pass_to_comments), photo);
                            startActivity(commentsIntent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }
                    }
            );


            if (numberOflikes != 0) {
                likes.setText(String.valueOf(numberOflikes));
            } else {
                likes.setText("");
            }


            if (likedByCurrentUser) {
                redHeart.setVisibility(View.VISIBLE);
                whiteHeart.setVisibility(View.GONE);

                redHeart.setOnTouchListener(
                        new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                return mGestureDetector.onTouchEvent(event);
                            }
                        }
                );
            }

            if (!likedByCurrentUser) {
                whiteHeart.setVisibility(View.VISIBLE);
                redHeart.setVisibility(View.GONE);

                whiteHeart.setOnTouchListener(
                        new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                return mGestureDetector.onTouchEvent(event);
                            }
                        }
                );
            }

        }catch (NullPointerException e){
            Log.d(TAG, "setupWidgets: NullPointerException : " + e.getMessage());
        }catch (IllegalArgumentException e){
            Log.d(TAG, "setupWidgets: IllegalArgumentsException : " + e.getMessage());
        }

    }

    public long getTimestampDifference(Photos photos){

        long difference;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date today = c.getTime();
        sdf.format(today);
        String photoTimestamp = photos.getDate_created();
        Date timestamp;
        try {
            timestamp = sdf.parse(photoTimestamp);
            difference = (Math.round(((today.getTime()-timestamp.getTime()))));
        } catch (ParseException e) {
            Log.d(TAG, "getTimestampDifference: ParseException : " + e.getMessage());
            difference = 0;
        }

        return difference;
    }

    public String timestampConversion(long time){
        String timeText = "";

        long newTime = time / 1000;
        if(newTime < 60){
            if(newTime == 1){
                timeText = newTime + "s ago";
            }else{
                timeText = newTime + "s ago";
            }

        }else{
            newTime = time / 1000 / 60;
            if(newTime < 60){

                if(newTime == 1){
                    timeText = newTime + "m ago";
                }else{
                    timeText = newTime + "m ago";
                }

            }else{
                newTime = time /1000 / 60 / 60;
                if(newTime < 24){

                    if(newTime == 1){
                        timeText = newTime + "h ago";
                    }else{
                        timeText = newTime + "h ago";
                    }

                }else{
                    newTime = time / 1000 / 60 / 60 / 24;

                    /*
                    if(newTime < 7){

                        if(newTime == 1){
                            timeText = newTime + " day ago";
                        }else{
                            timeText = newTime + " days ago";
                        }

                    }else{
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        Date date = new Date(newTime);
                        sdf.format(date);
                        timeText = String.valueOf(date);
                    }
                    */

                    if(newTime == 1){
                        timeText = newTime + "d ago";
                    }else{
                        timeText = newTime + "d ago";
                    }
                }
            }
        }

        return timeText;
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener{

        @Override
        public boolean onDown(MotionEvent e) {
             return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            //heart.toggeLike();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child(getString(R.string.dbname_photos))
                    .child(photo.getPhoto_id())
                    .child(getString(R.string.field_likes));

            query.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d(TAG, "onDataChange: all likes : " + dataSnapshot.getChildren());

                            //case1: the current user has liked the post
                            for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                                if(likedByCurrentUser && singleSnapshot.getValue(Like.class).getUser_id().equals(
                                        mAuth.getCurrentUser().getUid()
                                )){

                                    String keyID = singleSnapshot.getKey();
                                    mRef.child(getString(R.string.dbname_photos))
                                            .child(photo.getPhoto_id())
                                            .child(getString(R.string.field_likes))
                                            .child(keyID)
                                            .removeValue();

                                    mRef.child(getString(R.string.dbname_user_photos))
                                            .child(mAuth.getCurrentUser().getUid())
                                            .child(photo.getPhoto_id())
                                            .child(getString(R.string.field_likes))
                                            .child(keyID)
                                            .removeValue();


                                    heart.toggeLike();
                                    getAllTheLikes();

                                }else if(!likedByCurrentUser){
                                    addNewLike();
                                    break;
                                }
                            }

                            //case2: the current user has not liked the post

                            if(!dataSnapshot.exists()){
                                addNewLike();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }
            );
            return true;
        }

    }

    private void addNewLike(){
        Log.d(TAG, "addNewLike: adding the new like to the photo");

        String newKeyID = mRef.push().getKey();

        Like like = new Like();
        like.setUser_id(mAuth.getCurrentUser().getUid());

        mRef.child(getString(R.string.dbname_photos))
                .child(photo.getPhoto_id())
                .child(getString(R.string.field_likes))
                .child(newKeyID)
                .setValue(like);

        mRef.child(getString(R.string.dbname_user_photos))
                .child(photo.getUser_id())
                .child(photo.getPhoto_id())
                .child(getString(R.string.field_likes))
                .child(newKeyID)
                .setValue(like);

        heart.toggeLike();
        getAllTheLikes();

    }


    private void getCurrentUser(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.user_id))
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                            mUser = singleSnapshot.getValue(User.class);
                        }

                        getAllTheLikes();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

    }

    private void getAllTheLikes() {

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_photos))
                .child(photo.getPhoto_id())
                .child(getString(R.string.field_likes));

        query.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: Likes : " + dataSnapshot.getChildren());
                        mUsers = new StringBuilder();
                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                            Query newQuery = reference.child(getString(R.string.dbname_users))
                                    .orderByChild(getString(R.string.user_id))
                                    .equalTo(singleSnapshot.getValue(Like.class).getUser_id());

                            newQuery.addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                mUsers.append(snapshot.getValue(User.class).getUsername());
                                                mUsers.append(",");
                                            }

                                            String[] splitUsers = mUsers.toString().split(",");

                                            numberOflikes = splitUsers.length;
                                            Log.d(TAG, "onDataChange: number of users : " + splitUsers.length);

                                            Log.d(TAG, "onDataChange: current username : " + userAccountSettings.getUsername());
                                            if (mUsers.toString().contains(mUser.getUsername() + ",")) {
                                                likedByCurrentUser = true;
                                            } else {
                                                likedByCurrentUser = false;
                                            }

                                            setupWidgets();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    }
                            );
                        }

                        if (!dataSnapshot.exists()) {
                            numberOflikes = 0;
                            likedByCurrentUser = false;
                            setupWidgets();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

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

        String photo_id = getPhotoFromBundle().getPhoto_id();
        Query query = FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbname_photos))
                .orderByChild(getString(R.string.field_photo_id))
                .equalTo(photo_id);

        query.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: photo details : " + dataSnapshot.getChildren());
                        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                            Photos newPhoto = new Photos();
                            Map<String, Object> objectMap = (HashMap<String, Object>)singleSnapshot.getValue();
                            newPhoto.setPhoto_caption(objectMap.get(getString(R.string.field_photo_caption)).toString());
                            newPhoto.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                            newPhoto.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());
                            newPhoto.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                            newPhoto.setUser_id(objectMap.get(getString(R.string.user_id)).toString());

                            List<Comment> comments = new ArrayList<>();
                            for(DataSnapshot snapshot : singleSnapshot.child(getString(R.string.field_comments)).getChildren()){

                                Comment comment = new Comment();
                                comment.setComment(snapshot.getValue(Comment.class).getComment());
                                comment.setUser_id(snapshot.getValue(Comment.class).getUser_id());
                                comment.setDate_created(snapshot.getValue(Comment.class).getDate_created());

                                comments.add(comment);
                            }

                            if(comments != null){
                                Collections.sort(comments, new Comparator<Comment>() {
                                    @Override
                                    public int compare(Comment o1, Comment o2) {
                                        return o2.getDate_created().compareTo(o1.getDate_created());
                                    }
                                });

                                newPhoto.setComments(comments);
                            }


                            photo = newPhoto;

                            getPhotoPostInformation();
                            getCurrentUser();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );


    }

}
