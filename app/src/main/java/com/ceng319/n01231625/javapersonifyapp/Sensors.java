package com.ceng319.n01231625.javapersonifyapp;

public class Sensors {

    private String sensorNames;
    private int timeStamp;
    private int sensorValue;


    public Sensors(){}

    public String getSensorNames() {
        return sensorNames;
    }

    public void setSensorNames(String sensorNames) {
        this.sensorNames = sensorNames;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getSensorValue() {
        return sensorValue;
    }

    public void setSensorValue(int sensorValue) {
        this.sensorValue = sensorValue;
    }

    public Sensors(String sensorNames, int timeStamp, int sensorValue){

        this.sensorNames = sensorNames;
        this.timeStamp = timeStamp;
        this.sensorValue = sensorValue;
    }

}
