package com.servecreative.omeet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.servecreative.omeet.R;
import com.servecreative.omeet.databinding.ActivityRewardBinding;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class RewardActivity extends AppCompatActivity   {
    ActivityRewardBinding binding;
    FirebaseDatabase database;
    String currentUid;
    int coins= 0;
    int count=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRewardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
         
        database = FirebaseDatabase.getInstance();
        currentUid = FirebaseAuth.getInstance().getUid();
        database.getReference().child("profiles")
                .child(currentUid)
                .child("coins")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        coins = snapshot.getValue(Integer.class);
                        binding.textView7.setText(String.valueOf(coins));
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

        binding.free.setOnClickListener(new View.OnClickListener() {
            @Override
            
            public void onClick(View view) {
                
                 
                    Activity activityContext = RewardActivity.this;
                          count++;
                          if (count<4) {
                              coins = coins + 10;
                              database.getReference().child("profiles")
                                      .child(currentUid)
                                      .child("coins")
                                      .setValue(coins);
                          }
                          else 
                          {
                              Toast.makeText(activityContext, "Please support us", Toast.LENGTH_SHORT).show();
                          }
                        }
                    });

        binding.supportbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payment("https://www.instagram.com/serve_creative_inc/");


            }
        });


    }
    void payment(String pay)
    {
        Uri uri=Uri.parse(pay);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }
}
