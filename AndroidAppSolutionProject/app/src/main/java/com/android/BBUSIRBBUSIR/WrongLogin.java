package com.android.BBUSIRBBUSIR;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

/*
The page that the user is redirected to, if the entered credentials are incorrect
@author Dinesh Shetty
*/
public class WrongLogin extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wrong_login);
		//   Popup that the user is welcomed with, in case of invalid credentials and redirect back to login screen
		Toast.makeText(getApplicationContext(), "Invalid Credentials!!", Toast.LENGTH_LONG).show();
        Intent backtoLogin = new Intent(this, LoginActivity.class);
        startActivity(backtoLogin);
        finish();
	}
	// Added for handling menu operations
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void callPreferences() {
		// TODO Auto-generated method stub
		Intent i = new Intent(this, FilePrefActivity.class);
		startActivity(i);
	}
}