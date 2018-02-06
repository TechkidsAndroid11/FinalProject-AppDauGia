package com.example.haihm.shelf.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.event.OnClickUserModelEvent;
import com.example.haihm.shelf.fragments.ProfileFragment;
import com.example.haihm.shelf.model.UserModel;
import com.example.haihm.shelf.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ChangePasswordActivity extends AppCompatActivity {
    private static final String TAG = "ChangePasswordActivity";
    EditText etOldPass,etNewPass,etNewPassAgain;
    TextView tvNotify;
    Button btnChange;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    UserModel userModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        setupUI();
        EventBus.getDefault().register(this);
        addListener();

    }
    @Subscribe(sticky = true)
    public void loadData(OnClickUserModelEvent onClickUserModelEvent)
    {
        userModel=onClickUserModelEvent.userModel;
    }
    private void addListener() {

        Log.d(TAG, "addListener: "+etNewPass.getText().toString()+" "+etOldPass.getText().toString()+" "+etNewPassAgain.getText().toString());
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etOldPass.getText().toString().equals("")||etNewPass.getText().toString().equals("")||etNewPassAgain.getText().toString().equals(""))
                {
                    tvNotify.setText("Bạn phải điền đầy đủ thông tin!!!");
                }else if(!etOldPass.getText().toString().equals(userModel.getPassword()))
                {
                    tvNotify.setText("Mật khẩu hiện tại chưa đúng!!!");
                }else if(!etNewPass.getText().toString().equals(etNewPassAgain.getText().toString()))
                {
                    tvNotify.setText("Nhập lại mật khẩu chưa đúng!!!");
                }else
                {
                    Log.d(TAG, "onClick: ");
                    changePassword();
                    finish();
                }
            }
        });
    }

    private void changePassword() {
        Log.d(TAG, "changePassword: "+userModel.getId());
        databaseReference.orderByChild("id").equalTo(userModel.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnap : dataSnapshot.getChildren())
                {
                    UserModel userModel = userSnap.getValue(UserModel.class);
                    userModel.setPassword(etNewPass.getText().toString());
                    userModel.setConfirmPassword(etNewPass.getText().toString());
                    databaseReference.child(userSnap.getKey()).setValue(userModel);

                     Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thành công!!!", Toast.LENGTH_SHORT).show();
                    Utils.openFragment(getSupportFragmentManager(),R.id.rl_main,new ProfileFragment());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setupUI()
    {
        etOldPass = findViewById(R.id.edt_oldPassword);
        etNewPass = findViewById(R.id.et_newPassword);
        etNewPassAgain = findViewById(R.id.et_newPasswordAgain);
        btnChange = findViewById(R.id.bt_change);
        tvNotify = findViewById(R.id.tv_notify);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("UserInfo");
    }
}
