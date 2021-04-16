package com.ceng319.n01231625.javapersonifyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplayHardware extends AppCompatActivity {
    private Button btnClick;

    public RequestQueue queue;
    private FirebaseFunctions mFunctions;
    private ListView lv;
    private RecyclerView recyclerView;
    private Adapter Adapter;
    ArrayList<HashMap<String, String>>  dataList;
    List<sensorData> Sensordata=new ArrayList<>();
    private String value1,value2,value3,value4,value5,value6,value7,value8,value9,value10;
    Button mButtonAdd;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_hardware);
        Log.e("test","Before fuction");
      /*
        mFunctions = FirebaseFunctions.getInstance();


        List<sensorData> Sensordata=new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.list);
        Adapter = new Adapter(Sensordata);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(Adapter);//add adapter to recyclerview */
            jsonParse();

        //Log.e("test","After fuction");

        mButtonAdd = findViewById(R.id.refresh);
        mButtonAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                jsonParse();
            }
        });

    }

    private void jsonParse() {
        //  final TextView mTextViewResult = findViewById(R.id.coreVoltageResult);

        OkHttpClient client = new OkHttpClient();
        Request request;
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "\r\n    {\"uid\": \"XHVfPr23F7Meg0QuwEkYFvI8Fqd2\",\r\n    \"accessCode\": \"apple\"}\r\n");
        request = new Request.Builder()
                .url("https://us-central1-personify-c98fc.cloudfunctions.net/getDeviceData")
                .method("POST", body)
                .addHeader("Content-type", "application/json")
                .addHeader("Accept", "text/plain")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                   // Log.e("BEFORE PARSE",""+myResponse);


                            try {
                                JSONObject jsonObj = new JSONObject(myResponse);

                                // Getting JSON Array node

                                JSONArray data = jsonObj.getJSONArray("data");
                                JSONObject a = data.getJSONObject(0);
                                JSONObject lightSensor = a.getJSONObject("LIGHT_SENSOR");
                                value1 = lightSensor.getString("value");
                                JSONObject lightSensor2 = a.getJSONObject("LIGHT_SENSOR");
                                value2 = lightSensor2.getString("TIMESTAMP");

                                JSONArray data1 = jsonObj.getJSONArray("data");
                                JSONObject b = data1.getJSONObject(4);
                                JSONObject tempsensor = b.getJSONObject("TEMPERATURE");
                                value3 = tempsensor.getString("value");
                                JSONObject tempsensor2 = b.getJSONObject("TEMPERATURE");
                                value4 = tempsensor2.getString("TIMESTAMP");

                                JSONArray data2 = jsonObj.getJSONArray("data");
                                JSONObject c = data2.getJSONObject(5);
                                JSONObject core = c.getJSONObject("CORE_VOLTAGE");
                                value5 = core.getString("value");
                                JSONObject core2 = c.getJSONObject("CORE_VOLTAGE");
                                value6 = core2.getString("TIMESTAMP");

                                JSONArray data3 = jsonObj.getJSONArray("data");
                                JSONObject d = data3.getJSONObject(6);
                                JSONObject gesture = d.getJSONObject("GESTURE");
                                value7 = gesture.getString("value");
                                JSONObject gesture2 = d.getJSONObject("GESTURE");
                                value8 = gesture2.getString("TIMESTAMP");

                              /*  JSONArray data4 = jsonObj.getJSONArray("data");
                                JSONObject e = data4.getJSONObject(7);
                                JSONObject ir = e.getJSONObject("IR");
                                value7 = ir.getString("value");
                                JSONObject ir2 = e.getJSONObject("IR");
                                value8 = ir2.getString("TIMESTAMP"); */

                                    //  tv1.setText(d.getString("value"));

                                //    String lightSensorN = d.getString("TIMESTAMP");
                                  //  Log.e("parsing light",""+lightSensorN);


                               //lightSensorB.setText(lightSensorN);
                               //     String coreVoltageN = d.getString("CORE_VOLTAGE");
                               //     String temperatureN= d.getString("TEMPERATURE");
                               //     String gestureN = d.getString("GESTURE");
                                    //
                                    //          JSONObject nodeTime = d.getJSONObject("LIGHT_SENSOR");
                                    //        String timestamp = phone.getString("TIMESTAMP");
                                    // tmp hash map for single contact


                                  //Log.e("Parsing node",""+lightSensorN);


                                // Setup and Handover data to recyclerview


                            } catch (JSONException e) {

                            }
                    TextView tv1 = (TextView)findViewById(R.id.lsvalue);
                    tv1.setText(value1);
                    TextView tv2 = (TextView)findViewById(R.id.lstime);
                    tv2.setText(value2);
                    TextView tv3 = (TextView)findViewById(R.id.tempvalue);
                    tv3.setText(value3);
                    TextView tv4 = (TextView)findViewById(R.id.temptime);
                    tv4.setText(value4);
                    TextView tv5 = (TextView)findViewById(R.id.voltvalue);
                    tv5.setText(value5);
                    TextView tv6 = (TextView)findViewById(R.id.volttime);
                    tv6.setText(value6);
                    TextView tv7 = (TextView)findViewById(R.id.gesturevalue);
                    tv7.setText(value7);
                    TextView tv8 = (TextView)findViewById(R.id.gesturetime);
                    tv8.setText(value8);
                    /*TextView tv9 = (TextView)findViewById(R.id.irvalue);
                    tv7.setText(value9);
                    TextView tv10 = (TextView)findViewById(R.id.irvalue);
                    tv8.setText(value10);*/
                }
            }
        });
    }
}













