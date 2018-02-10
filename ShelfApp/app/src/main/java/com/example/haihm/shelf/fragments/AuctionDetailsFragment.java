package com.example.haihm.shelf.fragments;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
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
import com.example.haihm.shelf.activity.ProfileOthersActivity;
import com.example.haihm.shelf.event.OnClickShowProfileEvent;
import com.example.haihm.shelf.event.OnClickUserModelEvent;
import com.example.haihm.shelf.model.SanPhamDauGia;
import com.example.haihm.shelf.model.UserModel;
import com.example.haihm.shelf.utils.ImageUtils;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
    private UserModel userModel = new UserModel();
    private UserModel seller = new UserModel(), buyer = new UserModel();

    public AuctionDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_auction_details, container, false);
        EventBus.getDefault().register(this);
        setUpUI(view);
//        testFireBase();
        loadData();
        addController();
        return view;
    }

    @Subscribe(sticky = true)
    public void ReceivedUserModel(OnClickUserModelEvent onClickUserModelEvent) {
        userModel = onClickUserModelEvent.userModel;
        Log.d(TAG, "ReceivedUserModel: " + userModel.sdt);
    }

    private void setUpUI(View view) {
        sanPhamDauGia = AuctionDetailsActivity.sanPhamDauGia;
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
        try {
            return sanPhamDauGia.nguoiB.equals(userModel.id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void loadBuyerFireBase() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("UserInfo");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange_loadBuyerFireBase: ");
                buyer = dataSnapshot.child(sanPhamDauGia.nguoiMua).getValue(UserModel.class);
                if (buyer == null) {
                    buyer = new UserModel();
                }
                tvNameBuyer.setText(buyer.hoten);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadSellerFireBase() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("UserInfo");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange_loadSellerFireBase: ");
                seller = dataSnapshot.child(sanPhamDauGia.nguoiB).getValue(UserModel.class);
                if (seller == null) {
                    seller = new UserModel();
                }
                tvNameSeller.setText(seller.hoten);
                Picasso.with(getContext()).load(seller.anhAvatar)
                        .transform(new CropCircleTransformation()).into(ivAvatarSeller);
                float rate = seller.rate.tongLuotVote == 0 ? 0 : seller.rate.tongD / seller.rate.tongLuotVote;
                ratingBar.setRating(rate);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadData() {

        loadImage(sanPhamDauGia.anhSP);
        loadSellerFireBase();
        loadBuyerFireBase();

        tvDescription.setText(sanPhamDauGia.motaSP);
        tvAddress.setText(sanPhamDauGia.diaGD);
        etInputCost.setHint("Tăng tối thiểu "+sanPhamDauGia.buocGia+" đ");

        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        decimalFormat.applyPattern("#,###,###");

        tvStartCost.setText(decimalFormat.format(sanPhamDauGia.giaSP) + "đ");
        String formatTmp = decimalFormat.format(sanPhamDauGia.giaCaoNhat);
        tvCurentCost.setText(formatTmp + "đ");

        long timeRemaining = sanPhamDauGia.tgianKthuc.getTime() - new Date().getTime();
        if (timeRemaining >= 0) {
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
                    tvTimeRemaining.setText("Hết giờ");
                    etInputCost.setVisibility(View.GONE);
                    ivGavel.setVisibility(View.GONE);
                }
            }.start();
        } else {
            tvTimeRemaining.setText("Hết giờ");
            etInputCost.setVisibility(View.GONE);
            ivGavel.setVisibility(View.GONE);
        }
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
                if (buyer.sdt.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(true);

                    builder.setMessage("Chưa ai trả giá!!");

                    builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.show();
                    return;
                }
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + buyer.sdt));
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CALL_PHONE},
                            1);
                    return;
                } else startActivity(callIntent);
            }
        });
        //change
        loadCurentCost();
        //
        tvNameSeller.setOnClickListener(showProfile(sanPhamDauGia.nguoiB));
        ratingBar.setOnClickListener(showProfile(sanPhamDauGia.nguoiB));
        ivAvatarSeller.setOnClickListener(showProfile(sanPhamDauGia.nguoiB));

        tvNameBuyer.setOnClickListener(showProfile(sanPhamDauGia.nguoiMua));
    }

    private View.OnClickListener showProfile(final String idUser) {
        if(userModel.id.equals(idUser)) return null;
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().postSticky(new OnClickShowProfileEvent(idUser));
                Intent intent = new Intent(getActivity(), ProfileOthersActivity.class);
                startActivity(intent);
                Log.d(TAG, "onClick: ");
            }
        };
    }

    private void upDateHighestCost() {
        String newCost = etInputCost.getText().toString().replaceAll(",", "");
        try {
            double newC = Double.parseDouble(newCost);
            if (newC - sanPhamDauGia.giaCaoNhat < sanPhamDauGia.buocGia) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(true);

                builder.setMessage("Giá mới phải lớn hơn giá hiện tại + bước giá!!");

                builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.show();
            } else {
                final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference("Auction").child(sanPhamDauGia.loaiSP)
                        .child(sanPhamDauGia.idSP).child("giaCaoNhat");

                databaseReference.setValue(newC);
                //
                databaseReference = firebaseDatabase.getReference("Auction").child(sanPhamDauGia.loaiSP)
                        .child(sanPhamDauGia.idSP).child("nguoiMua");
                databaseReference.setValue(userModel.id);
                //
                DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                decimalFormat.applyPattern("#,###,###");
                etInputCost.setText("");
                //cap nhat phien đang tham gia
                final ArrayList<String> listJoinAuction = new ArrayList<>();
                DatabaseReference databaseReference1 = firebaseDatabase.getReference("UserInfo").child(userModel.id)
                        .child("listJoinAuction");
                databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange_check: "+dataSnapshot);
                        for(DataSnapshot idAuction : dataSnapshot.getChildren()){
                            String id = idAuction.getValue(String.class);
                            Log.d(TAG, "onDataChange_join: "+id);
                            listJoinAuction.add(id);
                        }
                        if(!checkExist(listJoinAuction,sanPhamDauGia.idSP)){
                            listJoinAuction.add(sanPhamDauGia.idSP);
                            firebaseDatabase.getReference("UserInfo").child(userModel.id)
                                    .child("listJoinAuction").setValue(listJoinAuction);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private boolean checkExist(ArrayList<String> listJoinAuction, String id) {
        for(int i = 0; i < listJoinAuction.size(); i++){
            if(listJoinAuction.get(i).equals(id)){
                return true;
            }
        }
        return false;
    }

    private void loadCurentCost() {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Auction").child(sanPhamDauGia.loaiSP)
                .child(sanPhamDauGia.idSP);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //giá
                double curentCost = Double.parseDouble(dataSnapshot.child("giaCaoNhat").getValue().toString());
                DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                decimalFormat.applyPattern("#,###,###");
                tvCurentCost.setText(decimalFormat.format(curentCost) + "đ");
                sanPhamDauGia.giaCaoNhat = curentCost;
                Log.d(TAG, "onDataChange: " + dataSnapshot.child("giaCaoNhat").getValue());

                //người ra giá cao nhất

                String idNguoiMua = dataSnapshot.child("nguoiMua").getValue().toString();
                sanPhamDauGia.nguoiMua = idNguoiMua;
                loadBuyerFireBase();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
