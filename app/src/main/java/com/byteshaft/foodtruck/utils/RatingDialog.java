package com.byteshaft.foodtruck.utils;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;

import com.byteshaft.foodtruck.R;
import com.byteshaft.requests.HttpRequest;

/**
 * Created by shahid on 25/01/2017.
 */

public class RatingDialog extends Dialog implements
        View.OnClickListener, HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener {

    public Activity c;
    public Dialog d;
    public Button yes, no;
    public RatingBar ratingBar;
    private HttpRequest request;

    public RatingDialog(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_rating);
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        yes = (Button) findViewById(R.id.btn_cancel);
        no = (Button) findViewById(R.id.btn_rate);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                break;
            case R.id.btn_rate:
//                rateTruck();
                break;
            default:
                break;
        }
        dismiss();
    }

    private void rateTruck(String email, String password) {
        request = new HttpRequest(AppGlobals.getContext());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%suser/login", AppGlobals.BASE_URL));
//        request.send(getUserLoginData(email, password));
        Helpers.showProgressDialog(c, "Logging In");
    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {

    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {

    }
}
