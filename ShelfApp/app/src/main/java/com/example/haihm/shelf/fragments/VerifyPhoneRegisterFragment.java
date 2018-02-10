package com.example.haihm.shelf.fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.haihm.shelf.R;
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
import com.wang.avi.AVLoadingIndicatorView;

import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class VerifyPhoneRegisterFragment extends Fragment {
    private static final String TAG = "VerifyPhoneFragment";
    public TextView tvDes, tvResend;
    public EditText etCode;
    public Button btVerify;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    public UserModel userModel;
    public String phoneVerificationId;
    AVLoadingIndicatorView avLoad;
    String phone;
    public PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    public PhoneAuthProvider.ForceResendingToken resendToken;
    public FirebaseAuth fbAuth;

    @SuppressLint("ValidFragment")
    public VerifyPhoneRegisterFragment(String phoneVerificationId, String phone) {
        // Required empty public constructor
        this.phone = phone;
        this.phoneVerificationId = phoneVerificationId;
    }

    public VerifyPhoneRegisterFragment() {
        Log.d(TAG, "VerifyPhoneRegisterFragment: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_verify_phone, container, false);
        Log.d(TAG, "onCreateView: ");
        setupUI(view);
        avLoad.hide();
        addListener();
        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        avLoad.hide();
        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    private void setupUI(View view) {
        avLoad = view.findViewById(R.id.avLoad);
        tvResend = view.findViewById(R.id.tv_resend);
        tvDes = view.findViewById(R.id.tv_des);
        etCode = view.findViewById(R.id.et_verifyCode);
        btVerify = view.findViewById(R.id.bt_Verify);
        fbAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("UserInfo");
        underLine();
    }

    public void underLine() {

        SpannableString content = new SpannableString("Gửi lại");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvResend.setText(content);
    }

    public void addListener() {
        btVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etCode.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Bạn chưa nhập mã code!!", Toast.LENGTH_SHORT).show();
                } else
                    verifyCode();
            }
        });
        tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendCode();
                Toast.makeText(getActivity(), "Hệ thống đang gửi lại mã xác nhận!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void verifyCode() {
        String code = etCode.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        fbAuth.signInWithCredential(credential).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = task.getResult().getUser();
                    avLoad.show();
                    Utils.openFragment(getFragmentManager(), R.id.rl_main, new MainRegisterFragment(firebaseUser, phone));
                }
            }
        });
    }


    private void setupVerificationCallbacks() {
        verificationCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // tvStatus.setText("Sign In");
//                signInWithPhoneAuthCredential(credential);
                Log.e("check login: ", "complete");
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.e("check login: ", "failed");
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Log.e("check login: ", "codeSent");
                phoneVerificationId = verificationId;
                resendToken = token;
            }
        };
    }

    public void resendCode() {
        String phoneNumber = phone;
        setupVerificationCallbacks();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                verificationCallbacks,
                resendToken);        // OnVerificationStateChangedCallbacks
    }

}
