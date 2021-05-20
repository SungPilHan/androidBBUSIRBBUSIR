package com.android.insecurebankv2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebChromeClient;
import android.widget.ImageView;

public class createuser extends Activity {
    ImageView image_back; // 로그인 페이지 이동

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

        //웹 뷰 시작
        WebView mWebView = (WebView) findViewById(R.id.wv_createuser);
        mWebView.loadUrl("http://3.20.202.177:8888/signup");
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
