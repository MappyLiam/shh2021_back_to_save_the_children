package org.snu.mask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

public class MaskActivity extends AppCompatActivity {
    ImageButton returnButton;
    ImageView maskView;
    TextView timeView;
    TableLayout tableLayout;
    ImageView timeBarview;
    ImageView imageView;
    TableLayout tableLayout2;
    ImageView weekTitleView;
    TextView dayView;
    ImageView timeBar2;

    static RequestQueue requestQueue;

    public final int DATA_NUM = 500;
    public final int MY_SOCKET_TIMEOUT_MS = 20000;
    public int dataNum;

    String falseColor = "#C0C0C0";
    String trueColor = "#65C6E5";
    String noDataColor = "#E3E3E3";

    public Data data = new Data();

    String url = "http://childsafemask-env.eba-gscrq5yn.ap-northeast-2.elasticbeanstalk.com/app_api/time_series_data?interval=week";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mask);

        returnButton = findViewById(R.id.returnButton);
        maskView = findViewById(R.id.maskView);
        timeView = findViewById(R.id.timeView);
        tableLayout = findViewById(R.id.TableLayout);
        timeBarview = findViewById(R.id.timeBarView);
        imageView = findViewById(R.id.imageView3);
        tableLayout2 = findViewById(R.id.TableLayout2);
        weekTitleView = findViewById(R.id.weekTitleView);
        dayView = findViewById(R.id.dayView);
        timeBar2 = findViewById(R.id.timeBar2);

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        makeRequest();
    }

    public void makeRequest() {
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        println("응답-> " + response);

                        processResponse(response);

                        setTime();
                        setMaskView();
                        setTableLayout();
                        setTableLayout2();
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
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }

    public void processResponse(String response) {
        JsonParser jsonParser = new JsonParser();

        JsonObject jsonObject = (JsonObject) jsonParser.parse(response);
        dataNum = jsonObject.get("num_of_data").getAsInt();
        JsonArray jsonArrayMaskWearData = (JsonArray) jsonObject.get("mask_wear_data");
        JsonArray jsonArrayFineDustData = (JsonArray) jsonObject.get("fine_dust_data");
        JsonArray jsonArrayCO2Data = (JsonArray) jsonObject.get("CO2_data");
        JsonArray jsonArrayTVOCData = (JsonArray) jsonObject.get("TVOC_data");

        for(int i = 0; i < dataNum; i++) {
            data.mask_wear_data[i] = new HomeActivity.Mask_wear_data();
            data.fine_dust_data[i] = new HomeActivity.Fine_dust_data();
            data.co2_data[i] = new HomeActivity.CO2_data();
            data.tvoc_data[i] = new HomeActivity.TVOC_data();
        }

        for(int i = 0; i < dataNum; i++){
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
        HomeActivity.Mask_wear_data[] mask_wear_data = new HomeActivity.Mask_wear_data[DATA_NUM];
        HomeActivity.Fine_dust_data[] fine_dust_data = new HomeActivity.Fine_dust_data[DATA_NUM];
        HomeActivity.CO2_data[] co2_data = new HomeActivity.CO2_data[DATA_NUM];
        HomeActivity.TVOC_data[] tvoc_data = new HomeActivity.TVOC_data[DATA_NUM];
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

    public int getMonth(String time) {
        return Integer.parseInt(time.substring(5, 7));
    }

    public int getDay(String time) {
        return Integer.parseInt(time.substring(8, 10));
    }

    public int getHour(String time) {
        return Integer.parseInt(time.substring(11, 13));
    }

    public int getMinute(String time) {
        return Integer.parseInt(time.substring(14, 16));
    }

    public void setTime() {
        String time = data.mask_wear_data[dataNum-1].datetime;

        timeView.setText(getMonth(time)+"/"+getDay(time)+"\n");
        timeView.append(getHour(time)+":"+getMinute(time));
    }

    public void setMaskView() {
        if(data.mask_wear_data[dataNum-1].wearing) {
            maskView.setImageResource(R.drawable.mask_button_yes);
        }
        else {
            maskView.setImageResource(R.drawable.mask_button_no);
        }
    }

    public void setTableLayout() {
        int todayIndex;
        int currDay = getDay(data.mask_wear_data[dataNum-1].datetime);

        imageView.setVisibility(View.VISIBLE);

        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT));

        for(int i = dataNum-1; ; i--) {
            if(getDay(data.mask_wear_data[i].datetime) != currDay) {
                todayIndex = i + 1;
                break;
            }
        }

        for(int i = 0 ; i < 48 ; i++){
            String color;

            TextView textView = new TextView(this);
            TableRow.LayoutParams params = new TableRow.LayoutParams();
            params.width = TableRow.LayoutParams.WRAP_CONTENT;
            params.height = 200;
            textView.setLayoutParams(params);

            if(todayIndex+i >= dataNum) {
                color = noDataColor;
            }
            else if(data.mask_wear_data[todayIndex+i].wearing) {
                color = trueColor;
            }
            else {
                color = falseColor;
            }

            textView.setText(String.valueOf(i));
            textView.setTextSize(7F);
            textView.setTextColor(Color.parseColor("#00ffffff"));

            textView.setBackgroundColor(Color.parseColor(color));

            tableRow.addView(textView);
        }

        tableLayout.addView(tableRow);

        timeBarview.setVisibility(View.VISIBLE);
    }

    public void setTableLayout2() {
        int todayIndex;
        int yesterdayIndex = dataNum - 1;
        int currDay;

        weekTitleView.setVisibility(View.VISIBLE);
        timeBar2.setVisibility(View.VISIBLE);

        for(int i = 0; i < 6; i++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT));

            currDay = getDay(data.mask_wear_data[yesterdayIndex].datetime);
            dayView.append(currDay+"\n");

            for(int j = yesterdayIndex; ; j--) {
                if(getDay(data.mask_wear_data[j].datetime) != currDay) {
                    yesterdayIndex = j;
                    todayIndex = j + 1;
                    break;
                }
            }

            for(int j = 0 ; j < 48 ; j++){
                TextView textView = new TextView(this);
                TableRow.LayoutParams params = new TableRow.LayoutParams();
                params.width = 28;
                params.height = 100;
                textView.setLayoutParams(params);

                if(i == 0) {
                    if(todayIndex+j >= dataNum) {
                        textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.cell_no_data));
                    }
                    else if(data.mask_wear_data[todayIndex+j].wearing) {
                        textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.cell_mask_on));
                    }
                    else {
                        textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.cell_mask_off));
                    }
                }
                else {
                    if(data.mask_wear_data[todayIndex+j].wearing) {
                        textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.cell_mask_on_old));
                    }
                    else {
                        textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.cell_mask_off_old));
                    }
                }


                textView.setText("");
                textView.setTextColor(Color.parseColor("#00ffffff"));

                tableRow.addView(textView);
            }

            tableLayout2.addView(tableRow);
        }
    }
}