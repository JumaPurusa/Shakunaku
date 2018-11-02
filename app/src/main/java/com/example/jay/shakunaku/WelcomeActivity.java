package com.example.jay.shakunaku;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.example.jay.shakunaku.Activities.LoginActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        setContentView(R.layout.activity_welcome);

        Thread thread = new Thread(){

            @Override
            public void run() {

                try {
                    sleep(3000);
                    startActivity(new Intent(getApplicationContext(), ContainerActivity.class));
                    finish();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };

        thread.start();
    }
}
