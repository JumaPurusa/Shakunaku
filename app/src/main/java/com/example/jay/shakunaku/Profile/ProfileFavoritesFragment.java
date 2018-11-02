package com.example.jay.shakunaku.Profile;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jay.shakunaku.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFavoritesFragment extends Fragment {


    public ProfileFavoritesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_favorites, container, false);

        return view;
    }

}
