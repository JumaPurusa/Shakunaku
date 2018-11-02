package com.example.jay.shakunaku.Profile;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.jay.shakunaku.Models.User;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragmentTabsAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> listOfFragments = new ArrayList<>();
    public static final  List<String> fragmentTitles = new ArrayList<>();


    public ProfileFragmentTabsAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment, String title){

        listOfFragments.add(fragment);
        fragmentTitles.add(title);

    }

    @Override
    public Fragment getItem(int position) {
        return listOfFragments.get(position);
    }

    @Override
    public int getCount() {
        return listOfFragments.size();
    }
}
