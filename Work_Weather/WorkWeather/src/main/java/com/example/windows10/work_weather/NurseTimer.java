package com.example.windows10.work_weather;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

class NurseTimer {
    private ImageView nurseImage;
    private String nurseId;
    private CountDownTimer nurseTimer;
    private Database database;
    private final Main_Room main_room;
    private ViewController viewController;

    NurseTimer(final ImageView nurseImage, final String nurseId, Context context, final ViewController viewController){
        Log.d("NurseTimer", "NurseTimer is created");
        this.nurseImage = nurseImage;
        this.nurseId = nurseId;
        database = new Database(context);
        main_room = new Main_Room();
        this.viewController = viewController;
        nurseTimer = new CountDownTimer((1000 * 60 * 120), (1000 * 60 * 120)) {
            public void onTick(long millisUntilFinished) {}
            public void onFinish() {
                nurseImage.setVisibility(View.GONE);
                database.changedMind(nurseId);
                main_room.checkWeather(database,viewController);
                Log.d("NurseTimer","nurseTimeout() "+"Removed");
            }
        };
    }

    void startTimer(){
        nurseTimer.start();
    }

    void maxedReached(){
        database.changedMind(nurseId);
        main_room.checkWeather(database,viewController);
        nurseTimer.cancel();
        Log.d("NurseTimer","timer is cleared: " + nurseId);
    }

    void closeNurseImage(){
        nurseImage.setVisibility(View.GONE);
    }

}
