package com.example.haihm.shelf.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.activity.ChangePasswordActivity;
import com.example.haihm.shelf.activity.DangSpDGActivity;
import com.example.haihm.shelf.activity.DangSpRvActivity;

import com.example.haihm.shelf.activity.LoginActivity;
import com.example.haihm.shelf.adapters.ViewPagerHistoryProfileAdapter;
import com.example.haihm.shelf.event.OnClickAddSanPhamEvent;
import com.example.haihm.shelf.event.OnClickUserModelEvent;
import com.example.haihm.shelf.model.UserModel;
import com.example.haihm.shelf.utils.ImageUtils;
import com.example.haihm.shelf.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {
    private static final String TAG = "ProfileFragment";

    Button btnAuction, btnClassified;
    String avatar;
    ImageView ivCover, ivAvatar,ivSetting;
    TextView tvName;
    UserModel userModel;
    ViewPager vpHistory;
    TabLayout tabHistory;
    AppBarLayout appBar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FirebaseStorage storage;
    StorageReference storageRef;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    Toolbar toolbar;
    Uri uri;
    ScrollView scrollView;
    Bitmap bitmap;
    public ProfileFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        setupUI(view);
        ivSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view);
            }
        });
        EventBus.getDefault().register(this);
        loadHistory();
        addListener();
        return view;
    }
    private void setupUI(View view) {
        btnAuction = view.findViewById(R.id.btBuy);
        btnClassified = view.findViewById(R.id.btDauGia);
        ivAvatar = view.findViewById(R.id.iv_avatar);
        tvName = view.findViewById(R.id.tv_name);
        vpHistory = view.findViewById(R.id.vp_history);
        tabHistory = view.findViewById(R.id.tab_history);
        ivSetting = view.findViewById(R.id.iv_setting);
        appBar = view.findViewById(R.id.app_bar);
        collapsingToolbarLayout= view.findViewById(R.id.toolbar_layout);
        toolbar = view.findViewById(R.id.toolbar);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://shelfapp-e48fb.appspot.com");//
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("UserInfo");
        //    scrollView = view.findViewById(R.id.scrollView);
    }
    private void addListener() {
        btnAuction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentPostAution();
            }
        });
        btnClassified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentPostClassified();
            }
        });
        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeAvatar();
            }
        });
    }

    private void changeAvatar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Bạn muốn thay đổi ảnh đại diện không?");
        builder.setCancelable(true);
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectFuntion();
            }
        });
        builder.setNeutralButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }


    public void showPopup(View v)
    {
        PopupMenu popupMenu = new PopupMenu(getActivity(),v);
        if(userModel.getPassword()!=null)
        {
            popupMenu.getMenu().add("Đổi mật khẩu").setTitle("Đổi mật khẩu");
        }
        popupMenu.getMenu().add("Đăng xuất").setTitle("Đăng xuất");
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        if(item.getTitle().equals("Đổi mật khẩu"))
        {
            Log.d(TAG, "onMenuItemClick: OKKKK");
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);

        }
        else if(item.getTitle().equals("Đăng xuất"))
        {
            logout();
        }
        return true;
    }

    private void logout() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPre", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =sharedPreferences.edit();
        String Uid = sharedPreferences.getString("UserId","NotFound");
        editor.clear();
        editor.commit();
        Toast.makeText(getActivity(), "Bạn đã đăng xuất thành công!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);

    }

    private void loadHistory() {
        tabHistory.addTab(tabHistory.newTab().setText("Sản phẩm"));
        tabHistory.addTab(tabHistory.newTab().setText("Đấu giá"));

        tabHistory.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vpHistory.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        ViewPagerHistoryProfileAdapter adapter = new ViewPagerHistoryProfileAdapter(getFragmentManager());
        vpHistory.setAdapter(adapter);
        vpHistory.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabHistory));

    }

    @Subscribe(sticky = true)
    public void loadData(OnClickUserModelEvent onClickUserModelEvent) {

        userModel = onClickUserModelEvent.userModel;
        Log.d(TAG, "loadData: "+userModel.getHoten()+" "+userModel.getSdt());
        Log.d(TAG, userModel.getAnhAvatar());
        Picasso.with(getActivity()).load(userModel.getAnhAvatar()).transform(new CropCircleTransformation()).into(ivAvatar);
        tvName.setText(userModel.getHoten());
    }


    private void intentPostClassified() {
        EventBus.getDefault().postSticky(new OnClickAddSanPhamEvent(userModel));
        Intent intent = new Intent(getActivity(), DangSpRvActivity.class);
        startActivity(intent);
    }

    private void intentPostAution() {
        EventBus.getDefault().postSticky(new OnClickAddSanPhamEvent(userModel));
        Intent intent = new Intent(getActivity(), DangSpDGActivity.class);
        startActivity(intent);
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
    public void changeAvatarInFirebase()
    {
        databaseReference.orderByChild("id").equalTo(userModel.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnap : dataSnapshot.getChildren())
                {
                    userModel = userSnap.getValue(UserModel.class);
                    userModel.setAnhAvatar(avatar);
                    databaseReference.child(userSnap.getKey()).setValue(userModel);
                    Picasso.with(getActivity()).load(avatar).transform(new CropCircleTransformation()).into(ivAvatar);
                    ivAvatar.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "Thay đổi ảnh đại diện thành công!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        StorageReference mountainsRef = storageRef.child(userModel.getId());
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
                changeAvatarInFirebase();
                Log.d(TAG, "onSuccess: avatar: "+avatar);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CropCircleTransformation circleTransformation = new CropCircleTransformation();
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                if (data != null) {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                }

                ivAvatar.setPadding(0, 0, 0, 0);
                ivAvatar.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 120, 120, false));
                ivAvatar.setVisibility(View.INVISIBLE);
                putData();
                //chup anh day
            } else if (requestCode == 2) {
                if (resultCode == RESULT_OK) {
                    bitmap = ImageUtils.getBitmap(getActivity());

                }
                ivAvatar.setPadding(0, 0, 0, 0);

                ivAvatar.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 120, 120, false));
                ivAvatar.setVisibility(View.INVISIBLE);
                putData();
            }

        }
    }
}
