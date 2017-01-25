package com.byteshaft.foodtruck.utils;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;

import com.byteshaft.foodtruck.R;
import com.byteshaft.foodtruck.customer.TruckDetailsActivity;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

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
    String ratingString;
    TruckDetailsActivity truckDetailsActivity;

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
        truckDetailsActivity = new TruckDetailsActivity();
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        yes = (Button) findViewById(R.id.btn_cancel);
        no = (Button) findViewById(R.id.btn_rate);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
                ratingString = String.valueOf(rating);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                break;
            case R.id.btn_rate:
                rateTruck(ratingString, AppGlobals.getStringFromSharedPreferences("truck_id"), AppGlobals.getStringFromSharedPreferences("uuid"));
                break;
            default:
                break;
        }
        dismiss();
    }

    private void rateTruck(String rating, String truckId, String uuid) {
        request = new HttpRequest(AppGlobals.getContext());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%strucks/review", AppGlobals.BASE_URL));
        request.send(getData(rating, truckId, uuid));
        Helpers.showProgressDialog(c, "Rating...");
    }

    private String getData(String rating, String truckId, String uuId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("rating", rating);
            jsonObject.put("reviewer_uuid", uuId);
            jsonObject.put("truck", truckId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                Helpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                        break;
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        break;
                    case HttpURLConnection.HTTP_UNAUTHORIZED:
                        break;
                    case HttpURLConnection.HTTP_FORBIDDEN:
                        break;
                    case HttpURLConnection.HTTP_OK:
                        try {
                            JSONObject jsonObject = new JSONObject(request.getResponseText());

                            dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
        }
    }


    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {

    }
}
