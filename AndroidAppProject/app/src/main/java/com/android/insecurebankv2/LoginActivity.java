package com.android.insecurebankv2;

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
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;


import com.android.insecurebankv2.DoLogin;
import com.marcohc.toasteroid.Toasteroid;

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
//      String mess = getResources().getString(R.string.is_admin);
//      if (mess.equals("no")) {
//         View button_CreateUser = findViewById(R.id.button_CreateUser2);
//         button_CreateUser.setVisibility(View.GONE);
//      }



		imageView = (ImageView) findViewById(R.id.imageView3);

		login_buttons = (Button) findViewById(R.id.login_button);
		login_buttons.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				performlogin();
			}
		});


        createuser_buttons = (Button) findViewById(R.id.button_CreateUser2);
        createuser_buttons.setOnClickListener(new View.OnClickListener() {

            @Override
			public void onClick(View view){
				Intent intent = new Intent(LoginActivity.this, createuser.class);
				startActivity(intent);
			}
        });
//
//        try {
//            fillData();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (NoSuchPaddingException e) {
//            e.printStackTrace();
//        } catch (InvalidAlgorithmParameterException e) {
//            e.printStackTrace();
//        } catch (IllegalBlockSizeException e) {
//            e.printStackTrace();
//        } catch (BadPaddingException e) {
//            e.printStackTrace();
//        }

//      fillData_button = (Button) findViewById(R.id.fill_data);
//      fillData_button.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                try {
//                    fillData();
//                } catch (InvalidKeyException | UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//        });

		//메뉴
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
	}

//onCreate


    /*
    The function that allows the user to create new user credentials.
    This functionality is available only to the admin user.
    <<WIP Code>>
    ToDo: Add functionality here.
    */
//    protected void createUser() {
//        Toasteroid.show(this, "Create User functionality is still Work-In-Progress!!", Toasteroid.STYLES.WARNING, Toasteroid.LENGTH_LONG);
//
//    }

	/*
    The function that allows the user to autofill the credentials
    if the user has logged in successfully atleast one earlier using
    that device
    */



	protected void fillData() throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		// TODO Auto-generated method stub
		SharedPreferences settings = getSharedPreferences(MYPREFS, 0);
		final String username = settings.getString("EncryptedUsername", null);
		final String password = settings.getString("superSecurePassword", null);


		if(username!=null && password!=null)
		{
			byte[] usernameBase64Byte = Base64.decode(username, Base64.DEFAULT);
			try {
				usernameBase64ByteString = new String(usernameBase64Byte, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Username_Text = (EditText) findViewById(R.id.loginscreen_username);
			Password_Text = (EditText) findViewById(R.id.loginscreen_password);
			Username_Text.setText(usernameBase64ByteString);
			CryptoClass crypt = new CryptoClass();
			String decryptedPassword = crypt.aesDeccryptedString(password);
			Password_Text.setText(decryptedPassword);
		}
		else if (username==null || password==null)
		{
			//  Toast.makeText(this, "No stored credentials found!!", Toast.LENGTH_LONG).show();
		}
		else
		{
			//  Toast.makeText(this, "No stored credentials found!!", Toast.LENGTH_LONG).show();
		}

	}

	/*
    The function that passes the control on to the authentication module
    Username_Text: Username entered by the user
    Password_Text: password entered by the user
    */


	protected void performlogin() {
		// TODO Auto-generated method stub
		Username_Text = (EditText) findViewById(R.id.loginscreen_username);
		Password_Text = (EditText) findViewById(R.id.loginscreen_password);
		Intent i = new Intent(this, DoLogin.class);
		i.putExtra("passed_username", Username_Text.getText().toString());
		i.putExtra("passed_password", Password_Text.getText().toString());
		startActivity(i);
	}

	public void callPreferences() {
		// TODO Auto-generated method stub
		Intent i = new Intent(this, FilePrefActivity.class);
		startActivity(i);
	}


}