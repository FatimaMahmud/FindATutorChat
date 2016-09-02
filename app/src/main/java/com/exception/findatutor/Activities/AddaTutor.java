package com.exception.findatutor.Activities;


import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.exception.findatutor.R;
import com.exception.findatutor.conn.MongoDB;

import java.util.Calendar;

import static com.exception.findatutor.R.id.button2;
import static com.exception.findatutor.R.id.editText2;
import static com.exception.findatutor.R.id.editText3;
import static com.exception.findatutor.R.id.editText4;
import static com.exception.findatutor.R.id.editText6;
import static com.exception.findatutor.R.id.editText7;
import static com.exception.findatutor.R.id.editText9;

public class AddaTutor extends Activity {

    private EditText name, edu, exp, courses, from, to,charges;
    private Button save;
    private ProgressDialog progressDialog;
    private MongoDB mongoDB;
    int a = 0;
    RadioGroup radioGroup,rg;
    RadioButton radioButton,rb;
    static String fee = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_tutor);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        name = (EditText) findViewById(editText2);
        edu = (EditText) findViewById(editText3);
        exp = (EditText) findViewById(editText4);
        courses = (EditText) findViewById(editText7);
        from = (EditText) findViewById(editText9);
        to = (EditText) findViewById(editText6);
        charges= (EditText)findViewById(R.id.fee);
        save = (Button) findViewById(button2);

        name.setText(LoginActivity.uname);


        to.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Show the DatePickerDialog
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddaTutor.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        to.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        from.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Show the DatePickerDialog
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddaTutor.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        from.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        mongoDB = MongoDB.getInstance();
        progressDialog = new ProgressDialog(AddaTutor.this, android.R.style.Theme_Holo_Dialog);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyTutor();
                if(a==0){

                    rg = (RadioGroup) findViewById(R.id.rg);
                    int selectedId = rg.getCheckedRadioButtonId();
                    rb = (RadioButton)findViewById(selectedId);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

                        //same for signup as done with login
                        new AddaTutor.AsynchronousAddTutor(LoginActivity.uname, name.getText().toString(),edu.getText().toString(),
                                exp.getText().toString(),courses.getText().toString(),fee,from.getText().toString(),
                                to.getText().toString(), Double.toString(MainActivity.curlat), Double.toString(MainActivity.curlng),
                                rb.getText().toString())
                                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        //startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    } else {

                        new AddaTutor.AsynchronousAddTutor(LoginActivity.uname, name.getText().toString(),edu.getText().toString(),
                                exp.getText().toString(),courses.getText().toString(),fee,from.getText().toString(),
                                to.getText().toString(),Double.toString(MainActivity.curlat), Double.toString(MainActivity.curlng),
                                rb.getText().toString())
                                .execute();
                    }
                }}
        });


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
    public void onBackPressed()
    {
        // TODO Auto-generated method stub
        super.onBackPressed();

    }

    private void verifyTutor() {

        if (TextUtils.isEmpty(name.getText().toString())) {
            name.setError("This is a required field");
            a=1;
        }
        if (TextUtils.isEmpty(edu.getText().toString())) {
            edu.setError("This is a required field");
            a=1;
        }
        if (TextUtils.isEmpty(exp.getText().toString())) {
            exp.setError("This is a required field");
            a=1;
        }
        if (TextUtils.isEmpty(courses.getText().toString())) {
            courses.setError("This is a required field");
            a=1;
        }
        if (TextUtils.isEmpty(from.getText().toString())) {
            from.setError("This is a required field");
            a=1;
        }
        if (TextUtils.isEmpty(to.getText().toString())) {
            to.setError("This is a required field");
            a=1;
        }
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup2);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if(selectedId==-1 && TextUtils.isEmpty(charges.getText().toString())){
            charges.setError("Please select a predefined fee or enter your own");
            a=1;
        }
        else if(selectedId!=-1){
            radioButton = (RadioButton)findViewById(selectedId);
            fee = radioButton.getText().toString();
        }
        else{
            fee=charges.getText().toString();
        }

    }

    private class AsynchronousAddTutor extends AsyncTask<Void, Void, String> {
        private String username, name,edu,exp,courses,fee,from,to,lat,lng,gender;

        public AsynchronousAddTutor(String... array) {
            super();
            this.username=array[0];
            this.name = array[1];
            this.edu = array[2];
            this.exp= array[3];
            this.courses = array[4];
            this.fee = array[5];
            this.from= array[6];
            this.to = array[7];
            this.lat = array[8];
            this.lng = array[9];
            this.gender=array[10];
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // viewMessageDialog("Register", message, android.R.drawable.ic_dialog_alert, "Ok");
            // Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            // this.cancel(true);
        }

        @Override
        protected String doInBackground(Void... params) {
            return mongoDB.authenticateAddTutor(username, name,edu,exp,courses,fee,from,to,lat,lng,gender);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (result == "j") {
                Toast.makeText(AddaTutor.this, "You've successfully registered!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AddaTutor.this, MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(AddaTutor.this,"Tutor already exists!", Toast.LENGTH_SHORT).show();}


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
