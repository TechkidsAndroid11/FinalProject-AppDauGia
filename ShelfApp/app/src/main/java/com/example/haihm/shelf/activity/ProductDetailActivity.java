package com.example.haihm.shelf.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.example.haihm.shelf.R;
import com.example.haihm.shelf.event.OnClickProductEvent;
import com.example.haihm.shelf.model.SanPhamDauGia;
import com.example.haihm.shelf.model.SanPhamRaoVat;
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
import java.util.Locale;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ProductDetailActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    private static final String TAG = "ProductDetailActivity";
    ImageView ivBack, ivAvatar;
    TextView tvAddress, tvNameSeller, tvDescription, tvProductPrice, tvCall, tvNameProduct;
    RatingBar ratingBar;
    SliderLayout slImageProduct;
    PagerIndicator pagerIndicator;
    SpinKitView skLoadImage;
    SanPhamRaoVat sanPhamRaoVat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        EventBus.getDefault().register(this);
//        testFireBase();
        setUpUI();
        loadData();
        addController();
    }

    @Subscribe(sticky = true)
    public void OnReceivedClickProductEvent(OnClickProductEvent onClickProductEvent) {
        sanPhamRaoVat = onClickProductEvent.sanPhamRaoVat;
    }

    private void setUpUI() {
        ivBack = findViewById(R.id.iv_back);
        ivAvatar = findViewById(R.id.iv_avatar_seller);
        tvAddress = findViewById(R.id.tv_address);
        tvNameSeller = findViewById(R.id.tv_name_seller);
        tvDescription = findViewById(R.id.tv_description_product);
        tvProductPrice = findViewById(R.id.tv_product_price);
        tvNameProduct = findViewById(R.id.tv_name_product);
        tvCall = findViewById(R.id.tv_call_buy);
        ratingBar = findViewById(R.id.rb_rating);
        slImageProduct = findViewById(R.id.sl_image_product);
        pagerIndicator = findViewById(R.id.pi_indicator);
        skLoadImage = findViewById(R.id.sk_load_image);
    }

    private void loadData() {
        try{
            loadImage(sanPhamRaoVat.anhSP);
            tvAddress.setText(sanPhamRaoVat.diaGD);
            tvNameProduct.setText(sanPhamRaoVat.tenSP);
            //
            DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            decimalFormat.applyPattern("#,###,###");
            String formatTmp = decimalFormat.format(sanPhamRaoVat.giaSP);
            tvProductPrice.setText(formatTmp + "đ");
            //
            tvNameSeller.setText(sanPhamRaoVat.nguoiB.hoten);
            tvDescription.setText(sanPhamRaoVat.motaSP);
            //
            float rate = (float) ((float) sanPhamRaoVat.nguoiB.rate.tongD / sanPhamRaoVat.nguoiB.rate.tongLuotVote);
            ratingBar.setRating(rate);
            //
            Picasso.with(this).load(sanPhamRaoVat.nguoiB.anhAvatar)
                    .transform(new CropCircleTransformation()).into(ivAvatar);
        }catch (Exception e ){e.printStackTrace();}
    }

    private void addController() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tvCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + sanPhamRaoVat.nguoiB.sdt));
                if (ActivityCompat.checkSelfPermission(ProductDetailActivity.this, Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ProductDetailActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            1);
                    return;
                } else startActivity(callIntent);
            }
        });
    }

    private void testFireBase() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Auction").child("Đồ gia dụng");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot spSnapSort : dataSnapshot.getChildren()) {
                    SanPhamDauGia sanPhamDauGia = spSnapSort.getValue(SanPhamDauGia.class);
                    Log.d(TAG, "onDataChange: " + sanPhamDauGia.toString());
                    sanPhamRaoVat = sanPhamDauGia;
                }
                loadData();
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
            TextSliderView sliderView = new TextSliderView(this);
            try {
                File f = File.createTempFile("tmp", "png", getCacheDir());
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

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
