package com.servecreative.omeet.models;

import android.webkit.JavascriptInterface;

import com.servecreative.omeet.activities.CallActivity;

public class InterfaceJava {
    CallActivity callActivity;
    public InterfaceJava(CallActivity callActivity)
    {
        this.callActivity=callActivity;
    }


    @JavascriptInterface
    public void onPeerConnected()
    {
      callActivity.onPeerConnected();
    }
}
