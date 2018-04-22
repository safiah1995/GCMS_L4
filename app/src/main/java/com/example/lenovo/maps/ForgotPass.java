package com.example.lenovo.maps;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPass extends AppCompatActivity {

    EditText emailtxt;
    AlertDialog alertDialog;
    String email;

    android.support.v7.widget.Toolbar toolbar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setTitle("إستعادة كلمة المرور");
        creatToolbar();

        emailtxt= (EditText) findViewById(R.id.emailInput) ;

        alertDialog = new AlertDialog.Builder(ForgotPass.this).create();
        alertDialog.setTitle("تنبيه");

    }
    public void sendEmail(View view) {
        email = emailtxt.getText().toString();

        if(email.equals("")){
            showmsg("* حقل البريد الإلكتروني فارغ ");

            return;
        }
        if(!isEmailValid(email)){
            showmsg("* صيغة البريد الالكتروني غير صحيحة ");

            return;
        }

        String ip = getString(R.string.ip);

        StringRequest request=new StringRequest(Request.Method.POST, "http://"+ip+"/gcms/forgotPassword.php", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("state");


                    if (status.equals("1")) {
                        showmsg("تم إرسال كلمة المرور الجديدة الى بريدك الإلكتروني بنجاح");


                    } else {
                        showmsg("الرجاء التأكد من البريد الالكتروني المدخل.");
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
                HashMap<String,String> map=new HashMap<>();
                map.put("email",email);
                return map;
            }
        };
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        request.setRetryPolicy(mRetryPolicy);
        Singleton_Queue.getInstance(getBaseContext()).Add(request);

    }


    boolean isEmailValid(CharSequence email ) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Alert Dialog

    void showmsg(String msg) {
        alertDialog.setMessage(msg);

        alertDialog.setButton("موافق", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                Intent i= new Intent(getApplicationContext(),loginPage.class);
                startActivity(i);

            }
        });

        alertDialog.show();    }

    ////////////////////    CREATE & HANDLING TOOLBAR & iT'S BUTTONS     ///////////////////////////

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void creatToolbar() {

        // Attaching the layout to the toolbar object
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        this.setSupportActionBar( toolbar);
    }





}
