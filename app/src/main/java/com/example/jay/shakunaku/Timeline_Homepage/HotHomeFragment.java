package com.example.jay.shakunaku.Timeline_Homepage;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jay.shakunaku.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HotHomeFragment extends Fragment {


    public HotHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hot_home, container, false);
    }

}
