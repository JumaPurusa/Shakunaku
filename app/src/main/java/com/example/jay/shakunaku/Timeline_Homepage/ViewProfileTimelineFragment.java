package com.example.jay.shakunaku.Timeline_Homepage;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jay.shakunaku.Adapters.ViewPostAdapter;
import com.example.jay.shakunaku.Models.Like;
import com.example.jay.shakunaku.Models.Photos;
import com.example.jay.shakunaku.Models.User;
import com.example.jay.shakunaku.R;
import com.example.jay.shakunaku.Utils.FirebaseMethods;
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

import static android.support.constraint.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewProfileTimelineFragment extends Fragment {

    private RecyclerView recyclerView;
    private ViewPostAdapter adapter;
    private ArrayList<Photos> listOfPhotos;
    private User user;

    public ViewProfileTimelineFragment(){
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public ViewProfileTimelineFragment(User user) {
        this.user = user;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_profile_timeline, container, false);

        setRecyclerView(view);

        return view;
    }

    private void setRecyclerView(View view){
        listOfPhotos = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ViewPostAdapter(getContext(), recyclerView);

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        Query query = mRef.child(getString(R.string.dbname_user_photos))
                .child(user.getUser_id());

        query.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<Photos> photosList = new ArrayList<>();
                        for(DataSnapshot ds : dataSnapshot.getChildren()){

                            Photos photo = new Photos();
                            Map<String, Object> objectMap = (HashMap<String, Object>)ds.getValue();

                            Log.d(TAG, "onDataChange: objectMap : " + objectMap);
                            photo.setPhoto_caption(objectMap.get(getString(R.string.field_photo_caption)).toString());
                            photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                            photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());
                            photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                            photo.setUser_id(objectMap.get(getString(R.string.user_id)).toString());

                            List<Like> likesList = new ArrayList<>();
                            for(DataSnapshot snapshot : dataSnapshot.child(getString(R.string.field_likes)).getChildren()){

                                Like like = new Like();
                                like.setUser_id(snapshot.getValue(Like.class).getUser_id());
                                likesList.add(like);
                            }

                            photo.setLikes(likesList);
                            photosList.add(photo);
                        }

                        if(photosList != null){
                            Collections.sort(photosList, new Comparator<Photos>() {
                                @Override
                                public int compare(Photos o1, Photos o2) {
                                     return o2.getDate_created().compareTo(o1.getDate_created());
                                }
                            });
                        }

                        adapter.addListItems(photosList);
                        if(recyclerView != null)
                            recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(isAdded()){
            View view = getView();
            setRecyclerView(view);
        }
    }
}
