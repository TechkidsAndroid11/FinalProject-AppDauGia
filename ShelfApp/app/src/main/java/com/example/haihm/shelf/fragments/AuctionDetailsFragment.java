package com.example.haihm.shelf.fragments;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.haihm.shelf.R;
import com.example.haihm.shelf.activity.AuctionDetailsActivity;
import com.example.haihm.shelf.model.SanPhamDauGia;
import com.example.haihm.shelf.utils.ImageUtils;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * A simple {@link Fragment} subclass.
 */
public class AuctionDetailsFragment extends Fragment {
    private static final String TAG = "AuctionDetailsFragment";
    RelativeLayout rlHightestCostMaster, rlHightestCostGuest;
    RatingBar ratingBar;
    SliderLayout slImageProduct;
    PagerIndicator pagerIndicator;
    SpinKitView skLoadImage;
    TextView tvAddress, tvNameSeller, tvDescription, tvStartCost;

    TextView tvNameBuyer, tvCurentCost, tvTimeRemaining, tvCall;
    EditText etInputCost;
    ImageView ivGavel, ivAvatarSeller;
    SanPhamDauGia sanPhamDauGia;

    public AuctionDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_auction_details, container, false);
        setUpUI(view);
        testFireBase();
//        loadData();
        addController();
        return view;
    }

    private void setUpUI(View view) {
        rlHightestCostMaster = view.findViewById(R.id.icl_highest_cost_master);
        rlHightestCostGuest = view.findViewById(R.id.icl_highest_cost_guest);

        tvNameSeller = view.findViewById(R.id.tv_name_seller);
        tvAddress = view.findViewById(R.id.tv_address);
        tvDescription = view.findViewById(R.id.tv_description);
        tvStartCost = view.findViewById(R.id.tv_start_cost);
        ivAvatarSeller = view.findViewById(R.id.iv_avatar_seller);
        ratingBar = view.findViewById(R.id.rb_rate);
        slImageProduct = view.findViewById(R.id.sl_image_product);
        pagerIndicator = view.findViewById(R.id.pi_indicator);
        skLoadImage = view.findViewById(R.id.sk_load_image);

        if (myAution()) {
            rlHightestCostGuest.setVisibility(View.GONE);

            tvNameBuyer = rlHightestCostMaster.findViewById(R.id.tv_name_buyer);
            tvTimeRemaining = rlHightestCostMaster.findViewById(R.id.tv_time_remaining);
            tvCurentCost = rlHightestCostMaster.findViewById(R.id.tv_current_cost);

        } else {
            rlHightestCostMaster.setVisibility(View.GONE);

            tvNameBuyer = rlHightestCostGuest.findViewById(R.id.tv_name_buyer);
            tvTimeRemaining = rlHightestCostGuest.findViewById(R.id.tv_time_remaining);
            tvCurentCost = rlHightestCostGuest.findViewById(R.id.tv_current_cost);
        }
        tvCall = rlHightestCostMaster.findViewById(R.id.tv_call);
        etInputCost = rlHightestCostGuest.findViewById(R.id.et_input_cost);
        ivGavel = rlHightestCostGuest.findViewById(R.id.iv_gavel);

        etInputCost.addTextChangedListener(onTextChangedListener(etInputCost));
    }

    private boolean myAution() {
        return false;
    }
    private void loadData() {
//        sanPhamDauGia = AuctionDetailsActivity.sanPhamDauGia;
        loadImage(sanPhamDauGia.anhSP);

        tvNameSeller.setText(sanPhamDauGia.nguoiB.hoten);
        tvDescription.setText(sanPhamDauGia.motaSP);
        tvAddress.setText(sanPhamDauGia.diaGD);
        Picasso.with(getContext()).load(sanPhamDauGia.nguoiB.anhAvatar)
                .transform(new CropCircleTransformation()).into(ivAvatarSeller);
        float rate = (float) sanPhamDauGia.nguoiB.rate.tongD / (float) sanPhamDauGia.nguoiB.rate.tongLuotVote;
        ratingBar.setRating(rate);

        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        decimalFormat.applyPattern("#,###,###");

        tvStartCost.setText(decimalFormat.format(sanPhamDauGia.giaSP) + "đ");
        String formatTmp = decimalFormat.format(sanPhamDauGia.giaCaoNhat);
        tvCurentCost.setText(formatTmp + "đ");
        tvNameBuyer.setText(sanPhamDauGia.nguoiMua.hoten);
        long timeRemaining = sanPhamDauGia.tgianKthuc.getTime() - new Date().getTime();
        new CountDownTimer(timeRemaining, 1000) {
            @Override
            public void onTick(long l) {
                long second = l / 1000;
                long house = second / 3600;
                second -= house * 3600;
                long minute = second / 60;
                second -= minute * 60;
                String h = house + "";
                String m = minute < 10 ? (minute == 0 ? "00" : ("0" + minute)) : (minute + "");
                String s = second < 10 ? (second == 0 ? "00" : ("0" + second)) : (second + "");
                ;
                tvTimeRemaining.setText(h + ":" + m + ":" + s);
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }
    private void addController() {

        ivGavel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upDateHighestCost();
            }
        });
        tvCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void upDateHighestCost() {

    }
    private void loadImage(ArrayList<String> anhSP) {
        new MyAsyncTask().execute(anhSP);
    }

    public class MyAsyncTask extends AsyncTask<ArrayList<String>, Void, ArrayList<Bitmap>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showAnimationLoadPhoto();
        }

        @Override
        protected ArrayList<Bitmap> doInBackground(ArrayList<String>[] arrayLists) {
            ArrayList<String> listI = arrayLists[0];
            ArrayList<Bitmap> listBitmaps = new ArrayList<>();
            for (int i = 0; i < listI.size(); i++) {
                listBitmaps.add(ImageUtils.base64ToImage(listI.get(i)));
            }
            return listBitmaps;
        }

        @Override
        protected void onPostExecute(ArrayList<Bitmap> bitmaps) {
            super.onPostExecute(bitmaps);
            showImage(bitmaps);
        }
    }

    private void showAnimationLoadPhoto() {
        skLoadImage.setVisibility(View.VISIBLE);
        slImageProduct.setVisibility(View.GONE);
        pagerIndicator.setVisibility(View.GONE);
    }

    public void showImage(ArrayList<Bitmap> bitmaps) {
        for (Bitmap bitmap : bitmaps) {
            TextSliderView sliderView = new TextSliderView(getContext());
            try {
                File f = File.createTempFile("tmp", "png", getContext().getCacheDir());
                FileOutputStream fos = new FileOutputStream(f);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                sliderView.image(f)
                        .setScaleType(BaseSliderView.ScaleType.CenterCrop);
                slImageProduct.addSlider(sliderView);
            } catch (IOException io) {
            }
        }
        skLoadImage.setVisibility(View.GONE);
        slImageProduct.setVisibility(View.VISIBLE);
        pagerIndicator.setVisibility(View.VISIBLE);
        slImageProduct.setCustomIndicator(pagerIndicator);
    }
    private TextWatcher onTextChangedListener(final EditText editText) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                editText.removeTextChangedListener(this);
                try {
                    String tmp = editable.toString();

                    Long longVar;
                    if (tmp.contains(",")) {
                        tmp = tmp.replaceAll(",", "");
                    }
                    longVar = Long.parseLong(tmp);

                    DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    decimalFormat.applyPattern("#,###,###");

                    String formatTmp = decimalFormat.format(longVar);
                    editText.setText(formatTmp);
                    editText.setSelection(editText.getText().length());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                editText.addTextChangedListener(this);
            }
        };
    }
    private void testFireBase() {
        showAnimationLoadPhoto();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Auction").child("Đồ gia dụng");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot spSnapSort : dataSnapshot.getChildren()) {
                    sanPhamDauGia = spSnapSort.getValue(SanPhamDauGia.class);
                    Log.d(TAG, "onDataChange: " + sanPhamDauGia.toString());
                }
                Date tmp = new Date();
                tmp.setTime(System.currentTimeMillis() + 3 * 3600 * 1000);
                sanPhamDauGia.tgianKthuc = tmp;

                loadData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
