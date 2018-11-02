package com.example.jay.shakunaku.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jay.shakunaku.Models.Comment;
import com.example.jay.shakunaku.Models.Like;
import com.example.jay.shakunaku.Models.Photos;
import com.example.jay.shakunaku.Models.User;
import com.example.jay.shakunaku.Models.UserAccountSettings;
import com.example.jay.shakunaku.R;
import com.example.jay.shakunaku.Utils.FirebaseMethods;
import com.example.jay.shakunaku.Utils.Heart;
import com.example.jay.shakunaku.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.support.constraint.Constraints.TAG;
import static java.security.AccessController.getContext;

public class ViewPostAdapter extends RecyclerView.Adapter{

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private Context mContext;
    private LayoutInflater inflater;
    private List<Photos> listOfPosts;
    private String currentUsername = "";
    DatabaseReference mRef;

    private OnItemClickListener onItemClickListener;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreItemsListener onLoadMoreItemsListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface  OnItemClickListener{
        public void onCommentClicked(View v, int position);

        public void onProfileImageClicked(View v, int position);

        public void onContextClicked(View v, int position);
    }

    public interface OnLoadMoreItemsListener{
        public void onLoadMoreItems();
    }

    public void setOnLoadMoreItemsListener(OnLoadMoreItemsListener onLoadMoreItemsListener) {
        this.onLoadMoreItemsListener = onLoadMoreItemsListener;
    }

