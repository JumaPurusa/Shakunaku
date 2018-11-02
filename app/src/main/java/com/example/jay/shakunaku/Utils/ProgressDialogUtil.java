package com.example.jay.shakunaku.Utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDialogUtil {

    private ProgressDialog mProgressDialog;
    private Context mContext;
    private String message;

    public ProgressDialogUtil(Context context, String newMessage){

        this.mContext = context;
        this.message = newMessage;

    }

    public Dialog setmProgressDialog(){

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);

        return mProgressDialog;
    }
}
