package com.byteshaft.foodtruck.utils;

import android.app.Activity;
import android.app.ProgressDialog;

/**
 * Created by shahid on 14/01/2017.
 */

public class Helpers {

    private static ProgressDialog progressDialog;

    public static void showProgressDialog(Activity activity, String message) {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public static void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

    }
}