    public ViewPostAdapter(Context context, RecyclerView recyclerView){
        this.mContext = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRef = FirebaseDatabase.getInstance().getReference();

        if(recyclerView.getLayoutManager() instanceof LinearLayoutManager){
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

            recyclerView.addOnScrollListener(
                    new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                            if(!loading && totalItemCount<=(lastVisibleItem + visibleThreshold)){
                                if(onLoadMoreItemsListener != null){
                                    onLoadMoreItemsListener.onLoadMoreItems();
                                }

                                loading = true;
                            }
                        }
                    }
            );
        }
    }

    @Override
    public int getItemViewType(int position) {
         return listOfPosts.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public void addListItems(List<Photos> list){
        this.listOfPosts = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         RecyclerView.ViewHolder viewHolder;
         if(viewType == VIEW_ITEM){
             View view = LayoutInflater.from(mContext).inflate(R.layout.layout_post_view, parent, false);
             viewHolder = new ViewPostHolder(view);
         }else{
             View view = LayoutInflater.from(mContext).inflate(R.layout.progressbar_item, parent, false);
             viewHolder = new ProgressViewHolder(view);
         }

         return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if(holder instanceof ViewPostHolder){

            ((ViewPostHolder) holder).photo = listOfPosts.get(position);

            ((ViewPostHolder) holder).heart = new Heart(((ViewPostHolder) holder).whiteHeart, ((ViewPostHolder) holder).redHeart);
            ((ViewPostHolder) holder).gestureDetector = new GestureDetector(mContext, new GestureListener(((ViewPostHolder) holder)));
            ((ViewPostHolder) holder).users = new StringBuilder();

            // get the current users username (need for the checking the like)

            if(!TextUtils.isEmpty(listOfPosts.get(position).getPhoto_caption())){
                ((ViewPostHolder) holder).postCaption.setVisibility(View.VISIBLE);
                ((ViewPostHolder) holder).postCaption.setText(listOfPosts.get(position).getPhoto_caption());
            }else{
                ((ViewPostHolder) holder).postCaption.setVisibility(View.GONE);
            }

            getCurrentUser();

            // get the likes
            getAllTheLikes(((ViewPostHolder) holder));

            //set the comment
            List<Comment> comments = listOfPosts.get(position).getComments();

            try {
                if(comments.size() == 0){
                    ((ViewPostHolder) holder).textComments.setText("");
                }else{
                    ((ViewPostHolder) holder).textComments.setText(String.valueOf(comments.size()));
                }

                ((ViewPostHolder) holder).postComment.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //onItemClickListener.onCommentClicked(v, position);
                                Toast.makeText(mContext, "go to comments", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }catch (NullPointerException e){
                Log.d(TAG, "onBindViewHolder: NullPointerException : " + e.getMessage());
            }

            //set the time it was posted
            long timeDef = getTimestampDifference(listOfPosts.get(position));
            if(timeDef != 0){
                ((ViewPostHolder) holder).timeElapsed.setText(timestampConversion(timeDef));
            }else{
                ((ViewPostHolder) holder).timeElapsed.setText("Today");
            }

            //set the post image
            final ImageLoader imageLoader = ImageLoader.getInstance();
            //imageLoader.displayImage(listOfPosts.get(position).getImage_path(), holder.postImage);
            UniversalImageLoader.setImage(listOfPosts.get(position).getImage_path(), ((ViewPostHolder) holder).postImage, ((ViewPostHolder) holder).progressBar, "");

            //set the username and profile image
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child(mContext.getString(R.string.dbname_user_account_settings))
                    .orderByChild(mContext.getString(R.string.user_id))
                    .equalTo(listOfPosts.get(position).getUser_id());

            query.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                                imageLoader.displayImage(singleSnapshot.getValue(UserAccountSettings.class).getProfile_photo(),
                                        ((ViewPostHolder) holder).profileImage);

                                ((ViewPostHolder) holder).profileImage.setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                //onItemClickListener.onProfileImageClicked(v, position);
                                                Toast.makeText(mContext, "go to user profile", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                );

                                ((ViewPostHolder) holder).postUsername.setText(singleSnapshot.getValue(UserAccountSettings.class).getUsername());
                                ((ViewPostHolder) holder).postUsername.setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                //onItemClickListener.onProfileImageClicked(v, position);
                                                Toast.makeText(mContext, "go to user profile", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                );

                                ((ViewPostHolder) holder).userAccountSettings = singleSnapshot.getValue(UserAccountSettings.class);

                                ((ViewPostHolder) holder).postComment.setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                //onItemClickListener.onCommentClicked(v, position);
                                                Toast.makeText(mContext, "go to comments", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                );
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }
            );

            ((ViewPostHolder) holder).postContext.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Toast.makeText(mContext, "display the context menu", Toast.LENGTH_SHORT).show();
                            Toast.makeText(mContext, "open post context menu", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            //get the user object
            Query query1 = reference.child(mContext.getString(R.string.dbname_users))
                    .orderByChild(mContext.getString(R.string.user_id))
                    .equalTo(listOfPosts.get(position).getUser_id());
            query1.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                                ((ViewPostHolder) holder).user = singleSnapshot.getValue(User.class);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }
            );


        }else{
            ((ProgressViewHolder)holder).progressBar.setIndeterminate(true);
        }

    }

    public void setLoaded() {
        loading = false;
    }

    @Override
    public int getItemCount() {
        return listOfPosts.size();
    }

    public class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar =  v.findViewById(R.id.progressBar1);
        }
    }

    public class ViewPostHolder extends RecyclerView.ViewHolder {

        private ImageView profileImage;
        private TextView postUsername;
        private ImageView postContext;

        private TextView postCaption;

        private ProgressBar progressBar;
        private ImageView postImage;

        private TextView timeElapsed;

        private TextView textComments;
        private ImageView postComment;

        private TextView textLikes;
        private ImageView whiteHeart, redHeart;

        UserAccountSettings userAccountSettings = new UserAccountSettings();
        User user = new User();
        StringBuilder users;
        int numberOflikes = 0;
        boolean likedByCurrentUser;
        Heart heart;
        GestureDetector gestureDetector;
        Photos photo;

        public ViewPostHolder(View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profile_image);
            postUsername = itemView.findViewById(R.id.post_username);
            postContext = itemView.findViewById(R.id.ellipses);
            postCaption = itemView.findViewById(R.id.post_caption);
            progressBar = itemView.findViewById(R.id.progressBar);
            postImage = itemView.findViewById(R.id.post_image);
            timeElapsed = itemView.findViewById(R.id.time);
            textComments = itemView.findViewById(R.id.text_comments);
            postComment = itemView.findViewById(R.id.post_comments);
            textLikes = itemView.findViewById(R.id.text_likes);
            whiteHeart = itemView.findViewById(R.id.white_heart);
            redHeart = itemView.findViewById(R.id.red_heart);

        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener{

        ViewPostHolder mHolder;
        public GestureListener(final ViewPostHolder holder){
            mHolder = holder;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            //heart.toggeLike();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child(mContext.getString(R.string.dbname_photos))
                    .child(mHolder.photo.getPhoto_id())
                    .child(mContext.getString(R.string.field_likes));

            query.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d(TAG, "onDataChange: all likes : " + dataSnapshot.getChildren());

                            //case1: the current user has liked the post
                            for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                                if(mHolder.likedByCurrentUser && singleSnapshot.getValue(Like.class).getUser_id().equals(
                                        FirebaseAuth.getInstance().getCurrentUser().getUid()
                                )){

                                    String keyID = singleSnapshot.getKey();
                                    mRef.child(mContext.getString(R.string.dbname_photos))
                                            .child(mHolder.photo.getPhoto_id())
                                            .child(mContext.getString(R.string.field_likes))
                                            .child(keyID)
                                            .removeValue();

                                    mRef.child(mContext.getString(R.string.dbname_user_photos))
                                            .child(mHolder.photo.getUser_id())
                                            .child(mHolder.photo.getPhoto_id())
                                            .child(mContext.getString(R.string.field_likes))
                                            .child(keyID)
                                            .removeValue();


                                    mHolder.heart.toggeLike();
                                    getAllTheLikes(mHolder);

                                }else if(!mHolder.likedByCurrentUser){
                                    addNewLike(mHolder);
                                    break;
                                }
                            }

                            //case2: the current user has not liked the post

                            if(!dataSnapshot.exists()){
                                addNewLike(mHolder);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }
            );
            return true;
        }

    }

    private void addNewLike(final ViewPostHolder holder){
        Log.d(TAG, "addNewLike: adding the new like to the photo");

        String newKeyID = mRef.push().getKey();

        Like like = new Like();
        like.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        mRef.child(mContext.getString(R.string.dbname_photos))
                .child(holder.photo.getPhoto_id())
                .child(mContext.getString(R.string.field_likes))
                .child(newKeyID)
                .setValue(like);

        mRef.child(mContext.getString(R.string.dbname_user_photos))
                .child(holder.photo.getUser_id())
                .child(holder.photo.getPhoto_id())
                .child(mContext.getString(R.string.field_likes))
                .child(newKeyID)
                .setValue(like);

        holder.heart.toggeLike();
        getAllTheLikes(holder);

    }

    private void getAllTheLikes(final ViewPostHolder holder) {

        try{

            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child(mContext.getString(R.string.dbname_photos))
                    .child(holder.photo.getPhoto_id())
                    .child(mContext.getString(R.string.field_likes));

            query.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d(TAG, "onDataChange: Likes : " + dataSnapshot.getChildren());
                            holder.users = new StringBuilder();
                            for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                                Query newQuery = reference.child(mContext.getString(R.string.dbname_users))
                                        .orderByChild(mContext.getString(R.string.user_id))
                                        .equalTo(singleSnapshot.getValue(Like.class).getUser_id());

                                newQuery.addListenerForSingleValueEvent(
                                        new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    holder.users.append(snapshot.getValue(User.class).getUsername());
                                                    holder.users.append(",");
                                                }

                                                String[] splitUsers = holder.users.toString().split(",");

                                                holder.numberOflikes = splitUsers.length;
                                                Log.d(TAG, "onDataChange: number of users : " + splitUsers.length);

                                                if (holder.users.toString().contains(currentUsername + ",")) {
                                                    holder.likedByCurrentUser = true;
                                                } else {
                                                    holder.likedByCurrentUser = false;
                                                }

                                                setupTheLikes(holder, holder.numberOflikes);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        }
                                );
                            }

                            if (!dataSnapshot.exists()) {
                                holder.numberOflikes = 0;
                                holder.likedByCurrentUser = false;
                                setupTheLikes(holder, holder.numberOflikes);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }
            );



        }catch(NullPointerException e){
            Log.d(TAG, "getAllTheLikes: NullPointerException : " + e.getMessage());
            holder.numberOflikes = 0;
            holder.likedByCurrentUser = false;
        }

    }

    private void setupTheLikes(final ViewPostHolder holder, int numberOfLikes){

        if (holder.likedByCurrentUser) {
            holder.redHeart.setVisibility(View.VISIBLE);
            holder.whiteHeart.setVisibility(View.GONE);

            holder.redHeart.setOnTouchListener(
                    new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return holder.gestureDetector.onTouchEvent(event);
                        }
                    }
            );
        }

        if (!holder.likedByCurrentUser) {
            holder.whiteHeart.setVisibility(View.VISIBLE);
            holder.redHeart.setVisibility(View.GONE);

            holder.whiteHeart.setOnTouchListener(
                    new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return holder.gestureDetector.onTouchEvent(event);
                        }
                    }
            );
        }

        if(numberOfLikes == 0){
            holder.textLikes.setText("");
        }else{
            holder.textLikes.setText(String.valueOf(numberOfLikes));
        }

    }

    public long getTimestampDifference(Photos photos){

        long difference;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date today = c.getTime();
        sdf.format(today);
        String photoTimestamp = photos.getDate_created();
        Date timestamp;
        try {
            timestamp = sdf.parse(photoTimestamp);
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
                timeText = newTime + "s ago";
            }else{
                timeText = newTime + "s ago";
            }

        }else{
            newTime = time / 1000 / 60;
            if(newTime < 60){

                if(newTime == 1){
                    timeText = newTime + "m ago";
                }else{
                    timeText = newTime + "m ago";
                }

            }else{
                newTime = time /1000 / 60 / 60;
                if(newTime < 24){

                    if(newTime == 1){
                        timeText = newTime + "h ago";
                    }else{
                        timeText = newTime + "h ago";
                    }

                }else{
                    newTime = time / 1000 / 60 / 60 / 24;

                    /*
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
                    */

                    if(newTime == 1){
                        timeText = newTime + "d ago";
                    }else{
                        timeText = newTime + "d ago";
                    }
                }
            }
        }

        return timeText;
    }

    private void getCurrentUser(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(mContext.getString(R.string.dbname_users))
                .orderByChild(mContext.getString(R.string.user_id))
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                            currentUsername = singleSnapshot.getValue(User.class).getUsername();
                        }

                        //getAllTheLikes();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

    }


}
