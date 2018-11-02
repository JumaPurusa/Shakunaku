package com.example.jay.shakunaku.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.jay.shakunaku.ContainerActivity;
import com.example.jay.shakunaku.R;

public class InitialAccountSettings extends AppCompatActivity {
    private static final String TAG = "InitialAccountSettings";
    private Context mContext = InitialAccountSettings.this;

    private Button skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        setContentView(R.layout.activity_initial_account_settings);


        initWidgets();
        onSkipClick();

    }

    private void initWidgets(){

        skip = findViewById(R.id.skip_btn);
    }

    private void onSkipClick(){
        skip.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent homeIntent = new Intent(mContext, ContainerActivity.class);
                        startActivity(homeIntent);
                        finish();
                    }
                }
        );
    }
}
