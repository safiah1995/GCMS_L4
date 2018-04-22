package com.example.lenovo.maps;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Person_Type extends AppCompatActivity {

    android.support.v7.widget.Toolbar toolbar;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person__type);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setTitle("تسجيل");
        creatToolbar();
    }

    public void newUser(View view) {
        Intent i= new Intent(getApplicationContext(),Add_UserAccount.class);
        startActivity(i);
    }

    public void newProvider(View view) {
        Intent i= new Intent(getApplicationContext(),Add_ProviderAccount.class);
        startActivity(i);
    }

    ////////////////////    CREATE & HANDLING TOOLBAR & iT'S BUTTONS     ///////////////////////////

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void creatToolbar() {

        // Attaching the layout to the toolbar object
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(toolbar);
    }

}
