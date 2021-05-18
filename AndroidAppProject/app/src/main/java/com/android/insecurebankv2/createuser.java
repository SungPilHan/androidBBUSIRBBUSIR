package com.android.insecurebankv2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

public class createuser extends Activity {
    private WebView mWebView; //웹 뷰 선언
    private WebSettings mWebSettings; // 웹 뷰 세팅

    ImageView image_back; // 로그인 페이지 이동


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createuser);

        // 회원가입 창에서 로그인 창으로 넘어가는 부분
        image_back = (ImageView) findViewById(R.id.createuser_back);
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mL = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(mL);
            }
        });


        //웹 뷰 시작
        mWebView = (WebView) findViewById(R.id.wv_createuser);

        mWebView.setWebViewClient(new WebViewClient()); // 클릭시 새창 안뜨게
        mWebSettings = mWebView.getSettings(); //세부 세팅 등록
        mWebSettings.setJavaScriptEnabled(true); // 웹페이지 자바스클비트 허용 여부
        mWebSettings.setSupportMultipleWindows(false); // 새창 띄우기 허용 여부
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(false); // 자바스크립트 새창 띄우기(멀티뷰) 허용 여부
        mWebSettings.setLoadWithOverviewMode(true); // 메타태그 허용 여부
        mWebSettings.setUseWideViewPort(true); // 화면 사이즈 맞추기 허용 여부
        mWebSettings.setSupportZoom(false); // 화면 줌 허용 여부
        mWebSettings.setBuiltInZoomControls(false); // 화면 확대 축소 허용 여부
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); // 컨텐츠 사이즈 맞추기
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 브라우저 캐시 허용 여부
        mWebSettings.setDomStorageEnabled(true); // 로컬저장소 허용 여부

        mWebView.loadUrl("file:///android_asset/createuser_Web.html");
        //mWebView.loadUrl("https://blog.naver.com/ahnmin410"); // 웹뷰에 표시할 웹사이트 주소, 웹뷰 시작
    }


}
