package com.example.lenovo.maps;
import android.Manifest;
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

public class EditUser extends AppCompatActivity {

    private SharedPreferences userfile;
    private SharedPreferences.Editor userfileEditer;

    EditText emailE, nameE, phoneE;
    String email, name, phone, startWork, endWork, msg = "", Errmsg = "";
    StringRequest request;

    android.support.v7.widget.Toolbar toolbar;

    private Button logOutBtn;
    AlertDialog alertDialog;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_edit_user);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setTitle("بياناتي");
        creatToolbar();


        InitializeView();

    }

    private void InitializeView() {

        /////////////////////////// FILLING EDITTEXT WITH USER DATA ////////////////////////////

        this.userfile = getSharedPreferences(Constants.UserFile, MODE_PRIVATE);
        this.userfileEditer = userfile.edit();

        emailE = (EditText) findViewById(R.id.Email);
        nameE = (EditText) findViewById(R.id.etName);
        phoneE = (EditText) findViewById(R.id.etPhone);


        this.logOutBtn = findViewById(R.id.logOutBtn);


        emailE.setText(userfile.getString(Constants.UEmail, ""));
        nameE.setText(userfile.getString(Constants.UName, ""));
        phoneE.setText(userfile.getString(Constants.UPhone, ""));

        alertDialog = new AlertDialog.Builder(EditUser.this).create();
        alertDialog.setTitle("تنبيه");


        // log out

        this.logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                android.support.v7.app.AlertDialog.Builder ConfirmationDialog = new android.support.v7.app.AlertDialog.Builder(EditUser.this);
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


    /////////////////////////// METHOD TO SAVE UPDATED DATA WHEN CLICK SAVE BUTTON ////////////////////////////

    public void save(View view) {
        msg="";
        email=emailE.getText().toString();
        name=nameE.getText().toString();
        phone=phoneE.getText().toString();

        ///////////////////////// check if user leave editText empty ////////////////////////////////

        if(email.equals("")){
            emailE.setText(userfile.getString(Constants.UEmail,""));
            email=userfile.getString(Constants.UEmail,"");
        }
        if(name.equals("")){
            nameE.setText(userfile.getString(Constants.UName,""));
        }
        if(phone.equals("")){
            phoneE.setText(userfile.getString(Constants.UPhone,""));
            phone=userfile.getString(Constants.UPhone,"");
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

        /////////////////////////////////  PHP Request to edit User  ///////////////////////////////////
        request = new StringRequest(Request.Method.POST, "http://"+ip+"/gcms/editUser.php", new Response.Listener <String>() {

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

                    /////////////////////////////////    UPDATE USER SSESION     ///////////////////////////////////

                    if (status.equals("1")&&emailNotExist.equals("1")&&phoneExist.equals("1")) {

                        userfileEditer.putString(Constants.UEmail,email);
                        userfileEditer.putString(Constants.UName,name);
                        userfileEditer.putString(Constants.UPhone,phone);
                        userfileEditer.putBoolean(Constants.UserIsLoggedIn,true);
                        userfileEditer.putString(Constants.UserType , "user");
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

                String emailText=userfile.getString(Constants.UEmail,"");
                String phoneText=userfile.getString(Constants.UPhone,"");


                /////////////////////////////////    SEND DATA TO PHP     ///////////////////////////////////

                HashMap<String, String> map = new HashMap <>();
                map.put("prevEmail",emailText);
                map.put("prevPhone",phoneText);
                map.put("u_email" ,email);
                map.put("u_name",name);
                map.put("u_phone",phone);

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


    // Alert dialog

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


            Intent intent = new Intent(getBaseContext(), userPage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } // end if user file

    }
}
