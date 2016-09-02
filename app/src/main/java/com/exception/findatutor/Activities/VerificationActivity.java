package com.exception.findatutor.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.exception.findatutor.R;
import com.exception.findatutor.conn.MongoDB;


public class VerificationActivity extends Activity {

    Button b2;
    EditText text2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        b2 = (Button) findViewById(R.id.b2);
        text2 = (EditText) findViewById(R.id.text2);

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = text2.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    Context c = view.getContext();
                    Toast.makeText(c, "Enter code.", Toast.LENGTH_LONG).show();
                } else if (code.equals(sms.message)) {
                    Context c = view.getContext();
                    Toast.makeText(c, "Verified.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(VerificationActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Context c = view.getContext();
                    Toast.makeText(c, "Wrong code.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}


