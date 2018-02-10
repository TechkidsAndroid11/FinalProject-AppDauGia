package com.example.haihm.shelf.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.haihm.shelf.R;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;


/**
 * A simple {@link Fragment} subclass.
 */
public class AuctionFragment extends Fragment {
    private static final String TAG = AuctionFragment.class.toString();
    public static final String JOINED = "Joined";
    SmartTabLayout stlAuctionJoined;
    ViewPager vpAuction;


    public AuctionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_auction, container, false);
        setupUI(view);
        return view;
    }

    private void setupUI(final View view) {
        stlAuctionJoined = view.findViewById(R.id.stl_auction_joined);
        vpAuction = view.findViewById(R.id.vp_auction);

        setupJoinedAuctionTab(view);
    }

    private void setupJoinedAuctionTab(View view) {
        FragmentPagerItems fragmentPagerItems = new FragmentPagerItems(view.getContext());
        fragmentPagerItems.add(FragmentPagerItem.of("Đang diễn ra", AuctionJoinedFragment.class, new Bundler().putBoolean(JOINED, false).get()));
        fragmentPagerItems.add(FragmentPagerItem.of("Đã tham gia", JoinedFragment.class));
        FragmentPagerItemAdapter fragmentPagerItemAdapter = new FragmentPagerItemAdapter(getChildFragmentManager(), fragmentPagerItems);
        vpAuction.setAdapter(fragmentPagerItemAdapter);
        stlAuctionJoined.setViewPager(vpAuction);

    }




}
