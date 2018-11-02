package com.example.jay.shakunaku.Timeline_Posting;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.jay.shakunaku.R;
import com.example.jay.shakunaku.Utils.Permissions;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostingMediaFragment extends Fragment {

    private static final String TAG = "PostingMediaFragment";
    private static final int VERIFY_PERMISSIONS_REQUEST = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int GALLERY_REQUEST_CODE = 2;

    // widgets
    private TextView camera, gallery;

    public PostingMediaFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_posting_media, container, false);

        if(checkPermissionsArray(Permissions.PERMISSIONS)){

            onClickWidgets(view);

        }else{
            verifyPermissions(Permissions.PERMISSIONS);
        }

        onClickWidgets(view);
        return view;
    }

    public void onClickWidgets(View view){
        camera = view.findViewById(R.id.camera_text);
        camera.setSelected(false);
        camera.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                        camera.setTextColor(Color.parseColor("#E6392F"));
                        camera.setSelected(true);
                    }
                }
        );

        gallery = view.findViewById(R.id.gallery_text);
        gallery.setSelected(false);
        gallery.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent galleryIntent = new Intent(getContext(), GalleryActivity.class);
                        startActivityForResult(galleryIntent,GALLERY_REQUEST_CODE);
                        gallery.setTextColor(Color.parseColor("#E6392F"));
                        gallery.setSelected(true);
                    }
                }
        );

    }

    /**
     *
     * @param permissions
     */
    private void verifyPermissions(String[] permissions) {
        Log.d(TAG, "verifyPermissions: verifying permissions");

        ActivityCompat.requestPermissions(
                getActivity(),
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
    }

    /**
     * check an array of permission
     * @param permissions
     * @return
     */
    private boolean checkPermissionsArray(String[] permissions) {
        Log.d(TAG, "checkPermissionsArray: checking permission array");

        for(int i=0; i<permissions.length; i++){
            String check = permissions[i];
            if(!checkPermissions(check)){
                return false;
            }
        }

        return true;
    }

    /**
     *
     * @param permission
     * @return
     */
    private boolean checkPermissions(String permission) {
        Log.d(TAG, "checkPermissions: checking a permission : " + permission);

        int requestPermission = ActivityCompat.checkSelfPermission(getActivity(), permission);

        if(requestPermission != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermissions: permission was not granted");
            return false;
        }else{
            Log.d(TAG, "checkPermissions: permission was granted");
            return true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(camera.isSelected()) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                Log.d(TAG, "onActivityResult: done taking a photo");
                Log.d(TAG, "onActivityResult: attempting to navigate to final share screen");
                camera.setTextColor(Color.WHITE);

                try{
                    if(data.hasExtra("data")){
                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                        Intent shareIntent = new Intent(getContext(), ShareActivity.class);
                        shareIntent.putExtra(getString(R.string.shared_bitmap), bitmap);
                        startActivity(shareIntent);
                    }

                }catch (NullPointerException e){
                    Log.d(TAG, "onActivityResult: NullPointerException : " + e.getMessage());
                }

                //navigate to the final share screen to publish photo
            }
        }else{
            camera.setTextColor(Color.WHITE);
        }

        if(gallery.isSelected()){
            if(requestCode == GALLERY_REQUEST_CODE){
                gallery.setTextColor(Color.WHITE);
            }
        }

    }
}
