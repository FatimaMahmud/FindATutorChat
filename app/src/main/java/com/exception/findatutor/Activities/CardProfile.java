package com.exception.findatutor.Activities;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.exception.findatutor.R;
import com.exception.findatutor.conn.MongoDB;

import java.util.ArrayList;


public class CardProfile extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private Bundle dataBundle;
    private String coverTitle;
    private TextView titleTextView;
    private ImageView coverImageView;
    private String coverImage;
    private CheckBox checkboxcontact;
    private TextView PhonenoText, Phoneno;
    private TextView Email;
    private TextView EmailText;
    private MongoDB mongoDB;
    private boolean NotificationBoolean = false;
    private TextView titleFeeView, tvAgeView, tvCoursesView, tvEducationtextView, tvExperiencetextView, tvCityView, tvOccupationView;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mongoDB = MongoDB.getInstance();
        dataBundle = getIntent().getExtras();
        new RetriveTHATUser().execute();

        coverTitle = dataBundle.getString("title");
        Bitmap bmp = (Bitmap) dataBundle.getParcelable("image");

        linearLayout = (LinearLayout) findViewById(R.id.parent);
        Toast.makeText(getApplicationContext(), coverTitle, Toast.LENGTH_SHORT).show();

        titleTextView = (TextView) findViewById(R.id.tv_cardprofile_name);
        titleFeeView = (TextView) findViewById(R.id.tv_profile_feeText);
        tvAgeView = (TextView) findViewById(R.id.tv_profile_age_card);
        tvCoursesView = (TextView) findViewById(R.id.tv_profilesubject_card);
        tvEducationtextView = (TextView) findViewById(R.id.tv_profile_educationtext);
        tvExperiencetextView = (TextView) findViewById(R.id.tv_profile_expereinceText);
        tvCityView = (TextView) findViewById(R.id.tv_profile_city);
        tvOccupationView = (TextView) findViewById(R.id.tv_profile_occupationText);

        coverImageView = (ImageView) findViewById(R.id.coverImageView);
        checkboxcontact = (CheckBox) findViewById(R.id.checkbox_contact);

//        Email = (TextView) findViewById(R.id.cardprofile_email);
//        EmailText = (TextView) findViewById(R.id.cardprofile_emailtext);

        checkboxcontact.setOnCheckedChangeListener(this);
        titleTextView.setText(coverTitle);
        if(bmp!=null){
        coverImageView.setImageBitmap(bmp);}
        else{
            coverImageView.setImageResource(R.drawable.seven);
        }

//        Email.setVisibility(View.INVISIBLE);
//        EmailText.setVisibility(View.INVISIBLE);


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

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (checkboxcontact.isChecked()) {
            Snackbar snackbar = Snackbar
                    .make(linearLayout, "Notification Sent!", Snackbar.LENGTH_LONG);

            snackbar.show();
            new UpdateNotification().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } else {
//            NotificationBoolean = false;
//            Phoneno.setVisibility(View.INVISIBLE);
//            PhonenoText.setVisibility(View.INVISIBLE);
//            Email.setVisibility(View.INVISIBLE);
//            EmailText.setVisibility(View.INVISIBLE);
        }
    }

    private class UpdateNotification extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void result) {

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                NotificationBoolean = true;
                mongoDB.UpdateTheTutorNOtification(coverTitle, "true", LoginActivity.uname);
                System.out.println("THERE1");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class RetriveTHATUser extends AsyncTask<Object, Object, ArrayList<TutorInfoFull>> {
        @Override
        protected void onPostExecute(ArrayList<TutorInfoFull> result) {
            if(result != null && result.size() != 0) {
                titleFeeView.setText(result.get(0).getFee());
                tvEducationtextView.setText(result.get(0).getEdu());
                tvExperiencetextView.setText(result.get(0).getExp());
                tvCoursesView.setText(result.get(0).getCourses());
                new RetrieveOtherInfoFromUserTable().executeOnExecutor(THREAD_POOL_EXECUTOR);
            }
            else
                System.out.println("YOUR RESULT IS EMPTY DUDE");
        }

        @Override
        protected ArrayList<TutorInfoFull> doInBackground(Object... params) {
            try {
                return mongoDB.retrieveOneTutorData(coverTitle);
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
            tvCityView.setText(result.get(0).getCity());
            tvOccupationView.setText(result.get(0).getOccupation());
            tvAgeView.setText(result.get(0).getAge());
        }

        @Override
        protected ArrayList<UserTableInfoFull> doInBackground(Object... params) {
            try {
                return mongoDB.RetrieveOtherInfoFromUserTable(coverTitle);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
