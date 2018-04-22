package com.example.lenovo.maps;

import  android.app.AlertDialog;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditCylinder extends AppCompatActivity implements Designable {

    private Button logOutBtn;
    private SharedPreferences userfile;
    private SharedPreferences.Editor userfileEditer;

    String msg;

    String c_id_intent;
    String c_name , c_type;
    EditText cid,cName,cType;


    android.support.v7.widget.Toolbar toolbar;

    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cylinder);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        this.setTitle("بيانات الاسطوانة");
        creatToolbar();
        c_id_intent = getIntent().getStringExtra("id");

        InitializeView();

    }


    @Override
    public void InitializeView() {

        alertDialog = new AlertDialog.Builder(EditCylinder.this).create();
        alertDialog.setTitle("تنبيه");

        this.logOutBtn = findViewById(R.id.logOutBtn);
        this.userfile = getSharedPreferences(Constants.UserFile, MODE_PRIVATE);
        this.userfileEditer = userfile.edit();

        cName=(EditText) findViewById(R.id.NAME);
        cType=(EditText) findViewById(R.id.TYPE);

        setCylinderText(); // prev info of the cylinder

        // log out

        this.logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                android.support.v7.app.AlertDialog.Builder ConfirmationDialog = new android.support.v7.app.AlertDialog.Builder(EditCylinder.this);
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
    public void Desing()
    {
        HandleAction();
    }

    @Override
    public void HandleAction() {


    } // end HandleAction



    // edit cylinder information

    public void EditC (View view){

        msg="";

        c_name=cName.getText().toString();
        c_type=cType.getText().toString();


        String ip = getString(R.string.ip);

        StringRequest request = new StringRequest(Request.Method.POST, "http://"+ip+"/gcms/editCylinder.php", new Response.Listener <String>() {

            @Override
            public void onResponse(String response) {

                try {



                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("state");

                    if (status.equals("1")) {


                        showmsg("تم حفظ بياناتك الجديدة بنجاح.");
                        Intent intent = new Intent(EditCylinder.this, cylnder_details.class);
                        intent.putExtra("id",getIntent().getStringExtra("id"));
                        startActivity(intent);




                    } else{
                        showmsg("حدث خطأ ما ، حاول مجددا ");
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


                /////////////////////////////////    SEND DATA TO PHP     ///////////////////////////////////

                HashMap<String, String> map = new HashMap <>();
                map.put("prevId",c_id_intent);
                map.put("c_name",c_name);
                map.put("c_type",c_type);

                return map;
            }
        };
        Singleton_Queue.getInstance(getBaseContext()).Add(request);


    }


    // prev info of the cylinder

    public void  setCylinderText(){

        String ip = getString(R.string.ip);

        /////////////////////////////////  PHP Request to edit  ///////////////////////////////////
        StringRequest request = new StringRequest(Request.Method.POST, "http://"+ip+"/gcms/cylinderData.php", new Response.Listener <String>() {

            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray cArray = jsonObject.getJSONArray("result");

                    JSONObject c = cArray.getJSONObject(0);

                    cName.setText(c.getString("c_name"));
                    cType.setText(c.getString("c_type"));




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


                /////////////////////////////////    SEND DATA TO PHP     ///////////////////////////////////

                HashMap<String, String> map = new HashMap <>();
                map.put("id",c_id_intent);

                return map;
            }
        };
        Singleton_Queue.getInstance(getBaseContext()).Add(request);

    }

    // alert dialoge

    void showmsg(String msg) {
        alertDialog.setMessage(msg);

        alertDialog.setButton("موافق", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                Intent i= new Intent(getApplicationContext(),view_cylinder_list.class);
                startActivity(i);

            }
        });

        alertDialog.show();

    }

    ////////////////////    CREATE & HANDLING TOOLBAR & iT'S BUTTONS     ///////////////////////////

    protected void creatToolbar() {

        // Attaching the layout to the toolbar object
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        this.setSupportActionBar(toolbar);
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






}//end class

