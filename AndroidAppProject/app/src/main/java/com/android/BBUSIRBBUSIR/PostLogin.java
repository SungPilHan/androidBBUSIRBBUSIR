package com.android.BBUSIRBBUSIR;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


/*
The page that allows gives the user below functionalities
Transfer: Module that allows transfer of amount between two accounts
View Statement: Module that allows the user to view transaction history for the logged in user
Change Password:  Module that allows the logged in user to change the password
@author Dinesh Shetty
*/
public class PostLogin extends Activity {
	JSONObject jsonObject;

	ImageView image_back;
	ImageView image_lock;
	Button add_count;
	TextView name;
	String uname;

	LinearLayout linearLayout;
	LinearLayout.LayoutParams linearParams;

	String result;
	BufferedReader reader;
	HttpResponse responseBody;
	InputStream in;
	String serverip = "";
	String serverport = "";
	SharedPreferences serverDetails;
	String protocol = "http://";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_main);
		Intent intent = getIntent();
		uname = intent.getStringExtra("uname");
		name = (TextView) findViewById(R.id.name);
		name.setText(uname);

		serverDetails = PreferenceManager.getDefaultSharedPreferences(this);
		serverip = serverDetails.getString("serverip", "3.20.202.177");
		serverport = serverDetails.getString("serverport", "8888");

		// 뒤로가기
		image_back = (ImageView) findViewById(R.id.new_main_back);
		image_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mL = new Intent(getApplicationContext(),LoginActivity.class);
				startActivity(mL);
			}
		});

		// 비밀번호 설정
		image_lock = (ImageView) findViewById(R.id.new_main_lock);
		image_lock.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				testchangepw();
			}
		});

		linearLayout = (LinearLayout) findViewById(R.id.linearlayout);
		linearLayout.setGravity(Gravity.TOP);
		linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		linearParams.gravity = Gravity.CENTER;

		new PostLogin.RequestAccountlistTask().execute("username");

		add_count = (Button) findViewById(R.id.add_account);
		add_count.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new PostLogin.RequestAccountaddTask().execute("username");
			}
		});
	}

	private void make_button(final String account, final String balance){
		Button buttons = new Button(this);
		buttons.setLayoutParams(linearParams);
		buttons.setBackgroundResource(R.drawable.border_button_account);
		String str= account + "\n" + balance;
		buttons.setText(str);
		buttons.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				move_dotransfer(account, balance);
			}
		});
		linearLayout.addView(buttons);
	}

	//비밀번호 변경 페이지로 이동, 사용자 id 전달
	private void testchangepw() {
		Intent b = new Intent(this, ChangePassword2.class);
		b.putExtra("uname", name.getText().toString());
		startActivity(b);
	}

	// 전송(계좌내역조회) 페이지로 금액, 계좌 값 전달
	private void move_dotransfer(String account, String balance) {
		Intent a = new Intent(this, test.class);
		a.putExtra("balance", balance);
		a.putExtra("account", account);
		startActivity(a);
	}

	public class RequestAccountlistTask extends AsyncTask<String, String, String> {

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
			HttpPost httppost = new HttpPost(protocol + serverip + ":" + serverport + "/accountlist");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("username", uname));
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
							try {
								jsonObject = new JSONObject(result);
								Log.d("-------------------", jsonObject.getString("account"));
								Log.d("-------------------", jsonObject.getJSONArray("account").getJSONObject(0).toString());
								Log.d(">>>>>>>>>>",Integer.toString(jsonObject.getJSONArray("account").length()));
								int i;
								for(i=0; i<jsonObject.getJSONArray("account").length();i++){
									make_button(jsonObject.getJSONArray("account").getJSONObject(i).getString("number"),
											jsonObject.getJSONArray("account").getJSONObject(i).getString("balance"));
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					});
					Log.d("Result : ", "transfer Done");
				}
				else{
					runOnUiThread(new Runnable(){
						@Override
						public void run(){
							Toast.makeText(getApplicationContext(), "계좌 목록을 불러오지 못했습니다.", Toast.LENGTH_LONG).show();
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

	public class RequestAccountaddTask extends AsyncTask<String, String, String> {

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
			HttpPost httppost = new HttpPost(protocol + serverip + ":" + serverport + "/accountadd");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("username", uname));
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
							try {
								jsonObject = new JSONObject(result);
								Log.d("---------->>>",jsonObject.getString("account"));
								Log.d("---------->>>",jsonObject.getString("balance"));
								make_button(jsonObject.getString("account"),jsonObject.getString("balance"));
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					});
					Log.d("Result : ", "transfer Done");
				}
				else{
					runOnUiThread(new Runnable(){
						@Override
						public void run(){
							Toast.makeText(getApplicationContext(), "계좌 목록을 불러오지 못했습니다.", Toast.LENGTH_LONG).show();
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