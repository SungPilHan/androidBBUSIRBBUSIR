package com.android.insecurebankv2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class test extends Activity {
    Button transfer;
    ImageView image_back;
    TextView account_number2;
    TextView price2;



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

        Intent intent = getIntent();

        account_number2 = (TextView)findViewById(R.id.account_number2);
        String number2 = intent.getStringExtra("passed_account");
        account_number2.setTextSize(20);
        account_number2.setText(number2);

        price2 = (TextView) findViewById(R.id.price2);
        String price22 = intent.getStringExtra("passed_price");
        price2.setTextSize(20);
        price2.setText(price22);




    }
}
