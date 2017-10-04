package com.example.windows10.work_weather;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

class NurseTimer {
    private ImageView nurseImage;
    private String nurseId;
    private CountDownTimer nurseTimer;


    NurseTimer(final Database database){
        nurseTimer = new CountDownTimer((1000 * 60 * 120), (1000 * 60 * 120)) {
            public void onTick(long millisUntilFinished) {}
            public void onFinish() {
                nurseImage.setVisibility(View.GONE);
                Log.d("NurseTimer","nurseTimeout() "+"Removed");
                database.changedMind(nurseId);
            }
        };
    }

    void setInfo(final ImageView nurseImage, final String nurseId){
        this.nurseImage = nurseImage;
        this.nurseId = nurseId;
    }

    void startTimer(){
        nurseTimer.start();
        Log.d("Main_Room","NurseTimer has started " + nurseId);
    }

    void maxedReached(){
        nurseTimer.cancel();
        nurseImage.setVisibility(View.GONE);
        Log.d("NurseTimer","nurse is closed and timer is cleared: " + nurseId);
    }
    String getNurseId(){
        return nurseId;
    }

}
