package com.example.windows10.work_weather;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import java.text.DateFormat;
import java.util.Date;

class ButtonController {
    private Database database;
    private Button stormy;
    private Button rainy;
    private Button overcast;
    private Button cloudy;
    private Button sunny;
    private ImageView weatherOverlay;
    private String id;
    private Double mood;
    private ViewController viewController;
    private Main_Room main_room;


    ButtonController(Button stormy, Button rainy, Button overcast, Button cloudy, Button sunny, ImageView weatherOverlay, Context context,ViewController viewController) {

        this.stormy = stormy;
        this.rainy = rainy;
        this.overcast = overcast;
        this.cloudy = cloudy;
        this.sunny = sunny;
        this.weatherOverlay = weatherOverlay;
        this.viewController = viewController;
        
        main_room = new Main_Room();
        database = new Database(context);
        
        stormy.setOnClickListener(stormyClicked);
        rainy.setOnClickListener(rainyClicked);
        overcast.setOnClickListener(overcastClicked);
        cloudy.setOnClickListener(cloudyClicked);
        sunny.setOnClickListener(sunnyClicked);

    }

    /**
     * setViewable
     * Makes the buttons visible so the users can select what their mood
     */
    void setViewable() {
        stormy.setVisibility(View.VISIBLE);
        rainy.setVisibility(View.VISIBLE);
        overcast.setVisibility(View.VISIBLE);
        cloudy.setVisibility(View.VISIBLE);
        sunny.setVisibility(View.VISIBLE);
    }

    /**
     * setInvisible
     * Makes all the buttons invisible(GONE) so they can't be used when the
     * nurses are showed.
     */
    void setInvisible() {
        stormy.setVisibility(View.GONE);
        rainy.setVisibility(View.GONE);
        overcast.setVisibility(View.GONE);
        cloudy.setVisibility(View.GONE);
        sunny.setVisibility(View.GONE);
    }

    private View.OnClickListener stormyClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mood = 1.0;
            weatherOverlay.setImageResource(R.drawable.input_thunderstorm);
            select();
        }
    };
    private View.OnClickListener rainyClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mood = 2.0;
            weatherOverlay.setImageResource(R.drawable.input2_rainy);
            select();
        }
    };
    private View.OnClickListener overcastClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mood = 3.0;
            weatherOverlay.setImageResource(R.drawable.input3_clouded);
            select();
        }
    };
    private View.OnClickListener cloudyClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mood = 4.0;
            weatherOverlay.setImageResource(R.drawable.input4_half_clouded);
            select();
        }
    };
    private View.OnClickListener sunnyClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mood = 5.0;
            weatherOverlay.setImageResource(R.drawable.input5_sunny);
            select();
        }
    };

    private void select() {
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        Double avg = database.getAverage(mood);
        String query = "INSERT into nurses(`id`,`input`,`median`,`date`,`shift_id`,`inputDate`,`changed`)" +
                "VALUES('" + id + "','"+ mood +"','"+ avg +"','"+ currentDateTimeString +"','"+ database.getShiftNumber()+"','"+
                database.getDay()+"','"+ 0 +"');";

        database.addMedian(avg,currentDateTimeString,database.getShiftNumber());
        int factCheck = database.factCheck(id);
        if(factCheck == 1) {
            Log.d("ButtonController","factCheck is 1");
            database.changedMind(id);
            database.execSQL(query);
        }
        else if(factCheck == 0) {
            Log.d("ButtonController","factCheck is 0");
            database.execSQL(query);
        }
        setInvisible();
        viewController.afterInput();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                main_room.checkWeather(database,viewController);
            }
        },400);

    }

    void getId(String id) {
        this.id = id;
    }

    String id(){
        return id;
    }

    void cancelledInput(){
        setInvisible();
        viewController.viewNurses();
        main_room.checkWeather(database,viewController);
        viewController.setBack();
    }

}
