package com.ceng319.n01231625.javapersonifyapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>   {
    LayoutInflater inflater;
    List<Sensors> sensors;

    public Adapter(Context ctx, List<Sensors> sensors){
        this.inflater = LayoutInflater.from(ctx);
        this.sensors = sensors;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.custom_list_layout,parent,false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.sensorNames.setText(sensors.get(position).getSensorNames());
    holder.sensorValue.setText(sensors.get(position).getSensorValue());
    holder.timeStamp.setText(sensors.get(position).getTimeStamp());

    }

    @Override
    public int getItemCount() {
        return sensors.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView sensorNames,  timeStamp, sensorValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sensorNames = itemView.findViewById(R.id.sensorName);
            timeStamp = itemView.findViewById(R.id.sensorValue);
            sensorValue = itemView.findViewById(R.id.sensorTimestamp);


        }
    }


}
