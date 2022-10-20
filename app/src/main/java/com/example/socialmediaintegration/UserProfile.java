package com.example.socialmediaintegration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.socialmediaintegration.databinding.ActivityUserProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class UserProfile extends AppCompatActivity {

    ActivityUserProfileBinding binding;
    private FirebaseAuth.AuthStateListener authStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        Intent intent = getIntent();
        String profileURL = intent.getStringExtra("profileURL");
        String userName = intent.getStringExtra("userName");
        String eMail = intent.getStringExtra("eMail");

        Picasso.get().load(profileURL).into(binding.profileImage);
        binding.tvName.setText(userName);
        binding.tvMail.setText(eMail);

        authStateListener = firebaseAuth -> {

        };
        binding.btnSignOut.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent1 = new Intent(UserProfile.this, LoginActivity.class);
            startActivity(intent1);
            finish();
        });

    }
}