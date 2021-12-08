package com.servecreative.omeet.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;



import com.bumptech.glide.Glide;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.razorpay.PaymentResultListener;
import com.servecreative.omeet.R;
import com.servecreative.omeet.databinding.ActivityMainBinding;
import com.servecreative.omeet.models.User;

public class MainActivity extends AppCompatActivity   {
    ActivityMainBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    long coins = 0;
    String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
    int requestcode = 1;
    User user;
    KProgressHUD progress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());






        progress = KProgressHUD.create(this);
        progress.setDimAmount(0.5f);
        progress.show();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        FirebaseUser currentuser = auth.getCurrentUser();
        database.getReference().child("profiles").child(currentuser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        progress.dismiss();
                        user = dataSnapshot.getValue(User.class);
                        coins = user.getCoins();
                        binding.coinstv.setText("You Have: " + coins);

                        Glide.with(MainActivity.this).load(user.getProfile()).into(binding.userdp);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });





        binding.findcall.setOnClickListener(v -> {
            if (isPermissionsGranted()) {
                if (coins >= 5) {
                    coins = coins - 5;
                    database.getReference().child("profiles")
                            .child(currentuser.getUid()).child("coins").setValue(coins);
                    Intent intent = new Intent(MainActivity.this, ConnectActivity.class);
                    intent.putExtra("profile", user.getProfile());
                    startActivity(intent);
//                        startActivity(new Intent(MainActivity.this, ConnectActivity.class));
                } else {
                    Toast.makeText(MainActivity.this, "insufficient", Toast.LENGTH_SHORT).show();

                }
            } else {
                askPermission();
            }


        });

binding.Rewardbtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        startActivity(new Intent(MainActivity.this,RewardActivity.class));
    }
});

    }

    void askPermission() {
        ActivityCompat.requestPermissions(this, permissions, requestcode);
    }

    boolean isPermissionsGranted() {
        for (String permission : permissions) {

            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }

        }
        return true;
    }


}