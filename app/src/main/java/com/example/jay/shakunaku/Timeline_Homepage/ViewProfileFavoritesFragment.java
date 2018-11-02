package com.example.jay.shakunaku.Timeline_Homepage;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jay.shakunaku.Models.User;
import com.example.jay.shakunaku.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewProfileFavoritesFragment extends Fragment {

    private static final String TAG = "ViewProfileFavoritesFra";

    private User user;

    public ViewProfileFavoritesFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public ViewProfileFavoritesFragment(User user){
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_profile_favorites, container, false);

        return view;
    }

}
