package com.example.windows10.work_weather;

import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class DataScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_screen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Futura Medium.ttf");
        Database database = new Database(this);
        Double databaseMean = database.getRoomMedian();
        int databaseShiftNumber = database.getShiftNumber();
        String databaseDay = database.getDay();

        TextView screenMean = (TextView)findViewById(R.id.idScreen);
        TextView screenShift = (TextView)findViewById(R.id.shiftView);
        TextView screenDay = (TextView)findViewById(R.id.textView3);

        screenShift.setTypeface(typeface);
        screenMean.setTypeface(typeface);
        screenDay.setTypeface(typeface);

        screenMean.setText(String.format("Current Mean: %s", String.valueOf(databaseMean)));
        screenShift.setText(String.format("Current Shift: %s", String.valueOf(databaseShiftNumber)));
        screenDay.setText(String.format("Current Day: %s", String.valueOf(databaseDay)));

        Log.d("Main_Room","Shift number: "+  +database.getShiftNumber()+ " in Main Activity "+ " Receiver");
    }


}