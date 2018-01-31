package com.example.haihm.shelf.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.activity.LoginActivity;
import com.example.haihm.shelf.utils.Utils;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {
    private static final String TAG = "RegisterFragment";
    public EditText etPhone;
    public Button btnSignIn;
    public String phoneVerificationId;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
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

    public void setupUI(View view) {
        etPhone = view.findViewById(R.id.edt_phone_number);
        btnSignIn = view.findViewById(R.id.bt_sign_in);

        fbAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("UserInfo");
    }

    public void addListener() {
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                Log.d(TAG, "onClick: ok");
                if (etPhone.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Bạn chưa nhập số điện thoại!!", Toast.LENGTH_SHORT).show();
                } else {
                    checkDuplicatedPhone(etPhone.getText().toString());

                }



            }
        });
    }

    public void showDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Thông báo");
        builder.setMessage("Số điện thoại này đã được đăng ký. Bạn có muốn đăng nhập không?");
        builder.setCancelable(true);
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public void checkDuplicatedPhone(String phone) {
        databaseReference.orderByChild("sdt").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    showDialog();
                } else {
                    sendCode();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void sendCode() {
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
                Log.d(TAG, "onVerificationFailed: " + e.getMessage());
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent: ");
                phoneVerificationId = verificationId;
                resendToken = token;
                Utils.openFragment(getFragmentManager(), R.id.rl_main, new VerifyPhoneRegisterFragment(phoneVerificationId, etPhone.getText().toString()));
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
