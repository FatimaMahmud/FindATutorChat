package com.exception.findatutor.Activities;

/**
 * Created by aleena on 03/08/2016.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.util.Random;


public class sms extends BroadcastReceiver {
    static String message = null;
    static String message2 = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        String phoneNumberReceiver = LoginActivity.phoneno;// phone number to which SMS to be send
        Random rand = new Random();
        int x = 50000 + rand.nextInt((90000 - 50000) + 1);
        message = Integer.toString(x);// message to send
        message2 = "Your code is:\n" + message;
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumberReceiver, null, message2, null, null);
        // Show the toast  like in above screen shot
        Toast.makeText(context, "Code sent.", Toast.LENGTH_LONG).show();

    }


}
