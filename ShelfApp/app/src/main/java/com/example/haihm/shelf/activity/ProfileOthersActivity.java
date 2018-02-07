package com.example.haihm.shelf.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.adapters.ViewPagerHistoryAdapter;
import com.example.haihm.shelf.event.OnClickShowProfileEvent;
import com.example.haihm.shelf.model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ProfileOthersActivity extends AppCompatActivity {
    private static final String TAG = "ProfileOthersActivity";
    ImageView ivBack,ivCall,ivStar,ivAvatar;
    TextView tvName,tvAddress,tvPhoneNumber;
    RatingBar ratingBar;
    TabLayout tabLayout;
    ViewPager viewPager;
    String id;
    public static UserModel userModel = new UserModel();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_others);
        EventBus.getDefault().register(this);
        setupUI();
        loadDataFireBase();
        addController();
    }
    @Subscribe(sticky = true)
    public void OnReceviedIdUser(OnClickShowProfileEvent onClickShowProfileEvent){
        id = onClickShowProfileEvent.id;
    }
    private void setupUI() {
        ivBack = findViewById(R.id.iv_back);
        ivStar = findViewById(R.id.iv_star);
        ivCall = findViewById(R.id.iv_call);
        ivAvatar = findViewById(R.id.iv_avatar);
        tvAddress = findViewById(R.id.tv_address);
        tvAddress = findViewById(R.id.tv_phone_number);
        tvName = findViewById(R.id.tv_name);
        ratingBar = findViewById(R.id.rb_rate);
        tabLayout = findViewById(R.id.tab_history);
        viewPager = findViewById(R.id.vp_history);

        tabLayout.addTab(tabLayout.newTab().setText("Rao Vặt"));
        tabLayout.addTab(tabLayout.newTab().setText("Đấu giá"));


    }
    public void loadDataFireBase(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("UserInfo");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userModel = dataSnapshot.child(id).getValue(UserModel.class);
                Log.d(TAG, "onDataChange: "+userModel.anhAvatar);
                setData();

                ViewPagerHistoryAdapter viewPagerHistoryAdapter = new ViewPagerHistoryAdapter(getSupportFragmentManager());
                viewPager.setAdapter(viewPagerHistoryAdapter);
                viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setData() {
        try{
            Picasso.with(this).load(userModel.anhAvatar)
                    .transform(new CropCircleTransformation()).into(ivAvatar);
            tvName.setText(userModel.hoten);
            if(userModel.diaC.equals("")|| userModel.diaC == null){
                tvAddress.setVisibility(View.INVISIBLE);
            }else tvAddress.setText(userModel.diaC);
            tvPhoneNumber.setText(userModel.sdt);
            Log.d(TAG, "setData: "+userModel.anhAvatar);
            float rate = userModel.rate.tongLuotVote ==0 ? 0 :  userModel.rate.tongD / userModel.rate.tongLuotVote;
            ratingBar.setRating(rate);

            ivCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + userModel.sdt));
                    if (ActivityCompat.checkSelfPermission(ProfileOthersActivity.this, Manifest.permission.CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ProfileOthersActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                1);
                        return;
                    } else startActivity(callIntent);
                }
            });
        }catch (Exception e){e.printStackTrace();}
    }

    private void addController() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        ivStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCardView();
            }
        });
    }

    private void showCardView() {
        LayoutInflater li = getLayoutInflater();
        View v = li.inflate(R.layout.rating_layout_dialog,null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v);
        final AlertDialog alertDialog = builder.create();

        TextView tvSendRate = v.findViewById(R.id.tv_send_rate);
        final RatingBar setRate = v.findViewById(R.id.rb_set_rate);
        ImageView ivClose = v.findViewById(R.id.iv_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
        alertDialog.show();

        tvSendRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userModel.rate.tongD+= setRate.getRating();
                userModel.rate.tongLuotVote++;

                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                firebaseDatabase.getReference("UserInfo").child(userModel.id).child("rate").setValue(userModel.rate);

                alertDialog.cancel();
            }
        });
    }
}
