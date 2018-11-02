package com.example.jay.shakunaku.Timeline_Homepage;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jay.shakunaku.Models.UserAccountSettings;
import com.example.jay.shakunaku.Models.UserSettings;
import com.example.jay.shakunaku.Profile.ProfileFavoritesFragment;
import com.example.jay.shakunaku.Profile.ProfileFragmentTabsAdapter;
import com.example.jay.shakunaku.Profile.ProfileMediaFragment;
import com.example.jay.shakunaku.Profile.ProfileTimelineFragment;
import com.example.jay.shakunaku.Profile.SettingsActivity;
import com.example.jay.shakunaku.R;
import com.example.jay.shakunaku.Utils.FirebaseMethods;
import com.example.jay.shakunaku.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewProfileActivity extends AppCompatActivity {

    private static final String TAG = "ViewProfileActivity";

    private Context mContext = ViewProfileActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        setContentView(R.layout.activity_view_profile);

        if(savedInstanceState == null){
            init();
        }else{

        }

    }

    private void init(){

        Intent comingIntentFromSearch = getIntent();
        if(comingIntentFromSearch.hasExtra(getString(R.string.calling_activity))){
            if(comingIntentFromSearch.hasExtra(getString(R.string.parcelable_user))){

                ViewProfileFragment viewProfileFragment = new ViewProfileFragment();

                Bundle args = new Bundle();
                args.putParcelable(getString(R.string.parcelable_user),
                        comingIntentFromSearch.getParcelableExtra(getString(R.string.parcelable_user)));
                viewProfileFragment.setArguments(args);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.view_profile_container, viewProfileFragment, getString(R.string.view_profile_fragment));
                ft.commit();
            }
        }
    }


}
