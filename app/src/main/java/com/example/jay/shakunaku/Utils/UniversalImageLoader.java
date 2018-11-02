package com.example.jay.shakunaku.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.jay.shakunaku.R;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.ByteArrayOutputStream;

import static android.support.constraint.Constraints.TAG;

public class UniversalImageLoader {

    private static final int defaultImage = 0;
    private Context mContext;

    public UniversalImageLoader(Context mContext) {
        this.mContext = mContext;
    }

    public ImageLoaderConfiguration getConfig(){

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(defaultImage)
                .showImageForEmptyUri(defaultImage)
                .considerExifParams(true)
                .showImageOnFail(defaultImage)
                .cacheOnDisk(true).cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(mContext)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .diskCacheSize(100 * 1024 * 1024)
                .build();

        return configuration;
    }

    /**
     * this method can be used to set images that are static. It can't be used if the images
     * are being changed in the fragments/Activity - OR if they are being set in a list or
     * grid view
     * @param imgURL
     * @param image
     * @param mProgressBar
     * @param append
     */
    public static void setImage(String imgURL, final ImageView image, final ProgressBar mProgressBar, String append){

        final ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(append + imgURL, image,  new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if(mProgressBar != null){
                    mProgressBar.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                if(mProgressBar != null){
                    mProgressBar.setVisibility(View.GONE);
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                if(mProgressBar != null){
                    mProgressBar.setVisibility(View.GONE);
                }


                //ByteArrayOutputStream baos = new ByteArrayOutputStream();
                //loadedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                //byte[] byteArray = baos.toByteArray();


                //Bitmap thumbnail = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeByteArray(byteArray,0, byteArray.length),400,200);
                //Matrix matrix = new Matrix();
                //Bitmap bmnThumbnail = Bitmap.createBitmap(thumbnail, 0,0, thumbnail.getWidth(), thumbnail.getHeight(), matrix, true);
                //image.setImageBitmap(bmnThumbnail);

                /*
                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(itemModels.get(position).getVideoUrl().toString(),
                        MediaStore.Images.Thumbnails.MINI_KIND);
                Matrix matrix = new Matrix();
                Bitmap bmThumbnail = Bitmap.createBitmap(thumb, 0, 0,
                        thumb.getWidth(), thumb.getHeight(), matrix, true)
                        */

            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

                if(mProgressBar != null){
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    public static int pow2Ceil(int number) {
        return 1 << -(Integer.numberOfLeadingZeros(number) + 1); // is equivalent to:
        // return Integer.rotateRight(1, Integer.numberOfLeadingZeros(number) + 1);
    }
}
