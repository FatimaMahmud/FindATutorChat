package com.exception.findatutor.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.exception.findatutor.R;
import com.exception.findatutor.conn.MongoDB;

import static com.exception.findatutor.R.id.student_profile_activity_agetext;
import static com.exception.findatutor.R.id.student_profile_activity_citytext;
import static com.exception.findatutor.R.id.student_profile_activity_phonenotext;


public class StudentProfile extends Activity {
    private TextView name, registeringAs, occupation, city, phoneno, edu, exp, age, charges;
    private Button shareContact;
    private ProgressDialog progressDialog;
    private MongoDB mongoDB;
    private String username = null;
    private String state = null;
    private Bundle dataBundle;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_profile_activity);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        name = (TextView) findViewById(R.id.student_profile_activity_nametext);
        registeringAs = (TextView) findViewById(R.id.T_S);
        city = (TextView) findViewById(student_profile_activity_citytext);
        phoneno = (TextView) findViewById(student_profile_activity_phonenotext);
        age = (TextView) findViewById(student_profile_activity_agetext);
        shareContact = (Button) findViewById(R.id.share);

        mongoDB = MongoDB.getInstance();
        progressDialog = new ProgressDialog(StudentProfile.this, android.R.style.Theme_Holo_Dialog);

        dataBundle = getIntent().getExtras();
        username = dataBundle.getString("username");
        state = dataBundle.getString("state");
        name.setText(username);
        Toast.makeText(StudentProfile.this, "Username is: " + username, Toast.LENGTH_SHORT).show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new StudentProfile.getFromUsers(username).execute();

        } else {
            new StudentProfile.getFromUsers(username).execute();
        }
        if (state.equals("card")) {
            shareContact.setVisibility(View.VISIBLE);
            shareContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new onClickButton().execute();
                    Intent intent = new Intent(StudentProfile.this, MainActivity.class);
                    intent.putExtra("username", LoginActivity.uname);
                    startActivity(intent);
                }
            });
        }
        else if (state.equals("normal"))
            shareContact.setVisibility(View.INVISIBLE);

    }


    private class onClickButton extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            mongoDB.UpdateTheStudentNOtification(mongoDB.TutorNotificationSenderName(LoginActivity.uname),
                    "accepted", LoginActivity.uname);
            System.out.println("HERE");
            mongoDB.UpdateTheTutorNOtification(LoginActivity.uname, "accepted", LoginActivity.uname);

            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();

    }

    private class getFromUsers extends AsyncTask<Void, Void, String[]> {
        private String username;
        String data[] = {"", "", "", "", "", ""};


        public getFromUsers(String... array) {
            super();
            this.username = array[0];
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(Void... params) {
            data = mongoDB.getFromUsers(username).clone();

            return data;
        }

        @Override
        protected void onPostExecute(final String[] result) {
            super.onPostExecute(result);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            if (data[0] == null) {
                Toast.makeText(StudentProfile.this, "No data found", Toast.LENGTH_SHORT).show();
                name.setText("");
                registeringAs.setText("");
                city.setText("");
                phoneno.setText("");
                age.setText("");
            } else {
                Toast.makeText(StudentProfile.this, "Data found", Toast.LENGTH_SHORT).show();
                registeringAs.setText(data[0]);
                city.setText(data[2]);
                phoneno.setText(data[3]);
                age.setText(data[4]);
            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }


}
