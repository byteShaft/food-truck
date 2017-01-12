package com.byteshaft.foodtruck.tabfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.byteshaft.foodtruck.R;

/**
 * Created by s9iper1 on 1/12/17.
 */

public class SearchFragment extends Fragment {

    private View mBaseView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.search_layout, container, false);
        return mBaseView;
    }
}
