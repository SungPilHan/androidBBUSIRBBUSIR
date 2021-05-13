package com.android.insecurebankv2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class test extends Activity {
    Button transfer;
    ImageView image_back;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_inquiry);

        transfer = (Button) findViewById(R.id.button_Transfer1);
        transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dT = new Intent(getApplicationContext(), DoTransfer.class);
                startActivity(dT);
            }
        });

        image_back = (ImageView) findViewById(R.id.account_back);
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pL = new Intent(getApplicationContext(), PostLogin.class);
                startActivity(pL);
            }
        });
    }
}
