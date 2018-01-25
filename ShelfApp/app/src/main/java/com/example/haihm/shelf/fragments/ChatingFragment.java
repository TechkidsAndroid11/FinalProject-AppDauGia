package com.example.haihm.shelf.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.model.SanPhamDauGia;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatingFragment extends Fragment {
    RecyclerView rvChat;
    EditText etChat;
    ImageView ivSend;

    public ChatingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chating, container, false);
        setUpUI(view);
        return view;
    }

    private void setUpUI(View view) {
        rvChat = view.findViewById(R.id.rv_chat);
        ivSend = view.findViewById(R.id.iv_send);
        etChat = view.findViewById(R.id.et_chat);

    }

}
