package com.example.haihm.shelf.fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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


import com.example.haihm.shelf.R;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class MainRegisterFragment extends Fragment {
    private static final String TAG = "MainRegisterFragment";
    public EditText etUsername,etPassword,etVerifyPassword;
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
    @SuppressLint("ValidFragment")
    public MainRegisterFragment(FirebaseUser user) {
        // Required empty public constructor
        this.user=user;

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
        btRegister = view.findViewById(R.id.bt_register);
        ivAvatar =view.findViewById(R.id.iv_avatar);
        rlAvatar = view.findViewById(R.id.rl_avatar);
        tvNotify = view.findViewById(R.id.tv_notify);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("UserInfo");
    }
    public void addListener()
    {
        rlAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraIntent();
            }
        });
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etPassword.getText().toString().equals("")|| etUsername.getText().toString().equals("")||etVerifyPassword.getText().toString().equals(""))
                {
                    tvNotify.setText("Bạn phải điền đầy đủ các thông tin chi tiết!");
                }else if(uri==null)
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
        String address="";
        String cover="";
        String phone = user.getPhoneNumber();
        String userName = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String verifyPass = etVerifyPassword.getText().toString();
        String avatar = String.valueOf(uri);

        UserModel userModel = new UserModel(id,avatar,cover,userName,phone,address,null);
        databaseReference.child(user.getUid()).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getActivity(), "Đăng ký thành công!!", Toast.LENGTH_SHORT).show();
                Utils.openFragment(getFragmentManager(),R.id.rl_main,new LoginFragment());
            }
        });
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
        if(requestCode == 2){

            if (resultCode == RESULT_OK) {

                bitmap = ImageUtils.getBitmap(getActivity());
//                ImageUtils imU = new ImageUtils();
                ImageUtils imU = new ImageUtils();
                String tempBase64 = ImageUtils.endcodeImageToBase64(bitmap);
                base64 = ImageUtils.resizeBase64Image(tempBase64);
            }

            Picasso.with(getActivity()).load(uri).transform(new CropCircleTransformation()).into(ivAvatar);

        }
    }
}
