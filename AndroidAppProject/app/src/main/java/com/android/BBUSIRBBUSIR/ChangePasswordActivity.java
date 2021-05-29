package com.android.BBUSIRBBUSIR;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
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
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ChangePasswordActivity extends Activity {

    ImageView image_back; // 뒤로가기
    //	The EditText that holds the new password entered by the user
    EditText changePassword_text; // 비밀번호 입력
    EditText changePassword_text2; // 비밀번호 한 번 더 입력


    //	The TextView that automatically grabs the current logged in user's username
    TextView textView_Username;
    // The Button that maps to the change password-Submit button
    Button changePassword_button; //등록
    //	Regex to ensure password is complex enough

    private static final String PASSWORD_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";
    private Pattern pattern;
    private Matcher matcher;
    String uname;
    String result;
    BufferedReader reader;
    String serverip = "";
    String serverport = "";
    String protocol = "http://";
    SharedPreferences serverDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepass);

        //뒤로가기 이미지 버튼
        image_back = findViewById(R.id.changepasswd_back);
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Get Server details from Shared Preference file.
        serverDetails = PreferenceManager.getDefaultSharedPreferences(this);
        serverip = serverDetails.getString("serverip", "3.20.202.177");
        serverport = serverDetails.getString("serverport", "8888");

        changePassword_text = findViewById(R.id.editText_newPassword);
        changePassword_text2 = findViewById(R.id.editText_newPassword2);

        Intent intent = getIntent();
        uname = intent.getStringExtra("uname");
        System.out.println("newpassword=" + uname);
        textView_Username = findViewById(R.id.textView_Username);
        textView_Username.setText(uname);

        // Manage the change password button click
        changePassword_button = findViewById(R.id.button_newPasswordSubmit);
        changePassword_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(changePassword_text.getText().toString().isEmpty() || changePassword_text2.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "비밀번호를 모두 입력해주세요!", Toast.LENGTH_LONG).show();
                }
                else {
                    new RequestChangePasswordTask().execute(uname);
                }
            }
        });
    }

    class RequestChangePasswordTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                postData(params[0]);
            } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | IOException | JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Double result) {

        }
        protected void onProgressUpdate(Integer... progress) {

        }

        /*
        The function that makes an HTTP Post to the server endpoint that handles the
        change password operation.
        */
        public void postData(String valueIWantToSend) throws IOException, JSONException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(protocol + serverip + ":" + serverport + "/changepassword");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			/*
			   Delete below test accounts once the application goes into production phase.
			   nameValuePairs.add(new BasicNameValuePair("username", "jack"));
			   nameValuePairs.add(new BasicNameValuePair("password", "Jack@123$"));
			 */
            nameValuePairs.add(new BasicNameValuePair("username", uname));
            nameValuePairs.add(new BasicNameValuePair("newpassword", changePassword_text.getText().toString()));
            HttpResponse responseBody;
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            pattern = Pattern.compile(PASSWORD_PATTERN);
            matcher = pattern.matcher(changePassword_text.getText().toString());

            // Check if the password is complex enough
            boolean isStrong = matcher.matches();
            if (isStrong) {
                responseBody = httpclient.execute(httppost);
                InputStream in = responseBody.getEntity().getContent();
                result = convertStreamToString(in);
                result = result.replace("\n", "");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result != null) {
                            if (result.indexOf("Change Password Successful") != -1) {
                                //	Below code handles the Json response parsing
                                JSONObject jsonObject;
                                try {
                                    jsonObject = new JSONObject(result);
                                    String login_response_message = jsonObject.getString("message");
                                    Toast.makeText(getApplicationContext(), login_response_message + ". Restart application to Continue.", Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                finish();
                            }
                        }
                    }
                });
            }
            else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Entered password is not complex enough.", Toast.LENGTH_LONG).show();
                    }
                });
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