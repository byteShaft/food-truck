package com.byteshaft.foodtruck.tabfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.byteshaft.foodtruck.R;

/**
 * Created by s9iper1 on 1/12/17.
 */

public class FoodTruckFragment extends Fragment {

    private View mBaseView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!((AppCompatActivity) getActivity()).getSupportActionBar().isShowing()) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        }
        mBaseView = inflater.inflate(R.layout.truck_fragment, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Food Trucks");

        return mBaseView;
    }
}
