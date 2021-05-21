package com.android.insecurebankv2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Thread.sleep;

public class test extends Activity {
    Button transfer;
    ImageView image_back;
    TextView account_number2;
    TextView price2;
    String number2;//송금할 때 보내는 계좌 text로 나타내기 위함
    WebView mWebView;

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
                finish();
            }
        });

        Intent intent = getIntent();
        account_number2 = (TextView)findViewById(R.id.account_number2);
        number2 = intent.getStringExtra("account");
        account_number2.setTextSize(20);
        account_number2.setText(number2);
        price2 = (TextView) findViewById(R.id.price2);
        String balance = intent.getStringExtra("balance");
        price2.setTextSize(20);
        price2.setText(balance);

        mWebView = (WebView) findViewById(R.id.wv_transferhistory);
    }

    @Override
    public void onResume(){
        super.onResume();
        //웹 뷰 시작
        mWebView.setWebViewClient(new WebViewClient());
        String postdata = "account="+number2;
        mWebView.postUrl("http://3.20.202.177:8888/searchtransferhistory", postdata.getBytes());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setSaveFormData(false);
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.setWebViewClient(new MyWebViewClient());
        WebChromeClient cClient = new WebChromeClient();
        mWebView.setWebChromeClient(cClient);
    }
    //송금 페이지로 이동, 사용자 계좌번호 전달
    private void trans_account() {
        Intent b = new Intent(this, DoTransfer.class);
        b.putExtra("account", account_number2.getText().toString());
        b.putExtra("balance", price2.getText().toString());
        startActivity(b);
    }
}
