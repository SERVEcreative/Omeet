package com.servecreative.omeet.activities;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.servecreative.omeet.R;
import com.servecreative.omeet.databinding.ActivityCallBinding;
import com.servecreative.omeet.models.InterfaceJava;

import java.util.UUID;

public class CallActivity extends AppCompatActivity {
    ActivityCallBinding binding;
    String uniqueId = "";
    FirebaseAuth auth;
    String username = "";
    String friendsUsername = "";

    boolean isPeerConnected = false;

    DatabaseReference firebaseRef;

    boolean isAudio = true;
    boolean isVideo = true;
    String createdBy;

    boolean pageExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.micBtn.setImageResource(R.drawable.btn_unmute_normal);
        binding.videoBtn.setImageResource(R.drawable.btn_video_normal);

        auth = FirebaseAuth.getInstance();

        firebaseRef = FirebaseDatabase.getInstance().getReference().child("users");
        username = getIntent().getStringExtra("username");
        String incoming = getIntent().getStringExtra("incoming");
        createdBy = getIntent().getStringExtra("createdBy");

        friendsUsername = incoming;
        setupWebView();

        binding.micBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isAudio = !isAudio;

                callJavaScript("javascript:toggleAudio(\"" + isAudio + "\")");

                if (isAudio) {
                    binding.micBtn.setImageResource(R.drawable.btn_unmute_normal);
                } else {
                    binding.micBtn.setImageResource(R.drawable.btn_mute_normal);
                }
            }
        });

        binding.videoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVideo = !isVideo;
                callJavaScript("javascript:toggleVideo(\"" + isVideo + "\")");

                if (isVideo) {
                    binding.videoBtn.setImageResource(R.drawable.btn_video_normal);
                } else {
                    binding.videoBtn.setImageResource(R.drawable.btn_video_muted);
                }

            }
        });

        binding.endCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDestroy();
                android.os.Process.killProcess(android.os.Process.myPid());


            }
        });


    }


    void setupWebView() {
        binding.webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.getResources());
                }
            }
        });

        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        binding.webView.addJavascriptInterface(new InterfaceJava(this), "Android");

        loadVideoCall();
    }

    public void loadVideoCall() {
        String filePath = "file:android_asset/call.html";
        binding.webView.loadUrl(filePath);

        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                initializePeer();
            }
        });
    }

    public void initializePeer() {
        uniqueId = getUniqueId();
        callJavaScript("javascript:init(\"" + uniqueId + "\")");
        if (createdBy.equalsIgnoreCase(username)) {
            if (pageExit)
                return;
            firebaseRef.child(username).child("connId").setValue(uniqueId);
            firebaseRef.child(username).child("isAvailable").setValue(true);
            binding.loadingGroup.setVisibility(View.GONE);
            binding.controls.setVisibility(View.VISIBLE);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    FirebaseDatabase.getInstance().getReference().child("users")
                            .child(friendsUsername)
                            .child("connId").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                sendCallRequest();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }, 1000);
        }


    }

    public void onPeerConnected() {
        isPeerConnected = true;
    }

    void sendCallRequest() {
        if (!isPeerConnected) {
            Toast.makeText(this, "Please Retry...", Toast.LENGTH_SHORT).show();
            return;
        }

        listenConnId();
    }

    void listenConnId() {
        firebaseRef.child(friendsUsername).child("connId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null)
                    return;
                binding.loadingGroup.setVisibility(View.GONE);
                binding.controls.setVisibility(View.VISIBLE);
                String connId = dataSnapshot.getValue(String.class);
                callJavaScript("javascript:startCall(\"" + connId + "\")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    String getUniqueId() {
        return UUID.randomUUID().toString();
    }

    void callJavaScript(String function) {
        binding.webView.post(new Runnable() {
            @Override
            public void run() {
                binding.webView.evaluateJavascript(function, null);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pageExit = true;
        firebaseRef.child(createdBy).setValue(null);
        finish();
    }
}