package com.example.haihm.shelf.fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.activity.MainActivity;
import com.example.haihm.shelf.event.OnClickUserModelEvent;
import com.example.haihm.shelf.fragments.MainRegisterFragment;
import com.example.haihm.shelf.model.UserModel;
import com.example.haihm.shelf.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;


/**
 * A simple {@link Fragment} subclass.
 */
public class VerifyPhoneFragment extends Fragment {
    private static final String TAG = "VerifyPhoneFragment";
    public TextView tvDes;
    public EditText etCode;
    public Button btVerify;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    public UserModel userModel;
    public String phoneVerificationId;
    public PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    public PhoneAuthProvider.ForceResendingToken resendToken;
    public FirebaseAuth fbAuth;
    public VerifyPhoneFragment()
    {

    }
    @SuppressLint("ValidFragment")
    public VerifyPhoneFragment(String phoneVerificationId, UserModel userModel) {
        this.phoneVerificationId =phoneVerificationId;
        this.userModel=userModel;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_verify_phone, container, false);
        setupUI(view);
        addListener();
        return view;
    }

    private void setupUI(View view) {
        tvDes = view.findViewById(R.id.tv_des);
        etCode = view.findViewById(R.id.et_verifyCode);
        btVerify = view.findViewById(R.id.bt_Verify);
        fbAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("UserInfo");
    }
    public void addListener()
    {
        btVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyCode();
            }
        });
    }

    public void verifyCode()
    {
        String code = etCode.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationId,code);
        signInWithPhoneAuthCredential(credential);
    }
    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        fbAuth.signInWithCredential(credential).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    userModel.setSdt(etCode.getText().toString());
                    Log.d(TAG, "onComplete: "+userModel.getHoten()+" "+userModel.getSdt());
                    EventBus.getDefault().postSticky(new OnClickUserModelEvent(userModel));
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                LoginFragment.saveLoginSuccess(userModel.getId());
                databaseReference.child(userModel.getId()).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity(), "Add User ok", Toast.LENGTH_SHORT).show();
                    }
                });
                }
            }
        });
    }

}
