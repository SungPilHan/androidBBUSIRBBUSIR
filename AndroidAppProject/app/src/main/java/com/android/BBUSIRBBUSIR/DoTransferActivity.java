package com.android.BBUSIRBBUSIR;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/*
The page that allows the user to transfer an amount between two accounts
@author Dinesh Shetty
*/
public class DoTransferActivity extends Activity {

    String result;
    String passNormalized;
    String usernameBase64ByteString;
    ImageView image_back;

    TextView editText_from;
    String from;

    BufferedReader reader;
    //	The EditText that holds the to account number
    EditText to;
    //	The EditText that holds the to amount to be transferred between the accounts
    EditText amount;
    //	The Button that handles the transfer operation activity
    Button transfer;
    HttpResponse responseBody;
    InputStream in;
    String serverip = "";
    String serverport = "";
    String protocol = "http://";
    SharedPreferences serverDetails;
    public static final String MYPREFS2 = "mySharedPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_transfer);

        image_back = findViewById(R.id.transfer_back);
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //송금 페이지로 이동할 때 보내는 계좌 TextView에 사용자 계좌번호 전달
        Intent intent = getIntent();
        from = intent.getStringExtra("account");

        editText_from = findViewById(R.id.editText_from);
        editText_from.setText(from);

        // Get Server details from Shared Preference file.
        serverDetails = PreferenceManager.getDefaultSharedPreferences(this);
        serverip = serverDetails.getString("serverip", "3.20.202.177");
        serverport = serverDetails.getString("serverport", "8888");

        // Handle the transfer functionality
        transfer = findViewById(R.id.Transfer);
        transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                to = findViewById(R.id.editText_to);
                amount = findViewById(R.id.editText_amount);
                new RequestDoTransferTask().execute("username");
            }
        });
    }

    public class RequestDoTransferTask extends AsyncTask<String, String, String> {

        /**
         * constructor
         *
         * @return
         */
        public void AsyncHttpTransferPost(String string) {
            //do something
        }

        /**
         * background functions
         */
        @Override
        protected String doInBackground(String... params) {
            String str = "";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(protocol + serverip + ":" + serverport + "/dotransfer");
            SharedPreferences settings = getSharedPreferences(MYPREFS2, 0);

            final String rsa_e_username = settings.getString("rsa_e_username", null);
            final String rsa_e_password = settings.getString("rsa_e_password", null);
            RSACryptor rsa = new RSACryptor();
            String rsa_d_username = rsa.decrypt(Base64.decode(rsa_e_username, Base64.DEFAULT));
            String rsa_d_password = rsa.decrypt(Base64.decode(rsa_e_password, Base64.DEFAULT));

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
            nameValuePairs.add(new BasicNameValuePair("username", rsa_d_username));
            nameValuePairs.add(new BasicNameValuePair("password", rsa_d_password));
            nameValuePairs.add(new BasicNameValuePair("from_acc", from));
            nameValuePairs.add(new BasicNameValuePair("to_acc", to.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("amount", amount.getText().toString()));
            try {
                //	The HTTP Post of the credentials plus the transaction information
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                //	Stores the HTTP response of the transaction activity
                responseBody = httpclient.execute(httppost);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            try {
                in = responseBody.getEntity().getContent();
            } catch (IllegalStateException | IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            try {
                result = convertStreamToString(in);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            result = result.replace("\n", "");
            if (result != null) {
                if (result.indexOf("Success") != -1) {
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            Toast.makeText(getApplicationContext(), "Transfer Done", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                    Log.d("Result : ", "transfer Done");
                }
                else{
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            Toast.makeText(getApplicationContext(), "Transfer Fail", Toast.LENGTH_LONG).show();
                        }
                    });
                    Log.d("Result : ", "transfer Fail");
                }
            }
            return str;
        }

        @Override
        protected void onPostExecute(String result) {
        }

        protected void onProgressUpdate(String... progress) {
        }

    }
    public String convertStreamToString(InputStream in) throws IOException {
        // TODO Auto-generated method stub
        reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        in.close();
        return sb.toString();
    }
}