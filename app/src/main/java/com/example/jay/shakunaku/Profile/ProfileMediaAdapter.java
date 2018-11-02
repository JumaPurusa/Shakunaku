package com.example.jay.shakunaku.Profile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.example.jay.shakunaku.Listeners.OnRecyclerviewItemClickListener;
import com.example.jay.shakunaku.Models.Photos;
import com.example.jay.shakunaku.R;
import com.example.jay.shakunaku.Utils.ImageManipulation;
import com.example.jay.shakunaku.Utils.UniversalImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

public class ProfileMediaAdapter extends RecyclerView.Adapter<ProfileMediaAdapter.MyViewHolder> {

    private Context mContext;
    private List<Photos> listOfMedia;
    private OnRecyclerviewItemClickListener onRecyclerviewItemClickListener;

    public void setOnRecyclerviewItemClickListener(OnRecyclerviewItemClickListener onRecyclerviewItemClickListener) {
        this.onRecyclerviewItemClickListener = onRecyclerviewItemClickListener;
    }

    public ProfileMediaAdapter(Context context){

        this.mContext = context;


    }

    public void addMediaList(List<Photos> list){
        this.listOfMedia = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.single_profile_media_view, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        Photos photos = listOfMedia.get(position);

        //UniversalImageLoader.setImage(profileMedia.getImgURL(), holder.imageView, holder.mProgressBar, "");


        ImageLoader imageLoader = ImageLoader.getInstance();

        /*
        imageLoader.displayImage(profileMedia.getImgURL(), holder.imageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                holder.mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                holder.mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.mProgressBar.setVisibility(View.GONE);

            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                holder.mProgressBar.setVisibility(View.GONE);
            }
        });
        */

        ImageSize targetSize = new ImageSize(400, 200);
        imageLoader.loadImage(photos.getImage_path(), targetSize, new SimpleImageLoadingListener(){
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                holder.mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                holder.mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.mProgressBar.setVisibility(View.GONE);

                holder.imageView.setImageBitmap(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                holder.mProgressBar.setVisibility(View.GONE);
            }
        });


        /*
        Glide.with(mContext)
                .load(profileMedia.getImgURL().toString())
                .into(holder.imageView);
                */

        holder.itemView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRecyclerviewItemClickListener.itemClick(v, position);
                    }
                }
        );

    }

    @Override
    public int getItemCount() {
        return listOfMedia.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private RelativeLayout cardView;
        private ImageView imageView;
        private ProgressBar mProgressBar;

        public MyViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card_view);
            imageView = itemView.findViewById(R.id.media_image);
            mProgressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}