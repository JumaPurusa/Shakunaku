package com.example.jay.shakunaku.Comments;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jay.shakunaku.Models.Comment;
import com.example.jay.shakunaku.Models.Photos;
import com.example.jay.shakunaku.Models.UserAccountSettings;
import com.example.jay.shakunaku.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class CommentsListAdapter extends RecyclerView.Adapter<CommentsListAdapter.CommentViewHolder>{

    private Context mContext;
    private LayoutInflater inflater;
    private List<Comment> comments;
    UserAccountSettings userAccountSettings;

    CommentsListAdapter(Context context){
        this.mContext = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addListItems(List<Comment> list){
        this.comments = list;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.single_comment_layout, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CommentViewHolder holder, int position) {

        Comment comment = comments.get(position);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(mContext.getString(R.string.dbname_user_account_settings))
                .orderByChild(mContext.getString(R.string.user_id))
                .equalTo(comment.getUser_id());

        query.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                            userAccountSettings = singleSnapshot.getValue(UserAccountSettings.class);
                        }

                        try {
                            //setupWidgets();
                            //ImageLoader imageLoader = ImageLoader.getInstance();
                            //imageLoader.displayImage(userAccountSettings.getProfile_photo(), holder.profileImageView);
                            Glide.with(mContext)
                                    .load(userAccountSettings.getProfile_photo())
                                    .into(holder.profileImageView);

                            holder.profileUsername.setText(userAccountSettings.getUsername());

                        }catch(NullPointerException e){
                            Log.d(TAG, "onDataChange: NullPointerException : " + e.getMessage());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

        holder.comment.setText(comment.getComment());
        holder.commentTime.setText(timestampConversion(getTimestampDifference(comment)));

    }

    @Override
    public int getItemCount() {
        if(comments.size() != 0){
            return comments.size();
        }else{
            return 0;
        }
    }


    public class CommentViewHolder extends RecyclerView.ViewHolder{

        private ImageView profileImageView, whiteHeart;
        private TextView profileUsername, comment, commentTime,reply, commentLikes;

        public CommentViewHolder(View itemView) {
            super(itemView);

            profileImageView = itemView.findViewById(R.id.comment_profile_image);
            profileUsername = itemView.findViewById(R.id.comment_username);
            comment = itemView.findViewById(R.id.comment);
            commentTime = itemView.findViewById(R.id.comment_time_posted);
            commentLikes = itemView.findViewById(R.id.comment_likes);
            reply = itemView.findViewById(R.id.comment_reply);
            whiteHeart = itemView.findViewById(R.id.comment_like);
        }
    }

    public long getTimestampDifference(Comment comment){

        long difference;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date today = c.getTime();
        sdf.format(today);
        String commentTimestamp = comment.getDate_created();
        Date timestamp;
        try {
            timestamp = sdf.parse(commentTimestamp);
            difference = (Math.round(((today.getTime()-timestamp.getTime()))));
        } catch (ParseException e) {
            Log.d(TAG, "getTimestampDifference: ParseException : " + e.getMessage());
            difference = 0;
        }

        return difference;
    }

    public String timestampConversion(long time){
        String timeText = "";

        long newTime = time / 1000;
        if(newTime < 60){
            if(newTime == 1){
                timeText = newTime + " sec ago";
            }else{
                timeText = newTime + " secs ago";
            }

        }else{
            newTime = time / 1000 / 60;
            if(newTime < 60){

                if(newTime == 1){
                    timeText = newTime + " min ago";
                }else{
                    timeText = newTime + " mins ago";
                }

            }else{
                newTime = time /1000 / 60 / 60;
                if(newTime < 24){

                    if(newTime == 1){
                        timeText = newTime + " hr ago";
                    }else{
                        timeText = newTime + " hrs ago";
                    }

                }else{
                    newTime = time / 1000 / 60 / 60 / 24;
                    if(newTime < 7){

                        if(newTime == 1){
                            timeText = newTime + " day ago";
                        }else{
                            timeText = newTime + " days ago";
                        }

                    }else{
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        Date date = new Date(newTime);
                        sdf.format(date);
                        timeText = String.valueOf(date);
                    }
                }
            }
        }

        return timeText;
    }
}
