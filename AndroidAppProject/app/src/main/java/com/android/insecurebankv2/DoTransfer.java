package com.android.insecurebankv2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.marcohc.toasteroid.Toasteroid;

/*
The page that allows the user to transfer an amount between two accounts
@author Dinesh Shetty
*/
public class DoTransfer extends Activity {

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

        image_back = (ImageView) findViewById(R.id.transfer_back);
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //송금 페이지로 이동할 때 보내는 계좌 TextView에 사용자 계좌번호 전달
        Intent intent = getIntent();
        from = intent.getStringExtra("account");

        editText_from = (TextView) findViewById(R.id.editText_from);
        editText_from.setText(from);

        // Get Server details from Shared Preference file.
        serverDetails = PreferenceManager.getDefaultSharedPreferences(this);
        serverip = serverDetails.getString("serverip", "3.20.202.177");
        serverport = serverDetails.getString("serverport", "8888");

        // Handle the transfer functionality
        transfer = (Button)findViewById(R.id.Transfer);
        transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
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
            final String username = settings.getString("EncryptedUsername", null);
            byte[] usernameBase64Byte = Base64.decode(username, Base64.DEFAULT);
            try {
                usernameBase64ByteString = new String(usernameBase64Byte, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            final String password = settings.getString("superSecurePassword", null);
            try {
            	//복호화
                //	Stores the decrypted form of the password from the locally stored shared preference file
                passNormalized = getNormalizedPassword(password);
            } catch (InvalidKeyException | UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
            nameValuePairs.add(new BasicNameValuePair("username", usernameBase64ByteString));
            nameValuePairs.add(new BasicNameValuePair("password", passNormalized));
            to = (EditText) findViewById(R.id.editText_to);
            amount = (EditText) findViewById(R.id.editText_amount);
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


    /*
    The function that handles the aes256 decryption of the password from the encrypted password.
    password: Encrypted password input to the aes function
    returns: Plaintext password outputted by the aes function
    */
    private String getNormalizedPassword(String password) throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        CryptoClass crypt = new CryptoClass();
        return crypt.aesDeccryptedString(password);
    }


    public String convertStreamToString(InputStream in) throws IOException {
        // TODO Auto-generated method stub
        try {
            reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        in.close();
        return sb.toString();
    }
}