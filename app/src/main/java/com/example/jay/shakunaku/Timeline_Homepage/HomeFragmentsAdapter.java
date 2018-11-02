package com.example.jay.shakunaku.Timeline_Homepage;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class HomeFragmentsAdapter extends FragmentPagerAdapter{


    public HomeFragmentsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                NewHomeFragment newHomeFragment = new NewHomeFragment();
                return newHomeFragment;

            case 1:
                HotHomeFragment hotHomeFragment = new HotHomeFragment();
                return hotHomeFragment;

                default:
                    return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
