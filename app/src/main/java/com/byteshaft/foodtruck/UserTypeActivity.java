package com.byteshaft.foodtruck;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.byteshaft.foodtruck.accounts.LoginActivity;

/**
 * Created by s9iper1 on 1/13/17.
 */

public class UserTypeActivity extends Activity implements View.OnClickListener {

    private AppCompatButton findTruck;
    private AppCompatButton truckOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usertype);
        findTruck = (AppCompatButton) findViewById(R.id.find_truck);
        truckOwner = (AppCompatButton) findViewById(R.id.truck_owner);
        findTruck.setOnClickListener(this);
        truckOwner.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.find_truck:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
            case R.id.truck_owner:
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
        }
    }
}
