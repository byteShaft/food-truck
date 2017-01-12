package com.byteshaft.foodtruck.tabfragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.byteshaft.foodtruck.R;

public class SearchFragment extends Fragment {

    private View mBaseView;
    private EditText searchBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        mBaseView = inflater.inflate(R.layout.search_layout, container, false);
        searchBar = (EditText) mBaseView.findViewById(R.id.search_bar);
        return mBaseView;
    }
}
