package com.example.haihm.shelf.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.haihm.shelf.R;
import com.example.haihm.shelf.activity.MainActivity;
import com.example.haihm.shelf.event.OnClickUserModelEvent;
import com.example.haihm.shelf.model.UserModel;
import com.example.haihm.shelf.utils.ImageUtils;
import com.example.haihm.shelf.utils.Utils;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
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

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
    UserModel.Rate rate;
    UserModel userModel;
    String base64;
    public static Bitmap bitmap;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    TextView tvSignIn, tvSignUp;
    EditText etUsername, etPass;

    public LoginFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        setupUI(view);
        checkLogined();
        addListener();

        return view;
    }

    public void setupUI(View view) {

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

            }
        });
    }

    public void checkLogined() {

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPre", Context.MODE_PRIVATE);
        String Uid = sharedPreferences.getString("UserId", "NotFound");
        if (!Uid.equals("NotFound")) {
            databaseReference.orderByChild("id").equalTo(Uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        userModel = userSnapshot.getValue(UserModel.class);
                        Log.d(TAG, "onDataChange: " + userModel.getSdt());

                        EventBus.getDefault().postSticky(new OnClickUserModelEvent(userModel));
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
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
            tvNotify.setText("Bạn phải điền đầy đủ thông tin tài khoản và mật khẩu!!");
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
                    Log.d(TAG, "onComplete: " + user.getPhoneNumber());
                    if (user.getPhoneNumber() == null) {
                        executeMyAsync(user);
//                        bitmap = ImageUtils.getBitmapFromURL(String.valueOf(user.getPhotoUrl()));
//                        String tempBase64 = ImageUtils.encodeTobase64(bitmap);
//                        base64 = ImageUtils.resizeBase64Image(tempBase64);
//
//                        avatar = String.valueOf(base64);
//                        name = user.getDisplayName();
//                        userModel = new UserModel(user.getUid(), avatar, cover, name, phone, address, rate);
//                        Utils.openFragment(getFragmentManager(),R.id.rl_main,new CheckPhoneFragment(userModel));
                    } else {
                        executeMyAsync2(user);
//                        avatar = String.valueOf(base64);
//                        name = user.getDisplayName();
//                        userModel = new UserModel(user.getUid(), avatar, cover, name, phone, address, rate);
//                        EventBus.getDefault().postSticky(new OnClickUserModelEvent(userModel));
//                        Intent intent = new Intent(getActivity(), MainActivity.class);
//                        startActivity(intent);
//                        saveLoginSuccess(user.getUid());
//                        databaseReference.child(user.getUid()).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                Toast.makeText(getActivity(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
//                            }
//                        });
                    }

                } else {
                    Log.d(TAG, "onComplete: " + task.getException().getMessage());
                }

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
                updateFacebookButtonUI();
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
            // FacebookLogin.setText("Logout");

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
                            Log.d(TAG, "onComplete: " + user.getPhoneNumber());
                            if (user.getPhoneNumber() == null) {

                                executeMyAsync(user);
//                                avatar = String.valueOf(base64);
//                                name = user.getDisplayName();
//                                userModel = new UserModel(user.getUid(), avatar, cover, name, phone, address, rate);
//                                Utils.openFragment(getFragmentManager(),R.id.rl_main,new CheckPhoneFragment(userModel));

                            } else {
//
                                executeMyAsync2(user);
//                                avatar = String.valueOf(base64);
//                                name = user.getDisplayName();
//                                userModel = new UserModel(user.getUid(), avatar, cover, name, phone, address, rate);
//                                EventBus.getDefault().postSticky(new OnClickUserModelEvent(userModel));
//                                Intent intent = new Intent(getActivity(), MainActivity.class);
//                                startActivity(intent);
//                                saveLoginSuccess(user.getUid());
//                                databaseReference.child(user.getUid()).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        Toast.makeText(getActivity(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
                            }
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

    public void executeMyAsync(final FirebaseUser user) {
        MyAsync obj = new MyAsync() {
            @Override
            protected void onPostExecute(Bitmap bmp) {
                super.onPostExecute(bmp);

                Bitmap bitmap = bmp;
                String tempBase64 = ImageUtils.encodeTobase64(bitmap);
                base64 = ImageUtils.resizeBase64Image(tempBase64);
                avatar = String.valueOf(base64);
                name = user.getDisplayName();
                userModel = new UserModel(user.getUid(), avatar, cover, name, phone, address, rate);
                Utils.openFragment(getFragmentManager(), R.id.rl_main, new CheckPhoneFragment(userModel));
            }
        };
        obj.execute(String.valueOf(user.getPhotoUrl()));
    }

    public void executeMyAsync2(final FirebaseUser user) {
        MyAsync obj = new MyAsync() {
            @Override
            protected void onPostExecute(Bitmap bmp) {
                super.onPostExecute(bmp);

                Bitmap bitmap = bmp;
                String tempBase64 = ImageUtils.encodeTobase64(bitmap);
                base64 = ImageUtils.resizeBase64Image(tempBase64);
                avatar = String.valueOf(base64);
                name = user.getDisplayName();
                userModel = new UserModel(user.getUid(), avatar, cover, name, phone, address, rate);
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
        };
        obj.execute(String.valueOf(user.getPhotoUrl()));
    }

    public class MyAsync extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }
    }
}


