package com.ceng319.n01231625.javapersonifyapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Collections;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.MyHolder>   {
    private Context context;
    LayoutInflater inflater;
    List<sensorData> Sensordata;
    sensorData current;
    int currentPos=0;

    public Adapter(List<sensorData> Sensordata){
        this.context=context;
        inflater= LayoutInflater.from(context);
        this.Sensordata = Sensordata;
    }

    @Override
    public Adapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.custom_list_layout,parent,false);
        MyHolder holder=new MyHolder(view);

        return holder;

    }

    @Override
    public void onBindViewHolder(Adapter.MyHolder holder, int position) {
        MyHolder myHolder= (MyHolder) holder;
        sensorData current=Sensordata.get(position);
        myHolder.texttimeStamp.setText(current.timeStamp);
        myHolder.textsensorValue.setText("Value: " + current.sensorValue);
    }

    @Override
    public int getItemCount() {
        return Sensordata.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        TextView  texttimeStamp, textsensorValue;

        public MyHolder(View itemView) {
            super(itemView);
            texttimeStamp = (TextView) itemView.findViewById(R.id.test1);
            textsensorValue = (TextView) itemView.findViewById(R.id.test2);


        }
    }


}
