package com.example.haihm.shelf.activity;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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
import com.example.haihm.shelf.event.OnClickAuctionEvent;
import com.example.haihm.shelf.model.SanPhamDauGia;
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

public class AuctionDetailsActivity extends AppCompatActivity {
    private static final String TAG = "AuctionDetailsActivity";
    RelativeLayout rlDescriopAuction, rlHightestCostMaster, rlHightestCostGuest, rlChat;

    ImageView ivBack, ivAvatarSeller;
    RatingBar ratingBar;
    SliderLayout slImageProduct;
    PagerIndicator pagerIndicator;
    SpinKitView skLoadImage;
    TextView tvAddress, tvNameSeller, tvDescription, tvStartCost, tvNameAuction;

    TextView tvNameBuyer, tvCurentCost, tvTimeRemaining, tvCall;
    EditText etInputCost;
    ImageView ivGavel;

    RecyclerView rvChat;
    EditText etChat;
    ImageView ivSend, ivMyAvata;

    SanPhamDauGia sanPhamDauGia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auction_details);
        EventBus.getDefault().register(this);
        setUpUI();
        testFireBase();
//        loadData();
        addController();
    }
    @Subscribe(sticky = true)
    public void OnReceivedClickAuctionEvent(OnClickAuctionEvent onClickAuctionEvent) {
        sanPhamDauGia = onClickAuctionEvent.sanPhamDauGia;
    }

    private void setUpUI() {
        ivBack = findViewById(R.id.iv_back);
        tvNameAuction = findViewById(R.id.tv_name_auction);
        rlDescriopAuction = findViewById(R.id.icl_des_auction);
        rlHightestCostMaster = findViewById(R.id.icl_highest_cost_master);
        rlHightestCostGuest = findViewById(R.id.icl_highest_cost_guest);
        rlChat = findViewById(R.id.icl_chat);

        tvNameSeller = rlDescriopAuction.findViewById(R.id.tv_name_seller);
        tvAddress = rlDescriopAuction.findViewById(R.id.tv_address);
        tvDescription = rlDescriopAuction.findViewById(R.id.tv_description);
        tvStartCost = rlDescriopAuction.findViewById(R.id.tv_start_cost);
        ivAvatarSeller = rlDescriopAuction.findViewById(R.id.iv_avatar_seller);
        ratingBar = rlDescriopAuction.findViewById(R.id.rb_rate);
        slImageProduct = rlDescriopAuction.findViewById(R.id.sl_image_product);
        pagerIndicator = rlDescriopAuction.findViewById(R.id.pi_indicator);
        skLoadImage = rlDescriopAuction.findViewById(R.id.sk_load_image);
        if (myAution()) {
            rlHightestCostGuest.setVisibility(View.GONE);

            tvNameBuyer = rlHightestCostMaster.findViewById(R.id.tv_name_seller);
            tvTimeRemaining = rlHightestCostMaster.findViewById(R.id.tv_time_remaining);
            tvCurentCost = rlHightestCostMaster.findViewById(R.id.tv_current_cost);

        } else {
            rlHightestCostMaster.setVisibility(View.GONE);

            tvNameBuyer = rlHightestCostGuest.findViewById(R.id.tv_name_seller);
            tvTimeRemaining = rlHightestCostGuest.findViewById(R.id.tv_time_remaining);
            tvCurentCost = rlHightestCostGuest.findViewById(R.id.tv_current_cost);
        }
        tvCall = rlHightestCostMaster.findViewById(R.id.tv_call);
        etInputCost = rlHightestCostGuest.findViewById(R.id.et_input_cost);
        ivGavel = rlHightestCostGuest.findViewById(R.id.iv_gavel);

        rvChat = rlChat.findViewById(R.id.rv_chat);
        ivSend = rlChat.findViewById(R.id.iv_send);
        etChat = rlChat.findViewById(R.id.et_chat);
        ivMyAvata = rlChat.findViewById(R.id.iv_my_avatar);
    }

    private boolean myAution() {
        return true;
    }

    private void loadData(){
        loadImage(sanPhamDauGia.anhSP);
        tvNameAuction.setText(sanPhamDauGia.tenSP);
        tvDescription.setText(sanPhamDauGia.motaSP);
        tvAddress.setText(tvAddress.getText()+sanPhamDauGia.diaGD);
        Picasso.with(this).load(sanPhamDauGia.nguoiB.anhAvatar)
                .transform(new CropCircleTransformation()).into(ivAvatarSeller);
        float rate = (float)sanPhamDauGia.nguoiB.rate.tongD/ (float)sanPhamDauGia.nguoiB.rate.tongLuotVote;
        ratingBar.setRating(rate);

        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        decimalFormat.applyPattern("#,###,###");

        tvStartCost.setText(decimalFormat.format(sanPhamDauGia.giaSP)+"đ");
        String formatTmp = decimalFormat.format(sanPhamDauGia.giaCaoNhat);
        tvCurentCost.setText(formatTmp+"đ");

        long timeRemaining = sanPhamDauGia.tgianKthuc.getTime() - new Date().getTime();
        new CountDownTimer(timeRemaining, 1000) {
            @Override
            public void onTick(long l) {
                long second = l/1000;
                long house = second/3600;second -=house*3600;
                long minute = second/60;second -=minute*60;
                String h = house+"";
                String m = minute<10?(minute == 0? "00": ("0"+minute)):(minute+"");
                String s = second<10?(second == 0? "00": ("0"+second)):(second+"");;
                tvTimeRemaining.setText(h+":"+m+":"+s);
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    private void addController() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ivGavel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upDateHighestCost();
            }
        });
        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        tvCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void sendMessage() {

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
            for(int i = 0; i < listI.size(); i++){
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
    public void showImage(ArrayList<Bitmap> bitmaps)  {
        for(Bitmap bitmap : bitmaps){
            TextSliderView sliderView = new TextSliderView(this);
            try {
                File f = File.createTempFile("tmp","png", getCacheDir());
                FileOutputStream fos = new FileOutputStream(f);
                bitmap.compress(Bitmap.CompressFormat.PNG,100,fos);
                fos.close();
                sliderView.image(f)
                        .setScaleType(BaseSliderView.ScaleType.CenterCrop);
                slImageProduct.addSlider(sliderView);
            }catch (IOException io){}
        }
        skLoadImage.setVisibility(View.GONE);
        slImageProduct.setVisibility(View.VISIBLE);
        pagerIndicator.setVisibility(View.VISIBLE);
        slImageProduct.setCustomIndicator(pagerIndicator);
    }
    private void testFireBase(){
        showAnimationLoadPhoto();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Auction").child("Đồ gia dụng");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot spSnapSort : dataSnapshot.getChildren()){
                    sanPhamDauGia = spSnapSort.getValue(SanPhamDauGia.class);
                    Log.d(TAG, "onDataChange: "+sanPhamDauGia.toString());
                }
                Date tmp = new Date();
                tmp.setTime(System.currentTimeMillis() + 3*3600*1000);
                sanPhamDauGia.tgianKthuc = tmp;

                loadData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
