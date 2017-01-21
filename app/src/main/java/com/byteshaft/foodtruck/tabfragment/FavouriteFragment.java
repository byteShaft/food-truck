package com.byteshaft.foodtruck.tabfragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.byteshaft.foodtruck.R;
import com.byteshaft.foodtruck.customer.MainActivity;

/**
 * Created by s9iper1 on 1/12/17.
 */

public class FavouriteFragment extends Fragment {

    private View mBaseView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!((AppCompatActivity) getActivity()).getSupportActionBar().isShowing()) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        }
        MainActivity.getInstance().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        MainActivity.getInstance().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        mBaseView = inflater.inflate(R.layout.favourite_fragment, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Favourites");
        return mBaseView;
    }
}
