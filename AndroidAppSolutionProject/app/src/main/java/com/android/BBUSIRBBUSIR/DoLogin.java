package com.android.BBUSIRBBUSIR;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import com.marcohc.toasteroid.Toasteroid;

/*
The page that accepts new password and passes it on to the change password 
module. This new password can then be used by the user to log in to the account.
@author Dinesh Shetty
*/

public class DoLogin extends Activity {
	String responseString = null;
	//	Stores the username passed by the calling intent
	String username;
	//	Stores the password passed by the calling intent
	String password;
	String result;
	String superSecurePassword;
	String rememberme_username, rememberme_password;
	public static final String MYPREFS = "mySharedPreferences";
	String serverip = "";
	String serverport = "";
	String protocol = "http://";
	BufferedReader reader;
	SharedPreferences serverDetails;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_do_login);
		finish();

        // Get Server details from Shared Preference file.
		serverDetails = PreferenceManager.getDefaultSharedPreferences(this);
		serverip = serverDetails.getString("serverip", "3.20.202.177");
		serverport = serverDetails.getString("serverport", "8888");
        if(serverip!=null && serverport!=null){
			Intent data = getIntent();
			username = data.getStringExtra("passed_username");
			password = data.getStringExtra("passed_password");
			new RequestTask().execute("username");
        }
        else
        {
            Intent setupServerdetails =new Intent(this,FilePrefActivity.class);
            startActivity(setupServerdetails);
            Toasteroid.show(this, "Server path/port not set!!", Toasteroid.STYLES.WARNING, Toasteroid.LENGTH_SHORT);
        }
	}

	class RequestTask extends AsyncTask < String, String, String > {
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
		public void postData(String valueIWantToSend) throws ClientProtocolException, IOException, JSONException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, CertificateException, KeyStoreException {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(protocol + serverip + ":" + serverport + "/login");
			HttpPost httppost2 = new HttpPost(protocol + serverip + ":" + serverport + "/devlogin");

			// Add your data
			List < NameValuePair > nameValuePairs = new ArrayList < NameValuePair > (2);
			//                Delete below test accounts in production
			//                nameValuePairs.add(new BasicNameValuePair("username", "jack"));
			//                nameValuePairs.add(new BasicNameValuePair("password", "jack@123$"));
			nameValuePairs.add(new BasicNameValuePair("username", username));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			HttpResponse responseBody;
			if (username.equals("devadmin")) {
				httppost2.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				// Execute HTTP Post Request
				responseBody = httpclient.execute(httppost2);
			} else {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				// Execute HTTP Post Request
				responseBody = httpclient.execute(httppost);
			}

			InputStream in = responseBody.getEntity().getContent();
			result = convertStreamToString( in );
			result = result.replace("\n", "");
			if (result != null) {
				if (result.indexOf("Correct Credentials") != -1) {
					Log.d("Successful Login:", ", account=" + username + ":" + password);
					saveCreds(username, password);
					trackUserLogins();
					Intent pL = new Intent(getApplicationContext(), PostLogin.class);
					pL.putExtra("uname", username);
					startActivity(pL);
				} else {
					Intent xi = new Intent(getApplicationContext(), WrongLogin.class);
					startActivity(xi);
				}
			}
		}

		/*
		The function that tracks all the users who have successfully
		logged in to the application using that device
		*/
		private void trackUserLogins() {
			// TODO Auto-generated method stub
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					ContentValues values = new ContentValues();
					values.put(TrackUserContentProvider.name, username);
					// Inserts content into the Content Provider to track the logged in user's list
					Uri uri = getContentResolver().insert(TrackUserContentProvider.CONTENT_URI, values);
				}
			});
		}

		/*
		The function that saves the credentials locally for future reference
		username: username entered by the user
		password: password entered by the user
		*/
		private void saveCreds(String username, String password) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, CertificateException, KeyStoreException {
			// TODO Auto-generated method stub
			SharedPreferences mySharedPreferences;
			mySharedPreferences = getSharedPreferences(MYPREFS, Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = mySharedPreferences.edit();
			rememberme_username = username;
			rememberme_password = password;
			String base64Username = new String(Base64.encodeToString(rememberme_username.getBytes(), 4));
			CryptoClass crypt = new CryptoClass();

			RSACryptor rsa = new RSACryptor();
			RSACryptor rsa2 = new RSACryptor();
			rsa.createKey();
			byte[] rsa_e_username = rsa.encrypt(username);
			String rsa_d_username = rsa2.decrypt(rsa_e_username);
			byte[] rsa_e_password = rsa.encrypt(password);
			String rsa_d_password = rsa2.decrypt(rsa_e_password);

			superSecurePassword = crypt.aesEncryptedString(rememberme_password);
			editor.putString("EncryptedUsername", base64Username);
			editor.putString("superSecurePassword", superSecurePassword);

			editor.putString("rsa_e_username", rsa_e_username.toString());
			editor.putString("rsa_d_username", rsa_d_username);
			editor.putString("rsa_e_password", rsa_e_password.toString());
			editor.putString("rsa_d_password", rsa_d_password);

			editor.commit();
		}

		private String convertStreamToString(InputStream in ) throws IOException {
			// TODO Auto-generated method stub
			try {
				reader = new BufferedReader(new InputStreamReader( in , "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			} in .close();
			return sb.toString();
		}
	}
}