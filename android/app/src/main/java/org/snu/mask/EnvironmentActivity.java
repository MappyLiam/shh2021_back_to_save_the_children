package org.snu.mask;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dinuscxj.progressbar.CircleProgressBar;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

public class EnvironmentActivity extends AppCompatActivity {

    TextView timeView;
    ImageButton returnButton;
    ImageView envView;
    CircleProgressBar progressBar_pm2_5;
    CircleProgressBar progressBar_pm10;
    CircleProgressBar progressBar_co2;
    CircleProgressBar progressBar_tvoc;
    TextView pm2_5View;
    TextView pm10View;
    TextView co2View;
    TextView tvocView;
    TableLayout tableLayout;
    ImageView airQualityView;
    ImageView timeBar;
    TextView dayView;
    TextView commentView;

    public int dataNum;
    public final int DATA_NUM = 500;
    public final int PM10_GOOD = 30;
    public final int PM10_NORMAL = 80;
    public final int PM10_BAD = 150;
    public final int PM2_5_GOOD = 15;
    public final int PM2_5_NORMAL = 35;
    public final int PM2_5_BAD = 75;
    public final int CO2_GOOD = 450;
    public final int CO2_NORMAL = 700;
    public final int CO2_BAD = 1000;
    public final int TVOC_GOOD = 200;
    public final int TVOC_NORMAL = 600;
    public final int TVOC_BAD = 2000;

    public String goodColor = "#65C6E5";
    public String normalColor = "#63DF7F";
    public String badColor = "#F0F41D";
    public String veryBadColor = "#F17293";

    public String falseColor = "#C0C0C0";
    public String trueColor = "#65C6E5";
    public String noDataColor = "#E3E3E3";

    public Data data = new Data();

    static RequestQueue requestQueue;

