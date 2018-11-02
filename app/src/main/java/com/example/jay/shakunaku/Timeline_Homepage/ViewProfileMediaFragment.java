package com.example.jay.shakunaku.Timeline_Homepage;


import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jay.shakunaku.Listeners.OnRecyclerviewItemClickListener;
import com.example.jay.shakunaku.Models.Comment;
import com.example.jay.shakunaku.Models.Like;
import com.example.jay.shakunaku.Models.Photos;
import com.example.jay.shakunaku.Models.User;
import com.example.jay.shakunaku.PostAcvitity;
import com.example.jay.shakunaku.Profile.ProfileFragment;
import com.example.jay.shakunaku.Profile.ProfileMediaAdapter;
import com.example.jay.shakunaku.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewProfileMediaFragment extends Fragment {

    private static final String TAG = "ViewProfileMediaFragmen";


    private List<Photos> listOfMedia;
    private ProfileMediaAdapter profileMediaAdapter;
    private RecyclerView recyclerView;
    private ArrayList<Photos> photos;

    private User user;

    public ViewProfileMediaFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public ViewProfileMediaFragment(User user){
        this.user = user;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_profile_media, container, false);

        setUpRecyclerView(view);
        setupItemClick();

        return view;
    }

    private void setUpRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        profileMediaAdapter = new ProfileMediaAdapter(getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        photos = new ArrayList<>();

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

        Query query = mRef.child(getContext().getString(R.string.dbname_user_photos))
                .child(user.getUser_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    //photos.add(singleSnapShot.getValue(Photos.class));

                    Photos photo = new Photos();

                    Map<String, Object> objectMap = (HashMap<String, Object>)singleSnapshot.getValue();
                    photo.setPhoto_caption(objectMap.get(getContext().getString(R.string.field_photo_caption)).toString());
                    photo.setDate_created(objectMap.get(getContext().getString(R.string.field_date_created)).toString());
                    photo.setImage_path(objectMap.get(getContext().getString(R.string.field_image_path)).toString());
                    photo.setPhoto_id(objectMap.get(getContext().getString(R.string.field_photo_id)).toString());
                    photo.setUser_id(objectMap.get(getContext().getString(R.string.user_id)).toString());

                    ArrayList<Comment> comments = new ArrayList<>();
                    comments.clear();
                    for(DataSnapshot snapshot : singleSnapshot.child(getContext().getString(R.string.field_comments)).getChildren()){

                        Comment comment = new Comment();
                        comment.setComment(snapshot.getValue(Comment.class).getComment());
                        comment.setUser_id(snapshot.getValue(Comment.class).getUser_id());
                        comment.setDate_created(snapshot.getValue(Comment.class).getDate_created());

                        comments.add(comment);
                    }

                    photo.setComments(comments);

                    ArrayList<Like> likesList = new ArrayList<>();

                    for(DataSnapshot snapshot : singleSnapshot.child(getContext().getString(R.string.field_likes)).getChildren()){
                        Like like = new Like();
                        like.setUser_id(snapshot.getValue(Like.class).getUser_id());
                        likesList.add(like);
                    }

                    photo.setLikes(likesList);
                    photos.add(photo);
                }


                listOfMedia = new ArrayList<>();
                for(int i=(photos.size()-1); i>=0; i--){
                    listOfMedia.add(photos.get(i));
                }

                profileMediaAdapter.addMediaList(listOfMedia);

                if (recyclerView != null)
                    recyclerView.setAdapter(profileMediaAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setupItemClick(){

        profileMediaAdapter.setOnRecyclerviewItemClickListener(
                new OnRecyclerviewItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void itemClick(View v, int position) {
                        Log.d(TAG, "itemClick: photo details : " + listOfMedia.get(position));
                        Intent postIntent = new Intent(getContext(), PostAcvitity.class);
                        Photos photo = listOfMedia.get(position);
                        postIntent.putExtra(getContext().getString(R.string.post_info), photo);

                        Pair[] pairs = new Pair[2];
                        pairs[0] = new Pair<View, String>(v, "postImage");
                        pairs[1] = new Pair<View, String>(ViewProfileFragment.viewProfileToolbar, "toolbarTransition");


                        //ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) getContext(),
                        // Pair.create(v, "postImage"));
                        //startActivity(postIntent, options.toBundle());

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), pairs);
                        startActivity(postIntent, options.toBundle());



                    }
                }
        );

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(isAdded()){
            View view = getView();
            setUpRecyclerView(view);
        }
    }
}
