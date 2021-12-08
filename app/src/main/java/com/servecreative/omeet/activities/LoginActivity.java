package com.servecreative.omeet.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.airbnb.lottie.L;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.servecreative.omeet.R;
import com.servecreative.omeet.models.User;

public class LoginActivity extends AppCompatActivity {
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGNIN=11;
    FirebaseAuth mauth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mauth = FirebaseAuth.getInstance();
        if (mauth.getCurrentUser() != null) {
            gotoNextActivity();
        }
            database = FirebaseDatabase.getInstance();

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            findViewById(R.id.loginbtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(intent, RC_SIGNIN);
                    //  startActivity(new Intent(LoginActivity.this,MainActivity.class));
                }
            });
        }

    void  gotoNextActivity()
    {
        startActivity(new Intent(LoginActivity.this,MainActivity.class));
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RC_SIGNIN)
        {
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account=task.getResult();
            authWithGoogle(account.getIdToken());
        }

    }


    void authWithGoogle(String idtokken)
    {

        AuthCredential credential= GoogleAuthProvider.getCredential(idtokken,null);
        mauth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
             if (task.isSuccessful())
             {
                 FirebaseUser user=mauth.getCurrentUser();
                 User firebaseuser = new User(user.getUid(), user.getDisplayName(),user.getPhotoUrl().toString(),200);
                 database.getReference().child("profiles").child(user.getUid()).setValue(firebaseuser).addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                      if (task.isSuccessful())
                      {
                          startActivity(new Intent(LoginActivity.this,MainActivity.class));
                          finishAffinity();
                      }
                     }
                 });
             }
             else
             {

             }
            }
        });
    }

}