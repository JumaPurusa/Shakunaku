package com.example.jay.shakunaku.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jay.shakunaku.ContainerActivity;
import com.example.jay.shakunaku.Models.User;
import com.example.jay.shakunaku.R;
import com.example.jay.shakunaku.Utils.FirebaseMethods;
import com.example.jay.shakunaku.Utils.ProgressDialogUtil;
import com.example.jay.shakunaku.Utils.StringManipulation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RegistrationActivity extends AppCompatActivity {
    private static final String TAG = "RegistrationActivity";

    // RegisterActivity Widgets
    private EditText fullname, email, password, confirm_password;
    private String reg_fullname, reg_email, reg_password, confirmPassword;
    private Button signUp;
    private TextView signIn;

    private Context mContext = RegistrationActivity.this;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods firebaseMethods;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;

    private ProgressDialogUtil progressDialogUtil;

    private String append = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        setContentView(R.layout.activity_registration);

        firebaseMethods = new FirebaseMethods(mContext);
        setupFirebaseAuth();
        Log.d(TAG, "onCreate: started");

        initWidgets();
        onSignUpClick();
        onSignInClick();

    }

    private void initWidgets(){
        Log.d(TAG, "initWidgets: initializing Widgets");
        fullname = findViewById(R.id.full_name);
        email = findViewById(R.id.email_phone);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        signUp = findViewById(R.id.sign_up);
        signIn = findViewById(R.id.sign_in);
    }

    private void onSignInClick(){

        signIn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }
        );

    }

    private void onSignUpClick(){

        signUp.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        reg_fullname = fullname.getText().toString();
                        reg_email = email.getText().toString();
                        reg_password = password.getText().toString();
                        confirmPassword = confirm_password.getText().toString();

                        progressDialogUtil = new ProgressDialogUtil(mContext, getString(R.string.registering_new_user));

                        if(TextUtils.isEmpty(reg_email) && TextUtils.isEmpty(reg_password) && TextUtils.isEmpty(confirmPassword)){
                            Toast.makeText(mContext, R.string.please_fill_all_the_fields, Toast.LENGTH_SHORT).show();
                        }else if(TextUtils.isEmpty(reg_email)){
                            Toast.makeText(mContext, R.string.provide_your_email, Toast.LENGTH_SHORT).show();
                        }else if(TextUtils.isEmpty(reg_password)){
                            Toast.makeText(mContext, R.string.please_set_the_password, Toast.LENGTH_SHORT).show();
                        }else if(TextUtils.isEmpty(confirmPassword)){
                            Toast.makeText(mContext, R.string.confirm_your_password, Toast.LENGTH_SHORT).show();
                        }else if(!confirmPassword.equals(reg_password)){
                            Toast.makeText(mContext, R.string.passwords_do_not_match, Toast.LENGTH_SHORT).show();
                        }else{

                            final ProgressDialog mProgressDialog = (ProgressDialog) progressDialogUtil.setmProgressDialog();
                            mProgressDialog.show();

                            //verify email address before registering

                            firebaseMethods.registerNewWithEmailAndPassword(reg_email, reg_password, mProgressDialog);


                        }

                    }
                }
        );

    }

    /* ------------------------------------------ Firebase ---------------------------------------- */

    //private void checkCurrentUser(FirebaseUser currentUser){
    //    Log.d(TAG, "checkCurrentUser: check if current user has logged in");

    //if(currentUser != null){
    //       // User is signed in
    //       Log.d(TAG, "checkCurrentUser:sign_in" + currentUser.getUid());
    //
    //  }
    // }

    private void checkIfUsernameExists(final String reg_fullname) {

        final String username = StringManipulation.condenseUsername(reg_fullname);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.username))
                .equalTo(username);

        query.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                            if(singleSnapshot.exists()){
                                Log.d(TAG, "onDataChange: checkIfUsernameExists: FOUND_A_MATCH : " + singleSnapshot.getValue(User.class).getUsername());
                                append = mRef.push().getKey().substring(3, 10);
                                Log.d(TAG, "onDataChange: username already exists, Appending string to name: " + append);
                            }
                        }

                        String mUsername = "";
                        mUsername = username + append;

                        //add new user to the database
                        firebaseMethods.addNewUser(reg_email, reg_fullname, mUsername, "", "", "");

                        //add new user_account_settings to the database

                        Toast.makeText(mContext, "signup successful. Sending verification email", Toast.LENGTH_SHORT).show();

                        mAuth.signOut();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }


    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                if(currentUser != null){
                    // user is signed in
                    Log.d(TAG, "onAuthStateChanged: signed_in" + currentUser.getUid());

                    mRef.addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                  checkIfUsernameExists(reg_fullname);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            }
                    );
                }
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

        if(mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
