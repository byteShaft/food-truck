package com.byteshaft.foodtruck.truckowner;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.byteshaft.foodtruck.R;

/**
 * Created by s9iper1 on 1/16/17.
 */

public class AddNewTruckStepTwo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_truck_step_two);
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_left_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_right_out);

    }
}