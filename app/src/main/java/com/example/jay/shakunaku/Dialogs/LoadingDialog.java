package com.example.jay.shakunaku.Dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.example.jay.shakunaku.R;

@SuppressLint("ValidFragment")
public class LoadingDialog extends DialogFragment {

    private String message;

    @SuppressLint("ValidFragment")
    public LoadingDialog(String message){
        this.message = message;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.loading, null);

        builder.setMessage(message);
        builder.setView(view);

        Dialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlide1;
        return dialog;
    }
}
