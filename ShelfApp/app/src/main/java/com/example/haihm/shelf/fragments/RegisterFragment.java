package com.example.haihm.shelf.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import java.util.concurrent.TimeUnit;

import com.example.haihm.shelf.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {
    private static final String TAG = "RegisterFragment";
    public EditText etPhone;
    public Button btnSignIn;
    public String phoneVerificationId;
    public PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    public PhoneAuthProvider.ForceResendingToken resendToken;
    public FirebaseAuth fbAuth;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        setupUI(view);
        addListener();
        return view;
    }
    public void setupUI(View view)
    {
        etPhone = view.findViewById(R.id.edt_phone_number);
//        etUser= view.findViewById(R.id.edt_user_name);
//        etPass = view.findViewById(R.id.edt_password);
//        etConfirmPass = view.findViewById(R.id.edt_confirm_password);
        btnSignIn = view.findViewById(R.id.bt_sign_in);
        fbAuth = FirebaseAuth.getInstance();
    }
    public void addListener()
    {
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etPhone.getText().toString().equals(""))
                {
                    Toast.makeText(getActivity(), "Số điện thoại chưa được nhập!!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    sendCode();
                }



            }
        });
    }
    public void sendCode()
    {
        String phoneNumber = etPhone.getText().toString();
        setupVerificationCallbacks();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                verificationCallbacks);        // OnVerificationStateChangedCallbacks
        Toast.makeText(getActivity(), "Hệ thống đang gửi mã để xác nhận!", Toast.LENGTH_SHORT).show();

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
                Utils.openFragment(getFragmentManager(),R.id.rl_main,new VerifyPhoneRegisterFragment(phoneVerificationId,etPhone.getText().toString()));
            }
        };
    }
//    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//        fbAuth.signInWithCredential(credential).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if(task.isSuccessful())
//                {
//                    FirebaseUser user = task.getResult().getUser();
//                    //Utils.openFragment(getFragmentManager(),R.id.rl_main,R.l);
//                }
//            }
//        });
//    }

}
