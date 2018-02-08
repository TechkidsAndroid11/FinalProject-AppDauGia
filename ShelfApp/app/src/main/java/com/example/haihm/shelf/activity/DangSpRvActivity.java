package com.example.haihm.shelf.activity;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.event.OnClickAddSanPhamEvent;
import com.example.haihm.shelf.event.OnClickUserModelEvent;
import com.example.haihm.shelf.model.SanPhamRaoVat;
import com.example.haihm.shelf.model.UserModel;
import com.example.haihm.shelf.utils.ImageUtils;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class DangSpRvActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DangSpRvActivity";
    EditText etTenSP, etgiaSP, etDiaC, etMoTaSP;
    ImageView ivBack, ivPhoto1, ivPhoto2, ivPhoto3, ivPhoto4, ivPhoto5;
    ArrayList<SpinKitView> lskPhoto = new ArrayList<>();
    ArrayList<ImageView> livPhoto = new ArrayList<>();
    SpinKitView skPhoto1, skPhoto2, skPhoto3, skPhoto4, skPhoto5;
    TextView tvHomeAppliance, ivDone, tvCar, tvFashion, tvTechnology, tvBeauty, tvFuniture, tvOther;
    HashMap<String, String> lanhSP;

    UserModel userModel;
    MyAsyncTask myAsyncTask;
    //
    String checkPhoto = "";
    String loaiSP = "Đồ gia dụng";
    //
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_sp_rv);
        EventBus.getDefault().register(this);
        setupUI();
        addController();
    }

    private void setupUI() {
        etTenSP = findViewById(R.id.et_ten_sp);
        etgiaSP = findViewById(R.id.et_gia);
        etDiaC = findViewById(R.id.et_diaC);
        etMoTaSP = findViewById(R.id.et_mo_ta);

        ivDone = findViewById(R.id.iv_done);
        ivBack = findViewById(R.id.iv_back);
        ivPhoto1 = findViewById(R.id.iv_photo1);
        ivPhoto2 = findViewById(R.id.iv_photo2);
        ivPhoto3 = findViewById(R.id.iv_photo3);
        ivPhoto4 = findViewById(R.id.iv_photo4);
        ivPhoto5 = findViewById(R.id.iv_photo5);

        skPhoto1 = findViewById(R.id.sk_photo1);
        skPhoto2 = findViewById(R.id.sk_photo2);
        skPhoto3 = findViewById(R.id.sk_photo3);
        skPhoto4 = findViewById(R.id.sk_photo4);
        skPhoto5 = findViewById(R.id.sk_photo5);

        lskPhoto.add(skPhoto1);
        lskPhoto.add(skPhoto2);
        lskPhoto.add(skPhoto3);
        lskPhoto.add(skPhoto4);
        lskPhoto.add(skPhoto5);
        livPhoto.add(ivPhoto1);
        livPhoto.add(ivPhoto2);
        livPhoto.add(ivPhoto3);
        livPhoto.add(ivPhoto4);
        livPhoto.add(ivPhoto5);

        tvHomeAppliance = findViewById(R.id.tv_home_appliance);
        tvTechnology = findViewById(R.id.tv_technology);
        tvCar = findViewById(R.id.tv_car);
        tvBeauty = findViewById(R.id.tv_beauty);
        tvFashion = findViewById(R.id.tv_fashion);
        tvFuniture = findViewById(R.id.tv_furniture);
        tvOther = findViewById(R.id.tv_other);

        lanhSP = new HashMap<>();
        lanhSP.put("1", "");
        lanhSP.put("2", "");
        lanhSP.put("3", "");
        lanhSP.put("4", "");
        lanhSP.put("5", "");
        //
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Product");
        //format giá sản phẩm
        etgiaSP.addTextChangedListener(onTextChangedListener());
    }

    private void addController() {
        ivDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DangSp();
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
    }

    private void DangSp() {
        if (TextUtils.isEmpty(etTenSP.getText())) {
            etTenSP.setError("Không được để trống");
            return;
        }
        if (TextUtils.isEmpty(etMoTaSP.getText())) {
            etMoTaSP.setError("Không được để trống");
            return;
        }
        if (etMoTaSP.getText().toString().length() < 60) {
            etMoTaSP.setError("Mô tả không được dưới 120 ký tự");
            return;
        }
        if (TextUtils.isEmpty(etgiaSP.getText())) {
            etgiaSP.setError("Không được để trống");
            return;
        }
        if (TextUtils.isEmpty(etDiaC.getText())) {
            etDiaC.setError("Không được để trống");
            return;
        }

        if (!checkListPhoto()) {
            Toast.makeText(this, "Thêm ảnh", Toast.LENGTH_SHORT).show();
            return;
        }
        double giaSP = Double.parseDouble(etgiaSP.getText().toString().replaceAll(",", ""));
        SanPhamRaoVat sanPhamRaoVat = new SanPhamRaoVat(etTenSP.getText().toString(), getList(lanhSP),
                giaSP,
                etMoTaSP.getText().toString(), loaiSP,
                etDiaC.getText().toString(),userModel.id);
        databaseReference.child(loaiSP).push().setValue(sanPhamRaoVat,new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseRefe) {
                Toast.makeText(DangSpRvActivity.this, "Rao bán thành công", Toast.LENGTH_SHORT).show();
                String keyProduct = databaseRefe.getKey();
                ArrayList<String> listProduct = userModel.listProduct;
                if (listProduct == null) {
                    listProduct = new ArrayList<>();
                }
                listProduct.add(keyProduct);
                Log.d(TAG, "onComplete: " + keyProduct);
                firebaseDatabase.getReference("UserInfo").child(userModel.id)
                        .child("listProduct").setValue(listProduct);
                userModel.listProduct = listProduct;
                EventBus.getDefault().postSticky(new OnClickUserModelEvent(userModel));
                finish();
            }
        });

    }

    private ArrayList<String> getList(HashMap<String, String> lanhSP) {
        ArrayList<String> list = new ArrayList<>();
        for (String i : lanhSP.keySet()) {
            String tmp = lanhSP.get(i);
            if (!tmp.equals("")) list.add(tmp);
        }
        return list;
    }

    private boolean checkListPhoto() {
        for (String i : lanhSP.keySet()) {
            String tmp = lanhSP.get(i);
            if (!tmp.equals("")) return true;
        }
        return false;
    }

    @Subscribe(sticky = true)
    public void OnReceivedOnClickAddSanPhamEvent(OnClickAddSanPhamEvent onClickAddSanPhamEvent) {
        userModel = onClickAddSanPhamEvent.userModel;
        Log.d(TAG, "OnReceivedOnClickAddSanPhamEvent: " + userModel.sdt);
    }

    private void selectFuntion() {
        final String[] item = {"Chụp ảnh", "Mở Bộ sưu tập", "Huỷ"};

        AlertDialog.Builder builder = new AlertDialog.Builder(DangSpRvActivity.this);
        builder.setTitle("Thêm Ảnh");
        builder.setItems(item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (item[i].equals("Chụp ảnh")) {
                    cameraIntent();
                } else if (item[i].equals("Mở Bộ sưu tập")) {
                    galleryIntent();
                } else {
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
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        //check xem co ton tai intent nao khong

        if (intent.resolveActivity(getPackageManager()) != null) {
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
                myAsyncTask = new MyAsyncTask();
                myAsyncTask.execute(bitmap);
            } else if (requestCode == 2) {
                Bitmap bitmap = null;
                if (resultCode == RESULT_OK) {
                    Log.e("check request", "I'm here");
                    bitmap = ImageUtils.getBitmap(this);
                    myAsyncTask = new MyAsyncTask();
                    myAsyncTask.execute(bitmap);
                }

            }

        }
    }

    public void showAnimationLoadPhoto(String i) {
        int vt = Integer.parseInt(i) - 1;
        for (int ii = 0; ii < lskPhoto.size(); ii++) {
            if (ii == vt) {
                lskPhoto.get(ii).setVisibility(View.VISIBLE);
                livPhoto.get(ii).setVisibility(View.INVISIBLE);
            } else {
                livPhoto.get(ii).setClickable(false);
            }
        }
    }

    public void showPhoto(String i, Bitmap bitmap) {
        int vt = Integer.parseInt(i) - 1;
        for (int ii = 0; ii < lskPhoto.size(); ii++) {
            if (ii == vt) {
                lskPhoto.get(ii).setVisibility(View.GONE);
                livPhoto.get(ii).setVisibility(View.VISIBLE);
                livPhoto.get(ii).setScaleType(ImageView.ScaleType.FIT_XY);
                livPhoto.get(ii).setImageBitmap(bitmap);
            } else {
                livPhoto.get(ii).setClickable(true);
            }
        }
    }

    private TextWatcher onTextChangedListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                etgiaSP.removeTextChangedListener(this);
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

                    etgiaSP.setText(formatTmp);
                    etgiaSP.setSelection(etgiaSP.getText().length());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                etgiaSP.addTextChangedListener(this);
            }
        };
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_photo1: {
                checkPhoto = "1";
                selectFuntion();
                break;
            }
            case R.id.iv_photo2: {
                checkPhoto = "2";
                selectFuntion();
                break;
            }
            case R.id.iv_photo3: {
                checkPhoto = "3";
                selectFuntion();
                break;
            }
            case R.id.iv_photo4: {
                checkPhoto = "4";
                selectFuntion();
                break;
            }
            case R.id.iv_photo5: {
                checkPhoto = "5";
                selectFuntion();
                break;
            }
            case R.id.tv_home_appliance: {
                loaiSP = tvHomeAppliance.getText().toString();
                selectCategory();
                break;
            }
            case R.id.tv_technology: {
                loaiSP = tvTechnology.getText().toString();
                selectCategory();
                break;
            }
            case R.id.tv_fashion: {
                loaiSP = tvFashion.getText().toString();
                selectCategory();
                break;
            }
            case R.id.tv_car: {
                loaiSP = tvCar.getText().toString();
                selectCategory();
                break;
            }
            case R.id.tv_beauty: {
                loaiSP = tvBeauty.getText().toString();
                selectCategory();
                break;
            }
            case R.id.tv_furniture: {
                loaiSP = tvFuniture.getText().toString();
                selectCategory();
                break;
            }
            case R.id.tv_other: {
                loaiSP = tvOther.getText().toString();
                selectCategory();
                break;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void selectCategory() {
        ArrayList<TextView> list = new ArrayList<>();
        list.add(tvHomeAppliance);
        list.add(tvCar);
        list.add(tvFashion);
        list.add(tvTechnology);
        list.add(tvBeauty);
        list.add(tvFuniture);
        list.add(tvOther);
        for (int i = 0; i < list.size(); i++) {
            if (loaiSP.equals(list.get(i).getText().toString())) {
                list.get(i).setBackground(getResources().getDrawable(R.drawable.ct_textview_post_check));
            } else
                list.get(i).setBackground(getResources().getDrawable(R.drawable.ct_textview_post_uncheck));
        }
    }

    public class MyAsyncTask extends AsyncTask<Bitmap, Void, Bitmap> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showAnimationLoadPhoto(checkPhoto);
        }

        @Override
        protected Bitmap doInBackground(Bitmap... bitmaps) {
            lanhSP.put(checkPhoto, ImageUtils.endcodeImageToBase64(bitmaps[0]));
            Bitmap bitmap = ImageUtils.base64ToImage(lanhSP.get(checkPhoto));
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            showPhoto(checkPhoto, bitmap);
        }
    }
}
