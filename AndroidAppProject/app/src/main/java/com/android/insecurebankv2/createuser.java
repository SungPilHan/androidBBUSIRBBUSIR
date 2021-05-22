package com.android.insecurebankv2;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebChromeClient;
import android.widget.ImageView;

import java.io.BufferedReader;

public class createuser extends Activity {
    ImageView image_back; // 로그인 페이지 이동
    String serverip = "";
    String serverport = "";
    String protocol = "http://";
    SharedPreferences serverDetails;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createuser);
        // 회원가입 창에서 로그인 창으로 넘어가는 부분
        image_back = (ImageView) findViewById(R.id.createuser_back);
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        serverDetails = PreferenceManager.getDefaultSharedPreferences(this);
        serverip = serverDetails.getString("serverip", "3.20.202.177");
        serverport = serverDetails.getString("serverport", "8888");

        //웹 뷰 시작
        WebView mWebView = (WebView) findViewById(R.id.wv_createuser);
        String url = protocol + serverip + ":" + serverport + "/signup";
        mWebView.loadUrl(url);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setSaveFormData(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.setWebViewClient(new MyWebViewClient());
        WebChromeClient cClient = new WebChromeClient();
        mWebView.setWebChromeClient(cClient);
        mWebView.addJavascriptInterface(new AndroidBridge(), "android");
    }

    private class AndroidBridge{
        @JavascriptInterface
        public void setMessage(final String arg){
            finish();
        }
    }
}
