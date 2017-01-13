package com.byteshaft.foodtruck.tabfragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.byteshaft.foodtruck.R;

public class SearchFragment extends Fragment {

    private View mBaseView;
    private EditText searchBar;
    private ListView truckList;
    private ArrayAdapter<String> listAdapter;
    String [] listViewAdapterContent = {"School", "House", "Building", "Food", "Sports", "Dress", "Ring"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        mBaseView = inflater.inflate(R.layout.search_layout, container, false);
        truckList = (ListView) mBaseView.findViewById(R.id.truck_list);
        searchBar = (EditText) mBaseView.findViewById(R.id.search_bar);
        listAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1, listViewAdapterContent);
        truckList.setAdapter(listAdapter);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                listAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return mBaseView;
    }
}
