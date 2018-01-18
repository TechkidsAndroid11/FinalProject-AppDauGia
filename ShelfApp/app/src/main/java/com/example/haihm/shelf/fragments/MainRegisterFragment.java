package com.example.haihm.shelf.fragments;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.haihm.shelf.activity.LoginActivity;
import com.example.haihm.shelf.model.UserModel;
import com.example.haihm.shelf.utils.ImageUtils;
import com.example.haihm.shelf.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;


import com.example.haihm.shelf.R;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class MainRegisterFragment extends Fragment {
    private static final String TAG = "MainRegisterFragment";
    public EditText etUsername,etPassword,etVerifyPassword,etPhone;
    TextView tvNotify;
    Button btRegister;
    ImageView ivAvatar;
    RelativeLayout rlAvatar;
    Uri uri;
    String base64;
    Bitmap bitmap;
    FirebaseUser user;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    String phone;
    @SuppressLint("ValidFragment")
    public MainRegisterFragment(FirebaseUser user,String phone) {
        // Required empty public constructor
        this.user=user;
        this.phone=phone;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_main_register, container, false);
        setupUI(view);
        addListener();
        return view;

    }

    private void setupUI(View view) {
        etUsername = view.findViewById(R.id.et_username);
        etVerifyPassword = view.findViewById(R.id.et_verifyPassword);
        etPassword = view.findViewById(R.id.et_password);
        btRegister = view.findViewById(R.id.bt_sign_in);
        ivAvatar =view.findViewById(R.id.iv_avatar);
        rlAvatar = view.findViewById(R.id.rl_avatar);
        tvNotify = view.findViewById(R.id.tv_notify);
        etPhone = view.findViewById(R.id.et_phone);
        etPhone.setText(phone);
        etPhone.setEnabled(false);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("UserInfo");
    }
    public void addListener()
    {
        rlAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFuntion();
            }
        });
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etPassword.getText().toString().equals("")|| etUsername.getText().toString().equals("")||etVerifyPassword.getText().toString().equals(""))
                {
                    tvNotify.setText("Bạn phải điền đầy đủ các thông tin chi tiết!");
                }else if(base64==null)
                {
                    tvNotify.setText("Bạn phải thêm ảnh đại diện!");
                }else if(etPassword.getText().toString().length()<6)
                {
                    tvNotify.setText("Mật khẩu phải có ít nhất 6 ký tự!");
                }else if(!etPassword.getText().toString().equals(etVerifyPassword.getText().toString()))
                {
                    tvNotify.setText("Nhập lại mật khẩu chưa đúng!");
                }
                else
                {
                    registerAccount();
                }
            }
        });
    }
    public void registerAccount()
    {

        String id = user.getUid();
        Log.d(TAG, "registerAccount: "+id);
        String address="";
        String phone = user.getPhoneNumber();
        String userName = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String verifyPass = etVerifyPassword.getText().toString();
        String avatar = base64;

        UserModel userModel = new UserModel(id,avatar,null,userName,password,verifyPass,phone,address,null);
        databaseReference.child(user.getUid()).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getActivity(), "Đăng ký thành công!!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }
    private void selectFuntion() {
        final String[] item = {"Chụp ảnh", "Mở Bộ sưu tập", "Huỷ"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

        uri = ImageUtils.getUriFromImage(getActivity());

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        if(intent.resolveActivity(getActivity().getPackageManager()) != null){
            startActivityForResult(intent, 2);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                if (data != null) {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("MainActivity", "Data Null!!!!");
                }

                ImageUtils imU = new ImageUtils();

                String tempBase64 = imU.encodeTobase64(bitmap);
                base64 = ImageUtils.resizeBase64Image(tempBase64);
                ivAvatar.setPadding(0,0,0,0);


                String[] sBase64 = base64.split(",");
                Bitmap bitmap = BitmapFactory.decodeByteArray(
                        Base64.decode(sBase64[0],Base64.DEFAULT),
                        0,// offset: vị trí bđ
                        (Base64.decode(sBase64[0],Base64.DEFAULT)).length

                );
//                Picasso.with(getActivity()).load(uri).into((ivAvatar));
                ivAvatar.setImageBitmap(bitmap);

            }
            else if(requestCode == 2){
                if (resultCode == RESULT_OK) {
                    bitmap = ImageUtils.getBitmap(getActivity());

                    ImageUtils imU = new ImageUtils();
                    String tempBase64 = imU .encodeTobase64(bitmap);
                    base64 = ImageUtils.resizeBase64Image(tempBase64);
                }
                ivAvatar.setPadding(0,0,0,0);

                String[] sBase64 = base64.split(",");
                Bitmap bitmap = BitmapFactory.decodeByteArray(
                        Base64.decode(sBase64[0],Base64.DEFAULT),
                        0,// offset: vị trí bđ
                        (Base64.decode(sBase64[0],Base64.DEFAULT)).length

                );

                //Picasso.with(getActivity()).load(uri).into((ivAvatar));
                ivAvatar.setImageBitmap(bitmap);
            }

        }

    }
}
