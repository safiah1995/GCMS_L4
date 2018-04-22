package com.example.lenovo.maps;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;


public class Add_UserAccount extends AppCompatActivity {

    AlertDialog alertDialog;

    EditText emailE,nameE,passwordE,phoneE;
    String email,name,password,phone;

    StringRequest request;

    android.support.v7.widget.Toolbar toolbar;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_useraccount);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
         toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        this.setTitle("مستخدم جديد");
        setSupportActionBar(toolbar);

        emailE = (EditText) findViewById(R.id.etEmail);
        nameE = (EditText) findViewById(R.id.etName);
        phoneE = (EditText) findViewById(R.id.etPhone);
        passwordE = (EditText) findViewById(R.id.etPass);

    }


    public void register(View view) throws UnknownHostException {

        alertDialog = new AlertDialog.Builder(
                Add_UserAccount.this).create();

        alertDialog.setTitle("تنبيه");

        email = emailE.getText().toString();
        name = nameE.getText().toString();
        phone = phoneE.getText().toString();
        password = passwordE.getText().toString();

        if (email.isEmpty() || name.isEmpty() || phone.isEmpty() || password.isEmpty()) {
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

        String ip = getString(R.string.ip);

        String url = "http://"+ip+"/gcms/AddUser.php";

        request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String emailErrmsg="";
                String phoneErrmsg="";


                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("state");
                    String emailNotExist = jsonObject.getString("emailNotExist");
                    String phoneExist = jsonObject.getString("phoneNotExist");

                    if (emailNotExist.equals("0")) {
                        emailErrmsg="* البريد الالكتروني مسجل مسبقا ادخل بريد الكتروني جديد";
                        showmsg(emailErrmsg);


                    }
                    if (phoneExist.equals("0")) {
                        phoneErrmsg="* رقم الجوال مسجل مسبقا ادخل رقم جديد";
                        showmsg(phoneErrmsg);

                    }

                    if (status.equals("1") && !emailNotExist.equals("0") && !phoneExist.equals("0")) {
                        showmsg("تم حفظ بياناتك بنجاح");
                        Intent i = new Intent(getBaseContext(), loginPage.class);
                        startActivity(i);

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

                map.put("u_email", email);
                map.put("u_name", name);
                map.put("u_phone", phone);
                map.put("u_password", password);

                return map;
            }
        };

        Singleton_Queue.getInstance(Add_UserAccount.this).Add(request);


    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    // Alert dialog
    void showmsg(String msg) {
        alertDialog.setMessage(msg);

        alertDialog.setButton("موافق", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {


            }
        });

        alertDialog.show();    }



}
