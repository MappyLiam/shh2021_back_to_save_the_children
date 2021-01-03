package org.snu.mask;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    ImageButton maskButton;
    ImageButton envButton;

    static RequestQueue requestQueue;

    public final int DATA_NUM = 1;
    public final int PM10_BAD = 150;
    public final int PM2_5_BAD = 75;
    public final int CO2_BAD = 1000;
    public final int TVOC_BAD = 2000;

    public Data data = new Data();

    String url = "http://childsafemask-env.eba-gscrq5yn.ap-northeast-2.elasticbeanstalk.com/app_api/now_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        maskButton = findViewById(R.id.maskButton);
        envButton = findViewById(R.id.envButton);

        maskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MaskActivity.class);
                startActivity(intent);
            }
        });

        envButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EnvironmentActivity.class);
                startActivity(intent);
                finish();
            }
        });

        makeRequest();
    }

    public void makeRequest() {
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        println("응답-> " + response);

                        processResponse(response);

                        maskButton.setVisibility(View.VISIBLE);
                        if(!data.mask_wear_data[DATA_NUM-1].wearing) {
                            maskButton.setImageResource(R.drawable.mask_button_no);
                        }

                        envButton.setVisibility(View.VISIBLE);
                        if(!isAirGood()) {
                            envButton.setImageResource(R.drawable.env_button_bad);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        println("에러-> " + error.getMessage());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }
        };

        request.setShouldCache(false);
        requestQueue.add(request);
    }

    public void processResponse(String response) {
        for(int i = 0; i < DATA_NUM; i++) {
            data.mask_wear_data[i] = new Mask_wear_data();
            data.fine_dust_data[i] = new Fine_dust_data();
            data.co2_data[i] = new CO2_data();
            data.tvoc_data[i] = new TVOC_data();
        }

        JsonParser jsonParser = new JsonParser();

        JsonObject jsonObject = (JsonObject) jsonParser.parse(response);
        JsonArray jsonArrayMaskWearData = (JsonArray) jsonObject.get("mask_wear_data");
        JsonArray jsonArrayFineDustData = (JsonArray) jsonObject.get("fine_dust_data");
        JsonArray jsonArrayCO2Data = (JsonArray) jsonObject.get("CO2_data");
        JsonArray jsonArrayTVOCData = (JsonArray) jsonObject.get("TVOC_data");

        for(int i = 0; i < DATA_NUM; i++){
            JsonObject object;

            object = (JsonObject) jsonArrayMaskWearData.get(i);
            data.mask_wear_data[i].datetime = object.get("datetime").getAsString();
            data.mask_wear_data[i].wearing = object.get("wearing").getAsBoolean();

            object = (JsonObject) jsonArrayFineDustData.get(i);
            data.fine_dust_data[i].datetime = object.get("datetime").getAsString();
            data.fine_dust_data[i].pm2_5 = object.get("pm2_5").getAsInt();
            data.fine_dust_data[i].pm10 = object.get("pm10").getAsInt();

            object = (JsonObject) jsonArrayCO2Data.get(i);
            data.co2_data[i].datetime = object.get("datetime").getAsString();
            data.co2_data[i].ppm = object.get("ppm").getAsInt();

            object = (JsonObject) jsonArrayTVOCData.get(i);
            data.tvoc_data[i].datetime = object.get("datetime").getAsString();
            data.tvoc_data[i].ppb = object.get("ppb").getAsInt();
        }
    }

    public class Data {
        Mask_wear_data[] mask_wear_data = new Mask_wear_data[DATA_NUM];
        Fine_dust_data[] fine_dust_data = new Fine_dust_data[DATA_NUM];
        CO2_data[] co2_data = new CO2_data[DATA_NUM];
        TVOC_data[] tvoc_data = new TVOC_data[DATA_NUM];
    }

    public static class Mask_wear_data {
        String datetime;
        boolean wearing;
    }

    public static class Fine_dust_data {
        String datetime;
        int pm2_5;
        int pm10;
    }

    public static class CO2_data {
        String datetime;
        int ppm;
    }

    public static class TVOC_data {
        String datetime;
        int ppb;
    }

    public boolean isAirGood() {
        if(data.fine_dust_data[DATA_NUM-1].pm10 >= PM10_BAD) {
            return false;
        }
        if(data.fine_dust_data[DATA_NUM-1].pm2_5 >= PM2_5_BAD) {
            return false;
        }
        if(data.co2_data[DATA_NUM-1].ppm >= CO2_BAD) {
            return false;
        }
        if(data.tvoc_data[DATA_NUM-1].ppb >= TVOC_BAD) {
            return false;
        }

        return true;
    }
}