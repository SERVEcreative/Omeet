package com.servecreative.omeet.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.servecreative.omeet.R;

public class Welcomeactivity extends AppCompatActivity {
    FirebaseAuth auth;
   private CheckBox checkBox;
   private TextView terms,privacy;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcomeactivity);
        auth=FirebaseAuth.getInstance();
        checkBox=findViewById(R.id.checkBox);
        terms=findViewById(R.id.termsbtn);
        privacy=findViewById(R.id.privacybtn);


        if (auth.getCurrentUser()!=null )
        {
            gotoNextActivity();
        }


            findViewById(R.id.start_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if (checkBox.isChecked())
                    gotoNextActivity();
                    else
                        Toast.makeText(Welcomeactivity.this, "Please Agree to our terms and conditions ", Toast.LENGTH_SHORT).show();
                }
            });
           privacy.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   privacyPolicy("https://pages.flycricket.io/omeet-0/privacy.html");
               }
           });
           terms.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   termsCondition("https://pages.flycricket.io/omeet/terms.html");
               }
           });

    }

    private void privacyPolicy(String s) {

        Uri uri= Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));

    }

    void  gotoNextActivity()
    {
        startActivity(new Intent(Welcomeactivity.this,LoginActivity.class));
        finish();
    }

    void termsCondition(String terms)
    {
     Uri uri=Uri.parse(terms);
     startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }
}