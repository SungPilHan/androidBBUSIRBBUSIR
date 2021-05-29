package com.android.BBUSIRBBUSIR;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static java.lang.Thread.sleep;

public class ViewStatmentActivity extends Activity {
    String uname;
    Button transfer;
    ImageView image_back;
    TextView account_number2;
    TextView price2;
    String number2;//송금할 때 보내는 계좌 text로 나타내기 위함
    WebView mWebView;
    String serverip = "";
    String serverport = "";
    String protocol = "http://";
    SharedPreferences serverDetails;
    String result;
    JSONObject jsonObject;
    BufferedReader reader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_inquiry);
        Intent intent = getIntent();
        uname = intent.getStringExtra("username");

        serverDetails = PreferenceManager.getDefaultSharedPreferences(this);
        serverip = serverDetails.getString("serverip", "3.20.202.177");
        serverport = serverDetails.getString("serverport", "8888");

        // 송금 페이지로 이동
        transfer = findViewById(R.id.button_Transfer1);
        transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trans_account();
                /*Intent dT = new Intent(getApplicationContext(), DoTransfer.class);
                startActivity(dT);*/
            }
        });

        // 뒤로가기
        image_back = findViewById(R.id.account_back);
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent post = new Intent(getApplicationContext(), UserMainActivity.class);
                post.putExtra("uname", uname);
                startActivity(post);
                finish();
            }
        });

        account_number2 = findViewById(R.id.account_number2);
        number2 = intent.getStringExtra("account");
        account_number2.setTextSize(20);
        account_number2.setText(number2);

        price2 = findViewById(R.id.price2);
        new RequestbalanceTask().execute("account");

        mWebView = findViewById(R.id.wv_transferhistory);
    }

    @Override
    public void onResume(){
        super.onResume();
        //웹 뷰 시작
        mWebView.setWebViewClient(new WebViewClient());
        String postdata = "account="+number2;
        String url = protocol + serverip + ":" + serverport + "/searchtransferhistory";
        mWebView.postUrl(url, postdata.getBytes());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setSaveFormData(false);
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.setWebViewClient(new MyWebViewClient());
        WebChromeClient cClient = new WebChromeClient();
        mWebView.setWebChromeClient(cClient);
    }
    //송금 페이지로 이동, 사용자 계좌번호 전달
    private void trans_account() {
        Intent b = new Intent(this, DoTransferActivity.class);
        b.putExtra("account", account_number2.getText().toString());
        startActivity(b);
    }

    class RequestbalanceTask extends AsyncTask< String, String, String > {
        @Override
        protected String doInBackground(String...params) {
            try {
                postData(params[0]);
            } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | IOException | JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (CertificateException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(Double result) {}
        protected void onProgressUpdate(Integer...progress) {}
        public void postData(String valueIWantToSend) throws IOException, JSONException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, CertificateException, KeyStoreException {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(protocol + serverip + ":" + serverport + "/getbalance");

            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList< NameValuePair >(2);
            //                Delete below test accounts in production
            //                nameValuePairs.add(new BasicNameValuePair("username", "jack"));
            //                nameValuePairs.add(new BasicNameValuePair("password", "jack@123$"));
            nameValuePairs.add(new BasicNameValuePair("account", number2));
            HttpResponse responseBody;
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Execute HTTP Post Request
            responseBody = httpclient.execute(httppost);

            InputStream in = responseBody.getEntity().getContent();
            result = convertStreamToString( in );
            result = result.replace("\n", "");
            if (result != null) {
                if (result.indexOf("success") != -1) {
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            try {
                                jsonObject = new JSONObject(result);
                                price2.setTextSize(20);
                                price2.setText(jsonObject.getString("balance"));
                            } catch (JSONException e) {
                                Toast.makeText(getApplicationContext(), "잔액 정보를 불러오지 못했습니다.", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            Toast.makeText(getApplicationContext(), "잔액 정보를 불러오지 못했습니다.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                }
            }
        }

        private String convertStreamToString(InputStream in ) throws IOException {
            // TODO Auto-generated method stub
            reader = new BufferedReader(new InputStreamReader( in , StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            } in .close();
            return sb.toString();
        }
    }

}
