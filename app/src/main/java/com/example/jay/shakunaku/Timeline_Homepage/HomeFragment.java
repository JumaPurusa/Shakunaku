package com.example.jay.shakunaku.Timeline_Homepage;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.jay.shakunaku.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment{

    private static final String TAG = "HomeFragment";

    //widgets
    private Toolbar toolbar;
    private ImageView icSearch;
    private TextView newContents, hotContents;
    private ViewPager viewPager;
    private HomeFragmentsAdapter homeFragmentsAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setPadding(0,0, 0, 0);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);

        icSearch = view.findViewById(R.id.ic_search);
        icSearch.setVisibility(View.GONE);
        newContents = view.findViewById(R.id.new_contents);
        hotContents = view.findViewById(R.id.hot_contents);

        /*
        icSearch.setOnClickListener(
                new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(View v) {

                        Intent searchIntent = new Intent(getContext(), SearchActivity.class);
                        startActivity(searchIntent);
                        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    }
                }
        );
        */


        viewPager = view.findViewById(R.id.view_pager);
        homeFragmentsAdapter = new HomeFragmentsAdapter(getChildFragmentManager());
        viewPager.setAdapter(homeFragmentsAdapter);

        viewPager.addOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {

                        changeTabs(position);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                }
        );

        newContents.setOnClickListener(
                new View.OnClickListener() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onClick(View v) {

                        viewPager.setCurrentItem(0);
                    }
                }
        );

        hotContents.setOnClickListener(
                new View.OnClickListener() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onClick(View v) {

                        viewPager.setCurrentItem(1);
                    }
                }
        );

        return view;
    }

    private void changeTabs(int position){
        if(position == 0){

            newContents.setBackgroundResource(R.drawable.new_hot_stroke2);
            newContents.setTextColor(Color.parseColor("#E6392F"));

            hotContents.setBackgroundResource(R.drawable.new_hot_stroke);
            hotContents.setTextColor(Color.WHITE);

        }else if(position == 1){

            newContents.setBackgroundResource(R.drawable.new_hot_stroke);
            newContents.setTextColor(Color.WHITE);

            hotContents.setBackgroundResource(R.drawable.new_hot_stroke2);
            hotContents.setTextColor(Color.parseColor("#E6392F"));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.ic_search:
                Intent searchIntent = new Intent(getContext(), SearchActivity.class);
                startActivity(searchIntent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


}
