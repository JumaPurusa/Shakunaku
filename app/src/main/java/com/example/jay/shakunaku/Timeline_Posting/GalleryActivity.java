package com.example.jay.shakunaku.Timeline_Posting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.jay.shakunaku.Profile.EditProfileActivity;
import com.example.jay.shakunaku.Profile.ImageCropperActivity;
import com.example.jay.shakunaku.R;
import com.example.jay.shakunaku.Utils.FilePaths;
import com.example.jay.shakunaku.Utils.FileSearch;
import com.example.jay.shakunaku.Utils.FirebaseMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {
    private static final String TAG = "GalleryActivity";

    private Context mContext = GalleryActivity.this;
    //widgets
    private Toolbar toolbar;
    private ImageView backArrow, forwardArrow, selectedImage;
    private Spinner directorySpinner;
    private ProgressBar progressBar;
    private RecyclerView imageGridView;

    //vars
    private ArrayList<String> directories;
    private String mAppend = "file:/";
    private String mSelectedImage;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(Color.BLACK);

        toolbar = findViewById(R.id.gallery_toolbar);
        toolbar.setPadding(0, 0, 0,0 );
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        backArrow = findViewById(R.id.return_back);
        forwardArrow = findViewById(R.id.next_arrow);
        selectedImage = findViewById(R.id.select_image);
        directorySpinner = findViewById(R.id.spinner_directory);
        imageGridView = findViewById(R.id.grid_view);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        directorySpinner.setSelection(0,true);

        backArrow.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );
        
        forwardArrow.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: navigating to the final share screen");

                        if(isRootTask()){

                            Intent shareIntent = new Intent(mContext, ShareActivity.class);
                            shareIntent.putExtra(getString(R.string.shared_image), mSelectedImage);
                            startActivity(shareIntent);
                        }else{

                            Intent editProfileIntent = new Intent(mContext, ImageCropperActivity.class);
                            editProfileIntent.putExtra(getString(R.string.shared_image), mSelectedImage);
                            startActivity(editProfileIntent);
                            finish();
                        }
                    }
                }
        );

        init();
    }

    private boolean isRootTask(){
        if(getIntent().getFlags() == 0){
            return true;
        }else{
            return false;
        }
    }

    public void init(){
        directories = new ArrayList<>();

        FilePaths filePaths = new FilePaths();

        //check for other folders inside "/storage/emulated/0/Pictures"
        if(FileSearch.getDirectoryPaths(filePaths.PICTURES) != null){
            directories = FileSearch.getDirectoryPaths(filePaths.PICTURES);
        }

        directories.add(filePaths.CAMERA);
        directories.add(filePaths.DOWNLOAD);
        directories.add(filePaths.WHATSAPP_IMAGES);
        directories.add(filePaths.WHATSAPP_PROFILE);
        directories.add(filePaths.WHATSAPP_GIFS);

        ArrayList<String> directoryNames = new ArrayList<>();

        for(int i=0; i<directories.size(); i++){
            int index = directories.get(i).lastIndexOf("/");
            String names = directories.get(i).substring(index).replace("/", "");
            directoryNames.add(names);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(GalleryActivity.this,
                android.R.layout.simple_spinner_item, directoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        directorySpinner.setAdapter(adapter);

        directorySpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Log.d(TAG, "onItemSelected: selected: " + directories.get(position));

                        setupGridView(directories.get(position));
                        ((TextView)view).setTextColor(Color.WHITE);
                        ((TextView)view).setTextSize(19);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );

    }

    private void setupGridView(String selectedDirectory){
        Log.d(TAG, "setupGridView: directory chosen: " + selectedDirectory);

        final ArrayList<String> imgURLs = FileSearch.getFilePaths(selectedDirectory);

        //set the grid column width
        imageGridView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 4);
        imageGridView.setLayoutManager(gridLayoutManager);


        GridImageAdapter adapter = new GridImageAdapter(mContext, imgURLs);
        imageGridView.setAdapter(adapter);
        View v = directorySpinner.getSelectedView();
        ((TextView)v).setTextColor(Color.GRAY);

        //set the first image to be displayed when the activity view is inflated
        try{

            setImage(imgURLs.get(0), selectedImage, mAppend);
            mSelectedImage = imgURLs.get(0);

        }catch(IndexOutOfBoundsException e){
            Log.d(TAG, "setupGridView: IndexOutBoundsException : " + e.getMessage());
        }

        adapter.setOnRecyclerViewItemClickListener(
                new GridImageAdapter.OnRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Log.d(TAG, "onItemClick: selected image : " + imgURLs.get(position));
                        setImage(imgURLs.get(position), selectedImage, mAppend);
                        mSelectedImage = imgURLs.get(position);
                    }
                }
        );
    }

    private void setImage(String imgURL, ImageView image, String append){
        Log.d(TAG, "setImage: setting image");

        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(append + imgURL, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }


}
