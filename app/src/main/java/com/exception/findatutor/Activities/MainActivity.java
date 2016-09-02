package com.exception.findatutor.Activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.exception.findatutor.R;
import com.exception.findatutor.conn.MongoDB;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowLongClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, LocationListener {
    public Marker[] markers;
    public ArrayList<String> arr=null;
    public ArrayList<String> arr2=null;
    private RelativeLayout rightRL;
    private DrawerLayout drawer;
    private FloatingActionButton fab;
    private boolean conStatus = false;
    private GoogleMap GoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public static String lat = "";      //33.669483
    public static String lng = "";      //73.074383
    private Handler handler = new Handler();
    private Bundle dataBundle;
    private String username;
    private MongoDB mongoDB;
    private List<TutorInfoNotification> alluserInfos;
    private ArrayList<String> allUserUsernames;
    private boolean first_time = true;
    private boolean done = true;
    private boolean busy = false;
    public static int count = 1;
    private Marker myMarker;
    private Intent intent1 = null;
    public static double curlat=0;
    public static double curlng=0;

    private SeekBar seekbar;
    private Switch male;
    private Switch female;
    private int maleFlag=0;
    private int femaleFlag=0;
    private int progress=0;
    private Spinner spinner;
    private String subject="All";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        InitializeLocationRequest();
        InitializeGoogleAPIClient();


        new ConnectToServer().execute();

        InitialiseViews();

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress=i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                progress=progress*1000;
                if(progress!=0) {
                    for (int i = 0; i < markers.length; i++) {
                        Location to = new Location("");
                        to.setLatitude(markers[i].getPosition().latitude);
                        to.setLongitude(markers[i].getPosition().longitude);

                        Location curlocation = new Location("");
                        curlocation.setLatitude(curlat);
                        curlocation.setLongitude(curlng);
                        if (curlocation !=null && curlocation.distanceTo(to) > progress) {
                            markers[i].setVisible(false);
                        }
                    }
                }
                else{
                    for(int i = 0; i < markers.length; i++){
                        String snippet=markers[i].getSnippet();
                        if((maleFlag==1 && snippet.equals("Female")) || (femaleFlag==1 && snippet.equals("Male"))){
                            markers[i].setVisible(false);
                        }
                       else if((subject.equals("All")|| markers[i].getTag().toString().contains(subject))){
                            markers[i].setVisible(true);
                    }
                    }
                }
            }
        });

        male.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    maleFlag=1;
                        for (int i = 0; i < markers.length; i++) {

                            Location to = new Location("");
                            to.setLatitude(markers[i].getPosition().latitude);
                            to.setLongitude(markers[i].getPosition().longitude);

                            Location curlocation = new Location("");
                            curlocation.setLatitude(curlat);
                            curlocation.setLongitude(curlng);

                            String snippet = markers[i].getSnippet();

                            if (snippet.equalsIgnoreCase("Female") && femaleFlag!=1) {
                                markers[i].setVisible(false);
                            }

                            if(progress==0 && snippet.equalsIgnoreCase("Male")
                                    && (subject.equals("All")|| markers[i].getTag().toString().contains(subject))){
                                markers[i].setVisible(true);
                            }
                            else if (snippet.equalsIgnoreCase("Male") && curlocation !=null && curlocation.distanceTo(to) < progress
                                    && (subject.equals("All")|| markers[i].getTag().toString().contains(subject))){
                                markers[i].setVisible(true);
                            }

                    }
                } else {
                    maleFlag = 0;
                    for (int i = 0; i < markers.length; i++) {
                        Location to = new Location("");
                        to.setLatitude(markers[i].getPosition().latitude);
                        to.setLongitude(markers[i].getPosition().longitude);

                        Location curlocation = new Location("");
                        curlocation.setLatitude(curlat);
                        curlocation.setLongitude(curlng);

                        String snippet= markers[i].getSnippet();

                        if(femaleFlag==1  && snippet.equalsIgnoreCase("Male")){
                            markers[i].setVisible(false);
                        }

                        if(progress==0 && snippet.equalsIgnoreCase("Female")
                                && (subject.equals("All")|| markers[i].getTag().toString().contains(subject))){
                            markers[i].setVisible(true);
                        }
                        else if(snippet.equalsIgnoreCase("Female") && curlocation !=null && curlocation.distanceTo(to) < progress
                                && (subject.equals("All")|| markers[i].getTag().toString().contains(subject))){
                            markers[i].setVisible(true);
                        }


                    }
                }
            }
        });

       female.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    femaleFlag=1;

                        for (int i = 0; i < markers.length; i++) {
                            Location to = new Location("");
                            to.setLatitude(markers[i].getPosition().latitude);
                            to.setLongitude(markers[i].getPosition().longitude);

                            Location curlocation = new Location("");
                            curlocation.setLatitude(curlat);
                            curlocation.setLongitude(curlng);

                            String snippet = markers[i].getSnippet();
                            if (snippet.equalsIgnoreCase("Male") && maleFlag!=1) {
                                markers[i].setVisible(false);
                            }
                            if(progress==0 && snippet.equalsIgnoreCase("Female")
                                    && (subject.equals("All")|| markers[i].getTag().toString().contains(subject))){
                                markers[i].setVisible(true);
                            }
                            else if (snippet.equalsIgnoreCase("Female")  && curlocation !=null && curlocation.distanceTo(to) < progress
                                    && (subject.equals("All")|| markers[i].getTag().toString().contains(subject))){
                                markers[i].setVisible(true);
                            }

                        }

                } else {
                    femaleFlag = 0;
                    for (int i = 0; i < markers.length; i++) {
                        Location to = new Location("");
                        to.setLatitude(markers[i].getPosition().latitude);
                        to.setLongitude(markers[i].getPosition().longitude);

                        Location curlocation = new Location("");
                        curlocation.setLatitude(curlat);
                        curlocation.setLongitude(curlng);

                        String snippet= markers[i].getSnippet();
                        if(maleFlag==1  && snippet.equalsIgnoreCase("Female")){
                            markers[i].setVisible(false);
                        }
                        if(progress==0 && snippet.equalsIgnoreCase("Male")
                                && (subject.equals("All")|| markers[i].getTag().toString().contains(subject))){
                            markers[i].setVisible(true);
                        }
                        else if(snippet.equalsIgnoreCase("Male") && curlocation !=null && curlocation.distanceTo(to) < progress
                                && (subject.equals("All")|| markers[i].getTag().toString().contains(subject))){
                            markers[i].setVisible(true);
                        }
                    }
                }
            }
       });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                subject=adapterView.getItemAtPosition(i).toString();
                for(int j=0; j<markers.length;j++){
                    Location to = new Location("");
                    to.setLatitude(markers[j].getPosition().latitude);
                    to.setLongitude(markers[j].getPosition().longitude);

                    Location curlocation = new Location("");
                    curlocation.setLatitude(curlat);
                    curlocation.setLongitude(curlng);

                    String tag=markers[j].getTag().toString();
                    String snippet=markers[j].getSnippet();
                    if(tag.contains(subject) && progress==0
                            && (!(maleFlag==1 && snippet.equals("Female"))) || (!(femaleFlag==1 && snippet.equals("Male")))){
                        markers[j].setVisible(true);
                    }
                    else if(tag.contains(subject) && curlocation !=null && curlocation.distanceTo(to) < progress
                            && (!(maleFlag==1 && snippet.equals("Female"))) || (!(femaleFlag==1 && snippet.equals("Male")))){
                        markers[j].setVisible(true);
                    }
                    if((markers[j].isVisible()) && (!tag.contains(subject)) && (!subject.equals("All"))) {
                        markers[j].setVisible(false);
                    }
                    else if(subject.equals("All") && progress==0 && (!(maleFlag==1 && snippet.equals("Female")))
                            || (!(femaleFlag==1 && snippet.equals("Male")))){
                        markers[j].setVisible(true);
                    }
                    else if(subject.equals("All") && curlocation !=null && curlocation.distanceTo(to) < progress
                            && (!(maleFlag==1 && snippet.equals("Female"))) || (!(femaleFlag==1 && snippet.equals("Male")))){
                        markers[j].setVisible(true);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dataBundle = getIntent().getExtras();
        username = dataBundle.getString("username");
        Toast.makeText(MainActivity.this, "Username is: " + username, Toast.LENGTH_SHORT).show();
        if (count == 1)
            ThreadNotification.getInstance(getApplicationContext(), username).start();
        new CheckNotification().execute();

    }

    public void InitialiseViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rightRL = (RelativeLayout) findViewById(R.id.whatYouWantInRightDrawer);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRight(fab);
            }
        });

        seekbar=(SeekBar)findViewById(R.id.seekBar);
        male=(Switch)findViewById(R.id.switch1);
        female=(Switch)findViewById(R.id.switch2);
        spinner=(Spinner)findViewById(R.id.spin);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.subjects, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0,false);

    }

    public void onRight(View v) {
        drawer.openDrawer(rightRL);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {

            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.about_us) {
            startActivity(new Intent(MainActivity.this, AboutUs.class));
        }
        if (id == R.id.exit) {
            System.exit(0);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            new ProfileChoosing().execute();


        } else if (id == R.id.nav_map) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
            //startActivity(new Intent(MainActivity.this, MapActivity.class));
        } else if (id == R.id.nav_setting) {
            onRight(fab);
        } else if (id == R.id.nav_listTutors) {
            Intent intent = new Intent(MainActivity.this, Tutors.class);
            Bundle dataBundle = new Bundle();
            dataBundle.putString("username", username);
            intent.putExtras(dataBundle);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(MainActivity.this, AddaTutor.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {
        String title = marker.getTitle();
        Intent intent = new Intent(MainActivity.this, CardProfile.class);
        Bundle dataBundle = new Bundle();
        dataBundle.putString("title", title);
        intent.putExtras(dataBundle);
        startActivity(intent);
    }





    private class ProfileChoosing extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            Bundle dataBundle = new Bundle();
            dataBundle.putString("username", username);
            dataBundle.putString("state", "normal");
            if (intent1 != null) {
                intent1.putExtras(dataBundle);
                startActivity(intent1);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (mongoDB.CheckRegisteringAsForNotification(username).equals("tutor")) {
                intent1 = new Intent(MainActivity.this, ProfileActivity.class);
            }
            else if (mongoDB.CheckRegisteringAsForNotification(username).equals("student")) {
                intent1 = new Intent(MainActivity.this, StudentProfile.class);
            }
            return null;
        }
    }

    protected void InitializeGoogleAPIClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();

        }
    }

    protected void InitializeLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }


    protected void startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap = googleMap;
        GoogleMap.getUiSettings().setMapToolbarEnabled(false);
        GoogleMap.setOnInfoWindowLongClickListener(this);

        myMarker= GoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("You are here!"));
        myMarker.showInfoWindow();

        mongoDB = MongoDB.getInstance();
        new getTutors().execute();


        }



    @Override
    public void onLocationChanged(Location location) {
        lat = Double.toString(location.getLatitude());
        lng = Double.toString(location.getLongitude());
        //Toast.makeText(MainActivity.this, "lat is: " + lat + " lng is : " + lng, Toast.LENGTH_SHORT).show();
        //System.out.println("lat is: " + lat + " lng is : " + lng );

        curlat = location.getLatitude();
        curlng = location.getLongitude();
        myMarker.setPosition(new LatLng(curlat,curlng));
        LatLng latLng = new LatLng(curlat, curlng);
        GoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        GoogleMap.animateCamera(CameraUpdateFactory.zoomTo(8));
        if (!busy) {
            busy = true;
            new UpdateLocationOnLocationChanged().execute();
        }

    }

    private class CheckNotification extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void result) {

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                if (mongoDB.CheckRegisteringAsForNotification(username).equals("tutor")) {
                    if (mongoDB.CheckTutorNotification(username, "true").equals("sent")) {
                        NotificationGenerator("Student Wants to connect", "Click to view profile", StudentProfile.class,
                                mongoDB.TutorNotificationSenderName(LoginActivity.uname));
//                        mongoDB.UpdateTheStudentNOtification(mongoDB.TutorNotificationSenderName(LoginActivity.uname),
//                                "accepted", LoginActivity.uname);
//                        mongoDB.UpdateTheTutorNOtification(username, "accepted", LoginActivity.uname);

                    }
                }
                if (mongoDB.CheckRegisteringAsForNotification(username).equals("student")) {
                    if (mongoDB.CheckStudentNotification(username, "accepted").equals("accepted")) {
                        NotificationGenerator("Tutor has accepted your request", "Click to view full profile", ProfileActivity.class,
                                mongoDB.StudentNotificationSenderName(LoginActivity.uname));
                        mongoDB.UpdateTheTutorNOtification(mongoDB.StudentNotificationSenderName(LoginActivity.uname), "false", null);
                        mongoDB.UpdateTheStudentNOtification(username, "false", null);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void NotificationGenerator(String title, String message, final Class<? extends Activity> ActivityToOpen, String username) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(MainActivity.this)
                        .setSmallIcon(R.drawable.logo)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentTitle(title)
                        .setContentText(message);
        Intent resultIntent = new Intent(MainActivity.this, ActivityToOpen);
        resultIntent.putExtra("username", username);
        resultIntent.putExtra("state", "card");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(MainActivity.this);
        stackBuilder.addParentStack(ActivityToOpen);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());

    }


    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected())
            stopLocationUpdates();
    }

    private void retrieve() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (done) {
                    new ShowUsersNearBy().execute();
                    retrieve();
                } else {
                    retrieve();
                }
            }
        }, 10000);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        GoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15));
        // Zoom in, animating the camera.
        GoogleMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
       GoogleMap.animateCamera(CameraUpdateFactory.zoomTo(50), 2000, null);


        return false;
    }

    private class ConnectToServer extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void result) {
            if (conStatus) {
                mGoogleApiClient.connect();
                retrieve();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            mongoDB = MongoDB.getInstance();
            conStatus = true;
            return null;
        }
    }


    private class UpdateLocationOnLocationChanged extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void result) {
            busy = false;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                mongoDB.updateLocation(username, lat, lng);
                //retrieve();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class ShowUsersNearBy extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            done = false;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                alluserInfos = new ArrayList<>();
                allUserUsernames = new ArrayList<>();
                //System.out.println("lat is: " + lat + " lng is : " + lng );
//                mongoDB.AddLocationInUsers(username,lat,lng);
//                mongoDB.showUsers(allUserUsernames, alluserInfos);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            done = true;
        }
    }

    private class getTutors extends AsyncTask<Object, Object, ArrayList<String>> {
        @Override
        protected void onPostExecute(ArrayList<String> result) {
           if(result!=null){
               arr=result;
               arr2=result;
              int a = result.size()/5;
               int b=0;
               markers = new Marker[a];
               for(int i=0; i<result.size();i=i+5){

                   String title=result.get(i);
                   double latitude=Double.valueOf(result.get(i+1));
                   double longitude=Double.valueOf(result.get(i+2));
                   String snippet=result.get(i+3);
                   markers[b] = GoogleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(title)
                                .snippet(snippet).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker3)));
                   markers[b].setTag(result.get(i+4).toString());
                   System.out.println(markers[b].getTag().toString());
                   b++;
               }

           }
        }

        @Override
        protected ArrayList<String> doInBackground(Object... params) {
            try {
                return mongoDB.getTutors();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
