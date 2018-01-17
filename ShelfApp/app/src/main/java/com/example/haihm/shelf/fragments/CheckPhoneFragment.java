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
import android.widget.Toast;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.activity.MainActivity;
import com.example.haihm.shelf.event.OnClickUserModelEvent;
import com.example.haihm.shelf.model.UserModel;
import com.example.haihm.shelf.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckPhoneFragment extends Fragment {
    private static final String TAG = "CheckPhoneFragment";
    EditText etPhone;
    Button btSend;
    UserModel userModel;
    public String phoneVerificationId;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    public PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    public PhoneAuthProvider.ForceResendingToken resendToken;
    public FirebaseAuth fbAuth;
    public CheckPhoneFragment() {
        // Required empty public constructor
    }
    @SuppressLint("ValidFragment")
    public CheckPhoneFragment(UserModel userModel) {
        // Required empty public constructor
        this.userModel=userModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_check_phone, container, false);
        setupUI(view);
        addListener();
        return view;

    }

    private void setupUI(View view) {
        etPhone = view.findViewById(R.id.edt_phone_number);
        btSend = view.findViewById(R.id.bt_SendCode);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("UserInfo");
    }
    public void addListener()
    {
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCode();
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
        Toast.makeText(getActivity(), "Waiting", Toast.LENGTH_SHORT).show();

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
                Utils.openFragment(getFragmentManager(),R.id.rl_main,new VerifyPhoneFragment(phoneVerificationId,userModel));
//                EventBus.getDefault().postSticky(new OnClickUserModelEvent(userModel));
//                Intent intent = new Intent(getActivity(), MainActivity.class);
//                startActivity(intent);
//                LoginFragment.saveLoginSuccess(userModel.getId());
//                databaseReference.child(userModel.getId()).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        Toast.makeText(getActivity(), "Add User ok", Toast.LENGTH_SHORT).show();
//                    }
//                });
            }
        };
    }
}