    String url = "http://childsafemask-env.eba-gscrq5yn.ap-northeast-2.elasticbeanstalk.com/app_api/time_series_data?interval=week";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_environment);

        timeView = findViewById(R.id.timeView);
        returnButton = findViewById(R.id.returnButton);
        envView = findViewById(R.id.envView);
        progressBar_pm2_5 = findViewById(R.id.progress_pm2_5);
        progressBar_pm10 = findViewById(R.id.progress_pm10);
        progressBar_co2 = findViewById(R.id.progress_co2);
        progressBar_tvoc = findViewById(R.id.progress_tvoc);
        pm2_5View = findViewById(R.id.pm2_5View);
        pm10View = findViewById(R.id.pm10View);
        co2View = findViewById(R.id.ppmView);
        tvocView = findViewById(R.id.ppbView);
        tableLayout = findViewById(R.id.TableLayout);
        airQualityView = findViewById(R.id.airQualityTitleView);
        timeBar = findViewById(R.id.timeBar);
        dayView = findViewById(R.id.dayView);
        commentView = findViewById(R.id.commentView);

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
                        processResponse(response);

                        setTime();
                        setEnvView();
                        setPm2_5();
                        setPm10();
                        setCO2();
                        setTvoc();
                        setTableLayout();
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
        JsonParser jsonParser = new JsonParser();

        JsonObject jsonObject = (JsonObject) jsonParser.parse(response);
        JsonArray jsonArrayMaskWearData = (JsonArray) jsonObject.get("mask_wear_data");
        JsonArray jsonArrayFineDustData = (JsonArray) jsonObject.get("fine_dust_data");
        JsonArray jsonArrayCO2Data = (JsonArray) jsonObject.get("CO2_data");
        JsonArray jsonArrayTVOCData = (JsonArray) jsonObject.get("TVOC_data");

        dataNum = jsonArrayMaskWearData.size();

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

    public boolean isAirGood() {
        if(data.fine_dust_data[dataNum-1].pm10 >= PM10_BAD) {
            return false;
        }
        if(data.fine_dust_data[dataNum-1].pm2_5 >= PM2_5_BAD) {
            return false;
        }
        if(data.co2_data[dataNum-1].ppm >= CO2_BAD) {
            return false;
        }
        if(data.tvoc_data[dataNum-1].ppb >= TVOC_BAD) {
            return false;
        }

        return true;
    }

    public int airQuality(int index) {
        int result = 0, quality;

        if(data.fine_dust_data[index].pm10 <= PM10_GOOD) {
            quality = 1;
        }
        else if(data.fine_dust_data[index].pm10 <= PM10_NORMAL) {
            quality = 2;
        }
        else if(data.fine_dust_data[index].pm10 <= PM10_BAD) {
            quality = 3;
        }
        else {
            quality = 4;
        }
        if(quality > result) {
            result = quality;
        }

        if(data.fine_dust_data[index].pm2_5 <= PM2_5_GOOD) {
            quality = 1;
        }
        else if(data.fine_dust_data[index].pm2_5 <= PM2_5_NORMAL) {
            quality = 2;
        }
        else if(data.fine_dust_data[index].pm2_5 <= PM2_5_BAD) {
            quality = 3;
        }
        else {
            quality = 4;
        }
        if(quality > result) {
            result = quality;
        }

        if(data.co2_data[index].ppm <= CO2_GOOD) {
            quality = 1;
        }
        else if(data.co2_data[index].ppm <= CO2_NORMAL) {
            quality = 2;
        }
        else if(data.co2_data[index].ppm <= CO2_BAD) {
            quality = 3;
        }
        else {
            quality = 4;
        }
        if(quality > result) {
            result = quality;
        }

        if(data.tvoc_data[index].ppb <= TVOC_GOOD) {
            quality = 1;
        }
        else if(data.tvoc_data[index].ppb <= TVOC_NORMAL) {
            quality = 2;
        }
        else if(data.tvoc_data[index].ppb <= TVOC_BAD) {
            quality = 3;
        }
        else {
            quality = 4;
        }
        if(quality > result) {
            result = quality;
        }

        return result;
    }

    public void setTime() {
        String time = data.mask_wear_data[dataNum-1].datetime;

        timeView.setText(getMonth(time)+"/"+getDay(time)+"\n");
        timeView.append(getHour(time)+":"+getMinute(time));
    }

    public void setEnvView() {
        if(isAirGood()) {
            envView.setImageResource(R.drawable.env_button_good);
        }
        else {
            envView.setImageResource(R.drawable.env_button_bad);
        }
    }

    public void setPm2_5() {
        String color;

        if(data.fine_dust_data[dataNum-1].pm2_5 <= PM2_5_GOOD) {
            color = goodColor;
        }
        else if(data.fine_dust_data[dataNum-1].pm2_5 <= PM2_5_NORMAL) {
            color = normalColor;
        }
        else if(data.fine_dust_data[dataNum-1].pm2_5 <= PM2_5_BAD) {
            color = badColor;
        }
        else {
            color = veryBadColor;
        }

        progressBar_pm2_5.setMax(PM2_5_BAD);
        progressBar_pm2_5.setProgressStartColor(Color.parseColor(color));
        progressBar_pm2_5.setProgressEndColor(Color.parseColor(color));
        progressBar_pm2_5.setProgress(data.fine_dust_data[dataNum-1].pm2_5);

        pm2_5View.setText("초미세먼지\n");
        pm2_5View.append(data.fine_dust_data[dataNum - 1].pm2_5 +"\n");
        pm2_5View.append("(㎍/m³)");
    }

    public void setPm10() {
        String color;

        if(data.fine_dust_data[dataNum-1].pm10 <= PM10_GOOD) {
            color = goodColor;
        }
        else if(data.fine_dust_data[dataNum-1].pm10 <= PM10_NORMAL) {
            color = normalColor;
        }
        else if(data.fine_dust_data[dataNum-1].pm10 <= PM10_BAD) {
            color = badColor;
        }
        else {
            color = veryBadColor;
        }

        progressBar_pm10.setMax(PM10_BAD);
        progressBar_pm10.setProgressStartColor(Color.parseColor(color));
        progressBar_pm10.setProgressEndColor(Color.parseColor(color));
        progressBar_pm10.setProgress(data.fine_dust_data[dataNum-1].pm10);

        pm10View.setText("미세먼지\n");
        pm10View.append(data.fine_dust_data[dataNum - 1].pm10 +"\n");
        pm10View.append("(㎍/m³)");
    }

    public void setCO2() {
        String color;

        if(data.co2_data[dataNum-1].ppm <= CO2_GOOD) {
            color = goodColor;
        }
        else if(data.co2_data[dataNum-1].ppm <= CO2_NORMAL) {
            color = normalColor;
        }
        else if(data.co2_data[dataNum-1].ppm <= CO2_BAD) {
            color = badColor;
        }
        else {
            color = veryBadColor;
        }

        progressBar_co2.setMax(CO2_BAD);
        progressBar_co2.setProgressStartColor(Color.parseColor(color));
        progressBar_co2.setProgressEndColor(Color.parseColor(color));
        progressBar_co2.setProgress(data.co2_data[dataNum-1].ppm);

        co2View.setText("CO2\n");
        co2View.append(data.co2_data[dataNum - 1].ppm +"\n");
        co2View.append("(ppm)");
    }

    public void setTvoc() {
        String color;

        if(data.tvoc_data[dataNum-1].ppb <= TVOC_GOOD) {
            color = goodColor;
        }
        else if(data.tvoc_data[dataNum-1].ppb <= TVOC_NORMAL) {
            color = normalColor;
        }
        else if(data.tvoc_data[dataNum-1].ppb <= TVOC_BAD) {
            color = badColor;
        }
        else {
            color = veryBadColor;
        }

        progressBar_tvoc.setMax(TVOC_BAD);
        progressBar_tvoc.setProgressStartColor(Color.parseColor(color));
        progressBar_tvoc.setProgressEndColor(Color.parseColor(color));
        progressBar_tvoc.setProgress(data.tvoc_data[dataNum-1].ppb);

        tvocView.setText("TVOC\n");
        tvocView.append(data.tvoc_data[dataNum - 1].ppb +"\n");
        tvocView.append("ppb");
    }

    public void setTableLayout() {
        int todayIndex;
        int yesterdayIndex = dataNum - 1;
        int currDay;

        airQualityView.setVisibility(View.VISIBLE);
        timeBar.setVisibility(View.VISIBLE);
        commentView.setVisibility(View.VISIBLE);

        for(int i = 0; i < 6; i++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT));

            currDay = getDay(data.fine_dust_data[yesterdayIndex].datetime);
            dayView.append(currDay+"\n");

            for(int j = yesterdayIndex; ; j--) {
                if(getDay(data.fine_dust_data[j].datetime) != currDay) {
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

                if(todayIndex+j >= dataNum) {
                    textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.cell_no_data));
                }
                else if(airQuality(todayIndex+j) == 1) {
                    textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.cell_good));
                }
                else if(airQuality(todayIndex+j) == 2) {
                    textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.cell_normal));
                }
                else if(airQuality(todayIndex+j) == 3) {
                    textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.cell_bad));
                }
                else if(airQuality(todayIndex+j) == 4) {
                    textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.cell_very_bad));
                }
                else {
                }

                textView.setText("");

                tableRow.addView(textView);
            }

            tableLayout.addView(tableRow);
        }
    }
}