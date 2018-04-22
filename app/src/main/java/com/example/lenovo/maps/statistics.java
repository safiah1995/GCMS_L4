package com.example.lenovo.maps;

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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class statistics extends AppCompatActivity implements Designable {

    String [] parts;
    BarChart barChart;

    public static String c_id ;

    android.support.v7.widget.Toolbar toolbar;


    private Button logOutBtn;
    private SharedPreferences userfile;
    private SharedPreferences.Editor userfileEditer;

    EditText myText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        this.setTitle("احصائيات");
        creatToolbar();


        myText= (EditText) findViewById(R.id.myText);


        this.logOutBtn = findViewById(R.id.logOutBtn);

        this.logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                android.support.v7.app.AlertDialog.Builder ConfirmationDialog = new android.support.v7.app.AlertDialog.Builder(statistics.this);
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




        this.userfile = getSharedPreferences(Constants.UserFile, MODE_PRIVATE);
        this.userfileEditer = userfile.edit();


        barChart=(BarChart)findViewById(R.id.bar);
        barChart.getDescription().setEnabled(false);



        String [] days={"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        InitializeView();
        barChart.setFitBars(true);
    }

    @Override
    public void InitializeView() {



        c_id = getIntent().getStringExtra("id");


        String ip = getString(R.string.ip);

        StringRequest request = new StringRequest(Request.Method.POST, "http://"+ip+"/gcms/statiscit.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //  CylinderID.setText(response.toString());
                    String x =response.toString();
                    parts = x.split("-");

                   myText.append(parts[7]);


                    ArrayList<BarEntry> barEntries=new ArrayList<>();

                    for(int i=0;i<7; i++){
                        int number =Integer.parseInt(parts[i]);

                        float v=(float) number;

                        barEntries.add(new BarEntry(i+1,(int)v));
                    }

                    BarDataSet set=new BarDataSet(barEntries,"معدل استخدام اسطوانة الغاز");
                    set.setDrawValues(true);

                    BarData barData=new BarData(set);
                    barChart.setData(barData);
                    barChart.invalidate();
                    barChart.animateY(500);





                    final ArrayList<String> xAxisLabel = new ArrayList<>();
                    for (int i = 0; i < 7; i++) {
                        xAxisLabel.add("سبت");
                        xAxisLabel.add("أحد");
                        xAxisLabel.add("أثنين");
                        xAxisLabel.add("ثلاثاء");
                        xAxisLabel.add("اربعاء");
                        xAxisLabel.add("خميس");
                        xAxisLabel.add("جمعه");

                    }
                    XAxis xAxis = barChart.getXAxis();
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setDrawGridLines(false);
                    xAxis.setValueFormatter(new IAxisValueFormatter() {
                        @Override
                        public String getFormattedValue(float value, AxisBase axis) {
                            return xAxisLabel.get((int)value);
                        }
                    });



                } catch (Exception e) {
                    e.printStackTrace();
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
    }

    @Override
    public void Desing()
    {
        HandleAction();
    }

    @Override
    public void HandleAction() { } // end HandleAction


    public void onBackPressed() {
        // this method to rtturn a prefuse page

        Intent intent = new Intent(getBaseContext(), cylnder_details.class);
        intent.putExtra("id",c_id);
        startActivity(intent);

    }

    public void stat(View view) {
        Intent i = new Intent(getApplicationContext(), statistics.class);
        startActivity(i);
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


}//end class