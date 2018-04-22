package com.example.lenovo.maps;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;

public class Singleton_Queue {

    private RequestQueue queue;
    private static Singleton_Queue singleton;
    private Context context;

    private Singleton_Queue(Context context) {
        this.context = context;
        queue=getRequestQueu(); /////////////////////
    }

    public static synchronized Singleton_Queue getInstance(Context context) {
        if (singleton == null) {
            singleton = new Singleton_Queue(context);
        }
        return singleton;

    }

    public RequestQueue getRequestQueu() {
        if (queue == null) {
            queue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return queue;
    }


    /*** To Add  the request into the qeueu */ ///////////////////////////
    public <T> void Add(Request<T> request) {

        queue.add(request);

    }
}




