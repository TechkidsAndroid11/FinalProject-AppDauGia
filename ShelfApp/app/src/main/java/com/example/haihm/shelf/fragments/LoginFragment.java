package com.example.haihm.shelf.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.activity.MainActivity;
import com.example.haihm.shelf.event.OnClickUserModelEvent;
import com.example.haihm.shelf.model.UserModel;
import com.example.haihm.shelf.utils.Utils;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    public static String UserModel = "UserModel";

    private static final String TAG = "LoginFragment";
    AccessTokenTracker mAccessTokenTracker;
    public GoogleApiClient mGoogleApiClient;
    public final int RC_SIGN_IN = 1;
    CallbackManager callbackManager;
    FirebaseAuth mAuth;
    LoginManager mLoginManager;
    TextView btnLoginFacebook, btnLoginGoogle, btnLoginApp;
    TextView tvNotify;
    public String cover, name, phone, address;
    String avatar;
    String fbId, fbName, fbAvatar;
    UserModel.Rate rate;
    UserModel userModel;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    TextView tvSignIn, tvSignUp;
    EditText etUsername, etPass;
    LinearLayout linearLayout;
    //AVLoadingIndicatorView avLoad;

    public LoginFragment() {
        // Required empty public constructor
        Log.d(TAG, "LoginFragment: ");
        //avLoad.hide();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        setupUI(view);
      //  avLoad.hide();
        checkLogined();
        addListener();

        return view;
    }





    public void setupUI(View view) {
        linearLayout = view.findViewById(R.id.linearLayout);
       // avLoad = view.findViewById(R.id.avLoad);
        userModel = new UserModel();
        btnLoginApp = view.findViewById(R.id.bt_login);
        btnLoginFacebook = view.findViewById(R.id.bt_register_with_facebook);
        mLoginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();
        btnLoginGoogle = view.findViewById(R.id.bt_register_with_mail);
        setupFacebookStuff();
        updateFacebookButtonUI();
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("UserInfo");
        tvNotify = view.findViewById(R.id.tv_notify);
        tvSignIn = view.findViewById(R.id.tv_sign_in);
        tvSignUp = view.findViewById(R.id.tv_sign_up);
        etUsername = view.findViewById(R.id.edt_username);
        etPass = view.findViewById(R.id.et_password);
        rate = new UserModel.Rate();
        // xin các quyền cơ bản của user
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // kết nối với GOogle APi Client
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public void addListener() {
        btnLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleFacebookLogin();
            }
        });
        btnLoginApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginWithPhone();

            }
        });

        btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utils.openFragment(getFragmentManager(), R.id.rl_main, new RegisterFragment());
            }

        });
    }

    public void checkLogined() {

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPre", Context.MODE_PRIVATE);
        String Uid = sharedPreferences.getString("UserId", "NotFound");
        Log.d(TAG, "checkLogined: "+Uid);
        if (!Uid.equals("NotFound")) {

//            linearLayout.setVisibility(View.GONE);
//            avLoad.show();

            databaseReference.orderByChild("id").equalTo(Uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        userModel = userSnapshot.getValue(UserModel.class);
                        Log.d(TAG, "onDataChange: " + userModel.getSdt());
                        EventBus.getDefault().postSticky(new OnClickUserModelEvent(userModel));
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void loginWithPhone() {
        String username = etUsername.getText().toString();
        final String pass = etPass.getText().toString();
        if (username.equals("") || pass.equals("")) {
            tvNotify.setText("Hãy điền đầy đủ thông tin tài khoản và mật khẩu!!");
        } else {
            databaseReference.orderByChild("hoten").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            userModel = userSnapshot.getValue(UserModel.class);

                            if (userModel.getPassword().equals(pass)) {
                                Toast.makeText(getActivity(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                saveLoginSuccess(userModel.getId());
                                EventBus.getDefault().postSticky(new OnClickUserModelEvent(userModel));
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            } else
                                tvNotify.setText("Sai mật khẩu!!!!");
                        }
                    } else
                        tvNotify.setText("Tên tài khoản chưa được đăng ký!!");

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                }
            });
        }

    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential authCredential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // đăng nhập thành công
                if (task.isSuccessful()) {

                    final FirebaseUser user = task.getResult().getUser();
                    checkDuplicatedIdDatabase(user);

                } else {
                    Log.d(TAG, "onComplete: " + task.getException().getMessage());
                }

            }
        });
    }

    private void loadData() {
        Bundle params = new Bundle();
        params.putString("fields", "id,name,picture.role(large),cover");
        GraphRequestAsyncTask graphRequestAsyncTask = new GraphRequest(AccessToken.getCurrentAccessToken(), "me",
                params, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                if (response != null) {
                    String userDetail = response.getRawResponse();
                    Log.e("checkloginfb", "login2 " + userDetail);
                    FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);
                    try {
                        JSONObject jsonObject = new JSONObject(userDetail);
                        fbId = jsonObject.getString("id");
                        fbName = jsonObject.getString("name");
                        fbAvatar = "https://graph.facebook.com/" + fbId + "/picture?width=960&height=960";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).executeAsync();
    }

    public void checkDuplicatedIdDatabase(final FirebaseUser user) {
       // linearLayout.setVisibility(View.GONE);
     //   avLoad.show();
        databaseReference.orderByChild("id").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) // đã được đăng ký 1 lần thì k cần veryfi phone nữa
                {
                    userModel = new UserModel(user.getUid(), fbAvatar, cover, fbName, phone, address, rate);
                    EventBus.getDefault().postSticky(new OnClickUserModelEvent(userModel));
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    saveLoginSuccess(user.getUid());
                    databaseReference.child(user.getUid()).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getActivity(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    if (user.getPhoneNumber() == null) {
                        avatar = String.valueOf(user.getPhotoUrl());
                        name = user.getDisplayName();
                        userModel = new UserModel(user.getUid(), fbAvatar, cover, fbName, phone, address, rate);
                        Log.d(TAG, "onDataChange: Usermodel: " + userModel.getHoten());
                        Utils.openFragment(getFragmentManager(), R.id.rl_main, new CheckPhoneFragment(userModel));

                    } else {

                        avatar = String.valueOf(user.getPhotoUrl());
                        name = user.getDisplayName();
                        userModel = new UserModel(user.getUid(), fbAvatar, cover, fbName, phone, address, rate);
                        EventBus.getDefault().postSticky(new OnClickUserModelEvent(userModel));
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        saveLoginSuccess(user.getUid());
                        databaseReference.child(user.getUid()).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getActivity(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public static void saveLoginSuccess(String userId) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPre", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserId", userId);
        editor.commit();
    }

    private void setupFacebookStuff() {

        // This should normally be on your application class
        FacebookSdk.sdkInitialize(getApplicationContext());

        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                updateFacebookButtonUI();
            }
        };

        mLoginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loadData();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    private void updateFacebookButtonUI() {
        if (AccessToken.getCurrentAccessToken() != null) {
            loadData();

        } else {
//            FacebookLogin.setText("Facebook Connect");

        }
    }

    private void handleFacebookLogin() {
        if (AccessToken.getCurrentAccessToken() != null) {
            mLoginManager.logOut();
        } else {
            mAccessTokenTracker.startTracking();
            mLoginManager.logInWithReadPermissions(getActivity(), Arrays.asList("public_profile"));
        }

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            final FirebaseUser user = task.getResult().getUser();
                            System.out.println("USer: " + user.getUid());
                            checkDuplicatedIdDatabase(user);

                        }


                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.d(TAG, "onActivityResult: " + e.getMessage());

            }
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}


