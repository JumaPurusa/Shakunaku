package com.example.jay.shakunaku.Profile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jay.shakunaku.Listeners.OnRecyclerviewItemClickListener;
import com.example.jay.shakunaku.R;
import com.example.jay.shakunaku.Timeline_Posting.GridImageAdapter;


public class ProfilePhotoSelectorAdapter extends RecyclerView.Adapter<ProfilePhotoSelectorAdapter.ActionViewHolder>{

    private static final String TAG = "ProfilePhotoSelectorAda";

    private Context mContext;
    private Action[] mActions;

    private OnRecyclerviewItemClickListener onRecyclerviewItemClickListener;

    public void setOnRecyclerviewItemClickListener(OnRecyclerviewItemClickListener onRecyclerviewItemClickListener){
        this.onRecyclerviewItemClickListener = onRecyclerviewItemClickListener;
    }

    public ProfilePhotoSelectorAdapter(Context context, Action[] actions){
        this.mContext = context;
        this.mActions = actions;
    }

    @Override
    public ActionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.action_layout, parent, false);
        return new ActionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ActionViewHolder holder, final int position) {

        Action action = mActions[position];

        holder.textView.setText(action.getActionText());

        int resID = mContext.getResources().getIdentifier(action.getActionImage(),
                "drawable", mContext.getPackageName());
        holder.imageView.setImageResource(resID);

        holder.layout.setOnClickListener(
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
        return mActions.length;
    }


    public class ActionViewHolder extends RecyclerView.ViewHolder{

        private RelativeLayout layout;
        private ImageView imageView;
        private TextView textView;

        public ActionViewHolder(View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.action_relative);
            imageView = itemView.findViewById(R.id.action_image);
            textView = itemView.findViewById(R.id.action_text);

        }
    }
}
