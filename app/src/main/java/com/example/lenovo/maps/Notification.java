package com.example.lenovo.maps;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class Notification extends AppCompatActivity implements Designable {

    private SharedPreferences userfile;
    private SharedPreferences.Editor userfileEditer;
    String email;
    private NotificationHelper mNotificationH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        this.userfile = getSharedPreferences(Constants.UserFile, MODE_PRIVATE);
        this.userfileEditer = userfile.edit();
        email= userfile.getString(Constants.UEmail,"");
        mNotificationH = new NotificationHelper(this);


        ///---------------------------------------------------------  refresh code of reading from server  ---

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                InitializeView();
                          //      mytext.setText(" ");

                                //  mytext.setText("hello    "+is+" times");
                                //  is++;
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();

///--------------------------------------------------  end of refresh code of reading from server  ---

    }

    public void sendChannel1(String title, String body) {
        NotificationCompat.Builder nb = mNotificationH.getChannel1Notification(title, body);
        mNotificationH.getManager().notify(1, nb.build());

    }


    public void notification() {
/*
        notification.setSmallIcon(R.drawable.ic_launcher_background);
        notification.setTicker("helloo :) ");
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("Title");
        notification.setContentText("body");

        Intent intent = new Intent(this, Notification.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(uniqueId, notification.build());
        */

    }

    @Override
    public void InitializeView() {



        String ip = getString(R.string.ip);
        StringRequest request = new StringRequest(Request.Method.POST, "http://"+ip+"/gcms/refreshing.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    String res=response.toString();
                    if (res.equals("9999")== true){
                        //  mytext.setText("lllllllll");
                        sendChannel1("Low Amount of Gas", "your cylinder has less than 30% of gas");

                        //  notification();


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

}
