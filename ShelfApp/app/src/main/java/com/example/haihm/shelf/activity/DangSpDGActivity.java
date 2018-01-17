package com.example.haihm.shelf.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.adapters.ThemAnhSPAdapter;
import com.example.haihm.shelf.event.OnClickAddPhotoEvent;
import com.example.haihm.shelf.event.OnClickAddSanPhamEvent;
import com.example.haihm.shelf.model.SanPhamDauGia;
import com.example.haihm.shelf.model.UserModel;
import com.example.haihm.shelf.utils.ImageUtils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DangSpDGActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DangSpDGActivity";
    EditText etTenSP,etgiaSP,etDiaC,etMoTaSP,etBuocG;
    HashMap<String,String> lanhSP;
    ImageView ivBack,ivDone,ivPhoto1,ivPhoto2,ivPhoto3,ivPhoto4,ivPhoto5;
    TextView tvHomeAppliance,tvCar,tvFashion,tvTechnology,tvBeauty,tvFuniture,tvOther,tv3h,tv6h,tv12h,tv24h;
    UserModel userModel;
    //
    String checkPhoto = "";
    int tgianDG = 3;
    String loaiSP = "Home appliance";
    //
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_sp_dg);
        EventBus.getDefault().register(this);
        setupUI();
        addController();
    }
    private void setupUI() {
        etTenSP = findViewById(R.id.et_ten_sp);
        etgiaSP = findViewById(R.id.et_gia);
        etBuocG = findViewById(R.id.et_buoc_gia);
        etDiaC = findViewById(R.id.et_diaC);
        etMoTaSP = findViewById(R.id.et_mo_ta);

        ivDone = findViewById(R.id.iv_done);
        ivBack = findViewById(R.id.iv_back);
        ivPhoto1 = findViewById(R.id.iv_photo1);
        ivPhoto2 = findViewById(R.id.iv_photo2);
        ivPhoto3 = findViewById(R.id.iv_photo3);
        ivPhoto4 = findViewById(R.id.iv_photo4);
        ivPhoto5 = findViewById(R.id.iv_photo5);

        tvHomeAppliance = findViewById(R.id.tv_home_appliance);
        tvTechnology = findViewById(R.id.tv_technology);
        tvCar = findViewById(R.id.tv_car);
        tvBeauty = findViewById(R.id.tv_beauty);
        tvFashion = findViewById(R.id.tv_fashion);
        tvFuniture = findViewById(R.id.tv_furniture);
        tvOther = findViewById(R.id.tv_other);

        tv3h = findViewById(R.id.tv_3h);
        tv6h = findViewById(R.id.tv_6h);
        tv12h = findViewById(R.id.tv_12h);
        tv24h = findViewById(R.id.tv_24h);
        //
        lanhSP = new HashMap<>();
        lanhSP.put("1","");
        lanhSP.put("2","");
        lanhSP.put("3","");
        lanhSP.put("4","");
        lanhSP.put("5","");
        //
        userModel =  new UserModel();
        //
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Auction");
        //format GiaSP va bướcG
        etBuocG.addTextChangedListener(onTextChangedListener(etBuocG));
        etgiaSP.addTextChangedListener(onTextChangedListener(etgiaSP));



    }
    private void addController() {
        ivDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DangSPDG();
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ivPhoto1.setOnClickListener(this);
        ivPhoto2.setOnClickListener(this);
        ivPhoto3.setOnClickListener(this);
        ivPhoto4.setOnClickListener(this);
        ivPhoto5.setOnClickListener(this);
        //
        tvHomeAppliance.setOnClickListener(this);
        tvTechnology.setOnClickListener(this);
        tvFashion.setOnClickListener(this);
        tvCar.setOnClickListener(this);
        tvBeauty.setOnClickListener(this);
        tvFuniture.setOnClickListener(this);
        tvOther.setOnClickListener(this);
        //
        tv3h.setOnClickListener(this);
        tv6h.setOnClickListener(this);
        tv12h.setOnClickListener(this);
        tv24h.setOnClickListener(this);
    }

    private void DangSPDG() {
        if(TextUtils.isEmpty(etTenSP.getText())){
            etTenSP.setError("Cannot be empty");
            return;
        }
        if(TextUtils.isEmpty(etMoTaSP.getText())){
            etMoTaSP.setError("Cannot be empty");
            return;
        }
        if(TextUtils.isEmpty(etgiaSP.getText())){
            etgiaSP.setError("Cannot be empty");
            return;
        }
        if(TextUtils.isEmpty(etBuocG.getText())){
            etBuocG.setError("Cannot be empty");
            return;
        }
        if(TextUtils.isEmpty(etDiaC.getText())){
            etDiaC.setError("Cannot be empty");
            return;
        }

        if(lanhSP.size()==1) {
            Toast.makeText(this,"Add photos!",Toast.LENGTH_SHORT).show();
            return;
        }
        double giaSP = Double.parseDouble(etgiaSP.getText().toString().replaceAll(",",""));
        double buocG = Double.parseDouble(etBuocG.getText().toString().replaceAll(",",""));

        SanPhamDauGia sanPhamDauGia = new SanPhamDauGia(userModel.id,etTenSP.getText().toString(),
                lanhSP,giaSP,
                etMoTaSP.getText().toString(),loaiSP,
                userModel.hoten,userModel.sdt,etDiaC.getText().toString(),
                buocG,giaSP,tgianDG,new SanPhamDauGia.NguoiMua());

        databaseReference.child(loaiSP).push().setValue(sanPhamDauGia);
        Toast.makeText(this,"Post auction successfully",Toast.LENGTH_SHORT).show();
        finish();
    }
    @Subscribe(sticky = true)
    public void OnReceivedOnClickAddSanPhamEvent(OnClickAddSanPhamEvent onClickAddSanPhamEvent){
        userModel = onClickAddSanPhamEvent.userModel;
        etDiaC.setText(userModel.diaC);
    }
    private void selectFuntion() {
        final String[] item = {"Open camera", "Open the library", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(DangSpDGActivity.this);
        builder.setTitle("Add photos");
        builder.setItems(item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(item[i].equals("Open camera")){
                    cameraIntent();
                }
                else if(item[i].equals("Open the library")){
                    galleryIntent();
                }
                else{
                    dialogInterface.dismiss();
                }
            }
        }).show();

    }
    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*"); // mở tất cả các folder lưa trữ ảnh
        intent.setAction(Intent.ACTION_GET_CONTENT); // đi đến folder mình chọn
        startActivityForResult(Intent.createChooser(intent, "Chọn Ảnh"), 1);
    }

    private void cameraIntent() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = ImageUtils.getUriFromImage(this);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        //check xem co ton tai intent nao khong

        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, 2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Bitmap bitmap = null;
                Log.d(TAG, "onActivityResult: ");
                if (data != null) {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e(TAG, "Data Null!!!!");
                }
                lanhSP.put(checkPhoto,ImageUtils.endcodeImageToBase64(bitmap));
                showPhoto(checkPhoto);
            }
            else if(requestCode == 2){
                Bitmap bitmap = null;
                if (resultCode == RESULT_OK) {
                    Log.e("check request", "I'm here");
                    bitmap = ImageUtils.getBitmap(this);
                    lanhSP.put(checkPhoto,ImageUtils.endcodeImageToBase64(bitmap));
                    showPhoto(checkPhoto);
                }

            }

        }
    }

    private void showPhoto(String i) {
        String photoBase64 = lanhSP.get(i);
        Log.d(TAG, "showPhoto: "+photoBase64);
        if(photoBase64.equals("")) return;
        else {
            switch (i){
                case "1": {
                    ivPhoto1.setImageBitmap(ImageUtils.base64ToImage(photoBase64));
                    ivPhoto1.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                }
                case "2": {
                    ivPhoto2.setImageBitmap(ImageUtils.base64ToImage(photoBase64));
                    ivPhoto2.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                }
                case "3": {
                    ivPhoto3.setImageBitmap(ImageUtils.base64ToImage(photoBase64));
                    ivPhoto3.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                }
                case "4": {
                    ivPhoto4.setImageBitmap(ImageUtils.base64ToImage(photoBase64));
                    ivPhoto4.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                }
                case "5": {
                    ivPhoto5.setImageBitmap(ImageUtils.base64ToImage(photoBase64));
                    ivPhoto5.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                }
            }
        }
    }

    private TextWatcher onTextChangedListener(final EditText editText){
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
                    if(tmp.contains(",")){
                        tmp = tmp.replaceAll(",","");
                    }
                    longVar = Long.parseLong(tmp);

                    DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    decimalFormat.applyPattern("#,###,###");

                    String formatTmp = decimalFormat.format(longVar);
                    editText.setText(formatTmp);
                    editText.setSelection(editText.getText().length());

                }catch (Exception e){
                    e.printStackTrace();
                }
                editText.addTextChangedListener(this);
            }
        };
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_photo1:{
                checkPhoto = "1";
                selectFuntion();
                break;
            }
            case R.id.iv_photo2:{
                checkPhoto = "2";
                selectFuntion();
                break;
            }
            case R.id.iv_photo3:{
                checkPhoto = "3";
                selectFuntion();
                break;
            }
            case R.id.iv_photo4:{
                checkPhoto = "4";
                selectFuntion();
                break;
            }
            case R.id.iv_photo5:{
                checkPhoto = "5";
                selectFuntion();
                break;
            }
            case R.id.tv_home_appliance:{
                loaiSP = tvHomeAppliance.getText().toString();
                selectCategory();
                break;
            }
            case R.id.tv_technology:{
                loaiSP = tvTechnology.getText().toString();
                selectCategory();
                break;
            }
            case R.id.tv_fashion:{
                loaiSP = tvFashion.getText().toString();
                selectCategory();
                break;
            }
            case R.id.tv_car:{
                loaiSP = tvCar.getText().toString();
                selectCategory();
                break;
            }
            case R.id.tv_beauty:{
                loaiSP = tvBeauty.getText().toString();
                selectCategory();
                break;
            }
            case R.id.tv_furniture:{
                loaiSP = tvFuniture.getText().toString();
                selectCategory();
                break;
            }
            case R.id.tv_other:{
                loaiSP = tvOther.getText().toString();
                selectCategory();
                break;
            }
            case R.id.tv_3h:{
                tgianDG = 3;
                selectTime();
                break;
            }
            case R.id.tv_6h:{
                tgianDG = 6;
                selectTime();
                break;
            }
            case R.id.tv_12h:{
                tgianDG = 12;
                selectTime();
                break;
            }
            case R.id.tv_24h:{
                tgianDG = 24;
                selectTime();
                break;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void selectTime() {
        if(tgianDG == 3){
            tv3h.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_check));
            tv6h.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tv12h.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tv24h.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
        }
        if(tgianDG == 6){
            tv3h.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tv6h.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_check));
            tv12h.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tv24h.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
        }
        if(tgianDG == 12){
            tv3h.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tv6h.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tv12h.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_check));
            tv24h.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
        }
        if(tgianDG == 24){
            tv3h.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tv6h.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tv12h.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tv24h.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_check));
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void selectCategory() {
        if(loaiSP.equals(tvHomeAppliance.getText().toString())){
            tvHomeAppliance.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_check));
            //
            tvTechnology.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvFashion.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvCar.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvBeauty.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvFuniture.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvOther.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
        }
        if(loaiSP.equals(tvTechnology.getText().toString())){
            tvTechnology.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_check));
            //
            tvHomeAppliance.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvFashion.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvCar.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvBeauty.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvFuniture.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvOther.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
        }
        if(loaiSP.equals(tvFashion.getText().toString())){
            tvFashion.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_check));
            //
            tvTechnology.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvHomeAppliance.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvCar.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvBeauty.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvFuniture.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvOther.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
        }
        if(loaiSP.equals(tvCar.getText().toString())){
            tvCar.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_check));
            //
            tvTechnology.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvFashion.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvHomeAppliance.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvBeauty.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvFuniture.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvOther.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
        }
        if(loaiSP.equals(tvBeauty.getText().toString())){
            tvBeauty.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_check));
            //
            tvTechnology.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvFashion.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvCar.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvHomeAppliance.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvFuniture.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvOther.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
        }
        if(loaiSP.equals(tvFuniture.getText().toString())){
            tvFuniture.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_check));
            //
            tvTechnology.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvFashion.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvCar.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvBeauty.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvHomeAppliance.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvOther.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
        }
        if(loaiSP.equals(tvOther.getText().toString())){
            tvOther.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_check));
            //
            tvTechnology.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvFashion.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvCar.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvBeauty.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvFuniture.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
            tvHomeAppliance.setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
        }
    }

}
