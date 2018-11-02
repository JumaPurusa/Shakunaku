package com.example.jay.shakunaku.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jay.shakunaku.R;

public class ConfirmPassword extends DialogFragment {
    private static final String TAG = "ConfirmPassword";

    private EditText confirmPassword;
    private TextView cancel, confirm;
    private PasswordCommunicator passwordCommunicator;

    public interface  PasswordCommunicator{

        public void passConfirmPassword(String password);

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.confirm_password_dialog, container, false);

         confirmPassword = view.findViewById(R.id.confirm_password_edit);
         cancel = view.findViewById(R.id.cancel_text);
         confirm = view.findViewById(R.id.confirm_text);

         confirm.setOnClickListener(
                 new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         String password = confirmPassword.getText().toString();
                         if(!password.equals("")){
                             passwordCommunicator.passConfirmPassword(confirmPassword.getText().toString());
                             getDialog().dismiss();
                         }else {
                             Toast.makeText(getContext(), "you must enter a password", Toast.LENGTH_SHORT).show();
                         }

                     }
                 }
         );

         cancel.setOnClickListener(
                 new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         getDialog().dismiss();
                     }
                 }
         );

         return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            passwordCommunicator = (PasswordCommunicator) context;
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
        }

    }
}
