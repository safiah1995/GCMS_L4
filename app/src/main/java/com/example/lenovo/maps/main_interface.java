package com.example.lenovo.maps;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class main_interface extends AppCompatActivity {


    Handler mHandler = new Handler();

    private SharedPreferences userfile;
    private SharedPreferences.Editor userfileEditer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_interface);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.userfile = getSharedPreferences(Constants.UserFile,MODE_PRIVATE);
        this.userfileEditer=userfile.edit();

        InitializeView();
    }


    public void InitializeView() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    try {
                        Thread.sleep(3000);
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                               HandleAction();
                                Thread.yield();
                            }
                        });
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    break;
                }
            }
        }).start();


    } // end InitializeView

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

        else{

            Intent intent=new Intent(getBaseContext(),loginPage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }// end HandleAction



} // end class