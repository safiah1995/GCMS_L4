package com.example.lenovo.maps;

import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import android.app.*;

import android.content.Intent;

import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {
    Timer timer;
    Intent notificationIntent;
    PendingIntent intent;
    private NotificationHelper mNotificationH;

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            notifyM();
        }
    };
    private SharedPreferences userfile;
    private SharedPreferences.Editor userfileEditer;
    String email;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.userfile = getSharedPreferences(Constants.UserFile, MODE_PRIVATE);
        this.userfileEditer = userfile.edit();
        email = userfile.getString(Constants.UEmail, "");
        mNotificationH = new NotificationHelper(this);
        notificationIntent = new Intent(getApplicationContext(), instruction.class);
        intent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

        timer = new Timer();
        timer.schedule(timerTask, 3000, 3 * 1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // super.onDestroy();
        try {
            timer.cancel();
            timerTask.cancel();

        } catch (Exception e) {
            e.printStackTrace();
        }
        //Intent i=new Intent("com.example.mashael.project");
        sendBroadcast(new Intent("com.example.mashael.project"));
    }

    public void notifyM() {

        InitializeView();
        //sendChannel1("تنبيه", "MM");

    }

    public void InitializeView() {


        String ip = getString(R.string.ip);
        StringRequest request = new StringRequest(Request.Method.POST, "http://"+ip+"/gcms/refreshing.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    String xx = (response.toString());
                    String[] parts = xx.split("-");
                    String Mymessage = "";
                    String Lowmessage = "";
                    for (int j = 1; j <= parts.length; j++) {
                        mNotificationH.getManager().cancel(j);
                    }
                    int id=1;
                    for (int i = 0; i < parts.length; i++) {

                        if (parts[i].substring(parts[i].length() - 2, parts[i].length()).contains("1") == true && parts[i].substring(parts[i].length() - 2, parts[i].length()).contains("2") == true) {

                            Mymessage = "يوجد تسرب و انخفاض في مستوى الغاز في اسطوانةرقم " + parts[i].substring(0, parts[i].length() - 2) + "\r\n"+"اضغط للذهاب الى التعليمات ";
                            sendChannel2("تنبيه", Mymessage, ++id);

                        }else if (parts[i].substring(parts[i].length() - 2, parts[i].length()).contains("2") == true) {

                            Lowmessage += "انخفض مستوى الغاز عن ٣٠٪ في الاسطوانه رقم" + parts[i].substring(0, parts[i].length() - 2) + "\r\n";


                        }  else if (parts[i].substring(parts[i].length() - 2, parts[i].length()).contains("1") == true) {

                            Mymessage = "يوجد تسرب غاز في اسطوانة رقم " + parts[i].substring(0, parts[i].length() - 2) + "\r\n"+"اضغط للذهاب الى التعليمات ";
                            sendChannel2("تنبيه", Mymessage, ++id);
                        }


                    }
                    if(Lowmessage.length()!=0){
                        sendChannel1("تنبيه", Lowmessage, 0);
                    }

                } catch (Exception e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getBaseContext(), "هنالك مشكلة في الخادم الرجاء المحاولة مرة اخرى", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("email", email);
                return map;
            }
        };
        Singleton_Queue.getInstance(getBaseContext()).Add(request);

    }
    int id=0; int i=0;
    public void sendChannel1(String title, String body, int y) {

        NotificationCompat.Builder nb = mNotificationH.getChannel1Notification(title, body);
        mNotificationH.getManager().notify(y, nb.build());


    }

    public void sendChannel2(String title, String body, int y) {
        NotificationCompat.Builder nb = mNotificationH.getChannel1Notification(title, body).setOnlyAlertOnce(false)
                .setContentIntent(intent);
        mNotificationH.getManager().notify(y, nb.build());

    }

}