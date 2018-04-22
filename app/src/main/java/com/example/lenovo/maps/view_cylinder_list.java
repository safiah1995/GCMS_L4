package com.example.lenovo.maps;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class view_cylinder_list extends AppCompatActivity implements Designable{


    String email;
    private ListView listView_Cyl  ;
    private ArrayList<String> list_Cyl;
    private SharedPreferences userfile;
    private SharedPreferences.Editor userfileEditer;
    Toolbar toolbar;


    private Button logOutBtn;
    TextView empty;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cylinder_list);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        this.setTitle("قائمة الأسطوانات");
        creatToolbar();
        InitializeView();
    }

    @Override
    public void InitializeView() {
        this.userfile = getSharedPreferences(Constants.UserFile,MODE_PRIVATE);
        this.userfileEditer=userfile.edit();

        this.logOutBtn = findViewById(R.id.logOutBtn);
        listView_Cyl = findViewById(R.id.listTheCylnder);
        empty = findViewById(R.id.empty);
        list_Cyl = new ArrayList<>();
        email = userfile.getString(Constants.UEmail, "");

        this.logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                android.support.v7.app.AlertDialog.Builder ConfirmationDialog = new android.support.v7.app.AlertDialog.Builder(view_cylinder_list.this);
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




        String ip = getString(R.string.ip);
        StringRequest request = new StringRequest(Request.Method.POST, "http://"+ip+"/gcms/selectCylinder.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {


                    if (response.toString().equals("1")){
                        empty.setText("لا يوجد اسطوانات مضافة ،ابدأ بإضافة اسطواناتك");

                    }

                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String name = jsonObject.getString("name");
                        String value = jsonObject.getString("Val");
                        String id = jsonObject.getString("id");





                        list_Cyl.add(name+","+value+","+id);

                    }



                    if (list_Cyl.size() <= 0) {

                        empty.setText("لا يوجد اسطوانات مضافة ،ابدأ بإضافة اسطواناتك");

                    } else {

                        Cyl_Adpt adapter = new Cyl_Adpt(getBaseContext(), list_Cyl);

                        listView_Cyl.setAdapter(adapter);

                        listView_Cyl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                Intent intent=new Intent(getBaseContext(),cylnder_details.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                String info [] = list_Cyl.get(i).split(",");
                                intent.putExtra("id",info[2]);
                                startActivity(intent);

                            }
                        });




                    }
                } catch (JSONException e) {


                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(), "هنالك مشكلة في الخادم الرجاء المحاولة مرة اخرى", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("email",email);
                return map;
            }
        };
        Singleton_Queue.getInstance(getBaseContext()).Add(request);



        Desing();

    }

    @Override
    public void Desing() {

        HandleAction();
    }

    @Override
    public void HandleAction() {

    }

    public void onBackPressed() {
        // this method to rtturn a prefuse page

        Intent intent = new Intent(getBaseContext(), userPage.class);
        startActivity(intent);

    }


    ////////////////////    CREATE & HANDLING TOOLBAR & iT'S BUTTONS     ///////////////////////////

    protected void creatToolbar() {

        // Attaching the layout to the toolbar object
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
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

    public void add(View view) {
        Intent i= new Intent(getApplicationContext(),add_cylinder.class);
        startActivity(i);
    }


}