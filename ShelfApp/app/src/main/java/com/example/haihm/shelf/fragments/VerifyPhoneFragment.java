package com.example.haihm.shelf.fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
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

import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 */
public class VerifyPhoneFragment extends Fragment {
    private static final String TAG = "VerifyPhoneFragment";
    public TextView tvDes,tvResend;
    public EditText etCode;
    public Button btVerify;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    public UserModel userModel;
    public String phoneVerificationId;
    String phone;
    public PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    public PhoneAuthProvider.ForceResendingToken resendToken;
    public FirebaseAuth fbAuth;
    public VerifyPhoneFragment()
    {

    }
    @SuppressLint("ValidFragment")
    public VerifyPhoneFragment(String phoneVerificationId, UserModel userModel,String phone) {
        this.phoneVerificationId =phoneVerificationId;
        this.userModel=userModel;
        this.phone=phone;
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
        tvResend= view.findViewById(R.id.tv_resend);
        tvDes = view.findViewById(R.id.tv_des);
        etCode = view.findViewById(R.id.et_verifyCode);
        btVerify = view.findViewById(R.id.bt_Verify);
        fbAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("UserInfo");
        underLine();
    }
    public void underLine()
    {

        SpannableString content = new SpannableString("Gửi lại");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvResend.setText(content);
    }
    public void addListener()
    {
        btVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyCode();
            }
        });
        tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendCode();
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
                    userModel.setSdt(phone);
                    Log.d(TAG, "onComplete: "+userModel.getHoten()+" "+userModel.getSdt());
                    EventBus.getDefault().postSticky(new OnClickUserModelEvent(userModel));
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                LoginFragment.saveLoginSuccess(userModel.getId());
                databaseReference.child(userModel.getId()).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity(), "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    }
                });
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
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.d(TAG, "onVerificationFailed: "+e.getMessage());
            }
            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent: ");
                phoneVerificationId = verificationId;
                resendToken = token;
            }
        };
    }
    public void resendCode()
    {
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
