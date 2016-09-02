package com.exception.findatutor.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.exception.findatutor.R;
import com.exception.findatutor.conn.MongoDB;

import java.util.ArrayList;

import static com.exception.findatutor.R.id.textView14;
import static com.exception.findatutor.R.id.textView15;
import static com.exception.findatutor.R.id.textView6;
import static com.exception.findatutor.R.id.textView7;
import static com.exception.findatutor.R.id.tv_profile_city;
import static com.exception.findatutor.R.id.tv_profile_educationtext;
import static com.exception.findatutor.R.id.tv_profile_expereinceText;
import static com.exception.findatutor.R.id.tv_profile_feeText;
import static com.exception.findatutor.R.id.tv_profile_occupationText;


public class ProfileActivity extends Activity {
    private TextView name, registeringAs, occupation, city, phoneno, edu, exp, age, charges, courses, emailid;
    private ProgressDialog progressDialog;
    private MongoDB mongoDB;
    private static String username = null;
    private Bundle dataBundle;
    private String state = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        name = (TextView) findViewById(textView6);
        registeringAs = (TextView) findViewById(textView7);
        occupation = (TextView) findViewById(tv_profile_occupationText);
        city = (TextView) findViewById(tv_profile_city);
        phoneno = (TextView) findViewById(textView14);
        edu = (TextView) findViewById(tv_profile_educationtext);
        exp = (TextView) findViewById(tv_profile_expereinceText);
        age = (TextView) findViewById(textView15);
        charges = (TextView) findViewById(tv_profile_feeText);
        courses = (TextView) findViewById(R.id.textView8);
        emailid = (TextView) findViewById(R.id.emailid_profile);

        mongoDB = MongoDB.getInstance();
        progressDialog = new ProgressDialog(ProfileActivity.this, android.R.style.Theme_Holo_Dialog);

        dataBundle = getIntent().getExtras();
        username = dataBundle.getString("username");
        state = dataBundle.getString("state");
        name.setText(username);
        new RetriveTHATUser().execute();
        phoneno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone_no = phoneno.getText().toString();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse(phone_no));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(callIntent);
            }
        });

        Toast.makeText(ProfileActivity.this, "Username is: " + username, Toast.LENGTH_SHORT).show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new RetriveTHATUser().execute();

        } else {
            new RetriveTHATUser().execute();
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

    private class RetriveTHATUser extends AsyncTask<Object, Object, ArrayList<TutorInfoFull>> {
        @Override
        protected void onPostExecute(ArrayList<TutorInfoFull> result) {
            charges.setText(result.get(0).getFee());
            edu.setText(result.get(0).getEdu());
            exp.setText(result.get(0).getExp());
            courses.setText(result.get(0).getCourses());
            new RetrieveOtherInfoFromUserTable().executeOnExecutor(THREAD_POOL_EXECUTOR);
        }

        @Override
        protected ArrayList<TutorInfoFull> doInBackground(Object... params) {
            try {
                return mongoDB.retrieveOneTutorData(username);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class RetrieveOtherInfoFromUserTable extends AsyncTask<Object, Object, ArrayList<UserTableInfoFull>> {
        @Override
        protected void onPostExecute(ArrayList<UserTableInfoFull> result) {
            System.out.println("result is " + result.size());
            city.setText(result.get(0).getCity());
            occupation.setText(result.get(0).getOccupation());
            age.setText(result.get(0).getAge());
            emailid.setText(result.get(0).getEmail());
            phoneno.setText(result.get(0).getPhoneno());
        }

        @Override
        protected ArrayList<UserTableInfoFull> doInBackground(Object... params) {
            try {
                return mongoDB.RetrieveOtherInfoFromUserTable(username);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
