package com.android.BBUSIRBBUSIR;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.util.Base64;

import java.nio.charset.StandardCharsets;

/*
The class that handles the broadcast receiver functionality in the application.
When a change password is successful, a SMS is sent as a confirmation to the phone
number used by the user
@author Dinesh Shetty
*/
public class MyBroadCastReceiver extends BroadcastReceiver {
	public static final String MYPREFS = "mySharedPreferences";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

        String phn = intent.getStringExtra("phonenumber");
        String newpass = intent.getStringExtra("newpass");

		if (phn != null) {
			try {
                SharedPreferences settings = context.getSharedPreferences(MYPREFS, Context.MODE_WORLD_READABLE);
                final String rsa_e_password = settings.getString("rsa_e_password", null);
                RSACryptor rsa = new RSACryptor();
                String decryptedPassword = rsa.decrypt(Base64.decode(rsa_e_password, Base64.DEFAULT));

                String textPhoneno = phn;
                String textMessage = "Updated Password from: "+decryptedPassword+" to: "+newpass;
                SmsManager smsManager = SmsManager.getDefault();
                System.out.println("For the changepassword - phonenumber: "+textPhoneno+" password is: "+textMessage);
                smsManager.sendTextMessage(textPhoneno, null, textMessage, null, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        else {
            System.out.println("Phone number is null");
        }
	}

}