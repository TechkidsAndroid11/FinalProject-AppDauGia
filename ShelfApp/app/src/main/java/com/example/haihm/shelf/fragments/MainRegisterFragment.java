package com.example.haihm.shelf.fragments;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.activity.MainActivity;
import com.example.haihm.shelf.event.OnClickUserModelEvent;
import com.example.haihm.shelf.model.UserModel;
import com.example.haihm.shelf.utils.ImageUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class MainRegisterFragment extends Fragment {
    private static final String TAG = "MainRegisterFragment";
    public EditText etUsername, etPassword, etVerifyPassword, etPhone;
    TextView tvNotify;
    Button btRegister;
    ImageView ivAvatar;
    RelativeLayout rlAvatar;
    Uri uri;
    Bitmap bitmap;
    FirebaseUser user;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    String phone, avatar;
    RelativeLayout rlMain;
    FirebaseStorage storage;
    StorageReference storageRef;
    public static boolean check = true;
    AVLoadingIndicatorView avLoad;
    @SuppressLint("ValidFragment")
    public MainRegisterFragment(FirebaseUser user, String phone) {
        // Required empty public constructor
        this.user = user;
        this.phone = phone;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_register, container, false);
        setupUI(view);
        avLoad.hide();
        addListener();
        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        avLoad.hide();
    }

    private void setupUI(View view) {
        rlMain = view.findViewById(R.id.rl_main);
        avLoad = view.findViewById(R.id.avLoad);
        etUsername = view.findViewById(R.id.et_username);
        etVerifyPassword = view.findViewById(R.id.et_verifyPassword);
        etPassword = view.findViewById(R.id.et_password);
        btRegister = view.findViewById(R.id.bt_sign_in);
        ivAvatar = view.findViewById(R.id.iv_avatar);
        rlAvatar = view.findViewById(R.id.rl_avatar);
        tvNotify = view.findViewById(R.id.tv_notify);
        etPhone = view.findViewById(R.id.et_phone);
        etPhone.setText(phone);
        etPhone.setEnabled(false);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("UserInfo");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://shelfapp-e48fb.appspot.com");
    }

    public void addListener() {
        rlAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFuntion();
            }
        });

    }

    public void checkDuplicatedUsername(String username) {
        databaseReference.orderByChild("hoten").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot.getKey());
                if (dataSnapshot.exists()) {
                    check = false;
                } else {
                    check = true;
                }
                Log.d(TAG, "onDataChange: check: "+check);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public void registerAccount() {

        rlMain.setVisibility(View.GONE);
        avLoad.show();
        String id = user.getUid();
        Log.d(TAG, "registerAccount: " + id);
        String address = "";
        String phone = user.getPhoneNumber();
        String userName = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String verifyPass = etVerifyPassword.getText().toString();
        final UserModel userModel = new UserModel(id, avatar, "", userName, password, verifyPass, phone, address, new UserModel.Rate());
        databaseReference.child(user.getUid()).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getActivity(), "Đăng ký thành công!!", Toast.LENGTH_SHORT).show();
                btRegister.setEnabled(false);
                EventBus.getDefault().postSticky(new OnClickUserModelEvent(userModel));
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();

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

        uri = ImageUtils.getUriFromImage(getActivity());

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, 2);
        }
    }

    public void putData() {
        StorageReference mountainsRef = storageRef.child(user.getUid());
        // Get the data from an ImageView as bytes
        ivAvatar.setDrawingCacheEnabled(true);
        ivAvatar.buildDrawingCache();

        Bitmap bitmap = ivAvatar.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "onFailure: " + exception.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                avatar = String.valueOf(downloadUrl);

                btRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkDuplicatedUsername(etUsername.getText().toString());
                        if (etPassword.getText().toString().equals("") || etUsername.getText().toString().equals("") || etVerifyPassword.getText().toString().equals("")) {
                            tvNotify.setText("Bạn phải điền đầy đủ các thông tin chi tiết!");
                        } else if (avatar==null) {
                            Log.d(TAG, "onClick: "+avatar);
                            tvNotify.setText("Bạn phải thêm ảnh đại diện!");
                        } else if (etPassword.getText().toString().length() < 6) {
                            tvNotify.setText("Mật khẩu phải có ít nhất 6 ký tự!");
                        } else if (!etPassword.getText().toString().equals(etVerifyPassword.getText().toString())) {
                            tvNotify.setText("Nhập lại mật khẩu chưa đúng!");
                        } else if (check == false) {
                            tvNotify.setText("Tài khoản đã tồn tại!");
                            Log.d(TAG, "onClick: ton tai");
                        } else {
                            registerAccount();
                        }
                    }
                });
            }
        });
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
                Log.d(TAG, "onActivityResult: "+bitmap);
                ivAvatar.setPadding(0, 0, 0, 0);
                ivAvatar.setImageBitmap(bitmap);
                putData();
            //chup anh day
            } else if (requestCode == 2) {
                if (resultCode == RESULT_OK) {
                    bitmap = ImageUtils.getBitmap(getActivity());

                }
                Log.d(TAG, "onActivityResult: "+bitmap);
                ivAvatar.setPadding(0, 0, 0, 0);
                ivAvatar.setImageBitmap(bitmap);
                putData();
            }

        }

    }
}
