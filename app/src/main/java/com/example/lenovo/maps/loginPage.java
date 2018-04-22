package com.example.lenovo.maps;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class loginPage extends AppCompatActivity  implements Designable {

    private Button LoginBtn ;
    private ProgressDialog progressDialog;
    private EditText email, password;
    private SharedPreferences userfile;
    private SharedPreferences.Editor userfileEditer;

    AlertDialog alertDialog;

    android.support.v7.widget.Toolbar toolbar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        this.setTitle("تسجيل");
        creatToolbar();

        InitializeView();
    }

    public void InitializeView() {

        this.userfile = getSharedPreferences(Constants.UserFile,MODE_PRIVATE);
        this.userfileEditer=userfile.edit();


        this.LoginBtn =  findViewById(R.id.login);
        this.email =  findViewById(R.id.emailText);
        this.password =  findViewById(R.id.passText);
        this.progressDialog=new ProgressDialog(loginPage.this);

        alertDialog = new AlertDialog.Builder(loginPage.this).create();
        alertDialog.setTitle("تنبيه");


        Desing();
    }// end InitializeView

    public void Desing() {

        //make (forget password textview) underline
        String FP="نسيت كلمة المرور؟";
        SpannableString content = new SpannableString(FP);
        content.setSpan(new UnderlineSpan(), 0, FP.length(), 0);
        TextView err = (TextView)findViewById(R.id.forget_password);
        err.setText(content);


        this.progressDialog.setCancelable(false);

        HandleAction();

    } // end Desing

    public void HandleAction() {

        if(userfile.getBoolean(Constants.UserIsLoggedIn,false)== true){
            String Type =  userfile.getString(Constants.UserType , "");

            if (Type.equals("user")) {

                Intent intent = new Intent(getBaseContext(), userPage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            } // end if type user

            else { // the type = provider
                Intent intent=new Intent(getBaseContext(),providerPage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } // end else



        } // end if user file

        this.LoginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //check if edittext is empty or not

                if (email.getText().toString().trim().isEmpty() || password.getText().toString().trim().isEmpty()) {

                    showmsg("يجب تعبئة جميع الجقول");
                } else if (Network.isConnected(getBaseContext()) == false) {
                    showmsg("لا يوجد اتصال بالانترنت ");

                } else {
                    progressDialog.setMessage("الرجاء الانتظار..."); // Please wait ...
                    progressDialog.show();

                    String ip = getString(R.string.ip);

                    StringRequest request= new StringRequest(Request.Method.POST, "http://"+ip+"/gcms/checkLogin.php", new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {

                            try {


                                JSONObject jsonObject = new JSONObject(response);
                                String status=jsonObject.getString("state");

                                if(status.equals("yes")){


                                    String Type=jsonObject.getString("type");
                                    progressDialog.dismiss();


                                    if (Type.equals("user")) {

                                        JSONObject UserOB=jsonObject.getJSONObject("user");
                                        String userEmail = UserOB.getString("u_email");
                                        String userName = UserOB.getString("u_name");
                                        String userPhone = UserOB.getString("u_phone");
                                        userfileEditer.putString(Constants.UEmail,userEmail);
                                        userfileEditer.putString(Constants.UName,userName);
                                        userfileEditer.putString(Constants.UPhone,userPhone);
                                        userfileEditer.putBoolean(Constants.UserIsLoggedIn,true);
                                        userfileEditer.putString(Constants.UserType , "user");
                                        userfileEditer.commit();

                                        Intent intent=new Intent(getBaseContext(),userPage.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                    } else { // type == provider

                                        JSONObject UserOB=jsonObject.getJSONObject("user");
                                        String providerEmail = UserOB.getString("p_email");
                                        String providerPhone = UserOB.getString("p_phone");
                                        String providerName = UserOB.getString("p_name");
                                        //String providerLocation = UserOB.getString("p_location");
                                        String providerStart = UserOB.getString("start_working");
                                        String providerEnd = UserOB.getString("end_working");
                                        userfileEditer.putString(Constants.PEmail,providerEmail);
                                        userfileEditer.putString(Constants.PPhone,providerPhone);
                                        userfileEditer.putString(Constants.PName,providerName);
                                        String locationLatitude = UserOB.getString("x_location");
                                        String locationLongitude = UserOB.getString("y_location");
                                        userfileEditer.putString(Constants.locationLatitude,locationLatitude);                                        userfileEditer.putString(Constants.PStart,providerStart);
                                        userfileEditer.putString(Constants.locationLongitude,locationLongitude);
                                        // userfileEditer.putString(Constants.PLocation,providerLocation);
                                        userfileEditer.putString(Constants.PStart,providerStart);
                                        userfileEditer.putString(Constants.PEnd,providerEnd);
                                        userfileEditer.putBoolean(Constants.UserIsLoggedIn,true);
                                        userfileEditer.putString(Constants.UserType , "provider");
                                        userfileEditer.commit();



                                        Intent intent=new Intent(getBaseContext(),providerPage.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                    }  // end else type provider



                                }else{
                                    progressDialog.dismiss();
                                    showmsg("الرجاء التأكد من البريد الالكتروني او كلمة المرور .");
                                }
                            } catch (JSONException e) {
                                Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            showmsg("فشل الاتصال بالخادم ، حاول مجددا");                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {


                            /*** Here you put the HTTP request parameters **/

                            HashMap<String,String> map=new HashMap<>();
                            map.put("email",email.getText().toString());
                            map.put("password",password.getText().toString());
                            return map;
                        }
                    };
                    Singleton_Queue.getInstance(getBaseContext()).Add(request);
                }



            }});
    } // end  HandleAction() {

    public void forgotPass(View view) {
        Intent i= new Intent(getApplicationContext(),ForgotPass.class);
        startActivity(i);
    }

    public void Person_Type(View view) {
        Intent i= new Intent(getApplicationContext(),Person_Type.class);
        startActivity(i);
    }


    // Alert Dialog

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



} // end class