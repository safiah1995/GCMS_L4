package com.example.lenovo.maps;

import android.widget.ArrayAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

import static com.example.lenovo.maps.R.drawable.gaslow;


public class Cyl_Adpt extends ArrayAdapter {

    // this class for view cylinder list icon

    private Context context;
    private ArrayList<String> Cyl_list;


    public Cyl_Adpt(Context context, ArrayList<String> Cyl_list) {
        super(context, R.layout.coustom_list, R.id.TextView_INcostum, Cyl_list);

        this.context = context;
        this.Cyl_list = Cyl_list;

    }

    public String getItem(int position){
        return Cyl_list.get(position);
    }

    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final View view = LayoutInflater.from(getContext()).inflate(R.layout.coustom_list, parent, false);

        TextView textView = (TextView) view.findViewById(R.id.TextView_INcostum);
        ImageView cylinderIMG= (ImageView) view.findViewById(R.id.imageView);



        String info [] = Cyl_list.get(position).split(",");

        //  double amount=((Integer.parseInt(info[1]))-90)*2;
          double amount=Integer.parseInt(info[1]);


        if (amount<=30) {

            cylinderIMG.setImageResource(R.drawable.gaslow);
        }

       else if (amount<=50){

           cylinderIMG.setImageResource(R.drawable.gas50);

        }else if (amount<=85){

            cylinderIMG.setImageResource(R.drawable.gas75);

        }else{

            cylinderIMG.setImageResource(R.drawable.gasfull);

        }

        textView.setText("اسم الأسطوانة:"+info[0]+"");

        return view;
    }


}