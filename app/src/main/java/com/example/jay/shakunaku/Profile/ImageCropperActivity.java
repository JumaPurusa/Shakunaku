package com.example.jay.shakunaku.Profile;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
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

public class ImageCropperActivity extends AppCompatActivity {

    private static final String TAG = "ImageCropperActivity";
    private static final int CAMERA_REQUEST_CODE = 1;

    private Context mContext = ImageCropperActivity.this;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseMethods firebaseMethods;

    //widgets
    private ImageView image;
    private TextView next;
    private TextView cancel;

    //vars
    private String mAppend = "file:/";
    private String imgURL;
    private Bitmap bitmap;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(Color.BLACK);

        setContentView(R.layout.activity_image_cropper);

        firebaseMethods = new FirebaseMethods(mContext);

        image = findViewById(R.id.image_to_crop);
        next = findViewById(R.id.done);
        cancel = findViewById(R.id.cancel);

        setupFirebaseAuth();


        next.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(imgURL != null){
                            firebaseMethods.uploadNewPhoto(getString(R.string.profile_photo),  null,
                                    0, imgURL, null, null);
                        }else{
                            firebaseMethods.uploadNewPhoto(getString(R.string.profile_photo),
                                    null,0, null, bitmap, null);
                        }

                    }
                }
        );

        cancel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );

        if(isRootTask()){
            setImage();
        }else{

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }
    }

    private void setImage(){

        Intent intent = getIntent();
        imgURL = intent.getStringExtra(getString(R.string.shared_image));
        // ImageView image = findViewById(R.id.image_share);
        UniversalImageLoader.setImage(imgURL, image, null, mAppend);

    }

    private boolean isRootTask(){

        if(getIntent().getFlags() == 0){
            return true;
        }else{
            return false;
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST_CODE){

            try{

                if(data.hasExtra("data")){
                    bitmap = (Bitmap)data.getExtras().get("data");
                    image.setImageBitmap(bitmap);
                }
            }catch(NullPointerException e){
                Log.d(TAG, "onActivityResult: NullPointerException : " + e.getMessage());
                finish();
            }
        }
    }
}
