package com.example.jay.shakunaku.Timeline_Homepage;

import android.app.DownloadManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jay.shakunaku.Listeners.OnRecyclerviewItemClickListener;
import com.example.jay.shakunaku.Models.User;
import com.example.jay.shakunaku.Models.UserAccountSettings;
import com.example.jay.shakunaku.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.AUserHolder> {

    private Context mContext;
    List<User> users;

    private OnRecyclerviewItemClickListener onRecyclerviewItemClickListener;

    public void setOnRecyclerviewItemClickListener(OnRecyclerviewItemClickListener onRecyclerviewItemClickListener) {
        this.onRecyclerviewItemClickListener = onRecyclerviewItemClickListener;
    }

    public UsersAdapter(Context context, List<User> list){
        this.mContext = context;
        this.users = list;
    }

    @Override
    public AUserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_user_view_for_search, parent, false);
        return new AUserHolder(view);
    }

    @Override
    public void onBindViewHolder(final AUserHolder holder, final int position) {

        User user = users.get(position);

        Query query = FirebaseDatabase.getInstance().getReference()
                .child(mContext.getString(R.string.dbname_user_account_settings))
                .orderByChild(mContext.getString(R.string.user_id))
                .equalTo(user.getUser_id());

        query.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                            ImageLoader imageLoader = ImageLoader.getInstance();
                            imageLoader.displayImage(singleSnapshot.getValue(UserAccountSettings.class).getProfile_photo(),
                                    holder.userProfileImage);

                            holder.fullName.setText(singleSnapshot.getValue(UserAccountSettings.class).getDisplay_name());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

        holder.username.setText(user.getUsername());

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
        return users.size();
    }

    public class AUserHolder extends RecyclerView.ViewHolder{

        private ImageView userProfileImage;
        private TextView fullName, username;

        public AUserHolder(View itemView) {
            super(itemView);

            userProfileImage = itemView.findViewById(R.id.user_profile_image);
            fullName = itemView.findViewById(R.id.full_name);
            username = itemView.findViewById(R.id.username);
        }
    }
}
