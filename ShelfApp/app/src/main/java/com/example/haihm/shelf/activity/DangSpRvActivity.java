package com.example.haihm.shelf.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.adapter.ThemAnhSPAdapter;
import com.example.haihm.shelf.event.OnClickAddPhotoEvent;
import com.example.haihm.shelf.event.OnClickAddSanPhamEvent;
import com.example.haihm.shelf.model.SanPhamRaoVat;
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
import java.util.List;
import java.util.Locale;

public class DangSpRvActivity extends AppCompatActivity {
    private static final String TAG = "DangSpRvActivity";
    EditText etTenSP,etgiaSP,etDiaC,etMoTaSP;
    Spinner spLoaiSP;
    Button btRaoBan;
    String []loaiSP;
    List<String> lanhSP;
    RecyclerView recyclerView;
    ThemAnhSPAdapter anhSPAdapter;
    UserModel userModel;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_sp_rv);
        initPermission();
        EventBus.getDefault().register(this);
        setupUI();
    }
    private void initPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            // >= API 23
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){//neu chua duoc cap phep
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }
        }
    }
    private void setupUI() {
        etTenSP = findViewById(R.id.et_ten_sp);
        etgiaSP = findViewById(R.id.et_gia);
        etDiaC = findViewById(R.id.et_diaC);
        etMoTaSP = findViewById(R.id.et_mo_ta);
        spLoaiSP = findViewById(R.id.sp_loai_sp);
        btRaoBan = findViewById(R.id.bt_rao_ban);
        recyclerView = findViewById(R.id.rv_anh_sp_rv);
        loaiSP = getResources().getStringArray(R.array.loai_sp);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,loaiSP);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLoaiSP.setAdapter(adapter);

        lanhSP = new ArrayList<>();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.add_picture);
        lanhSP.add(ImageUtils.endcodeImageToBase64(bitmap));

        anhSPAdapter = new ThemAnhSPAdapter(lanhSP,this);
        recyclerView.setAdapter(anhSPAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        //
        userModel = new UserModel();
        //
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("RaoVat");
        //format giá sản phẩm
        etgiaSP.addTextChangedListener(onTextChangedListener());

        btRaoBan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DangSp();
            }
        });
    }

    private void DangSp() {
        double giaSP = Double.parseDouble(etgiaSP.getText().toString().replaceAll(",",""));
        String loaiSP = spLoaiSP.getSelectedItem().toString();
        SanPhamRaoVat sanPhamRaoVat = new SanPhamRaoVat(userModel.id,etTenSP.getText().toString(),lanhSP,
                giaSP,
                etMoTaSP.getText().toString(),loaiSP,
                userModel.hoten,userModel.sdt,etDiaC.getText().toString());

        databaseReference.child(loaiSP).push().setValue(sanPhamRaoVat);
    }

    @Subscribe(sticky = true)
    public void OnReceivedOnClickAddPhotoEvent(OnClickAddPhotoEvent onClickAddPhotoEvent){
        selectFuntion();
    }
    @Subscribe(sticky = true)
    public void OnReceivedOnClickAddSanPhamEvent(OnClickAddSanPhamEvent onClickAddSanPhamEvent){
        userModel = onClickAddSanPhamEvent.userModel;
        etDiaC.setText(userModel.diaC);
    }
    private void selectFuntion() {
        final String[] item = {"Chụp ảnh", "Mở Bộ sưu tập", "Huỷ"};

        AlertDialog.Builder builder = new AlertDialog.Builder(DangSpRvActivity.this);
        builder.setTitle("Thêm Ảnh");
        builder.setItems(item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(item[i].equals("Chụp ảnh")){
                    cameraIntent();
                }
                else if(item[i].equals("Mở Bộ sưu tập")){
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
                lanhSP.add(0,ImageUtils.endcodeImageToBase64(bitmap));
                anhSPAdapter.notifyDataSetChanged();
            }
            else if(requestCode == 2){
                Bitmap bitmap = null;
                if (resultCode == RESULT_OK) {
                    Log.e("check request", "I'm here");
                    bitmap = ImageUtils.getBitmap(this);
                    lanhSP.add(0,ImageUtils.endcodeImageToBase64(bitmap));
                    anhSPAdapter.notifyDataSetChanged();
                }

            }

        }
    }
    private TextWatcher onTextChangedListener(){
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
                    if(tmp.contains(",")){
                        tmp = tmp.replaceAll(",","");
                    }
                    longVar = Long.parseLong(tmp);

                    DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    decimalFormat.applyPattern("#,###,###");

                    String formatTmp = decimalFormat.format(longVar);

                    etgiaSP.setText(formatTmp);
                    etgiaSP.setSelection(etgiaSP.getText().length());
                }catch (Exception e){
                    e.printStackTrace();
                }
                etgiaSP.addTextChangedListener(this);
            }
        };
    }
}
