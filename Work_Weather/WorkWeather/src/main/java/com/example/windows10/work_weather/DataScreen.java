package com.example.windows10.work_weather;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class DataScreen extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_screen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Futura Medium.ttf");
        Database db = new Database(this);
        Double a = db.getMedian();
        int y = db.getShiftNumber();
        String c = db.getDay();
        TextView i = (TextView)findViewById(R.id.idScreen);
        TextView x = (TextView)findViewById(R.id.shiftView);
        TextView b = (TextView)findViewById(R.id.textView3);
        x.setTypeface(typeface);
        i.setTypeface(typeface);
        b.setTypeface(typeface);
        i.setText("Current Median: "+String.valueOf(a));
        x.setText("Current Shift: "+String.valueOf(y));
        b.setText("Current Day: "+String.valueOf(c));
        Log.d("BB","Shift number: "+  +db.getShiftNumber()+ " in Main Activity "+ " Receiver");
    }


}