package com.exception.findatutor.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.exception.findatutor.R;
import com.exception.findatutor.conn.MongoDB;

import org.bson.Document;

public class SplashActivity extends Activity {

    private ProgressBar pbLoading;
    private TextView tvContentLoading;
    private ImageView ivLogo;
    private boolean connected;
    MongoDB mongoDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        initViewsAndVars();
        setLoadingBar(10, 500);
        tvContentLoading.setText("Connecting to Database...");
        setLoadingBar(30, 500);
        setLoadingBar(10, 1500);
        setLoadingBar(30, 1500);
        new Thread(new Runnable() {

            @SuppressLint("SetTextI18n")
            @Override
            public void run() {


                connected = false;
                System.out.println("database is " + mongoDB.getMongoDatabase());

                try {
                    setLoadingBar(20, 1500);

                    mongoDB.getMongoDatabase().runCommand(new Document("ping", 1));
                    connected = true;
                } catch (Exception e) {
                    connected = false;
                    final String error = e.getMessage();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SplashActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                        }
                    });
                }

                if (pbLoading.getProgress() == 100)
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));

            }
        }).start();
    }


    private void initViewsAndVars() {

        mongoDB = MongoDB.getInstance();

        pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        tvContentLoading = (TextView) findViewById(R.id.tvContentLoading);
        ivLogo = (ImageView) findViewById(R.id.ivlogo);

        tvContentLoading.setText("Loading...");


    }

    private void setLoadingBar(int valueToAdd, long timeInMilliSecs) {
        try {
            Thread.sleep(timeInMilliSecs);
        } catch (Exception e) {
        }
        pbLoading.setProgress(pbLoading.getProgress() + valueToAdd);
    }
}