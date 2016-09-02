package com.exception.findatutor.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.exception.findatutor.R;
import com.exception.findatutor.conn.MongoDB;

public class LoginActivity extends Activity implements View.OnClickListener {


    private ViewFlipper vfLoginFlipper;
    private EditText etUsernameLogin;
    private EditText etPasswordLogin;
    private EditText etName, etEmail, etUsernameSignup, etPasswordSignup, editText5, etoccupation, etage;

    private ProgressDialog progressDialog;
    private MongoDB mongoDB;
    int a = 0;
    int b = 0;
    static String password = null;
    public static String uname = null;
    static String email = null;
    static String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public static String phoneno = null;
    static String city = null;
    static String occupation = null;
    static int age = 0;
    static String registeringAs = null;
    private  Button loginButton;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private AutoCompleteTextView etcity;
    private TextView invalid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        initViewsAndVars();
    }

    private void initViewsAndVars() {
        vfLoginFlipper = (ViewFlipper) findViewById(R.id.vfLoginFlipper);
        vfLoginFlipper.setInAnimation(this, android.R.anim.slide_in_left);
        vfLoginFlipper.setOutAnimation(this, android.R.anim.slide_out_right);

        etUsernameLogin = initEditText(R.id.etUsername);
        uname = etUsernameLogin.getText().toString();
        etPasswordLogin = initEditText(R.id.etPassword);
        // etName = initEditText(R.id.etName);
        etEmail = initEditText(R.id.etEmail);
        etUsernameSignup = initEditText(R.id.etUsernameSignup);
        etPasswordSignup = initEditText(R.id.etPasswordSignup);
        editText5 = initEditText(R.id.editText5);
        etoccupation = initEditText(R.id.etoccupation);
        etage = initEditText(R.id.etage);
        loginButton = (Button)findViewById(R.id.btLogin);
        initButton(R.id.btLogin);
        initButton(R.id.btSignup);
        initButton(R.id.btRegister);
        initButton(R.id.btBack);

        // Get a reference to the AutoCompleteTextView in the layout
        etcity = (AutoCompleteTextView) findViewById(R.id.etcity);
        // Get the string array
        String[] countries = getResources().getStringArray(R.array.city_array);
        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, countries);
        etcity.setAdapter(adapter);
        invalid= (TextView)findViewById(R.id.invalid);

        mongoDB = MongoDB.getInstance();
        progressDialog = new ProgressDialog(LoginActivity.this, android.R.style.Theme_Holo_Dialog);
    }

    @SuppressWarnings("deprecation")
    private EditText initEditText(int id) {
        EditText editText = (EditText) findViewById(id);
        editText.getBackground().setColorFilter(getResources().getColor(R.color.blue), PorterDuff.Mode.SRC_ATOP);
        return editText;
    }

    private void initButton(int id) {
        ((Button) findViewById(id)).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btLogin:
            loginButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            /*
             *Login stuff here*
			 */
                verifyLogin();
                if (a == 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        new AsynchronousLogin(uname, password).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    } else {
                        new AsynchronousLogin(uname, password).execute();

                    }
                }
                break;

            case R.id.btSignup:
                vfLoginFlipper.showNext();
                // emptyTheFields();
                break;

            case R.id.btRegister:
            /*
             * Registration stuff here
			 */
                b=0;
                invalid.setText("");
                verifySignUp();
                if (b == 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

                        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
                        int selectedId = radioGroup.getCheckedRadioButtonId();
                        radioButton = (RadioButton) findViewById(selectedId);
                        registeringAs = radioButton.getText().toString();

                        //same for signup as done with login

                        new AsynchronousRegister(etUsernameSignup.getText().toString(), etPasswordSignup.getText().toString(),
                                etEmail.getText().toString(), editText5.getText().toString(), etcity.getText().toString(), etage.getText().toString(),
                                etoccupation.getText().toString(), registeringAs)
                                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    } else {
                        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
                        int selectedId = radioGroup.getCheckedRadioButtonId();
                        radioButton = (RadioButton) findViewById(selectedId);
                        registeringAs = radioButton.getText().toString();

                        new AsynchronousRegister(etUsernameSignup.getText().toString(), etPasswordSignup.getText().toString(),
                                etEmail.getText().toString(), editText5.getText().toString(), etcity.getText().toString(), etage.getText().toString(),
                                etoccupation.getText().toString(), registeringAs)
                                .execute();



                    }
                }
                else{
                    invalid.setText("Please fill all the required fields correctly!");
                }
                break;

            case R.id.btBack:
                vfLoginFlipper.showPrevious();
                //emptyTheFields();
                break;
        }
    }

    private void emptyTheFields() {
        // Login fields
        etUsernameLogin.setText("");
        etPasswordLogin.setText("");

        // Signup fields
        etName.setText("");
        etEmail.setText("");
        etUsernameSignup.setText("");
        etPasswordSignup.setText("");
    }

    private void verifyLogin() {
        uname = etUsernameLogin.getText().toString();
        if (TextUtils.isEmpty(uname)) {
            etUsernameLogin.setError("Enter Username");
            a = 1;
        }
        if (etUsernameLogin.getText().toString().contains(" ")) {
            etUsernameLogin.setError("No Spaces Allowed");
            a = 1;
        }
        password = etPasswordLogin.getText().toString();
        if (TextUtils.isEmpty(password)) {
            etPasswordLogin.setError("Incorrect Password");
            a = 1;
        }
        if (password.length() < 6) {
            etPasswordLogin.setError("Password must be of min 6 characters");
            a = 1;
        }
    }

    private void verifySignUp() {

        if (TextUtils.isEmpty(etUsernameSignup.getText().toString())) {
            etUsernameSignup.setError("Enter Username");
            b = 1;
        }
        if (etUsernameSignup.getText().toString().contains(" ")) {
            etUsernameSignup.setError("No Spaces Allowed");
            b = 1;
        }
        if (TextUtils.isEmpty(etPasswordSignup.getText().toString())) {
            etPasswordSignup.setError("Enter a password");
            b = 1;
        }
        if (etPasswordSignup.getText().toString().length() < 6) {
            etPasswordSignup.setError("Enter a password of min 6 characters");
            b = 1;
        }

        if (!etEmail.getText().toString().matches(emailPattern)) {
            etEmail.setError("Enter a valid email address");
            b = 1;
        }

        if (TextUtils.isEmpty(editText5.getText().toString()) || editText5.getText().toString().length() != 11
                || editText5.getText().toString().charAt(0) != '0' || editText5.getText().toString().charAt(1) != '3') {
            editText5.setError("Enter a valid phone number");
            b = 1;
        }
        else{
            phoneno=editText5.getText().toString();
        }
        if (TextUtils.isEmpty(etcity.getText().toString())) {
            etcity.setError("Enter city of residence");
            b = 1;
        }

        if (TextUtils.isEmpty(etoccupation.getText().toString())) {
            etoccupation.setError("Enter your occupation");
            b = 1;
        }
        if (!TextUtils.isEmpty(etage.getText().toString())) {
            if (Integer.parseInt(etage.getText().toString()) <= 0 || Integer.parseInt(etage.getText().toString()) >= 200) {
                etage.setError("Enter valid age");
                b = 1;
            }
        } else {
            etage.setError("Enter your age");
            b = 1;
        }
    }

    private class AsynchronousRegister extends AsyncTask<Void, Void, String> {
        private String username, password, email, phoneno, city, age, occupation, registeringAs;

        public AsynchronousRegister(String... array) {
            super();
            this.username = array[0];
            this.password = array[1];
            this.email = array[2];
            this.phoneno = array[3];
            this.city = array[4];
            this.age = array[5];
            this.occupation = array[6];
            this.registeringAs = array[7];
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return mongoDB.authenticateRegister(username, password, email, phoneno, city, age, occupation, registeringAs);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (result.equals("c")) {
                Toast.makeText(LoginActivity.this, "Proceeding to verification", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(),sms.class);
                sms instance = new sms();
                instance.onReceive(LoginActivity.this, i);
               Intent intent = new Intent(LoginActivity.this, VerificationActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(LoginActivity.this, "User already exists", Toast.LENGTH_SHORT).show();
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

    private class AsynchronousLogin extends AsyncTask<Void, Void, Object[]> {
        private String username, password;

        AsynchronousLogin(String... array) {
            super();
            this.username = array[0];
            this.password = array[1];
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object[] doInBackground(Void... params) {
            return mongoDB.authenticateLogin(username, password);
        }

        @Override
        protected void onPostExecute(final Object[] result) {
            super.onPostExecute(result);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if ((boolean) result[0]) {
                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                Bundle dataBundle = new Bundle();
                dataBundle.putString("username", etUsernameLogin.getText().toString());
                intent.putExtras(dataBundle);
                startActivity(intent);
            } else {
                Toast.makeText(LoginActivity.this, "Incorrect username/password", Toast.LENGTH_SHORT).show();
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
