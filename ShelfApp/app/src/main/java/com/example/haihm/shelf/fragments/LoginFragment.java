package com.example.haihm.shelf.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import static com.facebook.FacebookSdk.getApplicationContext;

import com.example.haihm.shelf.R;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "LoginFragment";
    AccessTokenTracker mAccessTokenTracker;
    public GoogleApiClient mGoogleApiClient;
    public final int RC_SIGN_IN = 1;
    CallbackManager callbackManager;
    FirebaseAuth mAuth;
    LoginManager mLoginManager;
    Button btnLoginFacebook, btnLoginGoogle, btnLoginApp;
    public String cover, name, phone, address;
    String avatar;
    UserModel.Rate rate;
    UserModel userModel;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

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
        getCover();
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
    public void addListener()
    {
        btnLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                handleFacebookLogin();
            }
        });

        btnLoginApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }
    public void checkLogined()
    {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPre", Context.MODE_PRIVATE);
        String Uid = sharedPreferences.getString("UserId","NotFound");
        if(!Uid.equals("NotFound"))
        {
            databaseReference.orderByChild("id").equalTo(Uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot userSnapshot: dataSnapshot.getChildren())
                    {
                        userModel = userSnapshot.getValue(UserModel.class);
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
        else
        {
            Log.d(TAG, "checkLogined: Not");
        }
    }
    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential authCredential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // đăng nhập thành công
                if (task.isSuccessful()) {
                    FirebaseUser user = task.getResult().getUser();

                    avatar = String.valueOf(user.getPhotoUrl());
                    name = user.getDisplayName();
                    phone = user.getPhoneNumber();
                    userModel = new UserModel(user.getUid(), avatar, cover, name, phone, address, rate);
                    EventBus.getDefault().postSticky(new OnClickUserModelEvent(userModel));

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    saveLoginSuccess(user.getUid());
                    databaseReference.child(user.getUid()).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getActivity(), "Add User ok", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.d(TAG, "onComplete: ");
                } else {
                    Log.d(TAG, "onComplete: " + task.getException().getMessage());
                }

            }
        });
    }
    public void saveLoginSuccess(String userId)
    {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPre", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserId",userId);
        editor.commit();
    }


    private void getCover() {

        Bundle params = new Bundle();
        params.putString("fields", "cover");
        GraphRequestAsyncTask graphRequestAsyncTask = new GraphRequest(AccessToken.getCurrentAccessToken(), "me", params, HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        if (response != null) {
                            String userDetail = response.getRawResponse();
                            FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);
                            try {
                                JSONObject jsonObject = new JSONObject(userDetail);
                                if (jsonObject.has("cover")) {
                                    String getInitialCover = jsonObject.getString("cover");

                                    if (getInitialCover.equals("null")) {
                                        jsonObject = null;
                                    } else {
                                        JSONObject JOCover = jsonObject.optJSONObject("cover");

                                        if (JOCover.has("source")) {

                                            cover = JOCover.getString("source");
                                        } else {
                                            cover = null;
                                        }
                                    }
                                } else {
                                    cover = null;
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Log.d(TAG, "onCompleted: "+cover);
                    }
                }).executeAsync();
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
                Log.e("checkLogin", "success");

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "onError: " + error.getMessage());
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
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                            saveLoginSuccess(user.getUid());
                            name = user.getDisplayName();
                            avatar = String.valueOf(user.getPhotoUrl());
                            userModel = new UserModel(user.getUid(), avatar, cover, name, phone, address, rate);
                            EventBus.getDefault().postSticky(new OnClickUserModelEvent(userModel));
                            databaseReference.child(user.getUid()).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getActivity(), "Add User ok", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }

                        // ...
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
