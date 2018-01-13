package com.example.haihm.shelf.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.activity.DangSpDGActivity;
import com.example.haihm.shelf.activity.DangSpRvActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment{

    View view;
    Button btnAuction, btnClassified;
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        setupUI();

        btnAuction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentPostAution();
            }
        });

        btnClassified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentPostClassified();
            }
        });
        return view;
    }

    private void setupUI() {
        btnAuction = view.findViewById(R.id.btBuy);
        btnClassified = view.findViewById(R.id.btDauGia);
    }


    private void intentPostClassified() {

        Intent intent = new Intent(getActivity(), DangSpDGActivity.class);
        startActivity(intent);
    }

    private void intentPostAution() {
        Intent intent = new Intent(getActivity(), DangSpRvActivity.class);
        startActivity(intent);
    }
}
