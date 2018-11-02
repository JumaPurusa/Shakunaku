package com.example.jay.shakunaku.Timeline_Posting;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.jay.shakunaku.R;
import com.example.jay.shakunaku.Utils.SquareImageView;

import java.util.ArrayList;

public class GridImageAdapter extends RecyclerView.Adapter<GridImageAdapter.MyViewHolder>{

    private Context mContext;
    private ArrayList<String> listURLs;
    private OnRecyclerViewItemClickListener listener;

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnRecyclerViewItemClickListener{
        public void onItemClick(View v, int position);
    }

    public GridImageAdapter(Context context, ArrayList<String> list){
        this.mContext = context;
        this.listURLs = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_grid_imageview, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Glide.with(mContext)
                .load(listURLs.get(position))
                .into(holder.imageView);

        holder.itemView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(listener != null){
                            listener.onItemClick(v, position);
                        }
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return listURLs.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

     private SquareImageView imageView;

     public MyViewHolder(View itemView) {
         super(itemView);

         imageView = itemView.findViewById(R.id.grid_image_view);
     }
 }

}