package com.example.lenovo.maps;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class add_cylinder extends AppCompatActivity {

    private Button logOutBtn;
    private SharedPreferences userfile;
    private SharedPreferences.Editor userfileEditer;

    EditText ID, NAME, TYPE;
    String id,name,type;

    AlertDialog alertDialog;
    StringRequest request;
    android.support.v7.widget.Toolbar toolbar;

    String email;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cylinder);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
       this.setTitle("إضافط اسطوانة غاز");
       creatToolbar();

        this.logOutBtn = findViewById(R.id.logOutBtn);
        this.userfile = getSharedPreferences(Constants.UserFile, MODE_PRIVATE);
        this.userfileEditer = userfile.edit();

        ID=(EditText)findViewById(R.id.ID);
        NAME=(EditText)findViewById(R.id.NAME);
        TYPE=(EditText)findViewById(R.id.TYPE);

        email=userfile.getString(Constants.UEmail,"");


        // log out
        this.logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                android.support.v7.app.AlertDialog.Builder ConfirmationDialog = new android.support.v7.app.AlertDialog.Builder(add_cylinder.this);
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



    public void register(View view) {

        alertDialog = new AlertDialog.Builder(add_cylinder.this).create();
        alertDialog.setTitle("تنبيه");


        id = ID.getText().toString();
        name = NAME.getText().toString();
        type = TYPE.getText().toString();

        if (id.isEmpty() || name.isEmpty() || type.isEmpty() ) {
            showmsg("الرجاء إدخال جميع الحقول");
            return;
        }

        if(id.length()>8 || id.length()<8){
            showmsg("الرجاء إدخال رقم الاسطوانة المكون من 8 خانات بشكل صحيح");
            return;
        }


        String ip = getString(R.string.ip);

        String url = "http://"+ip+"/gcms/addCylinder.php";

        request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {



                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("state");
                    String IdNotExist = jsonObject.getString("IdNotExist");



                   if (IdNotExist.equals("0")) {
                        showmsg("رقم الاسطوانة مسجل مسبقا");
                    }


                    if (status.equals("1") && !IdNotExist.equals("0")) {
                        showmsg("تم حفظ بياناتك بنجاح");
                        Intent i= new Intent(getApplicationContext(),view_cylinder_list.class);
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

                map.put("c_id", id);
                map.put("c_name", name);
                map.put("c_type", type);
                map.put("u_email", email);

                return map;
            }
        };

        Singleton_Queue.getInstance(add_cylinder.this).Add(request);

    }

      // Alert Dialog
    void showmsg(String msg) {
        alertDialog.setMessage(msg);

        alertDialog.setButton("موافق", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.show();
    }


    ////////////////////    CREATE & HANDLING TOOLBAR & iT'S BUTTONS     ///////////////////////////

    protected void creatToolbar() {

        // Attaching the layout to the toolbar object
        toolbar = ( android.support.v7.widget.Toolbar ) findViewById(R.id.tool_bar);
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