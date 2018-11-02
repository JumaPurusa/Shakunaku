package com.example.jay.shakunaku.Comments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jay.shakunaku.Models.Comment;
import com.example.jay.shakunaku.Models.Like;
import com.example.jay.shakunaku.Models.Photos;
import com.example.jay.shakunaku.Models.UserAccountSettings;
import com.example.jay.shakunaku.R;
import com.example.jay.shakunaku.Utils.FirebaseMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class CommentsActivity extends AppCompatActivity {

    private static final String TAG = "CommentsActivity";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mRef;

    //widgets
    private ImageView backArrow;
    private TextView commentTextNumber, toolbarTitle;
    private Toolbar commentsToolbar;

    //caption widgets
    private ImageView profileImage;
    private TextView profileUsername, photoCaption, commentPostedTime;

    //posting comments widgets
    private ImageView editProfilePhoto, sendCommentMark;
    private EditText mComment;

    //vars
    private Photos photo;
    private List<Comment> comments;
    private List<Comment> newCommentList;
    private RecyclerView commentRecyclerView;
    private CommentsListAdapter commentsListAdapter;
    private Context mContext = CommentsActivity.this;
    private UserAccountSettings userAccountSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        setContentView(R.layout.activity_comments);

        commentsToolbar = findViewById(R.id.comments_toolbar);
        commentsToolbar.setPadding(0, 0, 0, 0);
        setSupportActionBar(commentsToolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        backArrow = findViewById(R.id.close_comment);
        commentTextNumber = findViewById(R.id.number_of_comments);
        toolbarTitle = findViewById(R.id.toolbar_title);

        profileImage = findViewById(R.id.comment_profile_image);
        profileUsername = findViewById(R.id.comment_username);
        photoCaption = findViewById(R.id.comment);
        commentPostedTime = findViewById(R.id.comment_time_posted);
        editProfilePhoto = findViewById(R.id.edit_profile_photo);
        sendCommentMark = findViewById(R.id.post_sign);
        mComment = findViewById(R.id.posted_comment);

        comments = new ArrayList<>();

        backArrow.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        supportFinishAfterTransition();
                    }
                }
        );

        try{
            photo = getPhotoBundle();

        }catch (NullPointerException e){
            Log.d(TAG, "onCreate: NullPointerException : " + e.getMessage());
        }

        Log.d(TAG, "onCreate: Photo : " + photo);

        //Comment firstComment = new Comment();
        //firstComment.setComment(photo.getPhoto_caption());
        //firstComment.setUser_id(photo.getUser_id());
        //firstComment.setDate_created(photo.getDate_created());
        //comments.add(firstComment);

        commentRecyclerView = findViewById(R.id.comments_recycler_view);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        setupFirebaseAuth();
        setupCaption();
    }

    private void closeKeyboard(){
        View view = getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void addNewComment(String newComment){

        String newCommentID = mRef.push().getKey();

        Comment comment = new Comment();
        comment.setComment(newComment);
        comment.setUser_id(mAuth.getCurrentUser().getUid());
        comment.setDate_created(getTimeStamp(Calendar.getInstance().getTimeInMillis()));

        mRef.child(getString(R.string.dbname_photos))
                .child(photo.getPhoto_id())
                .child(getString(R.string.field_comments))
                .child(newCommentID)
                .setValue(comment);

        mRef.child(getString(R.string.dbname_user_photos))
                .child(photo.getUser_id())
                .child(photo.getPhoto_id())
                .child(getString(R.string.field_comments))
                .child(newCommentID)
                .setValue(comment);


    }

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

    private Photos getPhotoBundle(){
        Intent comingIntent = getIntent();
        return comingIntent.getParcelableExtra(getString(R.string.pass_to_comments));
    }

    private void setupCaption(){

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

                        try {
                            //setupWidgets();
                            ImageLoader imageLoader = ImageLoader.getInstance();
                            imageLoader.displayImage(userAccountSettings.getProfile_photo(), profileImage);


                            //imageLoader.displayImage(userAccountSettings.getProfile_photo(), editProfilePhoto);
                            //Glide.with(mContext)
                            //.load(userAccountSettings.getProfile_photo())
                            //.into(profileImage);

                            profileUsername.setText(userAccountSettings.getUsername());

                        }catch(NullPointerException e){
                            Log.d(TAG, "onDataChange: NullPointerException : " + e.getMessage());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();
        Query query2 = reference2.child(getString(R.string.dbname_user_account_settings))
                .orderByChild(getString(R.string.user_id))
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query2.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserAccountSettings settings = null;
                        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                            settings = singleSnapshot.getValue(UserAccountSettings.class);
                        }

                        try{

                            ImageLoader imageLoader = ImageLoader.getInstance();
                            imageLoader.displayImage(settings.getProfile_photo(), editProfilePhoto);

                        }catch(NullPointerException e){
                            Log.d(TAG, "onDataChange: NullPointerException : " + e.getMessage());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
        photoCaption.setText(photo.getPhoto_caption());
        commentPostedTime.setText(timestampConversion(getTimestampDifference(photo)));
    }

    private void setupWidgets(){
        commentsListAdapter = new CommentsListAdapter(mContext);

        if(comments.size() == 0){
            //toolbarTitle.setText(getString(R.string.comments));
            commentTextNumber.setText("");

            commentsListAdapter.addListItems(newCommentList);

            commentRecyclerView.setAdapter(commentsListAdapter);

            sendCommentMark.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(!TextUtils.isEmpty(mComment.getText().toString())){
                                addNewComment(mComment.getText().toString());

                                mComment.setText("");

                                closeKeyboard();
                            }else{
                                Toast.makeText(mContext, "You can't post a blank comment", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
            );

        }else if(comments.size() == 1){
            toolbarTitle.setText("Comment");
            commentTextNumber.setVisibility(View.VISIBLE);
            commentTextNumber.setText(String.valueOf(comments.size()-1));

            commentsListAdapter.addListItems(newCommentList);

            commentRecyclerView.setAdapter(commentsListAdapter);

            sendCommentMark.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(!TextUtils.isEmpty(mComment.getText().toString())){
                                addNewComment(mComment.getText().toString());

                                mComment.setText("");

                                closeKeyboard();
                            }else{
                                Toast.makeText(mContext, "You can't post a blank comment", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
            );
        }else{
            commentTextNumber.setVisibility(View.VISIBLE);
            commentTextNumber.setText(String.valueOf(comments.size()));

            commentsListAdapter.addListItems(newCommentList);

            commentRecyclerView.setAdapter(commentsListAdapter);

            sendCommentMark.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(!TextUtils.isEmpty(mComment.getText().toString())){
                                addNewComment(mComment.getText().toString());

                                mComment.setText("");

                                closeKeyboard();
                            }else{
                                Toast.makeText(mContext, "You can't post a blank comment", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
            );
        }





    }

    private void setupFirebaseAuth() {

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mRef = firebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                if (currentUser != null) {
                    // user is signed in
                    Log.d(TAG, "onAuthStateChanged: signed_in: " + currentUser.getUid());


                } else {
                    // user is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };


        Query query = mRef.child(getString(R.string.dbname_photos))
                .orderByChild(getString(R.string.field_photo_id))
                .equalTo(photo.getPhoto_id());

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

                            comments.clear();
                            for(DataSnapshot snapshot : singleSnapshot.child(getString(R.string.field_comments)).getChildren()){

                                Comment comment = new Comment();
                                comment.setComment(snapshot.getValue(Comment.class).getComment());
                                comment.setUser_id(snapshot.getValue(Comment.class).getUser_id());
                                comment.setDate_created(snapshot.getValue(Comment.class).getDate_created());

                                comments.add(comment);
                            }

                            newCommentList = new ArrayList<>();

                            for(int i=(comments.size()-1); i>=0; i--){
                                newCommentList.add(comments.get(i));
                            }

                            newPhoto.setComments(newCommentList);

                            photo = newPhoto;

                            if(photo.getComments().size() == 0){
                                comments.clear();
                                setupWidgets();
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );


         mRef.child(getString(R.string.dbname_photos))
        .child(photo.getPhoto_id())
        .child(getString(R.string.field_comments))
        .addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        Query query = mRef.child(getString(R.string.dbname_photos))
                                .orderByChild(getString(R.string.field_photo_id))
                                .equalTo(photo.getPhoto_id());

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

                                            comments.clear();
                                            for(DataSnapshot snapshot : singleSnapshot.child(getString(R.string.field_comments)).getChildren()){

                                                Comment comment = new Comment();
                                                comment.setComment(snapshot.getValue(Comment.class).getComment());
                                                comment.setUser_id(snapshot.getValue(Comment.class).getUser_id());
                                                comment.setDate_created(snapshot.getValue(Comment.class).getDate_created());

                                                comments.add(comment);
                                            }

                                            newCommentList = new ArrayList<>();

                                            for(int i=(comments.size()-1); i>=0; i--){
                                                newCommentList.add(comments.get(i));
                                            }

                                            newPhoto.setComments(newCommentList);

                                            photo = newPhoto;

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
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

    public long getTimestampDifference(Photos photo){

        long difference;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date today = c.getTime();
        sdf.format(today);
        String commentTimestamp = photo.getDate_created();
        Date timestamp;
        try {
            timestamp = sdf.parse(commentTimestamp);
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
                timeText = newTime + " sec ago";
            }else{
                timeText = newTime + " secs ago";
            }

        }else{
            newTime = time / 1000 / 60;
            if(newTime < 60){

                if(newTime == 1){
                    timeText = newTime + " min ago";
                }else{
                    timeText = newTime + " mins ago";
                }

            }else{
                newTime = time /1000 / 60 / 60;
                if(newTime < 24){

                    if(newTime == 1){
                        timeText = newTime + " hr ago";
                    }else{
                        timeText = newTime + " hrs ago";
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
                    }*/

                    if(newTime == 1){
                        timeText = newTime + " day ago";
                    }else{
                        timeText = newTime + " days ago";
                    }


                }
            }
        }

        return timeText;
    }

}
