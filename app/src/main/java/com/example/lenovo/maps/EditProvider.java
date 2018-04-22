package com.example.lenovo.maps;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditProvider extends AppCompatActivity {

    private SharedPreferences userfile;
    private SharedPreferences.Editor userfileEditer;

    StringRequest request;
    AlertDialog alertDialog;

    EditText emailE, nameE, phoneE, startWorkE, endWorkE;//provider EditText
    String email, name, phone, startWork, endWork, msg = "", Errmsg = "";;//provider strings

    static String long_map_s, late_map_s; //location parameters
    double long_map, late_map;
    private ProgressDialog progressDialog;

    private LocationManager locationManager;
    private LocationListener listener;

    android.support.v7.widget.Toolbar toolbar;

    private Button logOutBtn;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_edit_provider);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setTitle("بياناتي");
        creatToolbar();


        InitializeView();

        //////////////////////////////// Share Provider Location //////////////////////////////////
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        RadioGroup rg = (RadioGroup) findViewById(R.id.radio);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radioNotShareLoc:
                        late_map_s = userfile.getString(Constants.locationLatitude, "");
                        long_map_s = userfile.getString(Constants.locationLongitude, "");
                        break;

                    case R.id.radioShareLoc:
                        progressDialog=new ProgressDialog(EditProvider.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage("الرجاء الانتظار..."); // Please wait ...
                        progressDialog.show();

                        //noinspection MissingPermission
                        if (ActivityCompat.checkSelfPermission(EditProvider.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EditProvider.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

                        break;

                }
            }
        });

        listener = new LocationListener() {
            /////////////////////////// GETTING PROVIDER'S NEW LOCATION ////////////////////////////

            @Override
            public void onLocationChanged(Location location) {

                late_map = location.getLatitude();
                long_map = location.getLongitude();

                late_map_s = String.valueOf(late_map).toString();
                long_map_s = String.valueOf(long_map).toString();

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

    private void InitializeView() {
        /////////////////////////// FILLING EDITTEXT WITH PROVIDER DATA ////////////////////////////

        this.userfile = getSharedPreferences(Constants.UserFile, MODE_PRIVATE);
        this.userfileEditer = userfile.edit();

        emailE = (EditText) findViewById(R.id.Email);
        nameE = (EditText) findViewById(R.id.etName);
        phoneE = (EditText) findViewById(R.id.etPhone);
        startWorkE = (EditText) findViewById(R.id.startWork);
        endWorkE = (EditText) findViewById(R.id.endWork);

        this.logOutBtn = findViewById(R.id.logOutBtn);

        this.progressDialog = new ProgressDialog(EditProvider.this);
        this.progressDialog.setCancelable(false);

        emailE.setText(userfile.getString(Constants.PEmail, ""));
        nameE.setText(userfile.getString(Constants.PName, ""));
        phoneE.setText(userfile.getString(Constants.PPhone, ""));
        startWorkE.setText(userfile.getString(Constants.PStart, ""));
        endWorkE.setText(userfile.getString(Constants.PEnd, ""));
        late_map_s = userfile.getString(Constants.locationLatitude, "");
        long_map_s = userfile.getString(Constants.locationLongitude, "");

        alertDialog = new AlertDialog.Builder(EditProvider.this).create();

        alertDialog.setTitle("تنبيه");



        // log out

        this.logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                android.support.v7.app.AlertDialog.Builder ConfirmationDialog = new android.support.v7.app.AlertDialog.Builder(EditProvider.this);
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
    /////////////////////////// METHOD TO SAVE UPDATED DATA WHEN CLICK SAVE BUTTON ////////////////////////////

    public void save(View view) {
        msg="";
        email=emailE.getText().toString();
        name=nameE.getText().toString();
        phone=phoneE.getText().toString();
        startWork=startWorkE.getText().toString();
        endWork=endWorkE.getText().toString();

        ///////////////////////// check if user leave editText empty ////////////////////////////////

        if(email.equals("")){
            emailE.setText(userfile.getString(Constants.PEmail,""));
            email=userfile.getString(Constants.PEmail,"");
        }
        if(name.equals("")){
            nameE.setText(userfile.getString(Constants.PName,""));
        }
        if(phone.equals("")){
            phoneE.setText(userfile.getString(Constants.PPhone,""));
            phone=userfile.getString(Constants.PPhone,"");
        }
        if(startWork.equals("")){
            startWorkE.setText(userfile.getString(Constants.PStart,""));
            startWork=userfile.getString(Constants.PStart,"");
        }
        if(endWork.equals("")){
            endWorkE.setText(userfile.getString(Constants.PEnd,""));
            endWork=userfile.getString(Constants.PEnd,"");
        }

        /////////////////////////////////  validate phoneNumber  ///////////////////////////////////
        if(phone.length()>10){
            msg +="* رقم الهاتف لايجب أن يكون أكثر من 10 ارقام";
        }
        if(phone.length()<10){
            msg +="* رقم الهاتف لايجب أن يكون أقل من 10 ارقام";
        }
        ///////////////////////////////////  validate Email    /////////////////////////////////////
        if(!isEmailValid(email)){
            msg +="* صيغة البريد الالكتروني غير صحيحة ";
        }
        if(!msg.equals("")) {
        showmsg(msg);
         return;
        }


        String ip = getString(R.string.ip);

        /////////////////////////////////  PHP Request to edit Provider  ///////////////////////////////////

        request = new StringRequest(Request.Method.POST, "http://"+ip+"/gcms/editProvider.php", new Response.Listener <String>() {

            @Override
            public void onResponse(String response) {

                Errmsg="";
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("state");
                    String emailNotExist = jsonObject.getString("emailNotExist");
                    String phoneExist = jsonObject.getString("phoneNotExist");


                    if (emailNotExist.equals("0")){
                        Errmsg="* البريد الالكتروني مسجل مسبقا ادخل بريد الكتروني جديد";
                    }  if (phoneExist.equals("0")){
                        Errmsg+="* رقم الجوال مسجل مسبقا ادخل رقم جديد";
                    }

                    /////////////////////////////////    UPDATE PROVIDER SSESION     ///////////////////////////////////

                    if (status.equals("1")&&emailNotExist.equals("1")&&phoneExist.equals("1")) {

                        userfileEditer.putString(Constants.PEmail,email);
                        userfileEditer.putString(Constants.PName,name);
                        userfileEditer.putString(Constants.PPhone,phone);
                        userfileEditer.putString(Constants.PStart,startWork);
                        userfileEditer.putString(Constants.PEnd,endWork);
                        userfileEditer.putString(Constants.locationLongitude,long_map_s);
                        userfileEditer.putString(Constants.locationLatitude,late_map_s);
                        userfileEditer.putBoolean(Constants.UserIsLoggedIn,true);
                        userfileEditer.putString(Constants.UserType , "provider");
                        userfileEditer.commit();
                          showmsg("تم حفظ بياناتك الجديدة بنجاح.");
                    } else{
                         showmsg(Errmsg);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showmsg("فشل الاتصال بالخادم ، حاول مجددا");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                /*** Here you put the HTTP request parameters **/

                String emailText=userfile.getString(Constants.PEmail,"");
                String phoneText=userfile.getString(Constants.PPhone,"");


                /////////////////////////////////    SEND DATA TO PHP     ///////////////////////////////////

                HashMap<String, String> map = new HashMap <>();
                map.put("prevEmail",emailText);
                map.put("prevPhone",phoneText);
                map.put("p_email" ,email);
                map.put("p_name",name);
                map.put("p_phone",phone);
                map.put("p_startWork",startWork);
                map.put("p_endWork",endWork);
                map.put("x_location", late_map_s);
                map.put("y_location", long_map_s);
                return map;
            }
        };
        Singleton_Queue.getInstance(getBaseContext()).Add(request);

    }


    /////////////////////////////////    VALIDATE EMAIL     ///////////////////////////////////
    boolean isEmailValid(CharSequence email ) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    ////////////////////    DIRECT TO CHANGE PASSWORD INTERFACE     ///////////////////////////

    public void changePassword(View view) {
        Intent i= new Intent(getApplicationContext(),changePass.class);
        startActivity(i);
    }

    void showmsg(String msg) {
        alertDialog.setMessage(msg);

        alertDialog.setButton("موافق", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {


            }
        });


        alertDialog.show();    }


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
        if (id == R.id.home) {
            HandleAction();
        }

        return super.onOptionsItemSelected(item);
    }

    public void HandleAction() {

        if (userfile.getBoolean(Constants.UserIsLoggedIn, false) == true) {


            Intent intent = new Intent(getBaseContext(), providerPage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } // end if user file

    }
}
