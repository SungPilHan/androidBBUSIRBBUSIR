package com.android.insecurebankv2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class test extends Activity {
    Button transfer;
    ImageView image_back;
    TextView account_number2;
    TextView price2;

    String number2;//송금할 때 보내는 계좌 text로 나타내기 위함

    private WebView mWebView; //웹 뷰 선언
    private WebSettings mWebSettings; // 웹 뷰 세팅

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_inquiry);

        // 송금 페이지로 이동
        transfer = (Button) findViewById(R.id.button_Transfer1);
        transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trans_account();
                /*Intent dT = new Intent(getApplicationContext(), DoTransfer.class);
                startActivity(dT);*/
            }
        });

        // 뒤로가기
        image_back = (ImageView) findViewById(R.id.account_back);
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pL = new Intent(getApplicationContext(), PostLogin.class);
                startActivity(pL);
            }
        });

        Intent intent = getIntent();

        account_number2 = (TextView)findViewById(R.id.account_number2);
        number2 = intent.getStringExtra("passed_account");
        account_number2.setTextSize(20);
        account_number2.setText(number2);

        price2 = (TextView) findViewById(R.id.price2);
        String price22 = intent.getStringExtra("passed_price");
        price2.setTextSize(20);
        price2.setText(price22);

        //웹 뷰 시작
        mWebView = (WebView) findViewById(R.id.wv_transferhistory);

        mWebView.setWebViewClient(new WebViewClient());
        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);

        mWebView.loadUrl("file:///android_asset/searchtransferhistory.html");

    }

    //송금 페이지로 이동, 사용자 계좌번호 전달
    private void trans_account() {

        /*Intent df = new Intent(getApplicationContext(), DoTransfer.class);
        startActivity(df);*/

        //account_number2 = (TextView)findViewById(R.id.account_number2);

        Intent b = new Intent(this, DoTransfer.class);

        b.putExtra("number2", account_number2.getText().toString());
        System.out.println("------------------1" + number2);
        startActivity(b);

    }
    
}
