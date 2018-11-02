package com.example.jay.shakunaku.Timeline_Posting;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.jay.shakunaku.R;
import com.example.jay.shakunaku.Utils.FirebaseMethods;
import com.example.jay.shakunaku.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShareActivity extends AppCompatActivity {

    private static final String TAG = "ShareActivity";

    private Context mContext = ShareActivity.this;

    //Widgets
    private EditText caption;
    private ImageView image;
    private ProgressBar progressBar;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseMethods firebaseMethods;

    //vars
    private String mAppend = "file:/";
    private int imageCount = 0;
    private String imgURL;
    private Bitmap bitmap;
    private ProgressDialog progressDialog;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        firebaseMethods = new FirebaseMethods(mContext);

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(Color.BLACK);

        image = findViewById(R.id.image_share);
        caption = findViewById(R.id.post_caption);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        //caption.setInputType(InputType.TYPE_NULL);
        //caption.setCursorVisible(false);

        /*
        caption.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        v.requestFocus();
                        imm.showSoftInput(v, 0);

                        caption.setCursorVisible(true);
                        //caption.setCursorVisible(true);
                    }
                }
        );
       */


        setupFirebaseAuth();

        ImageView backArraw = findViewById(R.id.back_arrow);
        backArraw.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: closing the activity");
                        finish();
                    }
                }
        );



        setImage();

        final TextView share = findViewById(R.id.share_text);
        share.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: uploading the image to the database");
                        //upload the image to firebase

                        progressDialog = new ProgressDialog(mContext);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setMessage("Uploading ...");
                        progressDialog.setCancelable(false);

                        if(imgURL != null){
                            progressDialog.show();
                            firebaseMethods.uploadNewPhoto(getString(R.string.new_photo),
                                    caption.getText().toString(),imageCount, imgURL, null, progressDialog);
                        }else{
                            progressDialog.show();
                            firebaseMethods.uploadNewPhoto(getString(R.string.new_photo), caption.getText().toString(),
                                    imageCount, null,bitmap, progressDialog);
                        }

                    }
                }
        );


    }

    private void setImage(){
        Intent intent = getIntent();

        if(intent.hasExtra(getString(R.string.shared_image)) || intent.hasExtra(getString(R.string.shared_bitmap))){

            if(intent.hasExtra(getString(R.string.shared_image))){
                imgURL = intent.getStringExtra(getString(R.string.shared_image));
                // ImageView image = findViewById(R.id.image_share);
                UniversalImageLoader.setImage(imgURL, image, progressBar, mAppend);
            }else{

                bitmap = intent.getParcelableExtra(getString(R.string.shared_bitmap));
                image.setImageBitmap(bitmap);
            }
        }

    }

    private void setupFirebaseAuth(){

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mRef = firebaseDatabase.getReference();
        Log.d(TAG, "setupFirebaseAuth: imageCount : " + imageCount);

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

        mRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        imageCount = firebaseMethods.getImageCount(dataSnapshot);
                        Log.d(TAG, "onDataChange: imageCount : " + imageCount);
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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public void onStop() {
        super.onStop();

        if(mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
    }

}
