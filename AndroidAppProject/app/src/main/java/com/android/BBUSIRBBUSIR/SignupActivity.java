package com.android.BBUSIRBBUSIR;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebChromeClient;
import android.widget.ImageView;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class SignupActivity extends Activity {
    ImageView image_back; // 로그인 페이지 이동
    String serverip = "";
    String serverport = "";
    String protocol = "http://";
    SharedPreferences serverDetails;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createuser);
        // 회원가입 창에서 로그인 창으로 넘어가는 부분
        image_back = findViewById(R.id.createuser_back);
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        serverDetails = PreferenceManager.getDefaultSharedPreferences(this);
        CryptoClass cryptoClass = new CryptoClass();
        try {
            serverip = cryptoClass.aesDeccryptedString(serverDetails.getString("serverip", null));
            serverport = cryptoClass.aesDeccryptedString(serverDetails.getString("serverport", null));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        //웹 뷰 시작
        WebView mWebView = findViewById(R.id.wv_createuser);
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
