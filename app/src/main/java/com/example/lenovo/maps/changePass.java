package com.example.lenovo.maps;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class changePass extends AppCompatActivity {

    AlertDialog alertDialog;

    private SharedPreferences userfile;
    private SharedPreferences.Editor userfileEditer;

    EditText passwordE,NpasswordE;
    String password,Npassword, msg;

    StringRequest request;
    android.support.v7.widget.Toolbar toolbar;

    private Button logOutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setTitle("تغيير كلمة المرور");
        creatToolbar();

        this.userfile = getSharedPreferences(Constants.UserFile,MODE_PRIVATE);
        this.userfileEditer=userfile.edit();

        this.logOutBtn = findViewById(R.id.logOutBtn);

        passwordE = (EditText) findViewById(R.id.etPass);
        NpasswordE = (EditText) findViewById(R.id.newPass);


        // log out

        this.logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                android.support.v7.app.AlertDialog.Builder ConfirmationDialog = new android.support.v7.app.AlertDialog.Builder(changePass.this);
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

    public void changePass(View view) {

        alertDialog = new AlertDialog.Builder(changePass.this).create();
        alertDialog.setTitle("تنبيه");

        msg="";
        password=passwordE.getText().toString();
        Npassword=NpasswordE.getText().toString();

        if(password.equals("")||Npassword.equals("")){

            showmsg("* يرجى إدخال كلمة المرور في الحقل المخصص");
            return;
        }

        String ip = getString(R.string.ip);

        request = new StringRequest(Request.Method.POST, "http://"+ip+"/gcms/changePass.php", new Response.Listener <String>() {

            @Override
            public void onResponse(String response) {
                msg="";

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String statusPass = jsonObject.getString("statePass");


                    if (statusPass.equals("1")) {

                        showmsg("تم تغيير كلمة المرور بنجاح.");


                    } else{
                       showmsg("* كلمة المرور الحالية خاطئة.");
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
                String email;
                String UserType=userfile.getString(Constants.UserType,"");
                if (UserType.equals("user")){
                    email=userfile.getString(Constants.UEmail,"");

                }else{
                    email=userfile.getString(Constants.PEmail,"");
                }

                HashMap<String, String> map = new HashMap <>();
                map.put("UserType",UserType);
                map.put("prevEmail",email);
                map.put("prevPass",password);
                map.put("u_password",Npassword);

                return map;
            }
        };
        Singleton_Queue.getInstance(getBaseContext()).Add(request);

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
            HandleAction();

        }else if (id == R.id.home) {
            Intent intent = new Intent(getBaseContext(), providerPage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void HandleAction() {

        if(userfile.getBoolean(Constants.UserIsLoggedIn,false)== true){
            String Type =  userfile.getString(Constants.UserType , "");

            if (Type.equals("user")) {

                Intent intent = new Intent(getBaseContext(), EditUser.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            } // end if type user

            else { // the type = provider
                Intent intent=new Intent(getBaseContext(),EditProvider.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } // end else
        } // end if user file

    }// end HandleAction
}
