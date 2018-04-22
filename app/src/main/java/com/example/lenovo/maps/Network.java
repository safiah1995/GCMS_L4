package com.example.lenovo.maps;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public final class Network {

    /*** Check WI-FI Connection */
    public static final boolean isConnected(Context context) {
        /*** Access WI-FI Premission*/
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
