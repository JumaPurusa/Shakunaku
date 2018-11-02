package com.example.jay.shakunaku.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ImageManipulation {;

    private static final String TAG = "ImageManipulation";

    public static Bitmap getBitmap(String imgURL){
        File file = new File(imgURL);
        FileInputStream fileInputStream = null;
        Bitmap bitmap = null;

        try {
            fileInputStream = new FileInputStream(file);
            bitmap = BitmapFactory.decodeStream(fileInputStream);

        }catch(FileNotFoundException e){
            Log.d(TAG, "getBitmap: FileNotFoundException : " + e.getMessage());
        }finally{

            try{
                fileInputStream.close();
            }catch (IOException e){
                Log.d(TAG, "getBitmap: IOException : " + e.getMessage());
            }

        }

        return bitmap;
    }

    public static byte[] getBytesFromBitmap(Bitmap bm, int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }

    public static Bitmap scaleCropToFit(Bitmap original, int targetWidth, int targetHeight){
        //Need to scale the image, keeping the aspect ration first
        int width = original.getWidth();
        int height = original.getHeight();

        float widthScale = (float)targetWidth/(float)width;
        float heightScale = (float)targetHeight/(float)height;

        float scaledWidth;
        float scaledHeight;

        int startY = 0;
        int startX = 0;

        if(widthScale > heightScale){
            scaledWidth = targetWidth;
            scaledHeight = height * widthScale;
            //crop height by...
            startY = (int)((scaledHeight - targetHeight) / 2);
        }else{
            scaledHeight = targetHeight;
            scaledWidth = width * heightScale;
            //crop width by...
            startX = (int)((scaledWidth - targetWidth) / 2);
        }

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(original, (int)scaledWidth, (int)scaledHeight, true);

        Bitmap resizedBitmap = Bitmap.createBitmap(scaledBitmap, startX, startY, targetWidth, targetHeight);
        return resizedBitmap;
    }

}
