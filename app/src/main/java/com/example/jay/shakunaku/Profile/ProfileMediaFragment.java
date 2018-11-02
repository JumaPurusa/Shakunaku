package com.example.jay.shakunaku.Profile;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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
import com.example.jay.shakunaku.R;
import com.example.jay.shakunaku.Utils.FirebaseMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileMediaFragment extends Fragment {
    private static final String TAG = "ProfileMediaFragment";

    private List<Photos> listOfMedia;
    private ProfileMediaAdapter profileMediaAdapter;
    private RecyclerView recyclerView;
    private ArrayList<Photos> photos;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mRef;
    //private FirebaseMethods firebaseMethods;

    public ProfileMediaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_media, container, false);

        setupFirebaseAuth();

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

        Query query = mRef.child(getString(R.string.dbname_user_photos))
                          .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                        //photos.add(singleSnapShot.getValue(Photos.class));

                        Photos photo = new Photos();

                        Map<String, Object> objectMap = (HashMap<String, Object>)singleSnapshot.getValue();
                        photo.setPhoto_caption(objectMap.get(getString(R.string.field_photo_caption)).toString());
                        photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                        photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());
                        photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                        photo.setUser_id(objectMap.get(getString(R.string.user_id)).toString());

                        ArrayList<Comment> comments = new ArrayList<>();
                        comments.clear();
                        for(DataSnapshot snapshot : singleSnapshot.child(getString(R.string.field_comments)).getChildren()){

                            Comment comment = new Comment();
                            comment.setComment(snapshot.getValue(Comment.class).getComment());
                            comment.setUser_id(snapshot.getValue(Comment.class).getUser_id());
                            comment.setDate_created(snapshot.getValue(Comment.class).getDate_created());

                            comments.add(comment);
                        }

                        photo.setComments(comments);

                        ArrayList<Like> likesList = new ArrayList<>();

                        for(DataSnapshot snapshot : singleSnapshot.child(getString(R.string.field_likes)).getChildren()){
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
                        pairs[1] = new Pair<View, String>(ProfileFragment.profileToolbar, "toolbarTransition");


                        //ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) getContext(),
                               // Pair.create(v, "postImage"));
                        //startActivity(postIntent, options.toBundle());

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), pairs);
                        startActivity(postIntent, options.toBundle());



                    }
                }
        );

    }

    /*
    private List<ProfileMedia> getProfileMediaList() {



        return listOfMedia;
    }
    */

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
    }

    private void setupFirebaseAuth() {

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mRef = firebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                if (currentUser != null) {
                    // user is signed in
                    Log.d(TAG, "onAuthStateChanged: signed_in: " + currentUser.getUid());

                    mRef.addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    //retrieve user information from the database

                                    //retrieve images for the user in question

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {


                                }
                            }
                    );

                } else {
                    // user is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };


    }

}