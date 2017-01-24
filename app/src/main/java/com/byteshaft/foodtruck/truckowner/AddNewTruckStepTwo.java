package com.byteshaft.foodtruck.truckowner;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.byteshaft.foodtruck.R;
import com.byteshaft.foodtruck.utils.AppGlobals;
import com.byteshaft.foodtruck.utils.Helpers;
import com.byteshaft.requests.FormData;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Created by s9iper1 on 1/16/17.
 */

public class AddNewTruckStepTwo extends AppCompatActivity implements
        HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener, View.OnClickListener {

    private EditText facebookUrl;
    private EditText websiteUrl;
    private EditText twitterUrl;
    private EditText instagramUrl;
    private HttpRequest request;
    private AppCompatButton foodTruckButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_truck_step_two);
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_left_out);
        facebookUrl = (EditText) findViewById(R.id.facebook_url);
        websiteUrl = (EditText) findViewById(R.id.website_url);
        twitterUrl = (EditText) findViewById(R.id.twitter_url);
        instagramUrl = (EditText) findViewById(R.id.instagram_url);

        facebookUrl.setSelection(facebookUrl.getText().length());
        twitterUrl.setSelection(twitterUrl.getText().length());
        instagramUrl.setSelection(instagramUrl.getText().length());

        foodTruckButton = (AppCompatButton) findViewById(R.id.add_food_truck_button);
        foodTruckButton.setOnClickListener(this);
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            facebookUrl.setText(bundle.getString("facebook"));
            websiteUrl.setText(bundle.getString("website"));
            twitterUrl.setText(bundle.getString("twitter"));
            instagramUrl.setText(bundle.getString("instagram"));
            foodTruckButton.setText("Update Food Truck");
        }
    }

    private void addUpdateTruck() {
        request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        String method = "POST";
        String url = String.format("%suser/trucks", AppGlobals.BASE_URL);
        if (AddNewTruck.getInstance().updateMode) {
            method = "PUT";
            url = String.format("%suser/trucks/%s", AppGlobals.BASE_URL, AddNewTruck.getInstance().id);
        }
        request.open(method, url);
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        FormData data = new FormData();
        data.append(FormData.TYPE_CONTENT_TEXT, "name",
                AddNewTruck.getInstance().truckName.getText().toString());
        data.append(FormData.TYPE_CONTENT_TEXT, "location",
                AddNewTruck.getInstance().locationCoordinates.getText().toString());
        data.append(FormData.TYPE_CONTENT_TEXT, "address",
                AddNewTruck.getInstance().truckAddress.getText().toString());
        data.append(FormData.TYPE_CONTENT_TEXT, "phone_number",
                AddNewTruck.getInstance().phoneNumber.getText().toString());
        data.append(FormData.TYPE_CONTENT_TEXT, "products",
                AddNewTruck.getInstance().products.getText().toString());
        if (!AddNewTruck.getInstance().imageUrl.contains("http")) {
            data.append(FormData.TYPE_CONTENT_FILE, "photo",
                    AddNewTruck.getInstance().imageUrl);
        }
        data.append(FormData.TYPE_CONTENT_TEXT, "facebook",
                facebookUrl.getText().toString());
        data.append(FormData.TYPE_CONTENT_TEXT, "website",
                websiteUrl.getText().toString());
        data.append(FormData.TYPE_CONTENT_TEXT, "twitter",
                twitterUrl.getText().toString());
        data.append(FormData.TYPE_CONTENT_TEXT, "instagram",
                instagramUrl.getText().toString());
        request.send(data);
        Helpers.showProgressDialog(this, "Adding new truck");
    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                Helpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpURLConnection.HTTP_CREATED:
                    case HttpURLConnection.HTTP_OK:
                        Log.i("TAG", "Truck " + request.getResponseText());
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(request.getResponseText());
                            TruckDetail truckDetail = new TruckDetail();
                            truckDetail.setId(jsonObject.getInt("id"));
                            truckDetail.setTruckName(jsonObject.getString("name"));
                            truckDetail.setAddress(jsonObject.getString("address"));
                            truckDetail.setLatLng(jsonObject.getString("location"));
                            truckDetail.setContactNumber(jsonObject.getString("phone_number"));
                            truckDetail.setProducts(jsonObject.getString("products"));
                            truckDetail.setImageUrl(Uri.parse(jsonObject.getString("photo")
                                    .replace("http://localhost/", AppGlobals.SERVER_IP)));
                            truckDetail.setRating(jsonObject.getString("ratings"));
                            truckDetail.setWebsiteUrl(jsonObject.getString("website"));
                            truckDetail.setFacebookUrl(jsonObject.getString("facebook"));
                            truckDetail.setInstagramUrl(jsonObject.getString("instagram"));
                            truckDetail.setTwitterUrl(jsonObject.getString("twitter"));
                            TruckList.getInstance().truckDetails.add(truckDetail);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        AddNewTruck.getInstance().finish();
                        finish();
                        break;

                }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_right_out);

    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {
        Helpers.dismissProgressDialog();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_food_truck_button:
                addUpdateTruck();
                break;
        }
    }
}
