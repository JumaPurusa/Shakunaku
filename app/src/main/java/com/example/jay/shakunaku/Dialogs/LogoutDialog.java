package com.example.jay.shakunaku.Dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jay.shakunaku.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogoutDialog extends DialogFragment {

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Context context;

    public LogoutDialog(){

    }

    @SuppressLint("ValidFragment")
    public LogoutDialog(Context context){
            this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logout_dialog, container);

        setupFirebaseAuth();

        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogSlide2;

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView cancel = view.findViewById(R.id.cancel);
        final TextView confirm = view.findViewById(R.id.confirm);

        cancel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getDialog().dismiss();
                    }
                }
        );

        confirm.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        getDialog().dismiss();
                        LoadingDialog loadingDialog = new LoadingDialog("logging out ...");
                        loadingDialog.show(getFragmentManager(), "Dialog");

                        Thread thread = new Thread(){

                            @Override
                            public void run() {
                                super.run();

                                try {
                                    sleep(5000);
                                    mAuth.signOut();
                                    Activity activity = (Activity) context;
                                    if(activity != null){
                                       activity.finish();
                                    }

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }
                        };

                        thread.start();

                    }
                }
        );
    }

    private void setupFirebaseAuth(){

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){

                }else{

                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
    }
}
