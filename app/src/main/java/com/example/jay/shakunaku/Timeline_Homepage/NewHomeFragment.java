package com.example.jay.shakunaku.Timeline_Homepage;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.jay.shakunaku.Adapters.ViewPostAdapter;
import com.example.jay.shakunaku.Models.Comment;
import com.example.jay.shakunaku.Models.Photos;
import com.example.jay.shakunaku.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewHomeFragment extends Fragment {

    private static final String TAG = "NewHomeFragment";

    private ProgressBar progressBar;
    private RecyclerView mRecyclerView;
    private ViewPostAdapter viewPostAdapter;
    private List<Photos> photos;
    private List<Photos> paginatedPhotos;
    private List<String> following;

    //vars
    int mResults = 0;

    private Handler handler;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mRef;

    public NewHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_home, container, false);

        progressBar = view.findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.VISIBLE);

        setupFirebaseAuth(view);

        //progressBar.setVisibility(View.GONE);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        handler = new Handler();

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        viewPostAdapter = new ViewPostAdapter(getContext(), mRecyclerView);

        photos = new ArrayList<>();
        following = new ArrayList<>();

        getFollowing();

        viewPostAdapter.setOnLoadMoreItemsListener(
                new ViewPostAdapter.OnLoadMoreItemsListener() {
                    @Override
                    public void onLoadMoreItems() {
                        //add null , so the adapter will check view_type and show progress bar at bottom
                        paginatedPhotos.add(null);
                        viewPostAdapter.notifyItemInserted(paginatedPhotos.size()-1);

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                // remove progress item
                                paginatedPhotos.remove(paginatedPhotos.size()-1);
                                viewPostAdapter.notifyItemRemoved(paginatedPhotos.size());
                                displayMorePhotos();
                                viewPostAdapter.setLoaded();
                            }
                        }, 2000);
                    }
                }
        );

        return view;
    }

    private void getFollowing(){

        try{

            // DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = mRef.child(getContext().getString(R.string.dbname_following))
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            query.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                                //singleSnapshot.child(getContext().getString(R.string.user_id)).getValue();

                                following.add(singleSnapshot.child(getContext().getString(R.string.user_id)).getValue().toString());

                            }

                            following.add(FirebaseAuth.getInstance().getCurrentUser().getUid());

                            //get the photos
                            getPhotos();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }
            );


        }catch(NullPointerException e){
            Log.d(TAG, "getFollowing: NullPointerException e: " + e.getMessage());
        }

    }

    private void getPhotos(){
        //DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        for(int i=0; i<following.size(); i++){
            final int count = 1;
            Query query = mRef.child(getContext().getString(R.string.dbname_user_photos))
                    .child(following.get(i))
                    .orderByChild(getContext().getString(R.string.user_id))
                    .equalTo(following.get(i));

            query.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for(DataSnapshot ds : dataSnapshot.getChildren()){

                                Photos photo = new Photos();
                                Map<String, Object> objectMap = (HashMap<String, Object>)ds.getValue();

                                photo.setPhoto_caption(objectMap.get(getContext().getString(R.string.field_photo_caption)).toString());
                                photo.setDate_created(objectMap.get(getContext().getString(R.string.field_date_created)).toString());
                                photo.setImage_path(objectMap.get(getContext().getString(R.string.field_image_path)).toString());
                                photo.setPhoto_id(objectMap.get(getContext().getString(R.string.field_photo_id)).toString());
                                photo.setUser_id(objectMap.get(getContext().getString(R.string.user_id)).toString());

                                List<Comment> comments = new ArrayList<>();
                                for(DataSnapshot snapshot : dataSnapshot.child("Comments").getChildren()){
                                    Comment comment = new Comment();
                                    comment.setComment(snapshot.getValue(Comment.class).getComment());
                                    comment.setDate_created(snapshot.getValue(Comment.class).getDate_created());
                                    comment.setUser_id(snapshot.getValue(Comment.class).getUser_id());
                                    comments.add(comment);
                                }

                                photo.setComments(comments);


                                photos.add(photo);

                            }

                            if(count >= following.size()-1){
                                //display our photos
                                displayPhotos();

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }
            );
        }
    }

    private void displayPhotos(){
        paginatedPhotos = new ArrayList<>();
        if(photos != null){

            try{

                Collections.sort(photos, new Comparator<Photos>() {
                    @Override
                    public int compare(Photos o1, Photos o2) {
                        return o2.getDate_created().compareTo(o1.getDate_created());
                    }
                });

                int iterations = photos.size();
                if(iterations > 10){
                    iterations = 10;
                }

                mResults = 10;

                for(int i=0; i<iterations; i++){
                    paginatedPhotos.add(photos.get(i));
                }

                viewPostAdapter.addListItems(paginatedPhotos);

                if(mRecyclerView!= null){
                    mRecyclerView.setAdapter(viewPostAdapter);
                }

            }catch (NullPointerException e){
                Log.d(TAG, "displayPhotos: NullPointerException: " + e.getMessage());
            }catch (IndexOutOfBoundsException e){
                Log.d(TAG, "displayPhotos: IndexOutOfBoundException: " + e.getMessage());
            }

        }
    }

    public void displayMorePhotos(){

        try{

            if(photos.size() > mResults && photos.size() > 0){
                int iterations = 0;
                if(photos.size() > (mResults + 10)){
                    Log.d(TAG, "displayMorePhotos: there is greater than 10 more photos");
                    iterations = 10;
                }else{
                    Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    iterations = photos.size() - mResults;
                }

                //add the new photos to the paginated results;
                for(int i=mResults; i<mResults + iterations; i++){
                    paginatedPhotos.add(photos.get(i));
                }

                mResults = mResults + iterations;
                viewPostAdapter.notifyDataSetChanged();
            }
        }catch (NullPointerException e){
            Log.d(TAG, "displayMorePhotos: NullPointerException: " + e.getMessage());
        }catch (IndexOutOfBoundsException e){
            Log.d(TAG, "displayMorePhotos: IndexOutOfBoundsException e: " + e.getMessage());
        }
    }

    private void setupFirebaseAuth(View view){
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mRef = firebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                if(currentUser != null){

                }else{

                }
            }
        };


    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
    }

}
