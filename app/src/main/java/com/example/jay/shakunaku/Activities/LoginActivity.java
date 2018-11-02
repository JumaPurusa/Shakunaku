package com.example.jay.shakunaku.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jay.shakunaku.ContainerActivity;
import com.example.jay.shakunaku.R;
import com.example.jay.shakunaku.Utils.ProgressDialogUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private Context mContext = LoginActivity.this;

    private EditText username, password;
    private Button signIn;
    private TextView signUp;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    private ProgressDialogUtil progressDialogUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        signUp = findViewById(R.id.sign_up);


        signUp.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: navigating to the register screen");
                        Intent registerIntent = new Intent(mContext, RegistrationActivity.class);
                        startActivity(registerIntent);
                        //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }
        );

        setupFirebaseAuth();
        init();
    }

    /*  -------------------------------- Firebase ---------------------------------*/

    private void init(){

        // initialize the button for logging in
        signIn = findViewById(R.id.sign_in);
        signIn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: attempting to log in");

                        String email = username.getText().toString();
                        String pass = password.getText().toString();

                        progressDialogUtil = new ProgressDialogUtil(mContext, "Logging in ...");

                        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(pass)){
                            Toast.makeText(mContext, R.string.please_fill_all_the_fields, Toast.LENGTH_SHORT).show();
                        }else if(TextUtils.isEmpty(email)){
                            Toast.makeText(mContext, R.string.provide_your_email, Toast.LENGTH_SHORT).show();
                        }else if(TextUtils.isEmpty(pass)){
                            Toast.makeText(mContext, R.string.provide_your_password, Toast.LENGTH_SHORT).show();
                        }else{

                            final ProgressDialog mProgressDialog = (ProgressDialog) progressDialogUtil.setmProgressDialog();
                            mProgressDialog.show();

                            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(
                                    new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            Log.d(TAG, "onComplete: " + task.isSuccessful());
                                            FirebaseUser user = mAuth.getCurrentUser();

                                            if(task.isSuccessful()){

                                                try{

                                                    if(user.isEmailVerified()){

                                                        startActivity(new Intent(mContext, InitialAccountSettings.class));
                                                        finish();
                                                        //sendToContainer();

                                                    }else{

                                                        Toast.makeText(mContext, "Email is not verified\n check your email inbox", Toast.LENGTH_SHORT).show();
                                                        mProgressDialog.dismiss();
                                                        mAuth.signOut();
                                                    }
                                                }catch (NullPointerException e){
                                                    Log.e(TAG, "onComplete: NullPointerException" + e.getMessage() );
                                                }
                                                //sendToContainer();

                                            }else{

                                                String errorMessage = task.getException().getMessage();
                                                Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
                                                mProgressDialog.dismiss();
                                            }


                                        }
                                    }
                            );
                        }

                    }
                }
        );

    }


    /**
     * checks to see if the user is logged in
     */
    private void checkeCurrentUser(FirebaseUser currentUser){
        Log.d(TAG, "checkeCurrentUser: check if the user has logged in");
        if(currentUser != null){
            sendToContainer();
        }
    }

    /**
     * Setup the firebase auth object
     */

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebase: setting up the firebase Auth");
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                checkeCurrentUser(currentUser);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void sendToContainer(){

        startActivity(new Intent(mContext, ContainerActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
