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
}