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
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class userPage extends AppCompatActivity implements Designable {


    private ProgressDialog progressDialog;

    private TextView welcomeTextView;
    private Button clinderBtn;

    private Button logOutBtn;
    private Button searchProBtn;

    private SharedPreferences userfile;
    private SharedPreferences.Editor userfileEditer;
    private LocationManager locationManager;
    private LocationListener listener;

    double long_map, late_map;

    android.support.v7.widget.Toolbar toolbar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        startService(new Intent(getBaseContext(),NotificationService.class));
        this.setTitle("الصفحة الرئيسية");
        creatToolbar();

        InitializeView();

        //////// Share Location Button


        this.searchProBtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                progressDialog=new ProgressDialog(userPage.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("الرجاء الانتظار..."); // Please wait ...
                progressDialog.show();

                listener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                        late_map = location.getLatitude();
                        long_map = location.getLongitude();

                        progressDialog.dismiss();

                        Intent intent = new Intent(userPage.this, maps.class);
                        intent.putExtra("late", late_map);
                        intent.putExtra("longe",long_map);
                        startActivity(intent);
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(i);

                    }
                };


                configure_button();

                //noinspection MissingPermission
                if (ActivityCompat.checkSelfPermission(userPage.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(userPage.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, listener);


            }

        });


    } // end onCreate


    public void InitializeView() {

        this.welcomeTextView = findViewById(R.id.welcomeTextV);
        this.clinderBtn = findViewById(R.id.cylinderBtn);
        this.logOutBtn = findViewById(R.id.logOutBtn);
        this.searchProBtn=findViewById(R.id.searchProBtn);

        this.userfile = getSharedPreferences(Constants.UserFile, MODE_PRIVATE);
        this.userfileEditer = userfile.edit();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Desing();


    } // end InitializeView

    public void Desing() {

        String nameOfUuser = "مـرحـبـا " + userfile.getString(Constants.UName, "");
        welcomeTextView.setText(nameOfUuser);

        HandleAction();
    } // end desing


    public void HandleAction() {


        // log out

        this.logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder ConfirmationDialog = new AlertDialog.Builder(userPage.this);
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

    } // end HandleAction

    public void EditPage(View view) {
        Intent i = new Intent(getApplicationContext(), EditUser.class);
        startActivity(i);
    }

    public void cylinderList(View view) {
        Intent i = new Intent(getApplicationContext(), view_cylinder_list.class);
        startActivity(i);
    }

    public void ins(View view) {
        Intent i = new Intent(getApplicationContext(), instruction.class);
        startActivity(i);
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


    }

    ////////////////////    CREATE & HANDLING TOOLBAR & iT'S BUTTONS     ///////////////////////////

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void creatToolbar() {

        // Attaching the layout to the toolbar object
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(toolbar);
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
        }
        return true;
    }

}