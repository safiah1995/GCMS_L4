package com.example.lenovo.maps;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;


import android.content.Intent;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.app.ProgressDialog;

public class Add_ProviderAccount extends AppCompatActivity {

    AlertDialog alertDialog;
    private ProgressDialog progressDialog;

    EditText emailE, nameE, passwordE, phoneE, start_workingE, end_workingE;
    RadioGroup rg;
    RadioButton radioYes;

    String email, name, password, phone, start_working, end_working;

    static String long_map_s, late_map_s; // for provider location
    double long_map, late_map;

    StringRequest request;

    private LocationManager locationManager;
    private LocationListener listener;


    android.support.v7.widget.Toolbar toolbar;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_provideraccount);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        toolbar.setTitle("موزع جديد");
        setSupportActionBar(toolbar);

        emailE = (EditText) findViewById(R.id.etEmail);
        nameE = (EditText) findViewById(R.id.etName);
        phoneE = (EditText) findViewById(R.id.etPhone);
        passwordE = (EditText) findViewById(R.id.etPass);

        start_workingE = (EditText) findViewById(R.id.etFrom);
        end_workingE = (EditText) findViewById(R.id.etTo);

        rg = (RadioGroup) findViewById(R.id.radio);
        radioYes = (RadioButton)findViewById(R.id.radioShareLoc);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // radio button
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radioShareLoc:
                        progressDialog=new ProgressDialog(Add_ProviderAccount.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage("الرجاء الانتظار..."); // Please wait ...
                        progressDialog.show();

                        //noinspection MissingPermission
                        if (ActivityCompat.checkSelfPermission(Add_ProviderAccount.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Add_ProviderAccount.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

                   default:
                        showmsg("يجب مشاركة الموقع حتى يستطيع العميل الوصول إليك");
                        return;

                }
            }
        });

        // Coordination of provider location
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                late_map = location.getLatitude(); // x
                long_map = location.getLongitude(); // y

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

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);

            }
        };

        configure_button();

    }

    public void register(View view) {


        email = emailE.getText().toString();
        name = nameE.getText().toString();
        phone = phoneE.getText().toString();
        password = passwordE.getText().toString();

        start_working = start_workingE.getText().toString();
        end_working = end_workingE.getText().toString();

        if (email.isEmpty() || name.isEmpty() || phone.isEmpty() || password.isEmpty() || start_working.isEmpty() || end_working.isEmpty()) {
            showmsg("الرجاء إدخال جميع الحقول");
            return;
        }
        if (phone.length() > 10) {
            showmsg("* رقم الهاتف لايجب أن يكون أكثر من 10 ارقام");
            return;
        }
        if (phone.length() < 10) {
            showmsg("* رقم الهاتف لايجب أن يكون أقل من 10 ارقام");
            return;
        }
        if (!isEmailValid(email)) {
            showmsg(" صيغة البريد الالكتروني غير صحيحة");
            return;
        }
        if (!isEmailValid(email)) {
            showmsg(" صيغة البريد الالكتروني غير صحيحة");
            return;
        }
        if (!radioYes.isChecked()) {
            showmsg("يجب مشاركة الموقع حتى يستطيع العميل الوصول إليك");
            return;
        }


        String ip = getString(R.string.ip);

        String url = "http://"+ip+"/gcms/AddProvider.php";

        request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String emailErrmsg = "";
                String phoneErrmsg = "";


                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("state");
                    String emailNotExist = jsonObject.getString("emailNotExist");
                    String phoneExist = jsonObject.getString("phoneNotExist");

                    if (emailNotExist.equals("0")) {
                        emailErrmsg = "* البريد الالكتروني مسجل مسبقا ادخل بريد الكتروني جديد";
                        showmsg(emailErrmsg);


                    }
                    if (phoneExist.equals("0")) {
                        phoneErrmsg = "* رقم الجوال مسجل مسبقا ادخل رقم جديد";
                        showmsg(phoneErrmsg);

                    }

                    if (status.equals("1") && !emailNotExist.equals("0") && !phoneExist.equals("0")) {
                        showmsg("تم حفظ بياناتك بنجاح");
                        Intent i = new Intent(getBaseContext(), loginPage.class);
                        startActivity(i);

                    } else {

                        showmsg("حدث خطأ ما ، حاول مجددا");
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
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                /* Here you put the HTTP request parameters **/

                HashMap<String, String> map = new HashMap<>();

                map.put("p_email", email);
                map.put("p_name", name);
                map.put("p_phone", phone);
                map.put("p_password", password);
                map.put("start_working", start_working);
                map.put("end_working", end_working);
                map.put("x_location", late_map_s);
                map.put("y_location", long_map_s);


                return map;
            }
        };

        Singleton_Queue.getInstance(Add_ProviderAccount.this).Add(request);


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

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    // Alert dialog
    void showmsg(String msg) {

        alertDialog = new AlertDialog.Builder(
                Add_ProviderAccount.this).create();

        alertDialog.setTitle("تنبيه");
        alertDialog.setMessage(msg);

        alertDialog.setButton("موافق", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {


            }
        });

        alertDialog.show();

    }


}




