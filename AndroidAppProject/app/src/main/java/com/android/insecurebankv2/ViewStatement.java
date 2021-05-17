package com.android.insecurebankv2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import com.marcohc.toasteroid.Toasteroid;

import java.io.File;

/*
The page that allows the user to view transaction history for the logged in user
@author Dinesh Shetty
*/
public class ViewStatement extends Activity {
	String uname;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_statement);
		Intent intent = getIntent();
		uname = intent.getStringExtra("uname");
		//String statementLocation=Environment.getExternalStorageDirectory()+ "/Statements_" + uname + ".html";
		String FILENAME="Statements_" + uname + ".html";
		File fileToCheck = new File(Environment.getExternalStorageDirectory(), FILENAME);
		System.out.println(fileToCheck.toString());
		if (fileToCheck.exists()) {
			//Toast.makeText(this, "Statement Exists!!",Toast.LENGTH_LONG).show();

			WebView mWebView = (WebView) findViewById(R.id.webView1);
			//   Location where the statements are stored locally on the device sdcard
			mWebView.loadUrl("file://" + Environment.getExternalStorageDirectory() + "/Statements_" + uname + ".html");
			mWebView.getSettings().setJavaScriptEnabled(true);
			mWebView.getSettings().setSaveFormData(true);
			mWebView.getSettings().setBuiltInZoomControls(true);
			mWebView.setWebViewClient(new MyWebViewClient());
			WebChromeClient cClient = new WebChromeClient();
			mWebView.setWebChromeClient(cClient);
		} else
		{
			Intent gobacktoPostLogin =new Intent(this,PostLogin.class);
			startActivity(gobacktoPostLogin);
			Toasteroid.show(this, "Statement does not Exist!!", Toasteroid.STYLES.WARNING, Toasteroid.LENGTH_SHORT);

		}
	}
	public void callPreferences() {
		// TODO Auto-generated method stub
		Intent i = new Intent(this, FilePrefActivity.class);
		startActivity(i);
	}
}