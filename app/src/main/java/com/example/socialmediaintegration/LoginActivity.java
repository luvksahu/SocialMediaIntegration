package com.example.socialmediaintegration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.socialmediaintegration.databinding.ActivityLoginBinding;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

import javax.annotation.Nullable;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;
    private CallbackManager mCallbackManager;
    private AccessTokenTracker accessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(@androidx.annotation.Nullable AccessToken accessToken, @androidx.annotation.Nullable AccessToken accessToken1) {
                if(accessToken1 == null){
                    mAuth.signOut();
                }
            }
        };

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        binding.btnGoogleSignin.setOnClickListener(view -> {
            signInWithGoogle();
        });

        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        binding.btnFbSignin.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Log.d("TAG", loginResult+"");
                String token = loginResult.getAccessToken().getToken();
                AuthCredential firebaseCredential = FacebookAuthProvider.getCredential(token);
                firebaseAuth(token, firebaseCredential);
            }

            @Override
            public void onCancel() {
                Log.d("TAG", "onCancel");
            }

            @Override
            public void onError(@NonNull FacebookException e) {
                Log.e("TAG", e.getLocalizedMessage());
            }
        });

        binding.btnPhoneSignin.setOnClickListener(view ->{

        });
    }

    private void signInWithGoogle(){
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQ_SIGN_UP);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            verifiedLogin();
        }
    }

    private static final int REQ_SIGN_UP = 1;
    private boolean showOneTapUI = true;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_SIGN_UP:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    String idToken = account.getIdToken();

                    if (idToken !=  null) {
                        // Got an ID token from Google. Use it to authenticate
                        // with Firebase.
                        Log.d("TAG", "Got ID token.");
                        // Got an ID token from Google. Use it to authenticate
                        // with Firebase.
                        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                        firebaseAuth(idToken, firebaseCredential);
                    }
                } catch (ApiException e) {
                    Log.e("TAG", e.getLocalizedMessage());
                }
                break;
            default:
                mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void firebaseAuth(String idToken, AuthCredential firebaseCredential){

        mAuth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                                Log.d("TAG", "signInWithCredential:success");
                                verifiedLogin();
                        } else {
                            // If sign in fails, display a message to the user.
                           Log.w("TAG", "signInWithCredential:failure", task.getException());
                        }
                    }
                });

    }
    private void verifiedLogin(){
        FirebaseUser user = mAuth.getCurrentUser();
        String profileURL = Objects.requireNonNull(user.getPhotoUrl()) +"?type=large";
        String userName = user.getDisplayName();
        String eMail = user.getEmail();

        Intent intent = new Intent(LoginActivity.this, UserProfile.class);
        intent.putExtra("profileURL", profileURL);
        intent.putExtra("userName", userName);
        intent.putExtra("eMail", eMail);
        startActivity(intent);
        finish();
    }

}