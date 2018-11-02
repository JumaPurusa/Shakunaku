package com.example.jay.shakunaku.Timeline_Homepage;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.jay.shakunaku.Listeners.OnRecyclerviewItemClickListener;
import com.example.jay.shakunaku.Models.User;
import com.example.jay.shakunaku.Models.UserSettings;
import com.example.jay.shakunaku.R;
import com.example.jay.shakunaku.Utils.StringManipulation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";

    private Context mContext = SearchActivity.this;

    private Toolbar searchToolbar;
    private ImageView backArrow;
    private EditText searchWord;
    private RecyclerView recyclerView;
    private UsersAdapter usersAdapter;
    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        setContentView(R.layout.activity_search);

        searchToolbar = findViewById(R.id.search_toolbar);
        searchToolbar.setPadding(0,0,0,0);
        setSupportActionBar(searchToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }
        );

        searchWord = findViewById(R.id.search_word);
        recyclerView = findViewById(R.id.search_list_view);
        initTextListener();
    }

    private void initTextListener(){
        users = new ArrayList<>();

        searchWord.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String keyword = searchWord.getText().toString();
                        searchFormMatch(StringManipulation.condenseUsername(keyword));
                    }
                }
        );
    }

    private void searchFormMatch(String keyword){
        Log.d(TAG, "searchFormMatch: searching for a match : " + keyword);

        users.clear();
        if(keyword.length() == 0){

        }else{
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child(getString(R.string.dbname_users))
                    .orderByChild(getString(R.string.username))
                    .equalTo(keyword);

            query.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d(TAG, "onDataChange: found match : " + dataSnapshot.getValue(User.class));
                            for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                                    users.add(singleSnapshot.getValue(User.class));
                                    updateUserList();
                            }

                            if(!dataSnapshot.exists()){

                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }
            );
        }

    }

    private void updateUserList(){

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        usersAdapter = new UsersAdapter(mContext, users);
        recyclerView.setAdapter(usersAdapter);

        usersAdapter.setOnRecyclerviewItemClickListener(
                new OnRecyclerviewItemClickListener() {
                    @Override
                    public void itemClick(View v, int position) {
                        Log.d(TAG, "itemClick: user : " + users.get(position).toString());
                            Intent profileIntent = new Intent(mContext, ViewProfileActivity.class);
                            profileIntent.putExtra(getString(R.string.calling_activity), getString(R.string.search_activity));
                            profileIntent.putExtra(getString(R.string.parcelable_user), users.get(position));
                            startActivity(profileIntent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
