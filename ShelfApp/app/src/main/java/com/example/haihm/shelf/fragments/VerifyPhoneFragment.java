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

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.fragments.MainRegisterFragment;
import com.example.haihm.shelf.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;


/**
 * A simple {@link Fragment} subclass.
 */
public class VerifyPhoneFragment extends Fragment {
    private static final String TAG = "VerifyPhoneFragment";
    public TextView tvDes;
    public EditText etCode;
    public Button btVerify;

    public String phoneVerificationId;
    public PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    public PhoneAuthProvider.ForceResendingToken resendToken;
    public FirebaseAuth fbAuth;
    public VerifyPhoneFragment()
    {

    }
    @SuppressLint("ValidFragment")
    public VerifyPhoneFragment(String phoneVerificationId) {
        this.phoneVerificationId =phoneVerificationId;
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

                    Log.d(TAG, "signInWithPhoneAuthCredential: "+credential);
                    Log.d(TAG, "signInWithPhoneAuthCredential: "+credential.getProvider());
                    Log.d(TAG, "onVerificationCompleted: "+credential.getSmsCode());
                    FirebaseUser user = task.getResult().getUser();
                    Utils.openFragment(getFragmentManager(),R.id.rl_main,new MainRegisterFragment(user));
                }
            }
        });
    }

}
