package com.example.jay.shakunaku.Timeline_Homepage;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.jay.shakunaku.R;

import static android.support.constraint.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private Toolbar searchToolbar;
    private ImageView backArrow;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchToolbar = view.findViewById(R.id.search_toolbar);
        searchToolbar.setPadding(0, 0, 0, 0);
        ((AppCompatActivity)getActivity()).setSupportActionBar(searchToolbar);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        backArrow = view.findViewById(R.id.back_arrow);
        closeSearchFragment();

        return view;
    }

    private void closeSearchFragment(){
        backArrow.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();
                        Fragment fragment = getChildFragmentManager().findFragmentByTag(getString(R.string.search_fragment));
                        getChildFragmentManager().beginTransaction().remove(fragment);

                    }
                }
        );
    }



}
