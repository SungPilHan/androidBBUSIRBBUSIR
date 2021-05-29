package com.android.BBUSIRBBUSIR;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

/*
The page that accepts username and the password from the user. The credentials
are then sent to the server and the user is allowed to proceed to the postlogin
pages on a successful authentication
@author Dinesh Shetty
*/
public class LoginActivity extends Activity {
	//   The Button that calls the authentication function
	Button login_buttons;
	//   The Button that calls the create user function
	Button createuser_buttons;
	//   The EditText that holds the username entered by the user
	EditText Username_Text;
	//   The EditText that holds the password entered by the user
	EditText Password_Text;

	private ImageView imageView;
	//   The Button that allows the user to autofill the credentials,
	//  if the user has logged in successfully earlier
	Button fillData_button;
	String usernameBase64ByteString;
	public static final String MYPREFS = "mySharedPreferences";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_log_main);
		imageView = findViewById(R.id.imageView3);
		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				final PopupMenu popupMenu = new PopupMenu(getApplicationContext(),view);
				getMenuInflater().inflate(R.menu.popup,popupMenu.getMenu());
				popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						if (item.getItemId() == R.id.setNetwork){
							Intent intent = new Intent(getApplicationContext(), FilePrefActivity.class);
							startActivity(intent);
						}
						else {
							finish();
							Intent intent2 = new Intent(getApplicationContext(), LoginActivity.class);
							startActivity(intent2);
						}
						return false;
					}
				});
				popupMenu.show();
			}
		});

		login_buttons = findViewById(R.id.login_button);
		login_buttons.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				performlogin();
			}
		});

		createuser_buttons = findViewById(R.id.button_CreateUser2);
        createuser_buttons.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View view){
				Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
				startActivity(intent);
			}
        });

        try {
            fillData();
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
	}

	/*
    The function that allows the user to autofill the credentials
    if the user has logged in successfully atleast one earlier using
    that device
    */
	protected void fillData() throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		// TODO Auto-generated method stub
		SharedPreferences settings = getSharedPreferences(MYPREFS, 0);
		final String rsa_e_username = settings.getString("rsa_e_username", null);
		final String rsa_e_password = settings.getString("rsa_e_password", null);

		RSACryptor rsa = new RSACryptor();
		if(rsa_e_username!=null && rsa_e_password!=null)
		{
			Username_Text = findViewById(R.id.loginscreen_username);
			Password_Text = findViewById(R.id.loginscreen_password);

			String rsa_d_username = rsa.decrypt(Base64.decode(rsa_e_username, Base64.DEFAULT));
			Username_Text.setText(rsa_d_username);

			String rsa_d_password = rsa.decrypt(Base64.decode(rsa_e_password, Base64.DEFAULT));
			Password_Text.setText(rsa_d_password);
		}
		else if (rsa_e_username==null || rsa_e_password==null){
			//Toast.makeText(this, "No stored credentials found!!", Toast.LENGTH_LONG).show();
		}
		else{
			Toast.makeText(this, "No stored credentials found!!", Toast.LENGTH_LONG).show();
		}
	}

	/*
    The function that passes the control on to the authentication module
    Username_Text: Username entered by the user
    Password_Text: password entered by the user
    */
	protected void performlogin() {
		// TODO Auto-generated method stub
		Username_Text = findViewById(R.id.loginscreen_username);
		Password_Text = findViewById(R.id.loginscreen_password);
		Intent i = new Intent(this, DoLogin.class);
		i.putExtra("passed_username", Username_Text.getText().toString());
		i.putExtra("passed_password", Password_Text.getText().toString());
		startActivity(i);
	}
}