package com.example.lenovo.maps;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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

import java.util.Arrays;
import java.util.Comparator;


public class maps extends AppCompatActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {


    // this class is for searching about the nearest provider

    private SharedPreferences userfile;
    private SharedPreferences.Editor userfileEditer;

    private GoogleMap mMap ;
    Button hide;

    StringRequest request;

    ProviderLocation [] ProviderLocations;
    String email , phone , name , x , y , start , end ;
    EditText emailE , phoneE , nameE  , startE , endE ;

    private LocationManager locationManager;
    private LocationListener listener;

    double long_map, late_map;
    ProgressDialog progressDialog;
    LinearLayout PInfo;
    Marker currentMarker;
    Toolbar toolbar;



    private Button logOutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setTitle("البحث عن موزع");
        creatToolbar();
        InitializeView();

    }

    public void InitializeView() {

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        emailE = (EditText) findViewById(R.id.Email);
        nameE = (EditText) findViewById(R.id.etName);
        phoneE = (EditText) findViewById(R.id.etPhone);
        startE = (EditText) findViewById(R.id.startWork);
        endE = (EditText) findViewById(R.id.endWork);
        PInfo= (LinearLayout)findViewById(R.id.pInfo);
        hide = (Button) findViewById(R.id.hide);
        this.logOutBtn = findViewById(R.id.logOutBtn);
        this.userfile = getSharedPreferences(Constants.UserFile, MODE_PRIVATE);
        this.userfileEditer = userfile.edit();


        // log out

        this.logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                android.support.v7.app.AlertDialog.Builder ConfirmationDialog = new android.support.v7.app.AlertDialog.Builder(maps.this);
                ConfirmationDialog.setCancelable(false);
                ConfirmationDialog.setMessage("هل تريد بالفعل تسجيل الخروج ؟");
                ConfirmationDialog.setTitle("تأكيد");
                ConfirmationDialog.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (Network.isConnected(getBaseContext()) == false) {
                            Toast.makeText(getBaseContext(), "لا يوجد اتصال بالانترنت", Toast.LENGTH_LONG).show();
                        } else {
                            userfileEditer.putBoolean(Constants.UserIsLoggedIn, false);
                            userfileEditer.commit();
                            Intent intent = new Intent(getBaseContext(), main_interface.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });
                ConfirmationDialog.setNegativeButton("لا", null);
                ConfirmationDialog.show();


            } // end onClick
        }); // end setOnClickListener




    }

    public void getUserLocation(){

        ////////////////////////    Waiting to get User Current Location     ////////////////////////////

        progressDialog = new ProgressDialog(maps.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("الرجاء الانتظار..."); // Please wait ...
        progressDialog.show();


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        listener = new LocationListener() {

            /////////////////////////// GETTING USER'S NEW LOCATION ////////////////////////////
            @Override
            public void onLocationChanged(Location location) {
                late_map = location.getLatitude();
                long_map = location.getLongitude();
                progressDialog.dismiss();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            /////////////////////////// CHANGE PHONE LOCATION SETTING ////////////////////////////
            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        configure_button();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }

    void configure_button() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        //noinspection MissingPermission
        if (ActivityCompat.checkSelfPermission(maps.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(maps.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates("gps", 5000, 0, listener);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        String ip = getString(R.string.ip);

        String url = "http://"+ip+"/gcms/searchProvider.php";

        request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray_location = jsonObject.getJSONArray("result");
                    ProviderLocations = new ProviderLocation[jsonArray_location.length()]; // array

                    //////////////////////////////////////////////////////////////////////////////////

                    Bundle bundle = getIntent().getExtras();
                    double late = bundle.getDouble("late");
                    double longe = bundle.getDouble("longe");

                    LatLng CurrentLocation = new LatLng(late,longe);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CurrentLocation, 10));

                    currentMarker = mMap.addMarker(new MarkerOptions()
                            .position(CurrentLocation)
                            .title("موقعك الحالي")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                    // marker.
                    currentMarker.showInfoWindow();



                    ////////////////////////////////////////////////////////////////////////////////////

                    //now looping through all the elements of the json array
                    for (int i = 0; i < jsonArray_location.length(); i++) {
                        //getting the json object of the particular index inside the array

                        JSONObject location = jsonArray_location.getJSONObject(i);


                        email = location.getString("p_email");
                        phone = location.getString("p_phone");
                        name = location.getString("p_name");
                        x = location.getString("x_location");
                        y = location.getString("y_location");
                        start = location.getString("start_working");
                        end = location.getString("end_working");

                        LatLng latlng = new LatLng(Double.parseDouble(x), Double.parseDouble(y));

                        ProviderLocations[i]=new ProviderLocation(email,phone,name , latlng ,start,end, 0);


                        double fromLat = ProviderLocations[i].getLatlng().latitude;
                        double fromLon = ProviderLocations[i].getLatlng().longitude;
                        double toLat = CurrentLocation.latitude;
                        double toLon = CurrentLocation.longitude;

                        double distance = distance(fromLat,fromLon,toLat,toLon);
                        ProviderLocations[i].setDistance(distance);

                    }
                    ////////////////// sorting PROVIDERS BASED ON NEAREST ONE //////////////////////

                    Comparator<ProviderLocation> Comparator = new Comparator<ProviderLocation>() {
                        @Override
                        public int compare(ProviderLocation providerLocation, ProviderLocation t1) {
                            return providerLocation.compareTo(t1);
                        }

                    };

                    Arrays.sort(ProviderLocations , Comparator);

                    //     Marker mark;

                    for (int i=0 ; i<ProviderLocations.length;i++){


                        String pDistance =String.valueOf(ProviderLocations[i].getDistance());
                        LatLng latLng = new LatLng(ProviderLocations[i].getLatlng().latitude , ProviderLocations[i].getLatlng().longitude );
                        Marker mark = mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(ProviderLocations[i].getName())
                                .snippet(pDistance));



                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mMap.setOnMarkerClickListener(maps.this);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getBaseContext(), "فشل الاتصال بالخادم ، حاول مجددا", Toast.LENGTH_LONG).show();
            }
        });

        Singleton_Queue.getInstance(maps.this).Add(request);
    }

    public void onBackPressed() {
        // this method to rtturn a prefuse page

        Intent intent = new Intent(getBaseContext(), userPage.class);
        startActivity(intent);

    }

    /////////////////   COMPUTING DISTANCE FROM USER LOCATION TO PROVIDER LOCATION    //////////////////

    static double distance(double fromLat, double fromLon, double toLat, double toLon) {
        double radius = 6378137;   // approximate Earth radius, in meters
        double deltaLat = toLat - fromLat;
        double deltaLon = toLon - fromLon;
        double angle = 2 * Math.asin( Math.sqrt(
                Math.pow(Math.sin(deltaLat/2), 2) +
                        Math.cos(fromLat) * Math.cos(toLat) *
                                Math.pow(Math.sin(deltaLon/2), 2) ) );
        return (radius * angle);
    }


    /////////////////////   DISPLAY PROVIDER'S INFO WHEN CLICK ON MARKER     ///////////////////////
    @Override
    public boolean onMarkerClick(Marker marker) {

        if ( currentMarker.getTitle().equals(marker.getTitle())){
            PInfo.setVisibility(View.INVISIBLE);

        }else{
            ProviderLocation selectedProvider = null;
            for(int i=0;i< ProviderLocations.length;i++){

                if(ProviderLocations[i].getName().equals(marker.getTitle())){
                    selectedProvider = ProviderLocations[i];
                }
            }
            PInfo.setVisibility(View.VISIBLE);
            nameE.setText(selectedProvider.getName());
            emailE.setText(selectedProvider.getEmail());
            phoneE.setText(selectedProvider.getPhone());
            startE.setText(selectedProvider.getStartWorking());
            endE.setText(selectedProvider.getEndWorking());
        }

        return false;

    }
    /////////////////////   HIDE PROVIDER'S INFO WHEN CLICK ON BUTTON     ///////////////////////
    public void hide(View view){
        PInfo.setVisibility(View.GONE);

    }

    ////////////////////    CREATE & HANDLING TOOLBAR & iT'S BUTTONS     ///////////////////////////

    protected void creatToolbar() {

        // Attaching the layout to the toolbar object
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        this.setSupportActionBar( toolbar);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.setting) {
            Intent intent = new Intent(getBaseContext(), EditUser.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else if (id == R.id.home) {
            Intent intent = new Intent(getBaseContext(), userPage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}