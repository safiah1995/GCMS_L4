package com.example.lenovo.maps;

import android.annotation.SuppressLint;
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

public class cylnder_details extends AppCompatActivity implements Designable {


    String c_id ;
    EditText cid,cName,cType,cAmount,cLock,cLeak;


    android.support.v7.widget.Toolbar toolbar;

    private Button logOutBtn , delete;
    private SharedPreferences userfile;
    private SharedPreferences.Editor userfileEditer;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cylnder_details);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        this.setTitle("تفاصيل الاسطوانة");
        creatToolbar();

        alertDialog = new AlertDialog.Builder(cylnder_details.this).create();
        alertDialog.setTitle("تنبيه");

        this.userfile = getSharedPreferences(Constants.UserFile, MODE_PRIVATE);
        this.userfileEditer = userfile.edit();

        this.logOutBtn = findViewById(R.id.logOutBtn);
        this.delete=findViewById(R.id.delete);

        cid=(EditText) findViewById(R.id.ID);
        cName=(EditText) findViewById(R.id.NAME);
        cType=(EditText) findViewById(R.id.TYPE);
        cAmount=(EditText) findViewById(R.id.Amount);
        cLeak=(EditText) findViewById(R.id.Leak);
        cLock=(EditText) findViewById(R.id.Lock);

        creatToolbar();


        // log out

        this.logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                android.support.v7.app.AlertDialog.Builder ConfirmationDialog = new android.support.v7.app.AlertDialog.Builder(cylnder_details.this);
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

        this.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {


                android.support.v7.app.AlertDialog.Builder ConfirmationDialog = new android.support.v7.app.AlertDialog.Builder(cylnder_details.this);
                ConfirmationDialog.setCancelable(false);
                ConfirmationDialog.setMessage("هل تريد بالفعل حذف الاسطوانة ؟");
                ConfirmationDialog.setTitle("تأكيد");
                ConfirmationDialog.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (Network.isConnected(getBaseContext()) == false) {
                            Toast.makeText(getBaseContext(), "لا يوجد اتصال بالانترنت", Toast.LENGTH_LONG).show();
                        } else {
                            delete(view);
                        }
                    }
                });
                ConfirmationDialog.setNegativeButton("لا", null);
                ConfirmationDialog.show();


            } // end onClick
        }); // end setOnClickListener



        InitializeView();
    }

    @Override
    public void InitializeView() {

        c_id = getIntent().getStringExtra("id"); // cylinder ID

        String ip = getString(R.string.ip);

        StringRequest request = new StringRequest(Request.Method.POST, "http://"+ip+"/gcms/detailCylinder.php", new Response.Listener<String>() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onResponse(String response) {

                try {



                    String x =response.toString();
                    String[] parts = x.split("-");
                    String id = parts[0];
                    String name = parts[1];
                    String type = parts[2];
                    String a_amount = parts[3];
                    String a_lock = parts[4];
                    String a_leak = parts[5];

                    cid.append(" "+id);
                    cName.append(" "+name);
                    cType.append(" "+type);
                    cAmount.append(" "+a_amount);
                    if(a_lock.equalsIgnoreCase("1")){
                        cLock.setText("مغلقة");
                            cLock.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.lock, 0);

                    }else{
                        cLock.setText("مفتوحة");
                             cLock.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.unlock, 0);
                    }

                    int a_leakk = Integer.parseInt(a_leak);
                    if(a_leakk>=60){
                        cLeak.setText("الغاز يتسرب");
                        cLeak.setTextColor(R.color.red);

                    }else{
                        cLeak.setText("الغاز آمن");
                    }



                } catch (Exception e) {


                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                Toast.makeText(getBaseContext(), "هنالك مشكلة في الخادم الرجاء المحاولة مرة اخرى", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("c_id",c_id);
                return map;
            }
        };
        Singleton_Queue.getInstance(getBaseContext()).Add(request);



        Desing();

    }

    @Override
    public void Desing()
    {
        HandleAction();
    }

    @Override
    public void HandleAction() {



    } // end HandleAction


   // to statistics page
    public void stat(View view) {
        Intent intent = new Intent(cylnder_details.this, statistics.class);
        intent.putExtra("id",getIntent().getStringExtra("id"));
        startActivity(intent);
    }

    // to EditCylinder page
    public void EditCylinder (View view){


        Intent intent = new Intent(cylnder_details.this, EditCylinder.class);
        intent.putExtra("id",getIntent().getStringExtra("id"));
        startActivity(intent);

    }

    // delete specific cylinder
    public void delete(View view) {


        String ip = getString(R.string.ip);
        String url = "http://"+ip+"/gcms/deleteCylinder.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {



                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("state");



                    if (status.equals("1")) {
                        showmsg("تم حذف بينات الاسطوانة بنجاح");
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
                showmsg("فشل الاتصال بالخادم ، حاول مجددا");            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                /* Here you put the HTTP request parameters **/

                HashMap<String, String> map = new HashMap<>();

                map.put("c_id", c_id);
                return map;
            }
        };

        Singleton_Queue.getInstance(getBaseContext()).Add(request);

    }

    public void onBackPressed() {
        // this method to rtturn a prefuse page

        Intent intent = new Intent(getBaseContext(), view_cylinder_list.class);
        startActivity(intent);

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

