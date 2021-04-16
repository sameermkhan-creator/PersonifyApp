package com.ceng319.n01231625.javapersonifyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.hardware.Sensor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
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
    private TextView mTextViewResult;
    public RequestQueue queue;
    private FirebaseFunctions mFunctions;
    private ListView listView;
    RecyclerView recyclerView;
    List<Sensors> theSensors;
    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTextViewResult = findViewById(R.id.mTextViewResult);
        setContentView(R.layout.activity_display_hardware);
        mFunctions = FirebaseFunctions.getInstance();
        recyclerView = findViewById(R.id.dataList);
        // Button buttonParse = findViewById(R.id.getDataRequest);
        theSensors = new ArrayList<>();
        jsonParse();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(this, theSensors);


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

                    DisplayHardware.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                JSONObject json = new JSONObject(myResponse);
                                mTextViewResult.setText(json.getJSONObject("data").getString("LIGHT_SENSOR") + " " + json.getJSONObject("SENSORS").getInt("TIMESTAMP"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }



        });
    }
}












